package Boundary;

import Control.InventoryManager;
import Entity.Supplier;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.GroupLayout;
import javax.swing.JOptionPane;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.util.List;

/**
 * Supplier management frame (MODE + UPDATE + INSERT + DELETE)
 */
public class SupplierManagementFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private enum View { MODE, UPDATE, INSERT, DELETE }

    private final InventoryManager manager = new InventoryManager();

    // Root
    private JPanel rootCards;
    private CardLayout cardLayout;

    // MODE panel
    private JPanel panelMode;

    // UPDATE panel
    private JPanel panelUpdate;
    private JComboBox<Integer> comboSuppliers;
    private JTextField txtSupId;
    private JTextField txtName;
    private JTextField txtContact;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextArea txtAddress;
    private JButton btnUpdate;
    private JButton btnReset;
    private JButton btnRefreshList;
    private JButton btnUpdateBack;
    private JButton btnClose;
    private JLabel statusUpdateLabel;
    private Supplier currentLoadedSupplier;

    // INSERT panel
    private JPanel panelInsert;
    private JTextField txtInsId, txtInsName, txtInsContact, txtInsPhone, txtInsEmail;
    private JTextArea txtInsAddress;
    private JButton btnInsert, btnInsertClear, btnInsertBack, btnInsertClose;
    private JLabel statusInsertLabel;

    // DELETE panel
    private JPanel panelDelete;
    private JComboBox<Integer> comboDelSuppliers;
    private JTextField txtDelId, txtDelName, txtDelContact, txtDelPhone, txtDelEmail;
    private JTextArea txtDelAddress;
    private JButton btnDelRefresh, btnDelete, btnDelBack, btnDelClose;
    private JLabel statusDeleteLabel;
    private Supplier currentDeleteLoadedSupplier;

    public SupplierManagementFrame() {
    setTitle("Supplier Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(720, 450));
        initRoot();
        pack();
        setLocationRelativeTo(null);
        switchView(View.MODE);
    }

    private void initRoot() {
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
    JLabel title = new JLabel("Supplier Management");
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, title.getFont().getSize() + 2));

    JButton btnGoUpdate = new JButton("Update Existing Supplier");
    JButton btnGoInsert = new JButton("Add New Supplier");
    JButton btnGoDelete = new JButton("Delete Supplier");

        btnGoUpdate.addActionListener(e -> {
            switchView(View.UPDATE);
            if (comboSuppliers.getItemCount() == 0) loadSupplierIds();
        });

        btnGoInsert.addActionListener(e -> {
            switchView(View.INSERT);
            SwingUtilities.invokeLater(() -> txtInsId.requestFocusInWindow());
        });

        btnGoDelete.addActionListener(e -> {
            switchView(View.DELETE);
            if (comboDelSuppliers.getItemCount() == 0) loadDeleteSupplierIds();
        });

        GroupLayout gl = new GroupLayout(panelMode);
        panelMode.setLayout(gl);
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        gl.setHorizontalGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(title)
            .addComponent(btnGoUpdate, GroupLayout.PREFERRED_SIZE, 230, GroupLayout.PREFERRED_SIZE)
            .addComponent(btnGoInsert, GroupLayout.PREFERRED_SIZE, 230, GroupLayout.PREFERRED_SIZE)
            .addComponent(btnGoDelete, GroupLayout.PREFERRED_SIZE, 230, GroupLayout.PREFERRED_SIZE)
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGap(40)
            .addComponent(title)
            .addGap(30)
            .addComponent(btnGoUpdate, 42, 42, 42)
            .addGap(20)
            .addComponent(btnGoInsert, 42, 42, 42)
            .addGap(20)
            .addComponent(btnGoDelete, 42, 42, 42)
            .addGap(200)
        );

        rootCards.add(panelMode, View.MODE.name());
    }

    // === UPDATE CARD ===
    private void initUpdateCard() {
        panelUpdate = new JPanel();

    JLabel lblSelect = new JLabel("Select Supplier:");
        comboSuppliers = new JComboBox<>();
        comboSuppliers.setPrototypeDisplayValue(999999999);
        comboSuppliers.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && e.getItem() != null) {
                Integer id = (Integer) e.getItem();
                loadSupplierDetails(id);
            }
        });

    btnRefreshList = new JButton("Refresh");
        btnRefreshList.addActionListener(e -> refreshSupplierListPreserveSelection());

    JLabel lblId = new JLabel("ID:");
        txtSupId = new JTextField();
        txtSupId.setEditable(false);
    JLabel lblName = new JLabel("Name:");
        txtName = new JTextField();
    JLabel lblContact = new JLabel("Contact Person:");
        txtContact = new JTextField();
    JLabel lblPhone = new JLabel("Phone:");
        txtPhone = new JTextField();
    JLabel lblEmail = new JLabel("Email:");
        txtEmail = new JTextField();
    JLabel lblAddress = new JLabel("Address:");
        txtAddress = new JTextArea(4, 20);
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        JScrollPane scrollAddress = new JScrollPane(txtAddress);

    btnUpdate = new JButton("Update");
        btnUpdate.addActionListener(e -> onUpdateSupplier());
    btnReset = new JButton("Reset");
        btnReset.addActionListener(e -> {
            if (currentLoadedSupplier != null) {
                setFormFromSupplier(currentLoadedSupplier);
                statusUpdate("שחזור בוצע");
            }
        });
    btnUpdateBack = new JButton("Back");
        btnUpdateBack.addActionListener(e -> switchView(View.MODE));
    btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());

    statusUpdateLabel = new JLabel(" ");
    statusUpdateLabel.setHorizontalAlignment(JLabel.LEFT);

        GroupLayout gl = new GroupLayout(panelUpdate);
        panelUpdate.setLayout(gl);
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        gl.setHorizontalGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblSelect)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboSuppliers, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRefreshList)
                .addGap(0, 250, Short.MAX_VALUE))
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblId)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtSupId, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE))
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtName))
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblContact)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtContact))
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblPhone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPhone, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 240, Short.MAX_VALUE))
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblEmail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmail))
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblAddress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollAddress))
            .addGroup(GroupLayout.Alignment.TRAILING, gl.createSequentialGroup()
                .addComponent(btnUpdateBack)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnUpdate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReset)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose))
            .addComponent(statusUpdateLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblSelect)
                .addComponent(comboSuppliers, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(btnRefreshList))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblId)
                .addComponent(txtSupId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblName)
                .addComponent(txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblContact)
                .addComponent(txtContact, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblPhone)
                .addComponent(txtPhone, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblEmail)
                .addComponent(txtEmail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(lblAddress)
                .addComponent(scrollAddress, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))
            .addGap(10)
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(btnUpdateBack)
                .addComponent(btnUpdate)
                .addComponent(btnReset)
                .addComponent(btnClose))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(statusUpdateLabel)
        );

        rootCards.add(panelUpdate, View.UPDATE.name());
    }

    // === INSERT CARD ===
    private void initInsertCard() {
        panelInsert = new JPanel();
    JLabel title = new JLabel("Create New Supplier");
        title.setFont(title.getFont().deriveFont(Font.BOLD));

    JLabel lblId = new JLabel("ID:");
        txtInsId = new JTextField();
    JLabel lblName = new JLabel("Name:");
        txtInsName = new JTextField();
    JLabel lblContact = new JLabel("Contact Person:");
        txtInsContact = new JTextField();
    JLabel lblPhone = new JLabel("Phone:");
        txtInsPhone = new JTextField();
    JLabel lblEmail = new JLabel("Email:");
        txtInsEmail = new JTextField();
    JLabel lblAddress = new JLabel("Address:");
        txtInsAddress = new JTextArea(4, 20);
        txtInsAddress.setLineWrap(true);
        txtInsAddress.setWrapStyleWord(true);
        JScrollPane scrollAddress = new JScrollPane(txtInsAddress);

    btnInsert = new JButton("Add");
        btnInsert.addActionListener(e -> onInsertSupplier());
    btnInsertClear = new JButton("Clear");
        btnInsertClear.addActionListener(e -> { clearInsertForm(); statusInsert("נקה"); });
    btnInsertBack = new JButton("Back");
        btnInsertBack.addActionListener(e -> switchView(View.MODE));
    btnInsertClose = new JButton("Close");
        btnInsertClose.addActionListener(e -> dispose());

    statusInsertLabel = new JLabel(" ");
    statusInsertLabel.setHorizontalAlignment(JLabel.LEFT);

        GroupLayout gl = new GroupLayout(panelInsert);
        panelInsert.setLayout(gl);
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        gl.setHorizontalGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(title)
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblId)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtInsId, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 350, Short.MAX_VALUE))
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtInsName))
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblContact)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtInsContact))
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblPhone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtInsPhone, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 240, Short.MAX_VALUE))
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblEmail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtInsEmail))
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblAddress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollAddress))
            .addGroup(GroupLayout.Alignment.TRAILING, gl.createSequentialGroup()
                .addComponent(btnInsertBack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnInsertClose)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnInsert)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnInsertClear))
            .addComponent(statusInsertLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
            .addComponent(title)
            .addGap(10)
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblId)
                .addComponent(txtInsId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblName)
                .addComponent(txtInsName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblContact)
                .addComponent(txtInsContact, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblPhone)
                .addComponent(txtInsPhone, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblEmail)
                .addComponent(txtInsEmail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(lblAddress)
                .addComponent(scrollAddress, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))
            .addGap(10)
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(btnInsert)
                .addComponent(btnInsertClear)
                .addComponent(btnInsertBack)
                .addComponent(btnInsertClose))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(statusInsertLabel)
        );

        clearInsertForm();
    statusInsert("Ready for input");
        rootCards.add(panelInsert, View.INSERT.name());
    }

    // === DELETE CARD ===
    private void initDeleteCard() {
        panelDelete = new JPanel();

    JLabel lblDelSelect = new JLabel("Select Supplier:");
        comboDelSuppliers = new JComboBox<>();
        comboDelSuppliers.setPrototypeDisplayValue(999999999);
        comboDelSuppliers.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED && e.getItem()!=null) {
                Integer id = (Integer) e.getItem();
                loadDeleteSupplierDetails(id);
            }
        });

    btnDelRefresh = new JButton("Refresh");
        btnDelRefresh.addActionListener(e -> refreshDeleteListPreserveSelection());

    JLabel lblDelId = new JLabel("ID:");
        txtDelId = new JTextField(); txtDelId.setEditable(false);
    JLabel lblDelName = new JLabel("Name:");
        txtDelName = new JTextField(); txtDelName.setEditable(false);
    JLabel lblDelContact = new JLabel("Contact Person:");
        txtDelContact = new JTextField(); txtDelContact.setEditable(false);
    JLabel lblDelPhone = new JLabel("Phone:");
        txtDelPhone = new JTextField(); txtDelPhone.setEditable(false);
    JLabel lblDelEmail = new JLabel("Email:");
        txtDelEmail = new JTextField(); txtDelEmail.setEditable(false);
    JLabel lblDelAddress = new JLabel("Address:");
        txtDelAddress = new JTextArea(4, 20); txtDelAddress.setEditable(false);
        txtDelAddress.setLineWrap(true); txtDelAddress.setWrapStyleWord(true);
        JScrollPane scrollAddr = new JScrollPane(txtDelAddress);

    btnDelete = new JButton("Delete");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> onDeleteSupplier());

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

        gl.setHorizontalGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblDelSelect)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboDelSuppliers, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelRefresh)
                .addGap(0, 250, Short.MAX_VALUE))
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblDelId)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDelId, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE))
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblDelName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDelName))
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblDelContact)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDelContact))
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblDelPhone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDelPhone, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 240, Short.MAX_VALUE))
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblDelEmail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDelEmail))
            .addGroup(gl.createSequentialGroup()
                .addComponent(lblDelAddress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollAddr))
            .addGroup(GroupLayout.Alignment.TRAILING, gl.createSequentialGroup()
                .addComponent(btnDelBack)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnDelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelClose))
            .addComponent(statusDeleteLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblDelSelect)
                .addComponent(comboDelSuppliers, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(btnDelRefresh))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblDelId)
                .addComponent(txtDelId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblDelName)
                .addComponent(txtDelName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblDelContact)
                .addComponent(txtDelContact, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblDelPhone)
                .addComponent(txtDelPhone, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblDelEmail)
                .addComponent(txtDelEmail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(lblDelAddress)
                .addComponent(scrollAddr, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))
            .addGap(10)
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(btnDelBack)
                .addComponent(btnDelete)
                .addComponent(btnDelClose))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(statusDeleteLabel)
        );

        rootCards.add(panelDelete, View.DELETE.name());
    }

    private void switchView(View v) {
        cardLayout.show(rootCards, v.name());
        if (v == View.UPDATE && comboSuppliers.getItemCount() == 0) {
            statusUpdate("Loading...");
            loadSupplierIds();
        }
        if (v == View.INSERT) {
            statusInsert("Ready for input");
            SwingUtilities.invokeLater(() -> txtInsId.requestFocusInWindow());
        }
        if (v == View.DELETE) {
            statusDelete("Loading suppliers...");
            if (comboDelSuppliers.getItemCount() == 0) loadDeleteSupplierIds();
        }
    }

    // === UPDATE logic ===
    private void loadSupplierIds() { loadSupplierIds(null); }

    private void loadSupplierIds(Integer preserveId) {
    statusUpdate("Loading supplier IDs...");
        btnRefreshList.setEnabled(false);
        new SwingWorker<List<Integer>, Void>() {
            @Override protected List<Integer> doInBackground() { return manager.getAllSupplierIds(); }
            @Override protected void done() {
                btnRefreshList.setEnabled(true);
                try {
                    List<Integer> ids = get();
                    comboSuppliers.removeAllItems();
                    if (ids != null) for (Integer id : ids) comboSuppliers.addItem(id);
                    if (preserveId != null) comboSuppliers.setSelectedItem(preserveId);
                    else if (comboSuppliers.getItemCount() > 0) comboSuppliers.setSelectedIndex(0);
                    if (comboSuppliers.getItemCount()==0) statusUpdate("No suppliers");
                    else statusUpdate("Loaded " + comboSuppliers.getItemCount() + " suppliers");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Error loading list");
                    statusUpdate("Error");
                }
            }
        }.execute();
    }

    private void refreshSupplierListPreserveSelection() {
        Integer sel = (Integer) comboSuppliers.getSelectedItem();
        loadSupplierIds(sel);
    }

    private void loadSupplierDetails(int id) {
    statusUpdate("Loading supplier...");
        disableUpdateForm();
        new SwingWorker<Supplier, Void>() {
            @Override protected Supplier doInBackground() { return manager.getSupplierById(id); }
            @Override protected void done() {
                try {
                    Supplier s = get();
                    if (s == null) {
                        showError("Supplier not found");
                        clearUpdateForm();
                        currentLoadedSupplier = null;
                        disableUpdateForm();
                        statusUpdate("Supplier not found");
                    } else {
                        currentLoadedSupplier = s;
                        setFormFromSupplier(s);
                        enableUpdateForm();
                        statusUpdate("Supplier loaded");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Error loading supplier");
                    statusUpdate("Error");
                }
            }
        }.execute();
    }

    private boolean validateUpdateForm() {
        String name = txtName.getText();
    if (name == null || name.trim().isEmpty()) { showError("Name required"); return false; }
    if (name.length() > 100) { showError("Name too long"); return false; }
        String contact = txtContact.getText();
    if (contact == null || contact.trim().isEmpty()) { showError("Contact person required"); return false; }
    if (contact.length() > 100) { showError("Contact person too long"); return false; }
        String phone = txtPhone.getText();
    if (phone == null || phone.trim().isEmpty()) { showError("Phone required"); return false; }
    if (!phone.matches("^[0-9+\\-]{6,20}$")) { showError("Invalid phone"); return false; }
        String email = txtEmail.getText();
    if (email == null || email.trim().isEmpty()) { showError("Email required"); return false; }
    if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) { showError("Invalid email"); return false; }
        String address = txtAddress.getText();
    if (address != null && address.length() > 200) { showError("Address too long"); return false; }
        return true;
    }

    private Supplier buildSupplierFromForm() {
        int id = Integer.parseInt(txtSupId.getText().trim());
        return new Supplier(id, txtName.getText(), txtContact.getText(), txtPhone.getText(),
                txtEmail.getText(), txtAddress.getText());
    }

    private void setFormFromSupplier(Supplier s) {
        txtSupId.setText(String.valueOf(s.getSupplierId()));
        txtName.setText(s.getName());
        txtContact.setText(s.getContactPerson());
        txtPhone.setText(s.getPhone());
        txtEmail.setText(s.getEmail());
        txtAddress.setText(s.getAddress());
    }

    private void clearUpdateForm() {
        txtSupId.setText("");
        txtName.setText("");
        txtContact.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
    }

    private void disableUpdateForm() { setUpdateFieldsEnabled(false); }
    private void enableUpdateForm() { setUpdateFieldsEnabled(true); }

    private void setUpdateFieldsEnabled(boolean enabled) {
        txtName.setEnabled(enabled);
        txtContact.setEnabled(enabled);
        txtPhone.setEnabled(enabled);
        txtEmail.setEnabled(enabled);
        txtAddress.setEnabled(enabled);
        btnUpdate.setEnabled(enabled);
        btnReset.setEnabled(enabled);
    }

    private void onUpdateSupplier() {
        if (!validateUpdateForm()) return;
        Supplier s = buildSupplierFromForm();
        boolean ok = manager.updateSupplier(s);
        if (ok) {
            showInfo("Updated successfully");
            loadSupplierDetails(s.getSupplierId());
        } else {
            showError("Update failed");
        }
    }

    private void statusUpdate(String msg) { statusUpdateLabel.setText(msg); }

    // === INSERT logic ===
    private boolean validateInsertForm() {
    String idStr = txtInsId.getText();
    if (idStr == null || idStr.trim().isEmpty()) { showError("ID required"); return false; }
        idStr = idStr.trim();
    if (!idStr.matches("^\\d+$")) { showError("ID must be digits only"); return false; }
        int id;
    try { id = Integer.parseInt(idStr); } catch (NumberFormatException ex) { showError("Invalid ID"); return false; }
    if (id <= 0 || id >= 1_000_000) { showError("ID out of range"); return false; }
        Supplier existing = manager.getSupplierById(id);
    if (existing != null) { showError("Supplier ID already exists"); return false; }

        String name = txtInsName.getText();
    if (name == null || name.trim().isEmpty()) { showError("Name required"); return false; }
    if (name.length() > 100) { showError("Name too long"); return false; }
        String contact = txtInsContact.getText();
    if (contact == null || contact.trim().isEmpty()) { showError("Contact person required"); return false; }
    if (contact.length() > 100) { showError("Contact person too long"); return false; }
        String phone = txtInsPhone.getText();
    if (phone == null || phone.trim().isEmpty()) { showError("Phone required"); return false; }
    if (!phone.matches("^[0-9+\\-]{6,20}$")) { showError("Invalid phone"); return false; }
        String email = txtInsEmail.getText();
    if (email == null || email.trim().isEmpty()) { showError("Email required"); return false; }
    if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) { showError("Invalid email"); return false; }
        String address = txtInsAddress.getText();
    if (address != null && address.length() > 200) { showError("Address too long"); return false; }
        return true;
    }

    private Supplier buildSupplierFromInsertForm() {
        int id = Integer.parseInt(txtInsId.getText().trim());
        return new Supplier(id, txtInsName.getText(), txtInsContact.getText(),
                txtInsPhone.getText(), txtInsEmail.getText(), txtInsAddress.getText());
    }

    private void clearInsertForm() {
        txtInsId.setText("");
        txtInsName.setText("");
        txtInsContact.setText("");
        txtInsPhone.setText("");
        txtInsEmail.setText("");
        txtInsAddress.setText("");
    }

    private void disableInsertForm() { setInsertFieldsEnabled(false); }
    private void enableInsertForm() { setInsertFieldsEnabled(true); }

    private void setInsertFieldsEnabled(boolean enabled) {
        txtInsId.setEnabled(enabled);
        txtInsName.setEnabled(enabled);
        txtInsContact.setEnabled(enabled);
        txtInsPhone.setEnabled(enabled);
        txtInsEmail.setEnabled(enabled);
        txtInsAddress.setEnabled(enabled);
        btnInsert.setEnabled(enabled);
        btnInsertClear.setEnabled(enabled);
    }

    private void onInsertSupplier() {
        if (!validateInsertForm()) return;
        Supplier s = buildSupplierFromInsertForm();
        disableInsertForm();
    statusInsert("Adding supplier...");
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() { return manager.insertSupplier(s); }
            @Override protected void done() {
                enableInsertForm();
                try {
                    boolean ok = get();
                    if (ok) {
            showInfo("Added successfully");
            statusInsert("Supplier added");
            int ans = JOptionPane.showConfirmDialog(SupplierManagementFrame.this,
                "Switch to update the new supplier?", "Completed", JOptionPane.YES_NO_OPTION);
                        if (ans == JOptionPane.YES_OPTION) {
                            switchView(View.UPDATE);
                            loadSupplierIds(s.getSupplierId());
                        } else {
                            clearInsertForm();
                            statusInsert("Supplier added, you can add another");
                        }
                    } else {
                        showError("Insert failed");
                        statusInsert("Error");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Error inserting");
                    statusInsert("Error");
                }
            }
        }.execute();
    }

    private void statusInsert(String msg) { statusInsertLabel.setText(msg); }

    // === DELETE logic ===
    private void loadDeleteSupplierIds() { loadDeleteSupplierIds(null); }

    private void loadDeleteSupplierIds(Integer preserveId) {
    statusDelete("Loading suppliers...");
        disableDeleteForm();
        new SwingWorker<List<Integer>, Void>() {
            @Override protected List<Integer> doInBackground() { return manager.getAllSupplierIds(); }
            @Override protected void done() {
                try {
                    List<Integer> ids = get();
                    comboDelSuppliers.removeAllItems();
                    if (ids != null) for (Integer id : ids) comboDelSuppliers.addItem(id);
                    if (preserveId != null) comboDelSuppliers.setSelectedItem(preserveId);
                    else if (comboDelSuppliers.getItemCount() > 0) comboDelSuppliers.setSelectedIndex(0);
                    if (comboDelSuppliers.getItemCount() == 0) {
                        clearDeletePreview();
                        statusDelete("No suppliers");
                    } else {
                        statusDelete("Loaded " + comboDelSuppliers.getItemCount() + " suppliers");
                        comboDelSuppliers.setEnabled(true);
                        btnDelRefresh.setEnabled(true);
                        // Trigger details load for selected
                        Integer sel = (Integer) comboDelSuppliers.getSelectedItem();
                        if (sel != null) loadDeleteSupplierDetails(sel);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Error loading list");
                    statusDelete("Error");
                }
            }
        }.execute();
    }

    private void refreshDeleteListPreserveSelection() {
        Integer sel = (Integer) comboDelSuppliers.getSelectedItem();
        loadDeleteSupplierIds(sel);
    }

    private void loadDeleteSupplierDetails(int id) {
    statusDelete("Loading supplier details...");
        enableDeleteButton(false);
        new SwingWorker<Supplier, Void>() {
            @Override protected Supplier doInBackground() { return manager.getSupplierById(id); }
            @Override protected void done() {
                try {
                    Supplier s = get();
                    if (s == null) {
                        clearDeletePreview();
                        statusDelete("Supplier not found");
                        enableDeleteButton(false);
                    } else {
                        currentDeleteLoadedSupplier = s;
                        txtDelId.setText(String.valueOf(s.getSupplierId()));
                        txtDelName.setText(s.getName());
                        txtDelContact.setText(s.getContactPerson());
                        txtDelPhone.setText(s.getPhone());
                        txtDelEmail.setText(s.getEmail());
                        txtDelAddress.setText(s.getAddress());
                        enableDeleteButton(true);
                        statusDelete("Supplier loaded");
                        enableDeleteForm(); // ensure controls active
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Error loading supplier");
                    statusDelete("Error");
                }
            }
        }.execute();
    }

    private void onDeleteSupplier() {
        if (currentDeleteLoadedSupplier == null) {
            showError("No supplier selected");
            return;
        }
    int ans = JOptionPane.showConfirmDialog(this, "Delete this supplier?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ans != JOptionPane.YES_OPTION) return;

        int id = currentDeleteLoadedSupplier.getSupplierId();
    statusDelete("Deleting...");
        disableDeleteForm();
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() { return manager.deleteSupplier(id); }
            @Override protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        showInfo("Deleted successfully");
                        // Remove id from combo
                        comboDelSuppliers.removeItem(Integer.valueOf(id));
                        if (comboDelSuppliers.getItemCount() == 0) {
                            clearDeletePreview();
                            statusDelete("No suppliers");
                        } else {
                            statusDelete("Deleted. Remaining: " + comboDelSuppliers.getItemCount());
                            Integer next = (Integer) comboDelSuppliers.getSelectedItem();
                            if (next != null) loadDeleteSupplierDetails(next);
                        }
                    } else {
                        showError("Delete failed");
                        statusDelete("Error");
                        enableDeleteForm();
                        if (currentDeleteLoadedSupplier != null)
                            enableDeleteButton(true);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError("Error deleting");
                    statusDelete("Error");
                    enableDeleteForm();
                }
            }
        }.execute();
    }

    private void clearDeletePreview() {
        txtDelId.setText("");
        txtDelName.setText("");
        txtDelContact.setText("");
        txtDelPhone.setText("");
        txtDelEmail.setText("");
        txtDelAddress.setText("");
        currentDeleteLoadedSupplier = null;
        enableDeleteButton(false);
    }

    private void enableDeleteButton(boolean b) { btnDelete.setEnabled(b); }

    private void disableDeleteForm() {
        comboDelSuppliers.setEnabled(false);
        btnDelRefresh.setEnabled(false);
        btnDelete.setEnabled(false);
        // back & close remain
    }

    private void enableDeleteForm() {
        comboDelSuppliers.setEnabled(true);
        btnDelRefresh.setEnabled(true);
        // delete button enabled separately
    }

    private void statusDelete(String msg) { statusDeleteLabel.setText(msg); }

    // === Common helpers ===
    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE); }
    private void showInfo(String msg) { JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE); }

    // Main launcher
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SupplierManagementFrame().setVisible(true));
    }
}
