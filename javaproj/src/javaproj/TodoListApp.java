package javaproj;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class TodoListApp {
    private JFrame frame;
    private JTextField taskField;
    private DefaultListModel<String> taskListModel;
    private JList<String> taskList;
    private Connection connection;

    public TodoListApp() {
        initialize();
        connectToDatabase();
        loadTasks();
    }

    private void initialize() {
        frame = new JFrame("To-Do List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        taskField = new JTextField();
        frame.add(taskField, BorderLayout.NORTH);

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        frame.add(new JScrollPane(taskList), BorderLayout.CENTER);

        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(e -> addTask());
        JButton updateButton = new JButton("Update Task");
        updateButton.addActionListener(e -> updateTask());
        JButton deleteButton = new JButton("Delete Task");
        deleteButton.addActionListener(e -> deleteTask());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/todo_db";
            String user = "root"; // replace with your MySQL username
            String password = "kingston"; // replace with your MySQL password
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTasks() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT task FROM tasks");
            while (rs.next()) {
                taskListModel.addElement(rs.getString("task"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addTask() {
        String task = taskField.getText();
        if (!task.isEmpty()) {
            try {
                PreparedStatement pstmt = connection.prepareStatement("INSERT INTO tasks (task) VALUES (?)");
                pstmt.setString(1, task);
                pstmt.executeUpdate();
                taskListModel.addElement(task);
                taskField.setText("");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            String newTask = taskField.getText();
            if (!newTask.isEmpty()) {
                String oldTask = taskListModel.get(selectedIndex);
                try {
                    PreparedStatement pstmt = connection.prepareStatement("UPDATE tasks SET task = ? WHERE task = ?");
                    pstmt.setString(1, newTask);
                    pstmt.setString(2, oldTask);
                    pstmt.executeUpdate();
                    taskListModel.set(selectedIndex, newTask);
                    taskField.setText("");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            String task = taskListModel.get(selectedIndex);
            try {
                PreparedStatement pstmt = connection.prepareStatement("DELETE FROM tasks WHERE task = ?");
                pstmt.setString(1, task);
                pstmt.executeUpdate();
                taskListModel.remove(selectedIndex);
                taskField.setText("");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TodoListApp::new);
    }
}
