import java.time.LocalDate;

public class Employee {
    private int id;
    private String name;
    private String role;
    private String department;
    private String email;
    private String phone;
    private LocalDate joiningDate;
    private double salary;
    private double bonusPercent;
    private double taxPercent;

    public Employee(int id, String name, String role, String department, String email, String phone,
            LocalDate joiningDate, double salary, double bonusPercent, double taxPercent) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.department = department;
        this.email = email;
        this.phone = phone;
        this.joiningDate = joiningDate;
        this.salary = salary;
        this.bonusPercent = bonusPercent;
        this.taxPercent = taxPercent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public double getBonusPercent() {
        return bonusPercent;
    }

    public void setBonusPercent(double bonusPercent) {
        this.bonusPercent = bonusPercent;
    }

    public double getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(double taxPercent) {
        this.taxPercent = taxPercent;
    }

    public double calculateNetSalary() {
        double bonusAmount = salary * bonusPercent / 100.0;
        double taxAmount = salary * taxPercent / 100.0;
        return salary + bonusAmount - taxAmount;
    }

    @Override
    public String toString() {
        return id + " - " + name + " (" + role + ")";
    }
}
