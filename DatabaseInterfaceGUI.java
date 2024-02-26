import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DatabaseInterfaceGUI extends JFrame {

    private JButton createTableBtn;
    private JButton insertBtn;
    private JButton deleteBtn;
    private JButton showRecordsBtn;

    private Connection conn;

    public DatabaseInterfaceGUI() {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost/jeton_db", "JetonDauti", "JetonDauti");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error connecting to the database: " + e.getMessage());
            System.exit(1);
        }

        setLayout(new FlowLayout());

        createTableBtn = new JButton("Create Table");
        createTableBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createTable();
            }
        });

        insertBtn = new JButton("Insert Record");
        insertBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                insertRecord();
            }
        });

        deleteBtn = new JButton("Delete Record");
        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteRecord();
            }
        });

        showRecordsBtn = new JButton("Show Records");
        showRecordsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showRecords();
            }
        });

        add(createTableBtn);
        add(insertBtn);
        add(deleteBtn);
        add(showRecordsBtn);

        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void createTable() {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY, name VARCHAR(50), email VARCHAR(50))");
            JOptionPane.showMessageDialog(null, "Table created (or already exists).");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error creating table: " + e.getMessage());
        }
    }

    public void insertRecord() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Enter user id:"));
            String name = JOptionPane.showInputDialog("Enter name:");
            String email = JOptionPane.showInputDialog("Enter email:");

            PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES(?, ?, ?)");
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setString(3, email);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Record inserted!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error inserting record: " + e.getMessage());
        }
    }

    public void deleteRecord() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Enter user id to delete:"));

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id=?");
            stmt.setInt(1, id);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Record deleted!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting record: " + e.getMessage());
        }
    }

    public void showRecords() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * FROM users");
            StringBuilder sb = new StringBuilder();

            while (result.next()) {
                sb.append(result.getInt("id")).append(" ")
                  .append(result.getString("name")).append(" ")
                  .append(result.getString("email")).append("\n");
            }

            JOptionPane.showMessageDialog(null, sb.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error showing records: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        DatabaseInterfaceGUI gui = new DatabaseInterfaceGUI();
    }
}
