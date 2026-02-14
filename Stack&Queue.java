class StackQueue {

    // ---------- STACK ----------
    static int top = -1;
    static int stackSize = 5;
    static int[] stack = new int[stackSize];

    static void push(int value) {
        if (top == stackSize - 1) {
            System.out.println("Stack Overflow");
        } else {
            stack[++top] = value;
            System.out.println(value + " pushed into stack");
        }
    }

    static void pop() {
        if (top == -1) {
            System.out.println("Stack Underflow");
        } else {
            System.out.println(stack[top--] + " popped from stack");
        }
    }

    // ---------- QUEUE ----------
    static int front = 0, rear = -1;
    static int queueSize = 5;
    static int[] queue = new int[queueSize];

    static void enqueue(int value) {
        if (rear == queueSize - 1) {
            System.out.println("Queue Overflow");
        } else {
            queue[++rear] = value;
            System.out.println(value + " inserted into queue");
        }
    }

    static void dequeue() {
        if (front > rear) {
            System.out.println("Queue Underflow");
        } else {
            System.out.println(queue[front++] + " removed from queue");
        }
    }

    public static void main(String[] args) {

        System.out.println("---- Stack Operations ----");
        push(10);
        push(20);
        pop();

        System.out.println("\n---- Queue Operations ----");
        enqueue(30);
        enqueue(40);
        dequeue();
    }
}
