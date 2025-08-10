package Boundary;

import javax.swing.*;
import java.awt.*;

/**
 * Main navigation frame providing entry points to Inventory and Supplier management.
 * Opens single instances (reuses existing visible frames or brings them to front).
 */
public class MainFrame extends JFrame {
    private InventoryManagementFrame inventoryFrame;
    private SupplierManagementFrame supplierFrame;

    public MainFrame() {
        setTitle("DentalCare Administration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(420, 260));
        initUI();
        pack();
        setLocationRelativeTo(null);
    }

    private void initUI() {
        JLabel title = new JLabel("DentalCare Management Portal", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, title.getFont().getSize() + 2));

        JButton btnInventory = new JButton("Inventory Management");
        btnInventory.addActionListener(e -> openInventory());
        JButton btnSuppliers = new JButton("Supplier Management");
        btnSuppliers.addActionListener(e -> openSuppliers());
        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(e -> System.exit(0));

        JPanel center = new JPanel();
        GroupLayout gl = new GroupLayout(center);
        center.setLayout(gl);
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        gl.setHorizontalGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(title)
            .addComponent(btnInventory, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
            .addComponent(btnSuppliers, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
            .addComponent(btnExit, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGap(25)
            .addComponent(title)
            .addGap(25)
            .addComponent(btnInventory, 40, 40, 40)
            .addGap(15)
            .addComponent(btnSuppliers, 40, 40, 40)
            .addGap(25)
            .addComponent(btnExit, 32, 32, 32)
            .addGap(20)
        );

        getContentPane().add(center);
    }

    private void openInventory() {
        if (inventoryFrame == null || !inventoryFrame.isDisplayable()) {
            inventoryFrame = new InventoryManagementFrame();
        }
        inventoryFrame.setVisible(true);
        inventoryFrame.toFront();
    }

    private void openSuppliers() {
        if (supplierFrame == null || !supplierFrame.isDisplayable()) {
            supplierFrame = new SupplierManagementFrame();
        }
        supplierFrame.setVisible(true);
        supplierFrame.toFront();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
