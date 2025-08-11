package Boundary;

import Control.InventoryManager;
import Control.SupirIntegrationManager;
import Entity.Item;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

/**
 * UI frame for Supir XML batch integration.
 * Shows missing (not yet in DB) items parsed from XML and allows importing selected / all.
 */
public class SupirIntegrationFrame extends JFrame {

    private final SupirIntegrationManager supirManager;
    private final InventoryManager inventoryManager;

    private JTable table;
    private DefaultTableModel model;
    private JTextArea detailsArea;
    private JLabel statusLbl;

    public SupirIntegrationFrame() {
        this.supirManager = new SupirIntegrationManager();
        this.inventoryManager = new InventoryManager();
        buildUi();
        loadDataAsync();
    }

    private void buildUi() {
        setTitle("Supir XML Integration");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(new Object[]{"Serial", "Name", "Supplier", "Category", "Expiration"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> updateDetails());
        JScrollPane tableScroll = new JScrollPane(table);

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        JScrollPane detailScroll = new JScrollPane(detailsArea);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroll, detailScroll);
        split.setDividerLocation(550);

        JButton refreshBtn = new JButton(new AbstractAction("Refresh") {
            @Override public void actionPerformed(ActionEvent e) { loadDataAsync(); }
        });
        JButton importSelBtn = new JButton(new AbstractAction("Import Selected") {
            @Override public void actionPerformed(ActionEvent e) { importSelected(); }
        });
        JButton importAllBtn = new JButton(new AbstractAction("Import All") {
            @Override public void actionPerformed(ActionEvent e) { importAll(); }
        });
        JButton removeBtn = new JButton(new AbstractAction("Remove Row") {
            @Override public void actionPerformed(ActionEvent e) { removeSelectedRows(); }
        });
        JButton closeBtn = new JButton(new AbstractAction("Close") {
            @Override public void actionPerformed(ActionEvent e) { dispose(); }
        });

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.add(refreshBtn);
        topBar.add(importSelBtn);
        topBar.add(importAllBtn);
        topBar.add(removeBtn);
        topBar.add(closeBtn);

        statusLbl = new JLabel("Loading...");

        JPanel south = new JPanel(new BorderLayout());
        south.add(statusLbl, BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
    }

    private void loadDataAsync() {
        statusLbl.setText("Parsing XML...");
        model.setRowCount(0);
        detailsArea.setText("");
        new SwingWorker<List<Item>, Void>() {
            @Override protected List<Item> doInBackground() throws Exception {
                List<Item> xmlItems = supirManager.loadAllFromXml();
                List<String> existing = inventoryManager.getAllItemSerials();
                return supirManager.filterMissing(xmlItems, existing);
            }
            @Override protected void done() {
                try {
                    List<Item> missing = get();
                    for (Item it : missing) {
                            model.addRow(new Object[]{it.getSerialNumber(), it.getName(), it.getSupplierId(), it.getCategory(), it.getExpirationDate()});
                    }
                    statusLbl.setText("Missing items loaded: " + missing.size());
                } catch (Exception ex) {
                    statusLbl.setText("Failed: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void updateDetails() {
        int row = table.getSelectedRow();
        if (row < 0) { detailsArea.setText(""); return; }
        StringBuilder sb = new StringBuilder();
        sb.append("Serial: ").append(model.getValueAt(row,0)).append('\n');
        sb.append("Name: ").append(model.getValueAt(row,1)).append('\n');
        sb.append("Supplier ID: ").append(model.getValueAt(row,2)).append('\n');
        sb.append("Category: ").append(model.getValueAt(row,3)).append('\n');
        sb.append("Expiration: ").append(model.getValueAt(row,4)).append('\n');
        detailsArea.setText(sb.toString());
    }

    private void importSelected() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) { statusLbl.setText("No rows selected"); return; }
        new SwingWorker<Void, Void>() {
            int ok = 0;
            @Override protected Void doInBackground() {
                for (int r : rows) {
                    Item item = rowToItem(r);
                    if (supirManager.importItem(item)) ok++;
                }
                return null;
            }
            @Override protected void done() { statusLbl.setText("Imported " + ok + "/" + rows.length + " selected items"); loadDataAsync(); }
        }.execute();
    }

    private void importAll() {
        int count = model.getRowCount();
        if (count == 0) { statusLbl.setText("No rows to import"); return; }
        new SwingWorker<Void, Void>() {
            int ok = 0;
            @Override protected Void doInBackground() {
                for (int r = 0; r < count; r++) {
                    Item item = rowToItem(r);
                    if (supirManager.importItem(item)) ok++;
                }
                return null;
            }
            @Override protected void done() { statusLbl.setText("Imported " + ok + "/" + count + " items"); loadDataAsync(); }
        }.execute();
    }

    private void removeSelectedRows() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) { statusLbl.setText("No selection"); return; }
        // remove from bottom to top
        for (int i = rows.length - 1; i >= 0; i--) model.removeRow(rows[i]);
        statusLbl.setText("Removed " + rows.length + " rows (local only)");
        detailsArea.setText("");
    }

    private Item rowToItem(int row) {
        String serial = (String) model.getValueAt(row,0);
        String name = (String) model.getValueAt(row,1);
        int supplier = Integer.parseInt(model.getValueAt(row,2).toString());
        String category = (String) model.getValueAt(row,3);
        java.util.Date exp = (java.util.Date) model.getValueAt(row,4); // since we inserted Date object
        Entity.ItemCategory cat = Entity.ItemCategory.Tools;
        try { cat = Entity.ItemCategory.valueOf(category); } catch (Exception ignored) {}
        return new Item(serial, name, "Imported from Supir", exp, cat, supplier);
    }
}
