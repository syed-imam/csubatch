package csubatch.cli;

import csubatch.job.Job;
import csubatch.queue.JobQueue;
import csubatch.queue.SchedulingPolicy;

import java.util.List;

public class CommandParser {
    private final JobQueue jobQueue;

    public CommandParser(JobQueue jobQueue) {
        this.jobQueue = jobQueue;
    }

    public boolean handleCommand(String input) {
        String[] parts = input.trim().split("\\s+");
        String command = parts[0].toLowerCase();

        switch (command) {
            case "help":
                handleHelp(parts);
                return true;

            case "run":
                handleRun(parts);
                return true;

            case "queue":
                handleQueue();
                return true;

            case "list":
                handleList();
                return true;

            case "setpolicy":
                handleSetPolicy(parts);
                return true;

            case "stats":
                handleStats();
                return true;

            case "benchmark":
                handleBenchmark();
                return true;

            case "quit":
                return false;

            default:
                System.out.println("Unknown command: " + command);
                System.out.println("Type 'help' to see available commands.");
                return true;
        }
    }

    private void handleHelp(String[] parts) {
        if (parts.length == 2 && parts[1].equalsIgnoreCase("-test")) {
            System.out.println("Test command format:");
            System.out.println("test <benchmark> <policy> <num_of_jobs> <priority_levels> <min_CPU_time> <max_CPU_time>");
            return;
        }

        System.out.println("Available commands:");
        System.out.println("  help                              Show this help message");
        System.out.println("  run <job> <time> <pri>            Submit a job (time>0, pri>=0)");
        System.out.println("  setpolicy <fcfs|sjf|priority>     Set scheduling policy");
        System.out.println("  queue                             Show pending jobs sorted by policy");
        System.out.println("  list                              Show all jobs in queue");
        System.out.println("  stats                             Show scheduling metrics");
        System.out.println("  benchmark                         Load benchmark workload");
        System.out.println("  quit                              Exit CSUbatch");
    }

    private void handleRun(String[] parts) {
        if (parts.length != 4) {
            System.out.println("Usage: run <job> <time> <pri>");
            return;
        }

        String jobName = parts[1];
        String timeArg = parts[2];
        String priorityArg = parts[3];

        int cpuTime;
        int priority;

        try {
            cpuTime = Integer.parseInt(timeArg);
        } catch (NumberFormatException e) {
            System.out.println("Error: <time> must be a valid integer.");
            System.out.println("Usage: run <job> <time> <pri>");
            return;
        }

        try {
            priority = Integer.parseInt(priorityArg);
        } catch (NumberFormatException e) {
            System.out.println("Error: <pri> must be a valid integer.");
            System.out.println("Usage: run <job> <time> <pri>");
            return;
        }

        if (cpuTime <= 0) {
            System.out.println("Error: <time> must be greater than 0.");
            return;
        }

        if (priority < 0) {
            System.out.println("Error: <pri> must be 0 or greater.");
            return;
        }

        Job job = new Job(jobName, cpuTime, priority);
        jobQueue.enqueue(job);

        System.out.println("Job submitted successfully:");
        System.out.printf("  Name: %s | CPU_Time: %d | Pri: %d | Arrival_time: %d | Status: %s%n",
                job.getName(),
                job.getCpuTime(),
                job.getPriority(),
                job.getArrivalTime(),
                job.getStatus());
    }

    private void handleSetPolicy(String[] parts) {
        if (parts.length != 2) {
            System.out.println("Usage: setpolicy <fcfs|sjf|priority>");
            return;
        }

        String policyArg = parts[1].toUpperCase();
        SchedulingPolicy newPolicy;

        switch (policyArg) {
            case "FCFS":
                newPolicy = SchedulingPolicy.FCFS;
                break;
            case "SJF":
                newPolicy = SchedulingPolicy.SJF;
                break;
            case "PRIORITY":
                newPolicy = SchedulingPolicy.PRIORITY;
                break;
            default:
                System.out.println("Error: Unknown policy '" + parts[1] + "'.");
                System.out.println("Valid policies: fcfs, sjf, priority");
                return;
        }

        jobQueue.setPolicy(newPolicy);
        System.out.println("Scheduling policy changed to " + newPolicy.name() + ".");
    }

