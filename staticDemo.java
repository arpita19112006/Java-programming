class StaticDemo {
    static int count;
    static {
        count = 10;
        System.out.println("Static block executed");
    }

    // Static method
    static void show() {
        System.out.println("Static variable value: " + count);
    }

    public static void main(String[] args) {

        System.out.println("Main method started");

        // Calling static method
        show();
    }
}
