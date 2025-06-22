import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

// Main class to start the Ride Sharing Data Processing System.
public class Main {
    public static void main(String[] args) {
        // Create a logger for logging events and errors.
        Logger logger = Logger.getLogger("RideSharingSystem");
        // Create a shared task queue.
        TaskQueue<String> queue = new TaskQueue<>();
        // Create a shared list to store results.
        List<String> results = new ArrayList<>();
        // Number of worker threads to use.
        int numWorkers = 4;
        // Create a thread pool with a fixed number of workers.
        ExecutorService executor = Executors.newFixedThreadPool(numWorkers);

        // Add tasks to the queue.
        for (int i = 1; i <= 10; i++) {
            queue.addTask("Task-" + i);
        }
        // Add special END signals to tell each worker when to stop.
        for (int i = 0; i < numWorkers; i++) {
            queue.addTask("END");
        }

        // Start the worker threads by submitting them to the executor.
        for (int i = 0; i < numWorkers; i++) {
            executor.submit(new Worker(i, queue, results, logger));
        }
        // Shutdown the executor so it won't accept new tasks.
        executor.shutdown();
        // Wait for all worker threads to finish.
        while (!executor.isTerminated()) {
            // Busy-wait until all workers are done.
        }
        // Print out all the results after processing is complete.
        for (String result : results) {
            System.out.println(result);
        }
    }
}
