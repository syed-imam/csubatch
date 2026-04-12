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

                job.setStatus(JobStatus.RUNNING);
                System.out.printf("Dispatcher: Running job '%s' (CPU time: %d seconds)%n",
                        job.getName(), job.getCpuTime());

                Thread.sleep(job.getCpuTime() * 1000L);

                job.setStatus(JobStatus.COMPLETED);
                System.out.printf("Dispatcher: Job '%s' completed.%n", job.getName());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
