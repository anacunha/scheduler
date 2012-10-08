package sys.scheduler;

import java.util.LinkedList;
import sys.process.Process;
import sys.io.Device;

/**
 *
 * @author Ana Luiza Cunha, Guilherme Kelling, Mauricio Zaquia
 */
public class Scheduler {
    
    protected LinkedList<Process> futureProcesses;
    protected LinkedList<Process> readyProcesses;
    protected LinkedList<Process> blockedProcesses;
    protected LinkedList<Process> finishedProcesses;
    protected int time;
    protected double busyTime; //controla o tempo de ocupacao da CPU
    protected LinkedList<Device> devices;

    public Scheduler(LinkedList<Process> futureProcesses, LinkedList<Device> devices) {
        this.futureProcesses = futureProcesses;
        readyProcesses = new LinkedList<Process>();
        blockedProcesses = new LinkedList<Process>();
        finishedProcesses = new LinkedList<Process>();
        this.devices = devices;
        
        time = 0;
        busyTime = 0;
    }
    
    public int getTime()
    {
        return time;
    }
}