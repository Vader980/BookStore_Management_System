import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BookstoreSystem extends JFrame {
    private JTable customerTable;
    private DefaultTableModel customerTableModel;
    private JTextField searchField;

    public BookstoreSystem() {
        setTitle("Bookstore System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Maintain Customers", createMaintainCustomersPanel());
        tabbedPane.addTab("Maintain Employees", createMaintainEmployeesPanel());
        tabbedPane.addTab("Manage Inventory", createManageInventoryPanel());
        tabbedPane.addTab("POS", createPOSPanel());

        add(tabbedPane);

        setVisible(true);
    }

    private JPanel createMaintainCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create the table and table model
        customerTable = new JTable();
        customerTableModel = new DefaultTableModel();
        customerTableModel.addColumn("ID");
        customerTableModel.addColumn("Name");
        customerTableModel.addColumn("Last Name");
        customerTableModel.addColumn("ID Number");
        customerTableModel.addColumn("Age");
        customerTableModel.addColumn("Cell Number");
        customerTableModel.addColumn("Email");
        customerTable.setModel(customerTableModel);

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(customerTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load customer data into the table
        loadCustomerData();

        // Adjust the text field size
        Dimension textFieldSize = new Dimension(200, 25);

        // Create the input panel for search and action
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);

        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        searchField.setPreferredSize(textFieldSize);

        JLabel actionLabel = new JLabel("Action:");
        JComboBox<String> actionComboBox = new JComboBox<>(new String[]{"Update", "Insert", "Delete"});
        actionComboBox.setPreferredSize(textFieldSize);

        JButton searchButton = new JButton("Search");

        constraints.gridx = 0;
        constraints.gridy = 0;
        inputPanel.add(searchLabel, constraints);

        constraints.gridx = 1;
        inputPanel.add(searchField, constraints);

        constraints.gridx = 2;
        inputPanel.add(searchButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        inputPanel.add(actionLabel, constraints);

        constraints.gridx = 1;
        inputPanel.add(actionComboBox, constraints);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Adjust the input panel size
        inputPanel.setPreferredSize(new Dimension(inputPanel.getWidth(), 70));

        // Adjust the button size
        searchButton.setPreferredSize(new Dimension(80, 25));

        // Add action listeners for the buttons
        searchButton.addActionListener(e -> searchCustomer());
        actionComboBox.addActionListener(e -> {
            String selectedAction = (String) actionComboBox.getSelectedItem();
            if (selectedAction != null) {
                switch (selectedAction) {
                    case "Insert":
                        insertCustomer();
                        break;
                    case "Update":
                        updateCustomer();
                        break;
                    case "Delete":
                        deleteCustomer();
                        break;
                }
            }
        });

        return panel;
    }

    private void loadCustomerData() {
        final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
        final String USERNAME = "your_username";
        final String PASSWORD = "your_password";

        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM CUSTOMER")) 
             {

            // Clear existing data
            customerTableModel.setRowCount(0);

            // Iterate through the result set and add data to the table model
            while (resultSet.next()) {
                Object[] rowData = {
                        resultSet.getInt("CUSTOMER_ID"),
                        resultSet.getString("CUSTOMER_NAME"),
                        resultSet.getString("CUSTOMER_LNAME"),
                        resultSet.getInt("CUSTOMER_IDNUM"),
                        resultSet.getInt("CUSTOMER_AGE"),
                        resultSet.getString("CUSTOMER_CELLNUMBER"),
                        resultSet.getString("CUSTOMER_EMAIL")
                };
                customerTableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchCustomer() {
        String searchValue = searchField.getText().trim();

        if (searchValue.isEmpty()) {
            loadCustomerData();
            return;
        }

        final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
        final String USERNAME = "your_username";
        final String PASSWORD = "your_password";

        String query = "SELECT * FROM CUSTOMER WHERE CUSTOMER_NAME LIKE '%' || ? || '%' OR CUSTOMER_LNAME LIKE '%' || ? || '%' OR CUSTOMER_CELLNUMBER LIKE '%' || ? || '%'";

        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, "%" + searchValue + "%");
            statement.setString(2, "%" + searchValue + "%");
            statement.setString(3, "%" + searchValue + "%");

            ResultSet resultSet = statement.executeQuery();

            // Clear existing data
            customerTableModel.setRowCount(0);

            // Iterate through the result set and add data to the table model
            while (resultSet.next()) {
                Object[] rowData = {
                        resultSet.getInt("CUSTOMER_ID"),
                        resultSet.getString("CUSTOMER_NAME"),
                        resultSet.getString("CUSTOMER_LNAME"),
                        resultSet.getInt("CUSTOMER_IDNUM"),
                        resultSet.getInt("CUSTOMER_AGE"),
                        resultSet.getString("CUSTOMER_CELLNUMBER"),
                        resultSet.getString("CUSTOMER_EMAIL")
                };
                customerTableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertCustomer() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField lnameField = new JTextField();
        JTextField IDNUMField = new JTextField();
        JTextField ageField = new JTextField();
        JTextField cellNumberField = new JTextField();
        JTextField emailField = new JTextField();

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lnameField);
        panel.add(new JLabel("ID Number:"));
        panel.add(IDNUMField);
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Cell Number:"));
        panel.add(cellNumberField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Insert Customer", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String lname = lnameField.getText();
            String IDNUM = IDNUMField.getText();
            String age = ageField.getText();
            String cellNumber = cellNumberField.getText();
            String email = emailField.getText();

            final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
            final String USERNAME = "your_username";
            final String PASSWORD = "your_password";

            String query = "INSERT INTO CUSTOMER (CUSTOMER_NAME, CUSTOMER_LNAME, CUSTOMER_IDNUM, CUSTOMER_AGE, CUSTOMER_CELLNUMBER, CUSTOMER_EMAIL) VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, name);
                statement.setString(2, lname);
                statement.setString(3, IDNUM);
                statement.setString(4, age);
                statement.setString(5, cellNumber);
                statement.setString(6, email);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Customer inserted successfully.");
                }

                // Refresh the customer table
                loadCustomerData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to update.");
            return;
        }

        String id = customerTable.getValueAt(selectedRow, 0).toString();

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));

        JTextField nameField = new JTextField(customerTable.getValueAt(selectedRow, 1).toString());
        JTextField lnameField = new JTextField(customerTable.getValueAt(selectedRow, 2).toString());
        JTextField IDNUMField = new JTextField(customerTable.getValueAt(selectedRow, 3).toString());
        JTextField ageField = new JTextField(customerTable.getValueAt(selectedRow, 4).toString());
        JTextField cellNumberField = new JTextField(customerTable.getValueAt(selectedRow, 5).toString());
        JTextField emailField = new JTextField(customerTable.getValueAt(selectedRow, 6).toString());

        panel.add(new JLabel("ID:"));
        panel.add(new JLabel(id));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lnameField);
        panel.add(new JLabel("ID Number:"));
        panel.add(IDNUMField);
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Cell Number:"));
        panel.add(cellNumberField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Customer", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String lname = lnameField.getText();
            String IDNUM = IDNUMField.getText();
            String age = ageField.getText();
            String cellNumber = cellNumberField.getText();
            String email = emailField.getText();

            final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
            final String USERNAME = "your_username";
            final String PASSWORD = "your_password";

            String query = "UPDATE CUSTOMER SET CUSTOMER_NAME = ?, CUSTOMER_LNAME = ?, CUSTOMER_IDNUMBER = ?, CUSTOMER_AGE = ?, CUSTOMER_CELLNUMBER = ?, CUSTOMER_EMAIL = ? WHERE CUSTOMER_ID = ?";

            try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, name);
                statement.setString(2, lname);
                statement.setString(3, IDNUM);
                statement.setString(4, age);
                statement.setString(5, cellNumber);
                statement.setString(6, email);
                statement.setString(7, id);

                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Customer updated successfully.");
                }

                // Refresh the customer table
                loadCustomerData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.");
            return;
        }

        String id = customerTable.getValueAt(selectedRow, 0).toString();
        String name = customerTable.getValueAt(selectedRow, 1).toString();

        int confirmResult = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the customer:\nID: " + id + "\nName: " + name, "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirmResult == JOptionPane.YES_OPTION) {
            final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
            final String USERNAME = "your_username";
            final String PASSWORD = "your_password";

            String query = "DELETE FROM CUSTOMER WHERE CUSTOMER_ID = ?";

            try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, id);

                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Customer deleted successfully.");
                }

                // Refresh the customer table
                loadCustomerData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private JPanel createMaintainEmployeesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create the table and table model
        JTable employeeTable = new JTable();
        DefaultTableModel employeeTableModel = new DefaultTableModel();
        employeeTableModel.addColumn("ID");
        employeeTableModel.addColumn("First Name");
        employeeTableModel.addColumn("Last Name");
        employeeTableModel.addColumn("Position");
        employeeTable.setModel(employeeTableModel);

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load employee data into the table
        loadEmployeeData(employeeTableModel);

        // Adjust the text field size
        Dimension textFieldSize = new Dimension(200, 25);

        // Create the input panel for search and action
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);

        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(textFieldSize);

        JLabel actionLabel = new JLabel("Action:");
        JComboBox<String> actionComboBox = new JComboBox<>(new String[]{"Update", "Insert", "Delete"});
        actionComboBox.setPreferredSize(textFieldSize);

        JButton searchButton = new JButton("Search");

        constraints.gridx = 0;
        constraints.gridy = 0;
        inputPanel.add(searchLabel, constraints);

        constraints.gridx = 1;
        inputPanel.add(searchField, constraints);

        constraints.gridx = 2;
        inputPanel.add(searchButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        inputPanel.add(actionLabel, constraints);

        constraints.gridx = 1;
        inputPanel.add(actionComboBox, constraints);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Adjust the input panel size
        inputPanel.setPreferredSize(new Dimension(inputPanel.getWidth(), 70));

        // Adjust the button size
        searchButton.setPreferredSize(new Dimension(80, 25));

        // Add action listeners for the buttons
        searchButton.addActionListener(e -> searchEmployee(searchField.getText().trim(), employeeTableModel));
        actionComboBox.addActionListener(e -> {
            String selectedAction = (String) actionComboBox.getSelectedItem();
            if (selectedAction != null) {
                switch (selectedAction) {
                    case "Insert":
                        insertEmployee(employeeTableModel);
                        break;
                    case "Update":
                        updateEmployee(employeeTable, employeeTableModel);
                        break;
                    case "Delete":
                        deleteEmployee(employeeTable, employeeTableModel);
                        break;
                }
            }
        });

        return panel;
    }

    private void loadEmployeeData(DefaultTableModel tableModel) {
        final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
        final String USERNAME = "your_username";
        final String PASSWORD = "your_password";

        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM EMPLOYEE")) {

            // Clear existing data
            tableModel.setRowCount(0);

            // Iterate through the result set and add data to the table model
            while (resultSet.next()) {
                Object[] rowData = {
                        resultSet.getInt("EMPLOYEE_ID"),
                        resultSet.getString("EMPLOYEE_FIRST_NAME"),
                        resultSet.getString("EMPLOYEE_LAST_NAME"),
                        resultSet.getString("EMPLOYEE_POSITION")
                };
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchEmployee(String searchValue, DefaultTableModel tableModel) {
        if (searchValue.isEmpty()) {
            loadEmployeeData(tableModel);
            return;
        }

        final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
        final String USERNAME = "your_username";
        final String PASSWORD = "your_password";

        String query = "SELECT * FROM EMPLOYEE WHERE EMPLOYEE_FIRST_NAME LIKE ? OR EMPLOYEE_LAST_NAME LIKE ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, "%" + searchValue + "%");
            statement.setString(2, "%" + searchValue + "%");

            ResultSet resultSet = statement.executeQuery();

            // Clear existing data
            tableModel.setRowCount(0);

            // Iterate through the result set and add data to the table model
            while (resultSet.next()) {
                Object[] rowData = {
                        resultSet.getInt("EMPLOYEE_ID"),
                        resultSet.getString("EMPLOYEE_FIRST_NAME"),
                        resultSet.getString("EMPLOYEE_LAST_NAME"),
                        resultSet.getString("EMPLOYEE_POSITION")
                };
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertEmployee(DefaultTableModel tableModel) {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField positionField = new JTextField();

        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Position:"));
        panel.add(positionField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Insert Employee", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String position = positionField.getText();

            final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
            final String USERNAME = "your_username";
            final String PASSWORD = "your_password";

            String query = "INSERT INTO EMPLOYEE (EMPLOYEE_FIRST_NAME, EMPLOYEE_LAST_NAME, EMPLOYEE_POSITION) VALUES (?, ?, ?)";

            try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, firstName);
                statement.setString(2, lastName);
                statement.setString(3, position);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Employee inserted successfully.");
                }

                // Refresh the employee table
                loadEmployeeData(tableModel);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateEmployee(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to update.");
            return;
        }

        int employeeId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JTextField firstNameField = new JTextField(table.getValueAt(selectedRow, 1).toString());
        JTextField lastNameField = new JTextField(table.getValueAt(selectedRow, 2).toString());
        JTextField positionField = new JTextField(table.getValueAt(selectedRow, 3).toString());

        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("Position:"));
        panel.add(positionField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Employee", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String position = positionField.getText();

            final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
            final String USERNAME = "your_username";
            final String PASSWORD = "your_password";

            String query = "UPDATE EMPLOYEE SET EMPLOYEE_FIRST_NAME=?, EMPLOYEE_LAST_NAME=?, EMPLOYEE_POSITION=? WHERE EMPLOYEE_ID=?";

            try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, firstName);
                statement.setString(2, lastName);
                statement.setString(3, position);
                statement.setInt(4, employeeId);

                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Employee updated successfully.");
                }

                // Refresh the employee table
                loadEmployeeData(tableModel);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteEmployee(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.");
            return;
        }

        int employeeId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
        String firstName = table.getValueAt(selectedRow, 1).toString();
        String lastName = table.getValueAt(selectedRow, 2).toString();

        int confirmResult = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the employee:\nID: " + employeeId + "\nName: " + firstName + " " + lastName, "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirmResult == JOptionPane.YES_OPTION) {
            final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
            final String USERNAME = "your_username";
            final String PASSWORD = "your_password";

            String query = "DELETE FROM EMPLOYEE WHERE EMPLOYEE_ID=?";

            try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setInt(1, employeeId);

                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Employee deleted successfully.");
                }

                // Refresh the employee table
                loadEmployeeData(tableModel);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private JPanel createManageInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
    
        // Create the table and table model
        JTable inventoryTable = new JTable();
        DefaultTableModel inventoryTableModel = new DefaultTableModel();
        inventoryTableModel.addColumn("Book ID");
        inventoryTableModel.addColumn("Title");
        inventoryTableModel.addColumn("Author");
        inventoryTableModel.addColumn("Price");
        inventoryTable.setModel(inventoryTableModel);
    
        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        panel.add(scrollPane, BorderLayout.CENTER);
    
        // Load inventory data into the table
        loadInventoryData(inventoryTableModel);
    
        // Adjust the text field size
        Dimension textFieldSize = new Dimension(200, 25);
    
        // Create the input panel for search and action
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
    
        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(textFieldSize);
    
        JLabel actionLabel = new JLabel("Action:");
        JComboBox<String> actionComboBox = new JComboBox<>(new String[]{"Update", "Insert", "Delete"});
        actionComboBox.setPreferredSize(textFieldSize);
    
        JButton searchButton = new JButton("Search");
    
        constraints.gridx = 0;
        constraints.gridy = 0;
        inputPanel.add(searchLabel, constraints);
    
        constraints.gridx = 1;
        inputPanel.add(searchField, constraints);
    
        constraints.gridx = 2;
        inputPanel.add(searchButton, constraints);
    
        constraints.gridx = 0;
        constraints.gridy = 1;
        inputPanel.add(actionLabel, constraints);
    
        constraints.gridx = 1;
        inputPanel.add(actionComboBox, constraints);
    
        panel.add(inputPanel, BorderLayout.NORTH);
    
        // Adjust the input panel size
        inputPanel.setPreferredSize(new Dimension(inputPanel.getWidth(), 70));
    
        // Adjust the button size
        searchButton.setPreferredSize(new Dimension(80, 25));
    
        // Add action listeners for the buttons
        searchButton.addActionListener(e -> searchInventory(searchField.getText().trim(), inventoryTableModel));
        actionComboBox.addActionListener(e -> {
            String selectedAction = (String) actionComboBox.getSelectedItem();
            if (selectedAction != null) {
                switch (selectedAction) {
                    case "Insert":
                        insertInventoryItem(inventoryTableModel);
                        break;
                    case "Update":
                        updateInventoryItem(inventoryTable, inventoryTableModel);
                        break;
                    case "Delete":
                        deleteInventoryItem(inventoryTable, inventoryTableModel);
                        break;
                }
            }
        });
    
        return panel;
    }
    
    private void loadInventoryData(DefaultTableModel tableModel) {
        final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
        final String USERNAME = "your_username";
        final String PASSWORD = "your_password";
    
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM BOOK")) {
    
            // Clear existing data
            tableModel.setRowCount(0);
    
            // Iterate through the result set and add data to the table model
            while (resultSet.next()) {
                Object[] rowData = {
                        resultSet.getInt("BOOK_ID"),
                        resultSet.getString("TITLE"),
                        resultSet.getString("AUTHOR"),
                        resultSet.getDouble("PRICE"),
                        resultSet.getInt("QUANTITY")
                };
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    private void searchInventory(String searchValue, DefaultTableModel tableModel) {
        if (searchValue.isEmpty()) {
            loadInventoryData(tableModel);
            return;
        }
    
        final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
        final String USERNAME = "your_username";
        final String PASSWORD = "your_password";
    
        String query = "SELECT M.BOOK_ID, B.TITLE, B.AUTHOR, M.PRICE " + "FROM MANAGE_INVENTORY M " + "JOIN BOOK B ON M.BOOK_ID = B.BOOK_ID " + "WHERE B.TITLE LIKE ? OR B.AUTHOR LIKE ?";
    
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            statement.setString(1, "%" + searchValue + "%");
            statement.setString(2, "%" + searchValue + "%");
    
            ResultSet resultSet = statement.executeQuery();
    
            // Clear existing data
            tableModel.setRowCount(0);
    
            // Iterate through the result set and add data to the table model
            while (resultSet.next()) {
                Object[] rowData = {
                        resultSet.getInt("BOOK_ID"),
                        resultSet.getString("TITLE"),
                        resultSet.getString("AUTHOR"),
                        resultSet.getDouble("PRICE")
                };
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void insertInventoryItem(DefaultTableModel tableModel) {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
    
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField quantityField = new JTextField();
    
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
    
        int result = JOptionPane.showConfirmDialog(this, panel, "Insert Inventory Item", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText();
            String author = authorField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
    
            final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
            final String USERNAME = "your_username";
            final String PASSWORD = "your_password";
    
            String query = "INSERT INTO BOOK (TITLE, AUTHOR) VALUES (?, ?)";
    
            try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(query)) {
    
                statement.setString(1, title);
                statement.setString(2, author);
                statement.setDouble(3, price);
                statement.setInt(4, quantity);
    
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Inventory item inserted successfully.");
                }
    
                // Refresh the inventory table
                loadInventoryData(tableModel);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void updateInventoryItem(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an inventory item to update.");
            return;
        }
    
        int bookId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
    
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
    
        JTextField titleField = new JTextField(table.getValueAt(selectedRow, 1).toString());
        JTextField authorField = new JTextField(table.getValueAt(selectedRow, 2).toString());
        JTextField priceField = new JTextField(table.getValueAt(selectedRow, 3).toString());
        JTextField quantityField = new JTextField(table.getValueAt(selectedRow, 4).toString());
    
        panel.add(new JLabel("Book ID:"));
        panel.add(new JLabel(Integer.toString(bookId)));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
    
        int result = JOptionPane.showConfirmDialog(this, panel, "Update Inventory Item", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText();
            String author = authorField.getText();
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
    
            final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
            final String USERNAME = "your_username";
            final String PASSWORD = "your_password";
    
            String query = "UPDATE MANAGE_INVENTORY SET MANAGE_STOCKQTY = ?, BOOK_ID = ? WHERE BOOK_ID = ?";
    
            try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(query)) {
    
                statement.setString(1, title);
                statement.setString(2, author);
                statement.setDouble(3, price);
                statement.setInt(4, quantity);
                statement.setInt(5, bookId);
    
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Inventory item updated successfully.");
                }
    
                // Refresh the inventory table
                loadInventoryData(tableModel);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }    
    
    private void deleteInventoryItem(JTable table, DefaultTableModel tableModel) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an inventory item to delete.");
            return;
        }
    
        int bookId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
    
        int confirmResult = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the inventory item with Book ID: " + bookId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirmResult == JOptionPane.YES_OPTION) {
            final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
            final String USERNAME = "your_username";
            final String PASSWORD = "your_password";
    
            try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD)) {
                // Delete the inventory item from the MANAGE_INVENTORY table
                String deleteQuery = "DELETE FROM MANAGE_INVENTORY WHERE BOOK_ID = ?";
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                deleteStatement.setInt(1, bookId);
                deleteStatement.executeUpdate();
    
                // Delete the associated book from the BOOK table (if not used elsewhere)
                String deleteBookQuery = "DELETE FROM BOOK WHERE BOOK_ID = ? AND NOT EXISTS (SELECT 1 FROM MANAGE_INVENTORY WHERE BOOK_ID = ?)";
                PreparedStatement deleteBookStatement = connection.prepareStatement(deleteBookQuery);
                deleteBookStatement.setInt(1, bookId);
                deleteBookStatement.setInt(2, bookId);
                int rowsDeleted = deleteBookStatement.executeUpdate();
    
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Inventory item and associated book deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Inventory item deleted successfully.");
                }
    
                // Refresh the inventory table
                loadInventoryData(tableModel);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    

    private JPanel createPOSPanel() {
        JPanel panel = new JPanel();
    
        JButton posButton = new JButton("Start POS");
        posButton.addActionListener(e -> {
            String[] options = {"Sale", "Rental"};
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Select transaction type:",
                    "POS",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );
    
            if (choice == 0) {
                recordSale();
            } else if (choice == 1) {
                recordRental();
            }
        });
    
        panel.add(posButton);
        return panel;
    }
    
    private void recordSale() {
        String isbn = JOptionPane.showInputDialog(this, "Enter Book ISBN:");
        String customerName = JOptionPane.showInputDialog(this, "Enter Customer Name:");
        double amount = Double.parseDouble(JOptionPane.showInputDialog(this, "Enter Sale Amount:"));
        int quantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Quantity:"));
        java.util.Date currentDate = new java.util.Date();
        boolean deliveryOption = JOptionPane.showConfirmDialog(this, "Do you want delivery?", "Delivery Option", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        String deliveryLocation = "";
    
        if (deliveryOption) {
            deliveryLocation = JOptionPane.showInputDialog(this, "Enter Delivery Location:");
        }
    
        final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
        final String USERNAME = "your_username";
        final String PASSWORD = "your_password";
    
        String transactionQuery = "INSERT INTO TRANSACTION (TRANSACTION_ID, TRANSACTION_DATE, TRANSACTION_AMOUNT, TRANSACTION_QTY, CUSTOMER_ID, BOOK_ID, RENTAL_PERIOD) " +
                "VALUES (TRANSACTION_SEQ.NEXTVAL, ?, ?, ?, (SELECT CUSTOMER_ID FROM CUSTOMER WHERE CUSTOMER_NAME = ?), " +
                "(SELECT BOOK_ID FROM BOOK WHERE ISBN = ?), NULL)";
    
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement transactionStatement = connection.prepareStatement(transactionQuery, Statement.RETURN_GENERATED_KEYS)) {
    
            transactionStatement.setDate(1, new java.sql.Date(currentDate.getTime()));
            transactionStatement.setDouble(2, amount);
            transactionStatement.setInt(3, quantity);
            transactionStatement.setString(4, customerName);
            transactionStatement.setString(5, isbn);
    
            int rowsInserted = transactionStatement.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = transactionStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int transactionId = generatedKeys.getInt(1);
    
                    if (deliveryOption) {
                        String deliveryQuery = "INSERT INTO DELIVERY (DELIVERY_ID, DELIVERY_STATUS, TRANSACTION_ID, LOCATION_ID, EMP_ID) " +
                                "VALUES (DELIVERY_SEQ.NEXTVAL, 'Pending', ?, (SELECT LOCATION_ID FROM LOCATION WHERE LOCATION_NAME = ?), NULL)";
    
                        try (PreparedStatement deliveryStatement = connection.prepareStatement(deliveryQuery)) {
                            deliveryStatement.setInt(1, transactionId);
                            deliveryStatement.setString(2, deliveryLocation);
    
                            deliveryStatement.executeUpdate();
                            displayTransactionDetails(transactionId);
                        }
                    }
    
                    JOptionPane.showMessageDialog(this, "Sale recorded successfully!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void recordRental() {
        String isbn = JOptionPane.showInputDialog(this, "Enter Book ISBN:");
        String customerName = JOptionPane.showInputDialog(this, "Enter Customer Name:");
        double amount = Double.parseDouble(JOptionPane.showInputDialog(this, "Enter Rental Amount:"));
        int quantity = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Quantity:"));
        int rentalPeriod = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Rental Period (in days):"));
        java.util.Date currentDate = new java.util.Date();
        boolean deliveryOption = JOptionPane.showConfirmDialog(this, "Do you want delivery?", "Delivery Option", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        String deliveryLocation = "";
    
        if (deliveryOption) {
            deliveryLocation = JOptionPane.showInputDialog(this, "Enter Delivery Location:");
        }
    
        final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
        final String USERNAME = "your_username";
        final String PASSWORD = "your_password";
    
        String transactionQuery = "INSERT INTO TRANSACTION (TRANSACTION_ID, TRANSACTION_DATE, TRANSACTION_AMOUNT, TRANSACTION_QTY, CUSTOMER_ID, BOOK_ID, RENTAL_PERIOD) " +
                "VALUES (TRANSACTION_SEQ.NEXTVAL, ?, ?, ?, (SELECT CUSTOMER_ID FROM CUSTOMER WHERE CUSTOMER_NAME = ?), " +
                "(SELECT BOOK_ID FROM BOOK WHERE ISBN = ?), ?)";
    
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement transactionStatement = connection.prepareStatement(transactionQuery, Statement.RETURN_GENERATED_KEYS)) {
    
            transactionStatement.setDate(1, new java.sql.Date(currentDate.getTime()));
            transactionStatement.setDouble(2, amount);
            transactionStatement.setInt(3, quantity);
            transactionStatement.setString(4, customerName);
            transactionStatement.setString(5, isbn);
            transactionStatement.setInt(6, rentalPeriod);
    
            int rowsInserted = transactionStatement.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = transactionStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    if (deliveryOption) {
                        int transactionId = generatedKeys.getInt(1);
    
                        String deliveryQuery = "INSERT INTO DELIVERY (DELIVERY_ID, DELIVERY_STATUS, TRANSACTION_ID, LOCATION_ID, EMP_ID) " +
                                "VALUES (DELIVERY_SEQ.NEXTVAL, 'Pending', ?, (SELECT LOCATION_ID FROM LOCATION WHERE LOCATION_NAME = ?), NULL)";
    
                        try (PreparedStatement deliveryStatement = connection.prepareStatement(deliveryQuery)) {
                            deliveryStatement.setInt(1, transactionId);
                            deliveryStatement.setString(2, deliveryLocation);
    
                            deliveryStatement.executeUpdate();
                            displayTransactionDetails(transactionId);

                        }
                    }
    
                    JOptionPane.showMessageDialog(this, "Rental recorded successfully!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayTransactionDetails(int transactionId) {
        final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
        final String USERNAME = "your_username";
        final String PASSWORD = "your_password";
    
        String query = "SELECT * FROM TRANSACTION WHERE TRANSACTION_ID = ?";
    
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            statement.setInt(1, transactionId);
    
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int transactionIdResult = resultSet.getInt("TRANSACTION_ID");
                Date transactionDate = resultSet.getDate("TRANSACTION_DATE");
                double transactionAmount = resultSet.getDouble("TRANSACTION_AMOUNT");
                int transactionQuantity = resultSet.getInt("TRANSACTION_QTY");
                int customerId = resultSet.getInt("CUSTOMER_ID");
                int bookId = resultSet.getInt("BOOK_ID");
                int rentalPeriod = resultSet.getInt("RENTAL_PERIOD");
    
                String customerName = getCustomerName(customerId);
                String bookTitle = getBookTitle(bookId);
    
                String transactionDetails = "Transaction ID: " + transactionIdResult + "\n"
                        + "Transaction Date: " + transactionDate + "\n"
                        + "Customer: " + customerName + "\n"
                        + "Book: " + bookTitle + "\n"
                        + "Amount: $" + transactionAmount + "\n"
                        + "Quantity: " + transactionQuantity;
    
                if (rentalPeriod > 0) {
                    transactionDetails += "\nRental Period: " + rentalPeriod + " days";
                }
    
                JOptionPane.showMessageDialog(this, transactionDetails, "Transaction Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Transaction not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private String getCustomerName(int customerId) {
        final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
        final String USERNAME = "your_username";
        final String PASSWORD = "your_password";
    
        String query = "SELECT CUSTOMER_NAME FROM CUSTOMER WHERE CUSTOMER_ID = ?";
    
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            statement.setInt(1, customerId);
    
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("CUSTOMER_NAME");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return "";
    }
    
    private String getBookTitle(int bookId) {
        final String DB_URL = "jdbc:oracle:thin:@localhost:1521:XE";
        final String USERNAME = "your_username";
        final String PASSWORD = "your_password";
    
        String query = "SELECT TITLE FROM BOOK WHERE BOOK_ID = ?";
    
        try (Connection connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            statement.setInt(1, bookId);
    
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("TITLE");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return "";
    }
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(BookstoreSystem::new);
    }
}
