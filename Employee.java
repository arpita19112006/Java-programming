public class Employee {
    private String firstName;
    private String lastName;
    private double monthlySalary;

    // Constructor
    Employee(String fName, String lName, double salary) {
        firstName = fName;
        lastName = lName;

        if (salary > 0)
            monthlySalary = salary;
        else
            monthlySalary = 0.0;
    }
    String getFirstName() {
        return firstName;
    }

    void setFirstName(String fName) {
        firstName = fName;
    }

    String getLastName() {
        return lastName;
    }

    void setLastName(String lName) {
        lastName = lName;
    }

    double getMonthlySalary() {
        return monthlySalary;
    }

    void setMonthlySalary(double salary) {
        if (salary > 0)
            monthlySalary = salary;
        else
            monthlySalary = 0.0;
    }

    double yearlySalary() {
        return monthlySalary * 12;
    }


public class EmployeeTest {
    public static void main(String[] args) {

        // Create two Employee objects
        Employee e1 = new Employee("Rahul", "Sharma", 20000);
        Employee e2 = new Employee("Anita", "Patil", 30000);

        // Display yearly salary before raise
        System.out.println("Yearly Salary Before Raise:");
        System.out.println(e1.getFirstName() + " " + e1.getLastName() + " : " + e1.yearlySalary());
        System.out.println(e2.getFirstName() + " " + e2.getLastName() + " : " + e2.yearlySalary());

        // Give 10% raise
        e1.setMonthlySalary(e1.getMonthlySalary() * 1.10);
        e2.setMonthlySalary(e2.getMonthlySalary() * 1.10);

        // Display yearly salary after raise
        System.out.println("\nYearly Salary After 10% Raise:");
        System.out.println(e1.getFirstName() + " " + e1.getLastName() + " : " + e1.yearlySalary());
        System.out.println(e2.getFirstName() + " " + e2.getLastName() + " : " + e2.yearlySalary());
    }
}
}