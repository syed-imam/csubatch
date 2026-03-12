package csubatch.queue;

import csubatch.job.Job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JobQueue {
    private final List<Job> jobs;

    public JobQueue() {
        this.jobs = new ArrayList<>();
    }

    /* added "synchronized" to method definition because
    * it's necessary when dispatcher is introduced as that
    * introduces another thread that -also- accesses the job list.
    * helps to avoid race conditions and subsequently skipped jobs,
    * program crashes, corrupted queues, etc. */
    public synchronized void enqueue(Job job) {
        jobs.add(job);
        notifyAll(); //notifies any thread that could proceed
    }

    //keeps dispatcher from performing unnecessary tasks when there are no jobs
    public synchronized Job dequeueBlocking() {
        while (jobs.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return jobs.remove(0);
    }

    /* added "new ArrayList<>(...)" to help with thread-safety and encapsulation
    *  this way, the dispatcher changes the actual queue, and cli only gets a copy
     */
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