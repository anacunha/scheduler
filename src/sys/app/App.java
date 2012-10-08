package sys.app;

import java.util.LinkedList;
import sys.io.Device;
import sys.scheduler.RoundRobinScheduler;
import sys.process.Process;
import sys.scheduler.ShortestJobFirstScheduler;

/**
 *
 * @author Ana Luiza Cunha, Guilherme Kelling, Mauricio Zaquia
 * @description Class responsible for initializing the right Scheduler, 
 * browse for the files and get the results.
 */
public class App {

    private RoundRobinScheduler rr;
    private ShortestJobFirstScheduler sjf;
    
    //Contains all the processes from the file input.
    private LinkedList<Process> processes = new LinkedList<Process>();
    
    private LinkedList<Device> devices = new LinkedList<Device>();

    public App() {
        
    }
    
    //int q refers to Quantum.
    public void initializeRR(int q) {
        rr = new RoundRobinScheduler(processes, devices);
        rr.startScheduler(q);
    } 
    
    //int q refers to Quantum.
    public synchronized void initializeSJF() {
        sjf = new ShortestJobFirstScheduler(processes, devices);
        sjf.startScheduler();
    }
    
    //Calls the FileHandler to decode the file.
    public String browseFileA() throws Exception {
        processes = FileHandler.readFileA();
        return FileHandler.filePath;
    }
    
    public String browseFileB() throws Exception {
        devices = FileHandler.readFileB();
        return FileHandler.filePath;
    }
    
    //Shows the available algorithms
    public Object[] getAlgorithmList() {
        return new Object[]{"Round-Robin","Preemptive SJF"};
    }
    
    public Object[] getProcesses() {
        return processes.toArray();
    }
    
    public Object[] getOperationsForProcess(Process p)
    {
        return p.getOperations().toArray();
    }
    
    public Object[] getDevices()
    {
        return devices.toArray();
    }
    
    //Return the results for interface.    
    public String getResults() {
        try { return rr.getResuls(); }
        catch (NullPointerException np) { return sjf.getResuls(); }
    }
    
}
