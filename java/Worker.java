import java.util.List;
import java.util.logging.Logger;
import java.io.FileWriter;
import java.io.IOException;

// This class represents a worker that processes tasks from the shared queue.
// Each worker runs in its own thread and writes results to a shared list.
public class Worker implements Runnable {
    // Reference to the shared task queue.
    private final TaskQueue<String> queue;
    // Reference to the shared results list.
    private final List<String> results;
    // Logger for logging events and errors.
    private final Logger logger;
    // Unique ID for this worker.
    private final int id;

    // Constructor to initialize the worker with its ID, queue, results list, and logger.
    public Worker(int id, TaskQueue<String> queue, List<String> results, Logger logger) {
        this.id = id;
        this.queue = queue;
        this.results = results;
        this.logger = logger;
    }

    // The main logic for the worker thread.
    @Override
    public void run() {
        logger.info("Worker " + id + " started"); // Log when the worker starts.
        try {
            while (true) {
                // Get a task from the queue. This will wait if the queue is empty.
                String task = queue.getTask();
                // If the task is the special END signal, break the loop and finish.
                if (task.equals("END")) break;
                // Simulate processing the task by sleeping for 100ms.
                Thread.sleep(100);
                // Simulate an error: try to write to an invalid file path
                try (FileWriter fw = new FileWriter("/invalid_path/output.txt", true)) {
                    fw.write("Worker " + id + " processed: " + task + "\n");
                } catch (IOException ioEx) {
                    logger.severe("Worker " + id + " file write error: " + ioEx.getMessage());
                }
                // Add the result to the shared results list in a thread-safe way.
                synchronized (results) {
                    results.add("Worker " + id + " processed: " + task);
                }
            }
        } catch (InterruptedException e) {
            // Log if the worker is interrupted while waiting or sleeping.
            logger.severe("Worker " + id + " interrupted: " + e.getMessage());
        } catch (Exception e) {
            // Log any other errors that occur during processing.
            logger.severe("Worker " + id + " error: " + e.getMessage());
        }
        logger.info("Worker " + id + " completed"); // Log when the worker finishes.
    }
}
