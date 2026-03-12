package csubatch;

import csubatch.cli.CommandShell;
import csubatch.queue.JobQueue;
import csubatch.dispatcher.Dispatcher;

public class Main {
    public static void main(String[] args) {
        JobQueue jobQueue = new JobQueue();

        Thread dispatcherThread = new Thread(new Dispatcher(jobQueue));
        dispatcherThread.start();

        CommandShell shell = new CommandShell(jobQueue);
        shell.start();
    }
}