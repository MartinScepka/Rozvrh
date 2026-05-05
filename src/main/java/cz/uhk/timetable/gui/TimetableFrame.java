package cz.uhk.timetable.gui;

import com.google.gson.JsonParser;
import cz.uhk.timetable.model.LocationTimetable;
import cz.uhk.timetable.utils.TimetableProvider;
import cz.uhk.timetable.utils.impl.StagTimetableProvider;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimetableFrame extends JFrame {
    private LocationTimetable timetable;
    private TimetableProvider provider = new StagTimetableProvider();
    private JTable tabTimetable;
    private JPanel tabPanel = new JPanel();
    private JComboBox<String> cboBudova;
    private JComboBox<String> cboMistnost;
    private JButton btnNacist;

    public TimetableFrame() {
        super("FIM Rozvrhy");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initGui();
    }

    private void initGui() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        cboBudova = new JComboBox<>(new String[]{"A", "B", "C", "H", "J", "S"});
        cboMistnost = new JComboBox<>();
        btnNacist = new JButton("Načíst rozvrh");

        topPanel.add(new JLabel("Budova:"));
        topPanel.add(cboBudova);
        topPanel.add(new JLabel("Místnost:"));
        topPanel.add(cboMistnost);
        topPanel.add(btnNacist);

        cboBudova.addActionListener(e -> nacistMistnosti());
        btnNacist.addActionListener(e -> nacistRozvrh());

        timetable = new LocationTimetable();
        tabTimetable = new JTable(new TimetableModel());
        tabTimetable.setAutoCreateRowSorter(true);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(tabTimetable), BorderLayout.CENTER);

        nacistMistnosti();

        pack();
        setSize(900, 500);
    }

    private void nacistMistnosti() {
        String budova = (String) cboBudova.getSelectedItem();
        cboMistnost.removeAllItems();

        var url = "https://stag-demo.uhk.cz/ws/services/rest2/mistnost/getMistnostiInfo" +
                "?zkrBudovy=%s&pracoviste=%%25&typ=U&outputFormat=JSON&cisloMistnosti=%%25"
                        .formatted(budova);

        try {
            var reader = new InputStreamReader(new URL(url).openStream());
            var json = JsonParser.parseReader(reader).getAsJsonObject();
            var pole = json.getAsJsonArray("mistnostInfo");

            List<String> mistnosti = new ArrayList<>();
            for (var item : pole) {
                String cislo = item.getAsJsonObject().get("cisloMistnosti").getAsString();
                mistnosti.add(cislo);
            }
            Collections.sort(mistnosti);

            for (String m : mistnosti) {
                cboMistnost.addItem(m);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    private void nacistRozvrh() {
        String budova = (String) cboBudova.getSelectedItem();
        String mistnost = (String) cboMistnost.getSelectedItem();

        if (mistnost == null) return;

        timetable = provider.read(budova, mistnost);
        ((AbstractTableModel) tabTimetable.getModel()).fireTableDataChanged();
    }

    class TimetableModel extends AbstractTableModel {

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0: return "Zkratka";
                case 1: return "Celý Název";
                case 2: return "Den";
                case 3: return "Začátek";
                case 4: return "Konec";
                case 5: return "Vyučující";
            }
            return "";
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
            switch (columnIndex) {
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