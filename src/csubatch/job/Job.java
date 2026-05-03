package csubatch.job;

public class Job {
    private static long arrivalCounter = 0;

    private final String name;
    private final int cpuTime;
    private final int priority;
    private final long arrivalTime;
    private JobStatus status;

    private long submitTime;
    private long startTime;
    private long completionTime;

    public Job(String name, int cpuTime, int priority) {
        this.name = name;
        this.cpuTime = cpuTime;
        this.priority = priority;
        this.arrivalTime = ++arrivalCounter;
        this.status = JobStatus.WAITING;
        this.submitTime = System.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public int getCpuTime() {
        return cpuTime;
    }

    public int getPriority() {
        return priority;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public long getSubmitTime() {
        return submitTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(long completionTime) {
        this.completionTime = completionTime;
    }

    public static void resetCounter() {
        arrivalCounter = 0;
    }
}
