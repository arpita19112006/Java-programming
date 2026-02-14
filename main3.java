// Base class
class Student {
    int roll_no;

    void getRoll(int r) {
        roll_no = r;
    }

    void displayRoll() {
        System.out.println("Roll No: " + roll_no);
    }
}

// Derived class
class Test extends Student {
    int sub1, sub2;

    void getMarks(int m1, int m2) {
        sub1 = m1;
        sub2 = m2;
    }

    void displayMarks() {
        System.out.println("Marks in Subject 1: " + sub1);
        System.out.println("Marks in Subject 2: " + sub2);
    }
}

// Final derived class
class Result extends Test {
    void displayResult() {
        int total = sub1 + sub2;
        System.out.println("Total Marks: " + total);
    }
}

// Main class
public class main3 {
    public static void main(String[] args) {

        Result obj = new Result();

        obj.getRoll(32);
        obj.getMarks(85, 90);

        obj.displayRoll();
        obj.displayMarks();
        obj.displayResult();
    }
}
