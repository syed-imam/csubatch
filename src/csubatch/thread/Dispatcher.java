package csubatch.thread;

import csubatch.job.Job;
import csubatch.job.JobStatus;
import csubatch.queue.JobQueue;

public class Dispatcher implements Runnable {
    private final JobQueue jobQueue;

    public Dispatcher(JobQueue jobQueue) {
        this.jobQueue = jobQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Job job = jobQueue.waitForJob();
                if (job == null) {
                    break;
                }

                String policyName = jobQueue.getPolicy().name();

                job.setStartTime(System.currentTimeMillis());
                job.setStatus(JobStatus.RUNNING);
                System.out.printf("Dispatching %s using %s%n", job.getName(), policyName);
                System.out.printf("Running %s...%n", job.getName());

                Thread.sleep(job.getCpuTime() * 1000L);

                job.setCompletionTime(System.currentTimeMillis());
                job.setStatus(JobStatus.COMPLETED);
                System.out.printf("Completed %s%n", job.getName());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
