import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;

public class EmployeeManagementSystem extends JFrame {
    private final ArrayList<Employee> empList = new ArrayList<>();
    private final DefaultTableModel tableModel;
    private final JTable employeeTable;
    private final JTextField idField;
    private final JTextField nameField;
    private final JComboBox<String> roleCombo;
    private final JTextField departmentField;
    private final JTextField emailField;
    private final JTextField phoneField;
    private final JTextField joiningField;
    private final JTextField salaryField;
    private final JTextField bonusField;
    private final JTextField taxField;
    private final JTextField incrementField;
    private final JTextField searchField;
    private final JComboBox<String> searchTypeCombo;
    private final JComboBox<String> sortCombo;
    private final JLabel statusLabel;
    private final JLabel summaryLabel;
    private final JLabel netSalaryLabel;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final String DATA_FILE = "employees_data.txt";
    private static int nextSerialId = 1;

    public EmployeeManagementSystem() {
        super("Employee Management System");
        setModernTheme();

        // Add menu bar with logout
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        tableModel = new DefaultTableModel(new Object[] { "ID", "Name", "Role", "Department", "Email",
                "Phone", "Start Date", "Salary" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setRowHeight(26);

        idField = new JTextField(15);
        idField.setEditable(false);
        idField.setBackground(new Color(238, 238, 238));
        nameField = new JTextField(15);
        roleCombo = new JComboBox<>(new String[] { "Employee", "Manager", "HR", "Developer", "Intern" });
        departmentField = new JTextField(15);
        emailField = new JTextField(15);
        phoneField = new JTextField(15);
        joiningField = new JTextField(15);
        salaryField = new JTextField(15);
        bonusField = new JTextField("10", 8);
        taxField = new JTextField("12", 8);
        incrementField = new JTextField("5", 8);
        searchField = new JTextField(18);
        searchTypeCombo = new JComboBox<>(new String[] { "Search by ID", "Search by Name", "Search by Department" });
        sortCombo = new JComboBox<>(
                new String[] { "Sort by ID", "Sort by Name", "Sort by Salary", "Sort by Department", "Sort by Role" });
        statusLabel = new JLabel("Ready.");
        summaryLabel = new JLabel("Employees: 0 | Avg Salary: 0.00");
        netSalaryLabel = new JLabel("Net Salary: 0.00");

        initLayout();
        addListeners();
        loadEmployeeData();
        refreshTable();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    static void setModernTheme() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {

        }
    }

    private void setNextIdField() {
        int nextId = Math.max(nextSerialId, empList.stream().mapToInt(Employee::getId).max().orElse(0) + 1);
        nextSerialId = nextId;
        idField.setText(String.valueOf(nextId));
    }

    public static int peekNextEmployeeId() {
        return nextSerialId;
    }

    public static int getNextEmployeeSerial() {
        return nextSerialId++;
    }

    private void loadEmployeeData() {
        empList.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Employee employee = parseEmployeeLine(line);
                if (employee != null) {
                    empList.add(employee);
                    nextSerialId = Math.max(nextSerialId, employee.getId() + 1);
                }
            }
        } catch (IOException ignored) {
            // File may not exist yet; that's fine.
        }
    }

