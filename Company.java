// Base class
class Employee {
    String name;
    double salary;

    Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    double calculateBonus() {
        return salary * 0.10;   // default 10%
    }

    void report() {
        System.out.println(name + " Salary: " + salary);
    }

    void manageProject() {
        System.out.println(name + " is working on a project.");
    }
}

// Manager class
class Manager extends Employee {
    Manager(String name, double salary) {
        super(name, salary);
    }

    double calculateBonus() {
        return salary * 0.20;   // 20% bonus
    }
}

// Developer class
class Developer extends Employee {
    Developer(String name, double salary) {
        super(name, salary);
    }

    double calculateBonus() {
        return salary * 0.15;   // 15% bonus
    }
}

// Programmer class
class Programmer extends Employee {
    Programmer(String name, double salary) {
        super(name, salary);
    }

    double calculateBonus() {
        return salary * 0.12;   // 12% bonus
    }
}

// Main class
public class Company {
    public static void main(String[] args) {

        Manager m = new Manager("Arpita", 80000);
        Developer d = new Developer("Rahul", 60000);
        Programmer p = new Programmer("Sneha", 50000);

        System.out.println("Manager Bonus: " + m.calculateBonus());
        System.out.println("Developer Bonus: " + d.calculateBonus());
        System.out.println("Programmer Bonus: " + p.calculateBonus());
    }
}

