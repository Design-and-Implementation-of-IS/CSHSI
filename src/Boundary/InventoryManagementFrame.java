package Boundary;

import Control.InventoryManager;
import Entity.Item;
import Entity.ItemCategory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Inventory management UI (Update / Insert / Delete) in English (LTR).
 * Only UI text/orientation changed from the Hebrew version; business logic intact.
 */
public class InventoryManagementFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    // Removed hard-coded supplier ID (was 2). Insert panel now loads dynamic supplier IDs from DB.
    // Keeping constant commented if rollback needed.
    // private static final int TEMP_SUPPLIER_ID = 2;

    private final InventoryManager manager = new InventoryManager();

    // === Root & Cards ===
    private JPanel rootCards; // CardLayout container
    private JPanel panelMode;
    private JPanel panelUpdate;
    private JPanel panelInsert;
    private CardLayout cardLayout;

    private enum View { MODE, UPDATE, INSERT, DELETE }

    // === UPDATE CARD components ===
    private JComboBox<String> comboItems;
    private JTextField txtSerial;
    private JTextField txtName;
    private JTextArea txtDescription;
    private JFormattedTextField txtExpiration;
    private JComboBox<ItemCategory> comboCategory;
    private JTextField txtSupplierId;
    private JLabel statusLabel; // update status
    private JButton btnUpdate;
    private JButton btnReset;
    private JButton btnRefreshList;
    private JButton btnClose;
    private JButton btnUpdateBack;
    private Item currentLoadedItem; // snapshot for reset

    // === INSERT CARD components ===
    private JTextField txtInsSerial;
    private JTextField txtInsName;
    private JTextArea txtInsDescription;
    private JFormattedTextField txtInsExpiration;
    private JComboBox<ItemCategory> comboInsCategory;
    private JComboBox<Integer> comboInsSupplier;
    private JLabel statusInsertLabel;
    private JButton btnInsertCommit;
    private JButton btnInsertClear;
    private JButton btnInsertBack;
    private boolean insertPanelInitialized = false;

    // === DELETE CARD components ===
    private JPanel panelDelete;
    private JComboBox<String> comboDelItems;
    private JTextField txtDelSerial;
    private JTextField txtDelName;
    private JTextArea txtDelDescription;
    private JTextField txtDelExpiration;
    private JTextField txtDelCategory;
    private JTextField txtDelSupplier;
    private JButton btnDelRefresh;
    private JButton btnDelete;
    private JButton btnDelBack;
    private JButton btnDelClose;
    private JLabel statusDeleteLabel;

    public InventoryManagementFrame() {
        setTitle("Inventory Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(700, 450));
        initRootCards();
        pack();
        setLocationRelativeTo(null);
        switchView(View.MODE);
    }

    // === Root with CardLayout ===
    private void initRootCards() {
        cardLayout = new CardLayout();
        rootCards = new JPanel(cardLayout);
        getContentPane().add(rootCards);
        initModeCard();
        initUpdateCard();
    initInsertCard();
    initDeleteCard();
    }

    // === MODE CARD ===
    private void initModeCard() {
        panelMode = new JPanel();
    JLabel title = new JLabel("Inventory Management");
        title.setHorizontalAlignment(JLabel.CENTER);
    JButton btnGoUpdate = new JButton("Update Existing Item");
    JButton btnGoInsert = new JButton("Add New Item");
    JButton btnGoDelete = new JButton("Delete Item");
        btnGoUpdate.addActionListener(e -> { switchView(View.UPDATE); loadItemIds(); });
        btnGoInsert.addActionListener(e -> { switchView(View.INSERT); ensureInsertPanelInitializedOnce(); });
    btnGoDelete.addActionListener(e -> { switchView(View.DELETE); loadDeleteItemIds(); });

        GroupLayout gl = new GroupLayout(panelMode);
        panelMode.setLayout(gl);
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);
        gl.setHorizontalGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(title)
            .addComponent(btnGoUpdate, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
            .addComponent(btnGoInsert, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
            .addComponent(btnGoDelete, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE));
        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGap(40)
            .addComponent(title)
            .addGap(30)
            .addComponent(btnGoUpdate, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
            .addGap(20)
            .addComponent(btnGoInsert, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
            .addGap(20)
            .addComponent(btnGoDelete, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
            .addGap(200));

        rootCards.add(panelMode, View.MODE.name());
    }

    // === UPDATE CARD === (existing functionality refactored into a panel)
    private void initUpdateCard() {
        panelUpdate = new JPanel();

    JLabel lblSelect = new JLabel("Select Item:");
        comboItems = new JComboBox<>();
        comboItems.setPrototypeDisplayValue("0000000000");
        comboItems.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String serial = (String) e.getItem();
                loadItemDetails(serial);
            }
        });

    btnRefreshList = new JButton("Refresh");
        btnRefreshList.addActionListener(e -> refreshItemListPreserveSelection());

    JLabel lblSerial = new JLabel("Serial:");
        txtSerial = new JTextField();
        txtSerial.setEditable(false);

    JLabel lblName = new JLabel("Name:");
        txtName = new JTextField();

    JLabel lblDescription = new JLabel("Description:");
        txtDescription = new JTextArea(4, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane scrollDescription = new JScrollPane(txtDescription);

    JLabel lblExpiration = new JLabel("Expiration (" + DATE_PATTERN + "):");
        txtExpiration = new JFormattedTextField(new SimpleDateFormat(DATE_PATTERN));
        txtExpiration.setColumns(10);

    JLabel lblCategory = new JLabel("Category:");
        comboCategory = new JComboBox<>();
        for (ItemCategory c : ItemCategory.values()) comboCategory.addItem(c);

    JLabel lblSupplier = new JLabel("Supplier:");
        txtSupplierId = new JTextField();
        txtSupplierId.setEditable(false);

    btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(e -> onUpdate());
    btnReset = new JButton("Reset");
        btnReset.addActionListener(e -> resetForm());
    btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
    btnUpdateBack = new JButton("Back");
        btnUpdateBack.addActionListener(e -> switchView(View.MODE));

    statusLabel = new JLabel(" ");
    statusLabel.setHorizontalAlignment(JLabel.LEFT);

        GroupLayout gl = new GroupLayout(panelUpdate);
        panelUpdate.setLayout(gl);
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        gl.setHorizontalGroup(
            gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblSelect)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(comboItems, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnRefreshList)
                    .addGap(0, 160, Short.MAX_VALUE))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblSerial)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtSerial, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblName)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtName))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblDescription)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollDescription))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblExpiration)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtExpiration, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 160, Short.MAX_VALUE))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblCategory)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(comboCategory, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 210, Short.MAX_VALUE))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblSupplier)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtSupplierId, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 260, Short.MAX_VALUE))
                .addGroup(GroupLayout.Alignment.TRAILING, gl.createSequentialGroup()
                    .addComponent(btnUpdateBack)
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(btnUpdate)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnReset)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnClose))
                .addComponent(statusLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        gl.setVerticalGroup(
            gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSelect)
                    .addComponent(comboItems, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefreshList))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSerial)
                    .addComponent(txtSerial, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName)
                    .addComponent(txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(lblDescription)
                    .addComponent(scrollDescription, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblExpiration)
                    .addComponent(txtExpiration, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCategory)
                    .addComponent(comboCategory, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSupplier)
                    .addComponent(txtSupplierId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUpdateBack)
                    .addComponent(btnUpdate)
                    .addComponent(btnReset)
                    .addComponent(btnClose))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(statusLabel)
        );

        rootCards.add(panelUpdate, View.UPDATE.name());
    }

    // === INSERT CARD ===
    private void initInsertCard() {
        panelInsert = new JPanel();

    JLabel lblSerial = new JLabel("Serial:");
        txtInsSerial = new JTextField();
    JLabel lblName = new JLabel("Name:");
        txtInsName = new JTextField();
    JLabel lblDescription = new JLabel("Description:");
        txtInsDescription = new JTextArea(5, 20);
        txtInsDescription.setLineWrap(true);
        txtInsDescription.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtInsDescription);
    JLabel lblExpiration = new JLabel("Expiration (" + DATE_PATTERN + "):");
        txtInsExpiration = new JFormattedTextField(new SimpleDateFormat(DATE_PATTERN));
        txtInsExpiration.setColumns(10);
    JLabel lblCategory = new JLabel("Category:");
        comboInsCategory = new JComboBox<>();
    JLabel lblSupplier = new JLabel("Supplier:");
    comboInsSupplier = new JComboBox<>();
    comboInsSupplier.setEnabled(true);

    btnInsertCommit = new JButton("Add");
        btnInsertCommit.addActionListener(e -> onInsertCommit());
    btnInsertClear = new JButton("Clear");
        btnInsertClear.addActionListener(e -> clearInsertForm());
    btnInsertBack = new JButton("Back");
        btnInsertBack.addActionListener(e -> switchView(View.MODE));

    statusInsertLabel = new JLabel(" ");
    statusInsertLabel.setHorizontalAlignment(JLabel.LEFT);

        GroupLayout gl = new GroupLayout(panelInsert);
        panelInsert.setLayout(gl);
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        gl.setHorizontalGroup(
            gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblSerial)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtInsSerial, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 250, Short.MAX_VALUE))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblName)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtInsName))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblDescription)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollDesc))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblExpiration)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtInsExpiration, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 230, Short.MAX_VALUE))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblCategory)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(comboInsCategory, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 220, Short.MAX_VALUE))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblSupplier)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(comboInsSupplier, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 240, Short.MAX_VALUE))
                .addGroup(GroupLayout.Alignment.TRAILING, gl.createSequentialGroup()
                    .addComponent(btnInsertBack)
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(btnInsertCommit)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnInsertClear))
                .addComponent(statusInsertLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        gl.setVerticalGroup(
            gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSerial)
                    .addComponent(txtInsSerial, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName)
                    .addComponent(txtInsName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(lblDescription)
                    .addComponent(scrollDesc, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblExpiration)
                    .addComponent(txtInsExpiration, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCategory)
                    .addComponent(comboInsCategory, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSupplier)
                    .addComponent(comboInsSupplier, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnInsertBack)
                    .addComponent(btnInsertCommit)
                    .addComponent(btnInsertClear))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(statusInsertLabel)
        );

        rootCards.add(panelInsert, View.INSERT.name());
    }

    // === DELETE CARD ===
    private void initDeleteCard() {
        panelDelete = new JPanel();

    JLabel lblSelect = new JLabel("Select Item:");
        comboDelItems = new JComboBox<>();
        comboDelItems.setPrototypeDisplayValue("0000000000");
        comboDelItems.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String serial = (String) e.getItem();
                loadDeleteItemDetails(serial);
            }
        });

    btnDelRefresh = new JButton("Refresh");
        btnDelRefresh.addActionListener(e -> refreshDeleteListPreserveSelection());

    JLabel lblSerial = new JLabel("Serial:");
        txtDelSerial = new JTextField();
        txtDelSerial.setEditable(false);
    JLabel lblName = new JLabel("Name:");
        txtDelName = new JTextField();
        txtDelName.setEditable(false);
    JLabel lblDescription = new JLabel("Description:");
        txtDelDescription = new JTextArea(4, 20);
        txtDelDescription.setEditable(false);
        txtDelDescription.setLineWrap(true);
        txtDelDescription.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDelDescription);
    JLabel lblExpiration = new JLabel("Expiration:");
        txtDelExpiration = new JTextField();
        txtDelExpiration.setEditable(false);
    JLabel lblCategory = new JLabel("Category:");
        txtDelCategory = new JTextField();
        txtDelCategory.setEditable(false);
    JLabel lblSupplier = new JLabel("Supplier:");
        txtDelSupplier = new JTextField();
        txtDelSupplier.setEditable(false);

    btnDelete = new JButton("Delete");
        btnDelete.addActionListener(e -> onDelete());
        btnDelete.setEnabled(false);
    btnDelBack = new JButton("Back");
        btnDelBack.addActionListener(e -> switchView(View.MODE));
    btnDelClose = new JButton("Close");
        btnDelClose.addActionListener(e -> dispose());

    statusDeleteLabel = new JLabel(" ");
    statusDeleteLabel.setHorizontalAlignment(JLabel.LEFT);

        GroupLayout gl = new GroupLayout(panelDelete);
        panelDelete.setLayout(gl);
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        gl.setHorizontalGroup(
            gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblSelect)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(comboDelItems, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnDelRefresh)
                    .addGap(0, 160, Short.MAX_VALUE))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblSerial)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtDelSerial, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblName)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtDelName))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblDescription)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollDesc))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblExpiration)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtDelExpiration, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 160, Short.MAX_VALUE))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblCategory)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtDelCategory, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 210, Short.MAX_VALUE))
                .addGroup(gl.createSequentialGroup()
                    .addComponent(lblSupplier)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(txtDelSupplier, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 260, Short.MAX_VALUE))
                .addGroup(GroupLayout.Alignment.TRAILING, gl.createSequentialGroup()
                    .addComponent(btnDelBack)
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(btnDelete)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(btnDelClose))
                .addComponent(statusDeleteLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        gl.setVerticalGroup(
            gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSelect)
                    .addComponent(comboDelItems, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelRefresh))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSerial)
                    .addComponent(txtDelSerial, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName)
                    .addComponent(txtDelName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(lblDescription)
                    .addComponent(scrollDesc, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblExpiration)
                    .addComponent(txtDelExpiration, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCategory)
                    .addComponent(txtDelCategory, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSupplier)
                    .addComponent(txtDelSupplier, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(10)
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDelBack)
                    .addComponent(btnDelete)
                    .addComponent(btnDelClose))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(statusDeleteLabel)
        );

        rootCards.add(panelDelete, View.DELETE.name());
    }

    private void switchView(View v) {
        cardLayout.show(rootCards, v.name());
        if (v == View.UPDATE && (comboItems == null || comboItems.getItemCount() == 0)) {
            status("No items");
        }
        if (v == View.INSERT) {
            ensureInsertPanelInitializedOnce();
        }
        if (v == View.DELETE) {
            if (comboDelItems == null || comboDelItems.getItemCount() == 0) {
                statusDelete("No items");
            }
        }
    }

    // === UPDATE logic ===
    private void onUpdate() {
        if (!validateForm()) return;
        Item item = buildItemFromForm();
        boolean ok = manager.updateItem(item);
        if (ok) {
            showInfo("Update successful");
            // Re-fetch to ensure current snapshot
            loadItemDetails(item.getSerialNumber());
        } else {
            showError("Update failed");
        }
    }

    private void resetForm() {
        if (currentLoadedItem != null) {
            setFormFromItem(currentLoadedItem);
            status("Restored from database");
        } else {
            status("Nothing to restore");
        }
    }

    private void refreshItemListPreserveSelection() {
        String sel = (String) comboItems.getSelectedItem();
        loadItemIds(sel);
    }

    // --- Helper Methods ---

    private void loadItemIds() {
        loadItemIds(null);
    }

    private void loadItemIds(String preserveSelection) {
    status("Loading items list...");
        btnRefreshList.setEnabled(false);
    new SwingWorker<java.util.List<String>, Void>() {
            @Override
            protected List<String> doInBackground() {
                return manager.getAllItemSerials();
            }

            @Override
            protected void done() {
                btnRefreshList.setEnabled(true);
                try {
                    List<String> ids = get();
                    comboItems.removeAllItems();
                    if (ids != null) {
                        for (String s : ids) {
                            comboItems.addItem(s);
                        }
                    }
                    if (preserveSelection != null) {
                        comboItems.setSelectedItem(preserveSelection);
                    } else if (comboItems.getItemCount() > 0) {
                        comboItems.setSelectedIndex(0);
                    }
                    status("Items list loaded (" + (ids == null ? 0 : ids.size()) + ")");
                } catch (Exception ex) {
                    showError("Error loading list");
                    ex.printStackTrace();
                    status("Error loading list");
                }
            }
        }.execute();
    }

    private void loadItemDetails(String serial) {
        if (serial == null || serial.isEmpty()) {
            return;
        }
    status("Loading item " + serial + "...");
        disableForm();
        new SwingWorker<Item, Void>() {
            @Override
            protected Item doInBackground() {
                return manager.getItemBySerial(serial);
            }

            @Override
            protected void done() {
                enableForm();
                try {
                    Item it = get();
                    if (it == null) {
                        showError("Item not found");
                        clearForm();
                        currentLoadedItem = null;
                    } else {
                        currentLoadedItem = it;
                        setFormFromItem(it);
                        status("Item loaded " + serial);
                    }
                } catch (Exception ex) {
                    showError("Error loading item");
                    ex.printStackTrace();
                    status("Error");
                }
            }
        }.execute();
    }

    private boolean validateForm() {
        String name = txtName.getText();
        if (name == null || name.trim().isEmpty()) {
            showError("Name cannot be empty");
            return false;
        }
        String desc = txtDescription.getText();
        if (desc != null && desc.length() > 255) {
            showError("Description too long (max 255 chars)");
            return false;
        }
        String dateStr = txtExpiration.getText();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            showError("Invalid date format (" + DATE_PATTERN + ")");
            return false;
        }
        ItemCategory cat = (ItemCategory) comboCategory.getSelectedItem();
        if (cat == null) {
            showError("Select a category");
            return false;
        }
        return true;
    }

    private Item buildItemFromForm() {
        String serial = txtSerial.getText();
        String name = txtName.getText();
        String desc = txtDescription.getText();
        String dateStr = txtExpiration.getText();

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        sdf.setLenient(false);
        Date d;
        try {
            d = sdf.parse(dateStr);
        } catch (ParseException e) {
            d = new Date(); // fallback (should not happen after validation)
        }
        ItemCategory cat = (ItemCategory) comboCategory.getSelectedItem();
        int supplierId = 0;
        try {
            supplierId = Integer.parseInt(txtSupplierId.getText().trim());
        } catch (NumberFormatException ex) {
            // keep 0 if not parsable; updateItem likely fails if invalid
        }
        return new Item(serial, name, desc, d, cat, supplierId);
    }

    private void setFormFromItem(Item it) {
        if (it == null) {
            clearForm();
            return;
        }
        txtSerial.setText(it.getSerialNumber());
        txtName.setText(it.getName());
        txtDescription.setText(it.getDescription());
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        txtExpiration.setText(sdf.format(it.getExpirationDate()));

        // Map category string back to enum constant
        ItemCategory enumVal = null;
        if (it.getCategory() != null) {
            try { enumVal = ItemCategory.valueOf(it.getCategory()); }
            catch (IllegalArgumentException ex) { /* TODO: mismatch DB enum name */ }
        }
        if (enumVal == null && comboCategory.getItemCount() > 0) {
            enumVal = comboCategory.getItemAt(0);
        }
        comboCategory.setSelectedItem(enumVal);
        txtSupplierId.setText(String.valueOf(it.getSupplierId()));
    }

    private void clearForm() {
        txtSerial.setText("");
        txtName.setText("");
        txtDescription.setText("");
        txtExpiration.setText("");
        comboCategory.setSelectedItem(null);
        txtSupplierId.setText("");
    }

    private void disableForm() {
        setFormEnabled(false);
    }

    private void enableForm() {
        setFormEnabled(true);
    }

    private void setFormEnabled(boolean enabled) {
        txtName.setEnabled(enabled);
        txtDescription.setEnabled(enabled);
        txtExpiration.setEnabled(enabled);
        comboCategory.setEnabled(enabled);
        btnUpdate.setEnabled(enabled);
        btnReset.setEnabled(enabled);
    }

    private void showError(String msg) {
    JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
    JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void status(String msg) {
        statusLabel.setText(msg);
    }

    // Removed RTL orientation helper (English UI is LTR by default)

    // === INSERT logic ===
    private void ensureInsertPanelInitializedOnce() {
        if (insertPanelInitialized) return;
    // Load categories
    comboInsCategory.removeAllItems();
    for (ItemCategory c : ItemCategory.values()) comboInsCategory.addItem(c);
    // Load suppliers dynamically
    loadInsertSupplierIds();
        insertPanelInitialized = true;
    }


    private void onInsertCommit() {
        if (!validateInsertForm()) return;
        Item item = buildItemFromInsertForm();
        boolean ok = manager.insertItem(item);
        if (ok) {
            showInfo("Item added successfully");
            int choice = JOptionPane.showConfirmDialog(this, "Go to update screen for the new item?", "Question", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                switchView(View.UPDATE);
                loadItemIds(item.getSerialNumber());
            } else {
                clearInsertForm();
                // keep supplier & category selections
            }
        } else {
            showError("Insert failed");
        }
    }

    private boolean validateInsertForm() {
        String serial = txtInsSerial.getText();
    if (serial == null || serial.trim().isEmpty()) { showError("Serial is required"); return false; }
    if (!serial.matches("\\d+")) { showError("Serial must contain digits only"); return false; }
    if (serial.length() > 15) { showError("Serial too long"); return false; }
        // uniqueness
    if (manager.getItemBySerial(serial) != null) { showError("Serial already exists"); return false; }
        String name = txtInsName.getText();
    if (name == null || name.trim().isEmpty()) { showError("Item name is required"); return false; }
    if (name.length() > 100) { showError("Name too long"); return false; }
        String desc = txtInsDescription.getText();
    if (desc != null && desc.length() > 255) { showError("Description too long (max 255)"); return false; }
        String dateStr = txtInsExpiration.getText();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN); sdf.setLenient(false);
        Date parsed;
        try { parsed = sdf.parse(dateStr); }
    catch (ParseException e) { showError("Invalid date (" + DATE_PATTERN + ")"); return false; }
        // date not in past (allow today)
    if (isPastDate(parsed)) { showError("Expiration date cannot be in the past"); return false; }
        ItemCategory cat = (ItemCategory) comboInsCategory.getSelectedItem();
    if (cat == null) { showError("Select a category"); return false; }
    // TEMP supplier fixed -> validation assured
    Integer supp = (Integer) comboInsSupplier.getSelectedItem();
    if (supp == null) { showError("Select a supplier"); return false; }
        return true;
    }

    private boolean isPastDate(Date d) {
        Calendar cal = Calendar.getInstance();
        zeroTime(cal);
        Calendar other = Calendar.getInstance();
        other.setTime(d); zeroTime(other);
        return other.before(cal);
    }
    private void zeroTime(Calendar c) { c.set(Calendar.HOUR_OF_DAY,0); c.set(Calendar.MINUTE,0); c.set(Calendar.SECOND,0); c.set(Calendar.MILLISECOND,0); }

    private Item buildItemFromInsertForm() {
        String serial = txtInsSerial.getText().trim();
        String name = txtInsName.getText();
        String desc = txtInsDescription.getText();
        String dateStr = txtInsExpiration.getText();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN); sdf.setLenient(false);
        Date exp;
        try { exp = sdf.parse(dateStr); } catch (ParseException e) { exp = new Date(); }
        ItemCategory cat = (ItemCategory) comboInsCategory.getSelectedItem();
    Integer supplierId = (Integer) comboInsSupplier.getSelectedItem();
        return new Item(serial, name, desc, exp, cat, supplierId);
    }

    // === INSERT suppliers dynamic load ===
    private void loadInsertSupplierIds() {
        statusInsertLabel.setText("Loading suppliers...");
        comboInsSupplier.removeAllItems();
        comboInsSupplier.setEnabled(false);
        btnInsertCommit.setEnabled(false);
        new SwingWorker<List<Integer>, Void>() {
            @Override protected List<Integer> doInBackground() { return manager.getAllSupplierIds(); }
            @Override protected void done() {
                try {
                    List<Integer> ids = get();
                    if (ids != null) {
                        for (Integer id : ids) comboInsSupplier.addItem(id);
                    }
                    if (comboInsSupplier.getItemCount() > 0) {
                        comboInsSupplier.setSelectedIndex(0);
                        comboInsSupplier.setEnabled(true);
                        btnInsertCommit.setEnabled(true);
                        statusInsertLabel.setText("Loaded " + comboInsSupplier.getItemCount() + " suppliers");
                    } else {
                        statusInsertLabel.setText("No suppliers found");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    statusInsertLabel.setText("Error loading suppliers");
                }
            }
        }.execute();
    }

    private void clearInsertForm() {
        txtInsSerial.setText("");
        txtInsName.setText("");
        txtInsDescription.setText("");
        txtInsExpiration.setText("");
        // keep category & supplier selections
    }

    // === DELETE logic ===
    private void loadDeleteItemIds() { loadDeleteItemIds(null); }

    private void loadDeleteItemIds(String preserveSerial) {
    statusDelete("Loading items list...");
        if (btnDelRefresh != null) btnDelRefresh.setEnabled(false);
    new SwingWorker<java.util.List<String>, Void>() {
            @Override
            protected List<String> doInBackground() { return manager.getAllItemSerials(); }
            @Override
            protected void done() {
                if (btnDelRefresh != null) btnDelRefresh.setEnabled(true);
                try {
                    List<String> ids = get();
                    comboDelItems.removeAllItems();
                    if (ids != null) for (String s : ids) comboDelItems.addItem(s);
                    if (preserveSerial != null) comboDelItems.setSelectedItem(preserveSerial);
                    else if (comboDelItems.getItemCount() > 0) comboDelItems.setSelectedIndex(0);
                    if (comboDelItems.getItemCount() == 0) statusDelete("No items");
                    else statusDelete("Items list loaded (" + comboDelItems.getItemCount() + ")");
                } catch (Exception ex) {
                    showError("Error loading list");
                    ex.printStackTrace();
                    statusDelete("Error");
                }
            }
        }.execute();
    }

    private void refreshDeleteListPreserveSelection() {
        String sel = (String) comboDelItems.getSelectedItem();
        loadDeleteItemIds(sel);
    }

    private void loadDeleteItemDetails(String serial) {
        clearDeletePreview();
        enableDeleteForm(false);
        if (serial == null || serial.isEmpty()) return;
    statusDelete("Loading item " + serial + "...");
        new SwingWorker<Item, Void>() {
            @Override
            protected Item doInBackground() { return manager.getItemBySerial(serial); }
            @Override
            protected void done() {
                try {
                    Item it = get();
                    if (it == null) {
                        statusDelete("Item not found");
                        enableDeleteForm(false);
                    } else {
                        txtDelSerial.setText(it.getSerialNumber());
                        txtDelName.setText(it.getName());
                        txtDelDescription.setText(it.getDescription());
                        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
                        txtDelExpiration.setText(sdf.format(it.getExpirationDate()));
                        txtDelCategory.setText(it.getCategory());
                        txtDelSupplier.setText(String.valueOf(it.getSupplierId()));
                        enableDeleteForm(true);
                        statusDelete("Item loaded");
                    }
                } catch (Exception ex) {
                    showError("Error loading item");
                    ex.printStackTrace();
                    statusDelete("Error");
                }
            }
        }.execute();
    }

    private void onDelete() {
        String serial = (String) comboDelItems.getSelectedItem();
    if (serial == null || serial.isEmpty()) { showError("No item selected"); return; }
    int confirm = JOptionPane.showConfirmDialog(this, "Delete this item?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        boolean ok = manager.deleteItem(serial);
        if (ok) {
            showInfo("Item deleted successfully");
            comboDelItems.removeItem(serial);
            clearDeletePreview();
            enableDeleteForm(false);
            if (comboDelItems.getItemCount() == 0) statusDelete("No items");
            else statusDelete("Deleted. Remaining: " + comboDelItems.getItemCount());
        } else {
            showError("Delete failed");
        }
    }

    private void clearDeletePreview() {
        if (txtDelSerial != null) txtDelSerial.setText("");
        if (txtDelName != null) txtDelName.setText("");
        if (txtDelDescription != null) txtDelDescription.setText("");
        if (txtDelExpiration != null) txtDelExpiration.setText("");
        if (txtDelCategory != null) txtDelCategory.setText("");
        if (txtDelSupplier != null) txtDelSupplier.setText("");
    }

    private void enableDeleteForm(boolean enabled) {
        if (btnDelete != null) btnDelete.setEnabled(enabled);
    }

    private void statusDelete(String msg) { if (statusDeleteLabel != null) statusDeleteLabel.setText(msg); }


    // Optional main launcher for manual testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventoryManagementFrame().setVisible(true));
    }
}
