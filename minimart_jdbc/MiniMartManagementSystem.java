package minimart_jdbc;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class MiniMartManagementSystem extends JFrame {
    // Database URL
    // static final String DB_URL = "jdbc:mysql//sql12.freesqldatabase.com/3306/sql12707093";
    // static final String USERNAME = "sql12707093";
    // static final String PASSWORD = "kpncW4Dhnp";
    // GUI Components
    private JTextField nameField, priceField, quantityField, supplierField, searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public MiniMartManagementSystem() {
        setTitle("Mini Mart Management System Duplicate");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel for product details
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.add(new JLabel("Product Name:"));
        nameField = new JTextField();
        panel.add(nameField);
        panel.add(new JLabel("Price:"));
        priceField = new JTextField();
        panel.add(priceField);
        panel.add(new JLabel("Quantity In Stock:"));
        quantityField = new JTextField();
        panel.add(quantityField);
        panel.add(new JLabel("Supplier ID:"));
        supplierField = new JTextField();
        panel.add(supplierField);

        JButton addButton = new JButton("Add Product");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });
        panel.add(addButton);

        add(panel, BorderLayout.NORTH);

        // Panel for search and table
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(new JLabel("Search Product:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchProduct();
            }
        });
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.CENTER);

        tableModel = new DefaultTableModel(new Object[]{"ProductID", "Name", "Price", "QuantityInStock", "SupplierID"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.SOUTH);

        JButton viewButton = new JButton("View All Products");
        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewProducts();
            }
        });
        add(viewButton, BorderLayout.SOUTH);

        // Initialize database and populate table
        initializeDatabase();
        viewProducts();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://sql12.freesqldatabase.com/3306/sql12707093","sql12707093","kpncW4Dhnp")) {
            if (conn != null) {
                System.out.println("Connected to the database");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addProduct() {
        String name = nameField.getText();
        double price = Double.parseDouble(priceField.getText());
        int quantity = Integer.parseInt(quantityField.getText());
        int supplierID = Integer.parseInt(supplierField.getText());

        String sql = "INSERT INTO Product (Name, Price, QuantityInStock, SupplierID) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://sql12.freesqldatabase.com/3306/sql12707093","sql12707093","kpncW4Dhnp");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, quantity);
            pstmt.setInt(4, supplierID);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Product added successfully!");
            viewProducts();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void viewProducts() {
        String sql = "SELECT * FROM Product";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://sql12.freesqldatabase.com/3306/sql12707093","sql12707093","kpncW4Dhnp");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("ProductID"),
                        rs.getString("Name"),
                        rs.getDouble("Price"),
                        rs.getInt("QuantityInStock"),
                        rs.getInt("SupplierID")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void searchProduct() {
        String keyword = searchField.getText();
        String sql = "SELECT * FROM Product WHERE Name LIKE ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://sql12.freesqldatabase.com/3306/sql12707093","sql12707093","kpncW4Dhnp");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("ProductID"),
                        rs.getString("Name"),
                        rs.getDouble("Price"),
                        rs.getInt("QuantityInStock"),
                        rs.getInt("SupplierID")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MiniMartManagementSystem().setVisible(true);
            }
        });
    }
}