package csubatch.cli;

import csubatch.job.Job;
import csubatch.queue.JobQueue;

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

            case "list":
                handleList();
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
        System.out.println("  help");
        System.out.println("  help -test");
        System.out.println("  run <csubatch.job> <time> <pri>");
        System.out.println("  list");
        System.out.println("  quit");
    }

    private void handleRun(String[] parts) {
        if (parts.length != 4) {
            System.out.println("Usage: run <csubatch.job> <time> <pri>");
            return;
        }

        String jobName = parts[1];
        String timeArg = parts[2];
        String priorityArg = parts[3];

        int cpuTime;
        int priority;

        try {
            cpuTime = Integer.parseInt(timeArg);
            priority = Integer.parseInt(priorityArg);
        } catch (NumberFormatException e) {
            System.out.println("Error: <time> and <pri> must be numeric.");
            System.out.println("Usage: run <csubatch.job> <time> <pri>");
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


        System.out.println("Job submitted successfully:");
        System.out.printf("  Name: %s | CPU_Time: %d | Pri: %d | Arrival_time: %d | Status: %s%n",
                job.getName(),
                job.getCpuTime(),
                job.getPriority(),
                job.getArrivalTime(),
                job.getStatus());

        jobQueue.enqueue(job); //moved this line to the bottom to help with testing output; printing was a little disorganized.
    }

    private void handleList() {
        List<Job> jobs = jobQueue.getAllJobs();

        System.out.println("Current policy: FCFS");
        System.out.println("Total jobs in queue: " + jobs.size());

        if (jobs.isEmpty()) {
            System.out.println("No jobs in queue.");
            return;
        }

        System.out.printf("%-15s %-10s %-10s %-15s %-10s%n",
                "Name", "CPU_Time", "Pri", "Arrival_time", "Status");
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
}