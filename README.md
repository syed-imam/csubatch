# CSUbatch

CSUbatch is a command-line batch job scheduling simulator built in Java. It allows users to submit jobs to a queue, select scheduling policies (FCFS, SJF, Priority), and observe how different algorithms affect job ordering and performance metrics. This project demonstrates fundamental operating system concepts such as job scheduling, queue management, thread synchronization, and scheduling policy comparison.

## Description

CSUbatch simulates a batch processing system where users submit jobs with a name, CPU time, and priority level. A background dispatcher thread picks the next job from the queue based on the active scheduling policy and executes it. Users can switch policies at runtime, view pending jobs sorted by the current policy, load benchmark workloads, and compare scheduling metrics such as average waiting time, turnaround time, and throughput.

Key features:
* Interactive command-line interface (CLI)
* Job submission with name, CPU time, and priority
* Three scheduling policies: FCFS, SJF, and Priority
* Runtime policy switching
* Queue display sorted by current policy
* Scheduling metrics (waiting time, turnaround time, throughput)
* Benchmark workload for policy comparison
* Dispatcher thread with execution reporting
* Input validation and error handling

## Getting Started

### Dependencies

* Java Development Kit (JDK) 8 or higher
* Any operating system with a Java runtime (macOS, Linux, Windows)

### Installing

1. Clone the repository:
```bash
git clone https://github.com/your-username/csubatch.git
cd csubatch
```

2. Compile the project:
```bash
javac -d out src/csubatch/**/*.java
```

On Linux, if glob expansion does not work, use explicit paths:
```bash
javac -d out src/csubatch/Main.java src/csubatch/cli/*.java src/csubatch/job/*.java src/csubatch/queue/*.java src/csubatch/thread/*.java
```

### Executing Program

Run the simulator:
```bash
java -cp out csubatch.Main
```

You will see the interactive prompt:
```
Welcome to CSUbatch
Type 'help' to see available commands.
>
```

## Help

### Available Commands

| Command                           | Description                              |
|-----------------------------------|------------------------------------------|
| `help`                            | Show available commands                  |
| `run <job> <time> <pri>`          | Submit a job (time>0, pri>=0)            |
| `setpolicy <fcfs\|sjf\|priority>` | Set scheduling policy                    |
| `queue`                           | Show pending jobs sorted by policy       |
| `list`                            | Show all jobs in queue                   |
| `stats`                           | Show scheduling metrics                  |
| `benchmark`                       | Load a benchmark workload (5 test jobs)  |
| `quit`                            | Exit CSUbatch                            |

### Scheduling Policies

* **FCFS** (First-Come, First-Served) - Jobs are processed in the order they arrive.
* **SJF** (Shortest Job First) - Jobs with the shortest CPU time are processed first.
* **Priority** - Jobs with the lowest priority value are processed first.

Use `setpolicy` to switch policies at any time. The `queue` command shows pending jobs in the order they will be dispatched under the current policy.

### Scheduling Metrics

The `stats` command reports:

* **Total jobs submitted** - Number of jobs added to the queue
* **Total jobs completed** - Number of jobs that finished execution
* **Average waiting time** - Mean time from submission to start of execution
* **Average turnaround time** - Mean time from submission to completion
* **Throughput** - Jobs completed per second over the total elapsed time

### Example Session

```
Welcome to CSUbatch
Type 'help' to see available commands.
> benchmark
Loading benchmark workload...
  Added job1 runtime=10 priority=3
  Added job2 runtime=2 priority=1
  Added job3 runtime=6 priority=2
  Added job4 runtime=4 priority=5
  Added job5 runtime=8 priority=4
Benchmark loaded: 5 jobs submitted.

> setpolicy sjf
Scheduling policy changed to SJF.

> queue
Scheduling Policy: SJF
Pending jobs: 4
Name            CPU_Time   Pri        Arrival_Order   Status
---------------------------------------------------------------
job2            2          1          2               WAITING
job4            4          5          4               WAITING
job3            6          2          3               WAITING
job5            8          4          5               WAITING

> stats
Scheduling Metrics
------------------
Scheduling Policy:     SJF
Total jobs submitted:   5
Total jobs completed:   1
Average waiting time:   0.00 seconds
Average turnaround time: 10.01 seconds
Throughput:             0.1000 jobs/second
```

### Common Errors

If you enter an invalid command, CSUbatch prints a helpful message and continues:
```
> setpolicy random
Error: Unknown policy 'random'.
Valid policies: fcfs, sjf, priority

> run job1 -5 2
Error: <time> must be greater than 0.

> run job1 5 abc
Error: <pri> must be a valid integer.
Usage: run <job> <time> <pri>
```

## Authors

Syed Imam

## Version History

* 0.3 (Cycle 3)
    * Added scheduling policies: FCFS, SJF, Priority
    * Added `setpolicy` command for runtime policy switching
    * Added `queue` command to display pending jobs sorted by policy
    * Added `stats` command with scheduling metrics
    * Added `benchmark` command to load test workloads
    * Improved dispatcher execution reporting
    * Enhanced input validation and error handling
    * Updated README documentation
* 0.2 (Cycle 2)
    * Implemented dispatcher thread and job queue management
    * Added `help`, `list`, `run` command support
* 0.1 (Cycle 1)
    * Initial release with basic CLI and job submission

## License

This project is for educational purposes as part of a university course.

## Acknowledgments

* [README Template by DomPizzie](https://gist.github.com/DomPizzie/7a5ff55ffa9081f2de27c315f5018afc)
