import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Database Operations class
class DatabaseOperations {
    private final Connection conn;

    public DatabaseOperations() {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost/jeton_db", "JetonDauti", "JetonDauti");
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database: " + e.getMessage(), e);
        }
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY, name VARCHAR(50), email VARCHAR(50))";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            JOptionPane.showMessageDialog(null, "Table created (or already exists).");
        } catch (SQLException e) {
            throw new RuntimeException("Error creating table: " + e.getMessage(), e);
        }
    }

    public void insertRecord(int id, String name, String email) {
        String sql = "INSERT INTO users VALUES(?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setString(3, email);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Record inserted!");
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting record: " + e.getMessage(), e);
        }
    }

    public void deleteRecord(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Record deleted!");
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting record: " + e.getMessage(), e);
        }
    }

    public String showRecords() {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT * FROM users";
        try (Statement stmt = conn.createStatement();
             ResultSet result = stmt.executeQuery(sql)) {
            while (result.next()) {
                sb.append(result.getInt("id")).append(" ")
                        .append(result.getString("name")).append(" ")
                        .append(result.getString("email")).append("\n");
            }
            return sb.toString();
        } catch (SQLException e) {
            throw new RuntimeException("Error showing records: " + e.getMessage(), e);
        }
    }
}

public class DatabaseInterfaceGUI extends JFrame {
    private JButton createTableBtn, insertBtn, deleteBtn, showRecordsBtn;
    private final DatabaseOperations dbOps;

    public DatabaseInterfaceGUI() {
        super("Database Interface App"); // Set title of the window
        dbOps = new DatabaseOperations();
        setupUI();
        setupActions();
    }

    private void setupUI() {
        getContentPane().setLayout(new BorderLayout()); // Use BorderLayout for easier centering
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10)); // Use a GridLayout for the buttons
        buttonPanel.setBackground(Color.LIGHT_GRAY); // Match the background color
        getContentPane().setBackground(Color.LIGHT_GRAY); // Set background color

        createTableBtn = new JButton("Create Table");
        insertBtn = new JButton("Insert Record");
        deleteBtn = new JButton("Delete Record");
        showRecordsBtn = new JButton("Show Records");

        JButton[] buttons = {createTableBtn, insertBtn, deleteBtn, showRecordsBtn};
        for (JButton button : buttons) {
            button.setForeground(Color.WHITE); // Set text color to white by default
            button.setBackground(new Color(51, 51, 51)); // Default background
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(true);
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(Color.WHITE); // Change background on hover
                    button.setForeground(new Color(51, 51, 51)); // Text color to #333
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(new Color(51, 51, 51)); // Revert background
                    button.setForeground(Color.WHITE); // Revert text color
                }
            });
            buttonPanel.add(button); // Add the button to the panel
        }

        add(buttonPanel, BorderLayout.CENTER); // Add the panel to the center of the layout

        setSize(800, 500);
        setLocationRelativeTo(null); // Center on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void setupActions() {
        createTableBtn.addActionListener(e -> dbOps.createTable());
        insertBtn.addActionListener(e -> insertRecord());
        deleteBtn.addActionListener(e -> deleteRecord());
        showRecordsBtn.addActionListener(e -> showRecords());
    }

    private void insertRecord() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter user id:"));
            String name = JOptionPane.showInputDialog(this, "Enter name:");
            String email = JOptionPane.showInputDialog(this, "Enter email:");
            dbOps.insertRecord(id, name, email);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the ID.");
        }
    }

    private void deleteRecord() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter user id to delete:"));
            dbOps.deleteRecord(id);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the ID.");
        }
    }

    private void showRecords() {
        String records = dbOps.showRecords();
        JOptionPane.showMessageDialog(this, records.isEmpty() ? "No records found." : records);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DatabaseInterfaceGUI::new);
    }
}