    private void handleQueue() {
        SchedulingPolicy currentPolicy = jobQueue.getPolicy();
        List<Job> waiting = jobQueue.getWaitingJobsSorted();

        System.out.println("Scheduling Policy: " + currentPolicy.name());
        System.out.println("Pending jobs: " + waiting.size());

        if (waiting.isEmpty()) {
            System.out.println("No pending jobs.");
            return;
        }

        System.out.printf("%-15s %-10s %-10s %-15s %-10s%n",
                "Name", "CPU_Time", "Pri", "Arrival_Order", "Status");
        System.out.println("---------------------------------------------------------------");

        for (Job job : waiting) {
            System.out.printf("%-15s %-10d %-10d %-15d %-10s%n",
                    job.getName(),
                    job.getCpuTime(),
                    job.getPriority(),
                    job.getArrivalTime(),
                    job.getStatus());
        }
    }

    private void handleList() {
        List<Job> jobs = jobQueue.getAllJobs();

        System.out.println("Scheduling Policy: " + jobQueue.getPolicy().name());
        System.out.println("Total jobs in queue: " + jobs.size());

        if (jobs.isEmpty()) {
            System.out.println("No jobs in queue.");
            return;
        }

        System.out.printf("%-15s %-10s %-10s %-15s %-10s%n",
                "Name", "CPU_Time", "Pri", "Arrival_Order", "Status");
        System.out.println("---------------------------------------------------------------");

        for (Job job : jobs) {
            System.out.printf("%-15s %-10d %-10d %-15d %-10s%n",
                    job.getName(),
                    job.getCpuTime(),
                    job.getPriority(),
                    job.getArrivalTime(),
                    job.getStatus());
        }
    }

    private void handleStats() {
        List<Job> allJobs = jobQueue.getAllJobs();
        List<Job> completed = jobQueue.getCompletedJobs();

        int totalSubmitted = allJobs.size();
        int totalCompleted = completed.size();

        System.out.println("Scheduling Metrics");
        System.out.println("------------------");
        System.out.println("Scheduling Policy:     " + jobQueue.getPolicy().name());
        System.out.println("Total jobs submitted:   " + totalSubmitted);
        System.out.println("Total jobs completed:   " + totalCompleted);

        if (totalCompleted == 0) {
            System.out.println("Average waiting time:   N/A");
            System.out.println("Average turnaround time: N/A");
            System.out.println("Throughput:             N/A");
            return;
        }

        double totalWaitTime = 0;
        double totalTurnaroundTime = 0;

        for (Job job : completed) {
            double waitTime = (job.getStartTime() - job.getSubmitTime()) / 1000.0;
            double turnaroundTime = (job.getCompletionTime() - job.getSubmitTime()) / 1000.0;
            totalWaitTime += waitTime;
            totalTurnaroundTime += turnaroundTime;
        }

        double avgWait = totalWaitTime / totalCompleted;
        double avgTurnaround = totalTurnaroundTime / totalCompleted;

        long firstSubmit = Long.MAX_VALUE;
        long lastComplete = Long.MIN_VALUE;
        for (Job job : completed) {
            if (job.getSubmitTime() < firstSubmit) {
                firstSubmit = job.getSubmitTime();
            }
            if (job.getCompletionTime() > lastComplete) {
                lastComplete = job.getCompletionTime();
            }
        }
        double totalElapsed = (lastComplete - firstSubmit) / 1000.0;
        double throughput = totalElapsed > 0 ? totalCompleted / totalElapsed : 0;

        System.out.printf("Average waiting time:   %.2f seconds%n", avgWait);
        System.out.printf("Average turnaround time: %.2f seconds%n", avgTurnaround);
        System.out.printf("Throughput:             %.4f jobs/second%n", throughput);
    }

    private void handleBenchmark() {
        System.out.println("Loading benchmark workload...");

        String[][] benchmarkData = {
                {"job1", "10", "3"},
                {"job2", "2", "1"},
                {"job3", "6", "2"},
                {"job4", "4", "5"},
                {"job5", "8", "4"}
        };

        Job[] batch = new Job[benchmarkData.length];
        for (int i = 0; i < benchmarkData.length; i++) {
            String name = benchmarkData[i][0];
            int time = Integer.parseInt(benchmarkData[i][1]);
            int pri = Integer.parseInt(benchmarkData[i][2]);

            batch[i] = new Job(name, time, pri);
            System.out.printf("  Added %s runtime=%d priority=%d%n", name, time, pri);
        }

        jobQueue.enqueueAll(batch);

        System.out.println("Benchmark loaded: " + benchmarkData.length + " jobs submitted.");
        System.out.println("Use 'queue' to see job order under current policy.");
        System.out.println("Use 'stats' after completion to view metrics.");
    }
}
