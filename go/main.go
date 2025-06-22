package main

import (
	"fmt"
	"log"
	"os"
	"sync"
	"time"
)

type Task struct {
	Name string
}

type Result struct {
	WorkerID int
	TaskName string
}

func worker(id int, tasks <-chan Task, results chan<- Result, wg *sync.WaitGroup, logger *log.Logger) {
	defer wg.Done()
	logger.Printf("Worker %d started", id)
	for task := range tasks {
		if task.Name == "END" {
			break
		}
		time.Sleep(100 * time.Millisecond) // Simulate processing
		// Simulate an error: try to write to an invalid file path
		f, err := os.OpenFile("/invalid_path/output.txt", os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
		if err != nil {
			logger.Printf("Worker %d file write error: %v", id, err)
		} else {
			_, err = f.WriteString(fmt.Sprintf("Worker %d processed: %s\n", id, task.Name))
			if err != nil {
				logger.Printf("Worker %d file write error: %v", id, err)
			}
			f.Close()
		}
		results <- Result{WorkerID: id, TaskName: task.Name}
	}
	logger.Printf("Worker %d completed", id)
}

func main() {
	logger := log.New(os.Stdout, "", log.LstdFlags)
	tasks := make(chan Task, 10)
	results := make(chan Result, 10)
	var wg sync.WaitGroup
	numWorkers := 4

	// Start workers
	for i := 0; i < numWorkers; i++ {
		wg.Add(1)
		go worker(i, tasks, results, &wg, logger)
	}

	// Add tasks
	for i := 1; i <= 10; i++ {
		tasks <- Task{Name: fmt.Sprintf("Task-%d", i)}
	}
	// Add END signals
	for i := 0; i < numWorkers; i++ {
		tasks <- Task{Name: "END"}
	}
	close(tasks)

	// Wait for workers
	wg.Wait()
	close(results)

	// Print results
	for result := range results {
		fmt.Printf("Worker %d processed: %s\n", result.WorkerID, result.TaskName)
	}
}
