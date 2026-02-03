import java.util.Scanner;

class Area {

    int length;
    int breadth;

    // Method to set dimensions
    void setDim(int l, int b) {
        length = l;
        breadth = b;
    }

    // Method to calculate area
    int getArea() {
        return length * breadth;
    }
}

public class AreaTest {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Area rect = new Area();

        // Input from keyboard
        System.out.print("Enter length: ");
        int l = sc.nextInt();

        System.out.print("Enter breadth: ");
        int b = sc.nextInt();

        // Set dimensions
        rect.setDim(l, b);

        // Display area
        System.out.println("Area of Rectangle = " + rect.getArea());
    }
}
