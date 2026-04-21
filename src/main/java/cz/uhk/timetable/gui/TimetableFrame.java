package cz.uhk.timetable.gui;

import cz.uhk.timetable.model.LocationTimetable;
import cz.uhk.timetable.utils.TimetableProvider;
import cz.uhk.timetable.utils.impl.MockTimetableProvider;
import cz.uhk.timetable.utils.impl.StagTimetableProvider;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;

public class TimetableFrame extends JFrame {
    private LocationTimetable timetable;
    private TimetableProvider provider = new StagTimetableProvider();
    private JTable tabTimetable;
    private JPanel tabPanel = new JPanel();
    private JComboBox<String> cboBudova;
    private JComboBox<String> cboMistnost;
    private JButton btnNacist;

    public TimetableFrame(){
        super("FIM Rozvrhy");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initGui();
    }

    private void initGui() {
        // --- Horní panel pro výběr budovy a místnosti ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        cboBudova = new JComboBox<>(new String[]{"A", "J", "S"});
        cboMistnost = new JComboBox<>();
        btnNacist = new JButton("Načíst rozvrh");

        topPanel.add(new JLabel("Budova:"));
        topPanel.add(cboBudova);
        topPanel.add(new JLabel("Místnost:"));
        topPanel.add(cboMistnost);
        topPanel.add(btnNacist);

        // Při změně budovy načíst místnosti z API
        cboBudova.addActionListener(e -> nacistMistnosti());

        // Při kliknutí načíst rozvrh
        btnNacist.addActionListener(e -> nacistRozvrh());

        // --- Tabulka ---
        timetable = new LocationTimetable(); // prázdný timetable na začátek
        tabTimetable = new JTable(new TimetableModel());
        tabTimetable.setAutoCreateRowSorter(true);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(tabTimetable), BorderLayout.CENTER);

        // Načíst místnosti pro výchozí budovu
        nacistMistnosti();

        pack();
        setSize(900, 500);
    }

    private void nacistMistnosti() {
        String budova = (String) cboBudova.getSelectedItem();
        cboMistnost.removeAllItems();

        SwingWorker<java.util.List<String>, Void> worker = new SwingWorker<>() {
            @Override
            protected java.util.List<String> doInBackground() throws Exception {
                var url = "https://stag-demo.uhk.cz/ws/services/rest2/mistnost/getMistnostiInfo" +
                        "?zkrBudovy=%s&pracoviste=%%25&typ=U&outputFormat=JSON&cisloMistnosti=%%25"
                                .formatted(budova);

                var conn = new java.net.URL(url).openStream();
                var reader = new java.io.InputStreamReader(conn);
                var json = new com.google.gson.JsonParser().parse(reader).getAsJsonObject();

                // Správný klíč je "mistnostInfo", ne "mistnostInfoList"
                var pole = json.getAsJsonArray("mistnostInfo");

                java.util.List<String> mistnosti = new java.util.ArrayList<>();
                for (var item : pole) {
                    String cislo = item.getAsJsonObject().get("cisloMistnosti").getAsString();
                    mistnosti.add(cislo);
                }
                java.util.Collections.sort(mistnosti);
                return mistnosti;
            }

            @Override
            protected void done() {
                try {
                    for (String m : get()) {
                        cboMistnost.addItem(m);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TimetableFrame.this,
                            "Chyba při načítání místností: " + ex.getMessage(),
                            "Chyba", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void nacistRozvrh() {
        String budova = (String) cboBudova.getSelectedItem();
        String mistnost = (String) cboMistnost.getSelectedItem();

        if (mistnost == null) return;

        btnNacist.setEnabled(false);
        btnNacist.setText("Načítám...");

        SwingWorker<LocationTimetable, Void> worker = new SwingWorker<>() {
            @Override
            protected LocationTimetable doInBackground() {
                return provider.read(budova, mistnost);
            }

            @Override
            protected void done() {
                try {
                    timetable = get();
                    ((AbstractTableModel) tabTimetable.getModel()).fireTableDataChanged();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(TimetableFrame.this,
                            "Chyba při načítání rozvrhu: " + ex.getMessage(),
                            "Chyba", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnNacist.setEnabled(true);
                    btnNacist.setText("Načíst rozvrh");
                }
            }
        };
        worker.execute();
    }

    class TimetableModel extends AbstractTableModel{

        @Override
        public String getColumnName(int column) {
            switch (column){
                case 0: return "Zkratka";
                case 1: return "Celý Název";
                case 2: return "Den";
                case 3: return "Začátek";
                case 4: return "Konec";
                case 5: return "Vyučující";
            }
            return  "";
        }

        @Override
        public int getRowCount() {
            return timetable.getActivities().size();
        }

        @Override
        public int getColumnCount() {
            return 6;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            var act = timetable.getActivities().get(rowIndex);
            switch (columnIndex){
                case 0: return act.getCode();
                case 1: return act.getName();
                case 2: return act.getDay();
                case 3: return act.getStartTime();
                case 4: return act.getEndTime();
                case 5: return act.getTeacher();
            }
            return null;
        }
    }
}
