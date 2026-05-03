package csubatch.queue;

import csubatch.job.Job;
import csubatch.job.JobStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JobQueue {
    private final List<Job> jobs;
    private boolean shutdownRequested;
    private SchedulingPolicy policy;

    public JobQueue() {
        this.jobs = new ArrayList<>();
        this.shutdownRequested = false;
        this.policy = SchedulingPolicy.FCFS;
    }

    public synchronized void enqueue(Job job) {
        jobs.add(job);
        notifyAll();
    }

    public synchronized Job waitForJob() throws InterruptedException {
        while (!shutdownRequested) {
            Job job = selectNextJob();
            if (job != null) {
                return job;
            }
            wait();
        }
        return null;
    }

    private Job selectNextJob() {
        List<Job> waiting = new ArrayList<>();
        for (Job job : jobs) {
            if (job.getStatus() == JobStatus.WAITING) {
                waiting.add(job);
            }
        }
        if (waiting.isEmpty()) {
            return null;
        }

        switch (policy) {
            case SJF:
                waiting.sort(Comparator.comparingInt(Job::getCpuTime)
                        .thenComparingLong(Job::getArrivalTime));
                break;
            case PRIORITY:
                waiting.sort(Comparator.comparingInt(Job::getPriority)
                        .thenComparingLong(Job::getArrivalTime));
                break;
            case FCFS:
            default:
                waiting.sort(Comparator.comparingLong(Job::getArrivalTime));
                break;
        }

        return waiting.get(0);
    }

    public synchronized void setPolicy(SchedulingPolicy policy) {
        this.policy = policy;
    }

    public synchronized SchedulingPolicy getPolicy() {
        return policy;
    }

    public synchronized void shutdown() {
        shutdownRequested = true;
        notifyAll();
    }

    public synchronized List<Job> getAllJobs() {
        return Collections.unmodifiableList(new ArrayList<>(jobs));
    }

    public synchronized List<Job> getWaitingJobsSorted() {
        List<Job> waiting = new ArrayList<>();
        for (Job job : jobs) {
            if (job.getStatus() == JobStatus.WAITING) {
                waiting.add(job);
            }
        }

        switch (policy) {
            case SJF:
                waiting.sort(Comparator.comparingInt(Job::getCpuTime)
                        .thenComparingLong(Job::getArrivalTime));
                break;
            case PRIORITY:
                waiting.sort(Comparator.comparingInt(Job::getPriority)
                        .thenComparingLong(Job::getArrivalTime));
                break;
            case FCFS:
            default:
                waiting.sort(Comparator.comparingLong(Job::getArrivalTime));
                break;
        }

        return waiting;
    }

    public synchronized List<Job> getCompletedJobs() {
        List<Job> completed = new ArrayList<>();
        for (Job job : jobs) {
            if (job.getStatus() == JobStatus.COMPLETED) {
                completed.add(job);
            }
        }
        return completed;
    }

    public synchronized int size() {
        return jobs.size();
    }

    public synchronized boolean isEmpty() {
        return jobs.isEmpty();
    }
}