    private void saveEmployeeData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Employee employee : empList) {
                writer.println(formatEmployeeLine(employee));
            }
        } catch (IOException ex) {
            setStatus("Unable to save employee data: " + ex.getMessage());
        }
    }

    static void appendEmployeeToDataFile(Employee employee) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE, true))) {
            writer.println(formatEmployeeLine(employee));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Unable to save employee data: " + ex.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String formatEmployeeLine(Employee employee) {
        return String.join("|",
                String.valueOf(employee.getId()),
                employee.getName(),
                employee.getRole(),
                employee.getDepartment(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getJoiningDate().format(DATE_FORMAT),
                String.format("%.2f", employee.getSalary()),
                String.format("%.2f", employee.getBonusPercent()),
                String.format("%.2f", employee.getTaxPercent()));
    }

    private static Employee parseEmployeeLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 10) {
            return null;
        }
        try {
            int id = Integer.parseInt(parts[0]);
            String name = parts[1];
            String role = parts[2];
            String department = parts[3];
            String email = parts[4];
            String phone = parts[5];
            LocalDate joiningDate = LocalDate.parse(parts[6], DATE_FORMAT);
            double salary = Double.parseDouble(parts[7]);
            double bonus = Double.parseDouble(parts[8]);
            double tax = Double.parseDouble(parts[9]);
            return new Employee(id, name, role, department, email, phone, joiningDate, salary, bonus, tax);
        } catch (Exception ex) {
            return null;
        }
    }

    private JButton createButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        return button;
    }

    private void initLayout() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setPreferredSize(new Dimension(360, 0));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        formPanel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        formPanel.add(roleCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        formPanel.add(departmentField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Joining Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(joiningField, gbc);
        gbc.gridx = 2;
        formPanel.add(new JLabel("YYYY-MM-DD"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Salary:"), gbc);
        gbc.gridx = 1;
        formPanel.add(salaryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(new JLabel("Bonus %:"), gbc);
        gbc.gridx = 1;
        formPanel.add(bonusField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        formPanel.add(new JLabel("Tax %:"), gbc);
        gbc.gridx = 1;
        formPanel.add(taxField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        formPanel.add(new JLabel("Increment %:"), gbc);
        gbc.gridx = 1;
        formPanel.add(incrementField, gbc);

        JButton addButton = createButton("➕ Add", new Color(36, 123, 160));
        JButton updateButton = createButton("✏️ Update", new Color(68, 138, 255));
        JButton deleteButton = createButton("🗑️ Delete", new Color(220, 70, 70));
        JButton clearButton = createButton("🧹 Clear All", new Color(120, 130, 150));
        JButton exportButton = createButton("📄 Export", new Color(80, 100, 180));
        JButton calculateButton = createButton("🧮 Calculate Salary", new Color(16, 124, 49));
        JButton incrementButton = createButton("⬆️ Increment", new Color(255, 159, 67));
        JButton logoutButton = createButton("🚪 Logout", new Color(120, 100, 120));

        JPanel actionPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        actionPanel.setOpaque(false);
        actionPanel.add(addButton);
        actionPanel.add(updateButton);
        actionPanel.add(deleteButton);
        actionPanel.add(clearButton);
        actionPanel.add(calculateButton);
        actionPanel.add(incrementButton);
        actionPanel.add(exportButton);
        actionPanel.add(logoutButton);

        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(actionPanel, gbc);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search / Sort"));
        searchPanel.setBackground(new Color(245, 247, 250));
        JButton searchButton = createButton("🔍 Search", new Color(44, 120, 117));
        JButton showAllButton = createButton("📚 Show All", new Color(96, 125, 139));
        JButton sortButton = createButton("📊 Sort", new Color(60, 150, 160));
        searchPanel.add(searchTypeCombo);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);
        searchPanel.add(sortCombo);
        searchPanel.add(sortButton);

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setPreferredSize(new Dimension(380, 0));
        leftPanel.setBackground(new Color(245, 247, 250));
        leftPanel.add(formPanel, BorderLayout.CENTER);
        leftPanel.add(searchPanel, BorderLayout.SOUTH);

        employeeTable.setFillsViewportHeight(true);
        JScrollPane tableScrollPane = new JScrollPane(employeeTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Employees"));
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        tableScrollPane.setPreferredSize(new Dimension(620, 0));

        JPanel statusPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        statusPanel.setBackground(new Color(225, 230, 235));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(225, 230, 235));
        netSalaryLabel.setOpaque(true);
        netSalaryLabel.setBackground(new Color(225, 230, 235));
        summaryLabel.setOpaque(true);
        summaryLabel.setBackground(new Color(225, 230, 235));
        statusPanel.add(summaryLabel);
        statusPanel.add(netSalaryLabel);
        statusPanel.add(statusLabel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, tableScrollPane);
        splitPane.setDividerLocation(380);
        splitPane.setResizeWeight(0.0);
        splitPane.setContinuousLayout(true);
        splitPane.setOneTouchExpandable(true);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(10, 10));
        contentPane.add(splitPane, BorderLayout.CENTER);
        contentPane.add(statusPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addEmployee());
        updateButton.addActionListener(e -> updateSelectedEmployee());
        deleteButton.addActionListener(e -> deleteSelectedEmployee());
        clearButton.addActionListener(e -> clearInputs());
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        searchButton.addActionListener(e -> searchEmployees());
        showAllButton.addActionListener(e -> refreshTable());
        sortButton.addActionListener(e -> sortEmployees());
        exportButton.addActionListener(e -> exportData());
        calculateButton.addActionListener(e -> calculateSalary());
        incrementButton.addActionListener(e -> applySalaryIncrement());
    }

    private void addListeners() {
        employeeTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && employeeTable.getSelectedRow() >= 0) {
                    int selectedRow = employeeTable.getSelectedRow();
                    idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    roleCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 2).toString());
                    departmentField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    emailField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                    phoneField.setText(tableModel.getValueAt(selectedRow, 5).toString());
                    joiningField.setText(tableModel.getValueAt(selectedRow, 6).toString());
                    salaryField.setText(tableModel.getValueAt(selectedRow, 7).toString());
                    bonusField.setText("10");
                    taxField.setText("12");
                }
            }
        });
    }

    private void addEmployee() {
        String idText = idField.getText().trim();
        String name = nameField.getText().trim();
        String role = roleCombo.getSelectedItem().toString();
        String department = departmentField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String joiningDateText = joiningField.getText().trim();
        String salaryText = salaryField.getText().trim();
        String bonusText = bonusField.getText().trim();
        String taxText = taxField.getText().trim();

        if (idText.isEmpty() || name.isEmpty() || department.isEmpty() || email.isEmpty() || phone.isEmpty()
                || joiningDateText.isEmpty() || salaryText.isEmpty()) {
            setStatus("Please complete all required fields.");
            return;
        }

        int id;
        double salary;
        double bonus;
        double tax;
        LocalDate joiningDate;
        try {
            id = Integer.parseInt(idText);
            salary = Double.parseDouble(salaryText);
            bonus = bonusText.isEmpty() ? 0 : Double.parseDouble(bonusText);
            tax = taxText.isEmpty() ? 0 : Double.parseDouble(taxText);
            joiningDate = LocalDate.parse(joiningDateText, DATE_FORMAT);
        } catch (NumberFormatException ex) {
            setStatus("ID, salary, bonus and tax must be numeric.");
            return;
        } catch (DateTimeParseException ex) {
            setStatus("Joining date must be YYYY-MM-DD.");
            return;
        }

        if (salary < 0 || bonus < 0 || tax < 0) {
            setStatus("Salary, bonus and tax percentages cannot be negative.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            setStatus("Enter a valid email address.");
            return;
        }

        if (!phone.matches("\\+?\\d{7,15}")) {
            setStatus("Phone must contain 7-15 digits.");
            return;
        }

        if (findEmployeeById(id) != null) {
            setStatus("An employee with ID " + id + " already exists.");
            return;
        }

        Employee employee = new Employee(id, name, role, department, email, phone, joiningDate, salary, bonus, tax);
        empList.add(employee);
        nextSerialId = Math.max(nextSerialId, id + 1);
        saveEmployeeData();
        refreshTable();
        clearInputs();
        JOptionPane.showMessageDialog(this, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        setStatus("Employee added successfully.");
    }

    private void updateSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow < 0) {
            setStatus("Select an employee row to update.");
            return;
        }

        String idText = idField.getText().trim();
        String name = nameField.getText().trim();
        String role = roleCombo.getSelectedItem().toString();
        String department = departmentField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String joiningDateText = joiningField.getText().trim();
        String salaryText = salaryField.getText().trim();
        String bonusText = bonusField.getText().trim();
        String taxText = taxField.getText().trim();

        if (idText.isEmpty() || name.isEmpty() || department.isEmpty() || email.isEmpty() || phone.isEmpty()
                || joiningDateText.isEmpty() || salaryText.isEmpty()) {
            setStatus("Please complete all required fields.");
            return;
        }

        int id;
        double salary;
        double bonus;
        double tax;
        LocalDate joiningDate;
        try {
            id = Integer.parseInt(idText);
            salary = Double.parseDouble(salaryText);
            bonus = bonusText.isEmpty() ? 0 : Double.parseDouble(bonusText);
            tax = taxText.isEmpty() ? 0 : Double.parseDouble(taxText);
            joiningDate = LocalDate.parse(joiningDateText, DATE_FORMAT);
        } catch (NumberFormatException ex) {
            setStatus("ID, salary, bonus and tax must be numeric.");
            return;
        } catch (DateTimeParseException ex) {
            setStatus("Joining date must be YYYY-MM-DD.");
            return;
        }

        if (salary < 0 || bonus < 0 || tax < 0) {
            setStatus("Salary, bonus and tax percentages cannot be negative.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            setStatus("Enter a valid email address.");
            return;
        }

        if (!phone.matches("\\+?\\d{7,15}")) {
            setStatus("Phone must contain 7-15 digits.");
            return;
        }

        int currentId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        Employee selectedEmployee = findEmployeeById(currentId);
        if (selectedEmployee == null) {
            setStatus("Selected employee was not found.");
            return;
        }

        if (currentId != id && findEmployeeById(id) != null) {
            setStatus("Another employee is already using ID " + id + ".");
            return;
        }

        selectedEmployee.setId(id);
        selectedEmployee.setName(name);
        selectedEmployee.setRole(role);
        selectedEmployee.setDepartment(department);
        selectedEmployee.setEmail(email);
        selectedEmployee.setPhone(phone);
        selectedEmployee.setJoiningDate(joiningDate);
        selectedEmployee.setSalary(salary);
        selectedEmployee.setBonusPercent(bonus);
        selectedEmployee.setTaxPercent(tax);
        saveEmployeeData();
        refreshTable();
        JOptionPane.showMessageDialog(this, "Employee updated successfully!", "Updated",
                JOptionPane.INFORMATION_MESSAGE);
        setStatus("Employee updated successfully.");
    }

    private void deleteSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow < 0) {
            setStatus("Select an employee row to delete.");
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the selected employee?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result != JOptionPane.YES_OPTION) {
            setStatus("Delete cancelled.");
            return;
        }

        int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        Employee employee = findEmployeeById(id);
        if (employee != null) {
            empList.remove(employee);
            saveEmployeeData();
            refreshTable();
            clearInputs();
            setStatus("Employee deleted.");
        } else {
            setStatus("Employee not found for deletion.");
        }
    }

    private void calculateSalary() {
        String salaryText = salaryField.getText().trim();
        String bonusText = bonusField.getText().trim();
        String taxText = taxField.getText().trim();

        if (salaryText.isEmpty()) {
            setStatus("Enter a salary to calculate.");
            return;
        }

        try {
            double salary = Double.parseDouble(salaryText);
            double bonus = bonusText.isEmpty() ? 0 : Double.parseDouble(bonusText);
            double tax = taxText.isEmpty() ? 0 : Double.parseDouble(taxText);
            if (salary < 0 || bonus < 0 || tax < 0) {
                setStatus("Salary, bonus and tax percentages cannot be negative.");
                return;
            }
            double bonusAmount = salary * bonus / 100.0;
            double taxAmount = salary * tax / 100.0;
            double netPay = salary + bonusAmount - taxAmount;
            netSalaryLabel.setText(String.format("Net Salary: %.2f", netPay));
            JOptionPane.showMessageDialog(this,
                    String.format("Base salary: %.2f\nBonus: %.2f%% (%.2f)\nTax: %.2f%% (%.2f)\nNet salary: %.2f",
                            salary, bonus, bonusAmount, tax, taxAmount, netPay),
                    "Salary Breakdown", JOptionPane.INFORMATION_MESSAGE);
            setStatus("Salary calculated.");
        } catch (NumberFormatException ex) {
            setStatus("Salary, bonus and tax must be numeric.");
        }
    }

    private void applySalaryIncrement() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow < 0) {
            setStatus("Select an employee to increment salary.");
            return;
        }

        String incrementText = incrementField.getText().trim();
        if (incrementText.isEmpty()) {
            setStatus("Enter an increment percentage.");
            return;
        }

        try {
            double increment = Double.parseDouble(incrementText);
            if (increment < 0) {
                setStatus("Increment percentage cannot be negative.");
                return;
            }
            int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            Employee employee = findEmployeeById(id);
            if (employee == null) {
                setStatus("Selected employee was not found.");
                return;
            }
            double newSalary = employee.getSalary() * (1 + increment / 100.0);
            employee.setSalary(newSalary);
            salaryField.setText(String.format("%.2f", newSalary));
            refreshTable();
            JOptionPane.showMessageDialog(this,
                    String.format("%s's salary increased by %.2f%% to %.2f.", employee.getName(), increment, newSalary),
                    "Salary Incremented", JOptionPane.INFORMATION_MESSAGE);
            setStatus("Salary increment applied.");
        } catch (NumberFormatException ex) {
            setStatus("Increment must be numeric.");
        }
    }

    private void searchEmployees() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            setStatus("Enter text to search.");
            return;
        }

        String searchType = (String) searchTypeCombo.getSelectedItem();
        tableModel.setRowCount(0);
        int found = 0;
        for (Employee employee : empList) {
            boolean matches = false;
            if ("Search by ID".equals(searchType)) {
                try {
                    matches = employee.getId() == Integer.parseInt(query);
                } catch (NumberFormatException ignored) {
                }
            } else if ("Search by Name".equals(searchType)) {
                matches = employee.getName().toLowerCase().contains(query.toLowerCase());
            } else if ("Search by Department".equals(searchType)) {
                matches = employee.getDepartment().toLowerCase().contains(query.toLowerCase());
            }
            if (matches) {
                addRow(employee);
                found++;
            }
        }
        setStatus(found + " record(s) found.");
    }

    private void sortEmployees() {
        String sortType = (String) sortCombo.getSelectedItem();
        if ("Sort by Name".equals(sortType)) {
            empList.sort(Comparator.comparing(Employee::getName, String.CASE_INSENSITIVE_ORDER));
        } else if ("Sort by Salary".equals(sortType)) {
            empList.sort(Comparator.comparingDouble(Employee::getSalary));
        } else if ("Sort by Department".equals(sortType)) {
            empList.sort(Comparator.comparing(Employee::getDepartment, String.CASE_INSENSITIVE_ORDER));
        } else if ("Sort by Role".equals(sortType)) {
            empList.sort(Comparator.comparing(Employee::getRole, String.CASE_INSENSITIVE_ORDER));
        } else {
            empList.sort(Comparator.comparingInt(Employee::getId));
        }
        refreshTable();
        setStatus(sortType + " applied.");
    }

    private void exportData() {
        if (empList.isEmpty()) {
            setStatus("No employee data available to export.");
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("employees_report.txt"))) {
            writer.println("Employee Report");
            writer.println("==============================");
            for (Employee employee : empList) {
                writer.printf("ID: %d\n", employee.getId());
                writer.printf("Name: %s\n", employee.getName());
                writer.printf("Role: %s\n", employee.getRole());
                writer.printf("Department: %s\n", employee.getDepartment());
                writer.printf("Email: %s\n", employee.getEmail());
                writer.printf("Phone: %s\n", employee.getPhone());
                writer.printf("Joining Date: %s\n", employee.getJoiningDate().format(DATE_FORMAT));
                writer.printf("Salary: %s\n", formatSalary(employee.getSalary()));
                writer.printf("Bonus: %.2f%%\n", employee.getBonusPercent());
                writer.printf("Tax: %.2f%%\n", employee.getTaxPercent());
                writer.printf("Net Salary: %s\n", formatSalary(employee.calculateNetSalary()));
                writer.println("------------------------------");
            }
            writer.printf("Total employees: %d\n", empList.size());
            writer.printf("Average salary: %s\n", formatSalary(getAverageSalary()));
            setStatus("Exported data to employees_report.txt.");
        } catch (IOException ex) {
            setStatus("Failed to export data: " + ex.getMessage());
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Employee employee : empList) {
            addRow(employee);
        }
        updateSummary();
        setNextIdField();
    }

    private void addRow(Employee employee) {
        tableModel.addRow(new Object[] {
                employee.getId(),
                employee.getName(),
                employee.getRole(),
                employee.getDepartment(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getJoiningDate().format(DATE_FORMAT),
                formatSalary(employee.getSalary())
        });
    }

    private void clearInputs() {
        nameField.setText("");
        roleCombo.setSelectedIndex(0);
        departmentField.setText("");
        emailField.setText("");
        phoneField.setText("");
        joiningField.setText("");
        salaryField.setText("");
        bonusField.setText("10");
        taxField.setText("12");
        incrementField.setText("5");
        searchField.setText("");
        employeeTable.clearSelection();
        netSalaryLabel.setText("Net Salary: 0.00");
        setNextIdField();
        setStatus("Inputs cleared.");
    }

    private Employee findEmployeeById(int id) {
        for (Employee employee : empList) {
            if (employee.getId() == id) {
                return employee;
            }
        }
        return null;
    }

    private void updateSummary() {
        int count = empList.size();
        double average = getAverageSalary();
        summaryLabel.setText(String.format("Employees: %d | Avg Salary: %s", count, formatSalary(average)));
    }

    private double getAverageSalary() {
        if (empList.isEmpty()) {
            return 0.0;
        }
        return empList.stream().mapToDouble(Employee::getSalary).average().orElse(0.0);
    }

    private String formatSalary(double salary) {
        return String.format("%.2f", salary);
    }

    private void setStatus(String message) {
        statusLabel.setText(message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}

class LoginFrame extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JComboBox<String> userTypeCombo;

    public LoginFrame() {
        super("Employee System Login");
        EmployeeManagementSystem.setModernTheme();

        usernameField = new JTextField(16);
        passwordField = new JPasswordField(16);
        userTypeCombo = new JComboBox<>(new String[] { "Admin", "User" });

        initLayout();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initLayout() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Login as:"), gbc);
        gbc.gridx = 1;
        panel.add(userTypeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        JButton exitButton = new JButton("Exit");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        loginButton.addActionListener(e -> authenticate());
        exitButton.addActionListener(e -> System.exit(0));

        getContentPane().add(panel);
    }

    private void authenticate() {
        String userType = userTypeCombo.getSelectedItem().toString();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.", "Login Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("Admin".equals(userType)) {
            if ("admin".equals(username) && "admin123".equals(password)) {
                new EmployeeManagementSystem();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Admin login failed. Use admin/admin123.", "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if ("user".equals(username) && "user123".equals(password)) {
                new UserFormFrame();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "User login failed. Use user/user123.", "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

class UserFormFrame extends JFrame {
    private final JTextField idField;
    private final JTextField nameField;
    private final JComboBox<String> roleCombo;
    private final JTextField departmentField;
    private final JTextField emailField;
    private final JTextField phoneField;
    private final JTextField joiningField;
    private final JTextField salaryField;

    public UserFormFrame() {
        super("Employee User Form");
        EmployeeManagementSystem.setModernTheme();

        // Add menu bar with logout
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        idField = new JTextField(12);
        idField.setEditable(false);
        idField.setBackground(new Color(238, 238, 238));
        nameField = new JTextField(15);
        roleCombo = new JComboBox<>(new String[] { "Employee", "Manager", "HR", "Developer", "Intern" });
        departmentField = new JTextField(15);
        emailField = new JTextField(15);
        phoneField = new JTextField(15);
        joiningField = new JTextField(15);
        salaryField = new JTextField(15);

        initLayout();
        setNextIdField();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initLayout() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Employee ID:"), gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Designation:"), gbc);
        gbc.gridx = 1;
        panel.add(roleCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        panel.add(departmentField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Joining Date:"), gbc);
        gbc.gridx = 1;
        panel.add(joiningField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Salary:"), gbc);
        gbc.gridx = 1;
        panel.add(salaryField, gbc);

        JButton submitButton = new JButton("Submit");
        JButton clearButton = new JButton("Clear");
        JButton logoutButton = new JButton("Logout");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.add(logoutButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(submitButton);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        submitButton.addActionListener(e -> submitForm());
        clearButton.addActionListener(e -> clearForm());
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        getContentPane().add(panel);
    }

    private void setNextIdField() {
        idField.setText(String.valueOf(EmployeeManagementSystem.peekNextEmployeeId()));
    }

    private void submitForm() {
        String name = nameField.getText().trim();
        String role = roleCombo.getSelectedItem().toString();
        String department = departmentField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String joiningDateText = joiningField.getText().trim();
        String salaryText = salaryField.getText().trim();

        if (name.isEmpty() || department.isEmpty() || email.isEmpty() || phone.isEmpty()
                || joiningDateText.isEmpty() || salaryText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please complete all required fields.", "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Enter a valid email address. It must contain '@' and a valid domain.",
                    "Invalid Email", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!isValidPhone(phone)) {
            JOptionPane.showMessageDialog(this,
                    "Phone must contain 7-15 digits, with an optional leading '+'.",
                    "Invalid Phone", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double salary;
        try {
            salary = Double.parseDouble(salaryText);
            if (salary < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Salary must be a positive number.", "Invalid Salary",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate joiningDate;
        try {
            joiningDate = LocalDate.parse(joiningDateText, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Joining date must be YYYY-MM-DD.", "Invalid Date",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = EmployeeManagementSystem.getNextEmployeeSerial();
        Employee employee = new Employee(id, name, role, department, email, phone, joiningDate, salary, 0, 0);
        EmployeeManagementSystem.appendEmployeeToDataFile(employee);
        appendToFile(employee);
        JOptionPane.showMessageDialog(this,
                "Your information was submitted successfully. Assigned employee ID: " + id,
                "Submitted", JOptionPane.INFORMATION_MESSAGE);
        clearForm();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\+?\\d{7,15}");
    }

    private void appendToFile(Employee employee) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("employees_report.txt", true))) {
            writer.println("ID: " + employee.getId());
            writer.println("Name: " + employee.getName());
            writer.println("Role: " + employee.getRole());
            writer.println("Department: " + employee.getDepartment());
            writer.println("Email: " + employee.getEmail());
            writer.println("Phone: " + employee.getPhone());
            writer.println("Joining Date: " + employee.getJoiningDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
            writer.println("Salary: " + String.format("%.2f", employee.getSalary()));
            writer.println("------------------------------");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Unable to save submission: " + ex.getMessage(), "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        nameField.setText("");
        roleCombo.setSelectedIndex(0);
        departmentField.setText("");
        emailField.setText("");
        phoneField.setText("");
        joiningField.setText("");
        salaryField.setText("");
        setNextIdField();
    }
}
