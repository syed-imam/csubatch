package csubatch.queue;

import csubatch.job.Job;
import csubatch.job.JobStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JobQueue {
    private final List<Job> jobs;
    private boolean shutdownRequested;

    public JobQueue() {
        this.jobs = new ArrayList<>();
        this.shutdownRequested = false;
    }

    public synchronized void enqueue(Job job) {
        jobs.add(job);
        notifyAll();
    }

    public synchronized Job dequeue() {
        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            if (job.getStatus() == JobStatus.WAITING) {
                return job;
            }
        }
        return null;
    }

    public synchronized Job waitForJob() throws InterruptedException {
        while (!shutdownRequested) {
            Job job = dequeueInternal();
            if (job != null) {
                return job;
            }
            wait();
        }
        return null;
    }

    private Job dequeueInternal() {
        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            if (job.getStatus() == JobStatus.WAITING) {
                return job;
            }
        }
        return null;
    }

    public synchronized void shutdown() {
        shutdownRequested = true;
        notifyAll();
    }

    public synchronized List<Job> getAllJobs() {
        return Collections.unmodifiableList(new ArrayList<>(jobs));
    }

    public synchronized int size() {
        return jobs.size();
    }

    public synchronized boolean isEmpty() {
        return jobs.isEmpty();
    }
}
