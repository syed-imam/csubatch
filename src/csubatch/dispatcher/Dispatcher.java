package csubatch.dispatcher;

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

            Job job = jobQueue.dequeueBlocking();

            job.setStatus(JobStatus.RUNNING);

            System.out.println("Running job: " + job.getName());

            try {
                // simulate CPU work
                Thread.sleep(job.getCpuTime() * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            job.setStatus(JobStatus.COMPLETED);

            System.out.println("Completed job: " + job.getName());
        }
    }
}