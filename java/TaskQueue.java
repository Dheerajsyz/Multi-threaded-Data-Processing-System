import java.util.LinkedList;
import java.util.Queue;

// This class represents a thread-safe queue for tasks.
// It uses Java's built-in synchronization to ensure only one thread can access the queue at a time.
public class TaskQueue<T> {
    // The actual queue that holds the tasks.
    private final Queue<T> queue = new LinkedList<>();

    // Adds a task to the queue in a thread-safe way.
    // Notifies waiting threads that a new task is available.
    public synchronized void addTask(T task) {
        queue.add(task);
        notifyAll(); // Wake up any threads waiting for a task.
    }

    // Retrieves and removes a task from the queue in a thread-safe way.
    // If the queue is empty, waits until a task is available.
    public synchronized T getTask() throws InterruptedException {
        while (queue.isEmpty()) {
            wait(); // Wait for a task to be added.
        }
        return queue.poll(); // Remove and return the next task.
    }
}
