package sys.io;

import java.util.LinkedList;
import sys.scheduler.RoundRobinScheduler;
import sys.scheduler.Scheduler;
import sys.scheduler.ShortestJobFirstScheduler;


/**
 *
 * @author Ana Luiza Cunha, Guilherme Kelling, Mauricio Zaquia
 */
public class Device {
    
    private boolean uso;
    private String name;
    private int deviceTime;
    private double busyTime;
    private OperationIO currentOperation;
    private LinkedList<OperationIO> waitingOperations;

    public Device(String name, int deviceTime) {
        this.name = name;
        this.deviceTime = deviceTime;
        this.currentOperation = null;
        this.waitingOperations = new LinkedList<OperationIO>();
        uso = false;
    }

    public String getName() {
        return name;
    }

    public OperationIO getCurrentProcess() {
        return currentOperation;
    }

    public int getDeviceTime() {
        return deviceTime;
    }

    // Metodo chamado por uma operacao de IO.
    // Adciona a operacao na lista de operacoes do device.
    public boolean addOperationToQueue(OperationIO operation) {
        
        // Check if there's duplicate Processes on WaitingProcesses list.
        if (waitingOperations.contains(operation))
                return false;
        
        // If there's not, add it to the list.
        waitingOperations.add(operation);
        return true;
    }
    
    public OperationIO popOperationFromQueue() {
        
        OperationIO o = null;
        
        if (waitingOperations.size() > 0)
            o = waitingOperations.removeFirst();
        
        uso = false;
        try{
            currentOperation = waitingOperations.getFirst();
        }
        catch(Exception e){}
        
        return o;
    }
    
    // Metodo que executa operações no dispositivo.
    // Precisa ser chamado em cada ciclo do escalonador quando ha uma operacao
    // para ser executada.
    public void makeIO(Scheduler s) {
        
        if (currentOperation.remainingTime > 0) 
        {  
            busyTime++;
            currentOperation.remainingTime--;
            currentOperation.increaseBlockedTime();
            if(s instanceof ShortestJobFirstScheduler)
                ShortestJobFirstScheduler.results.append("| Time: ").append(s.getTime()).append("\t").append(getName()).append(" running an operation from Process ").append(currentOperation.getProcessId()).append(".\n");     
            else
                RoundRobinScheduler.results.append("| Time: ").append(s.getTime()).append("\t").append(getName()).append(" running an operation from Process ").append(currentOperation.getProcessId()).append(".\n");     
        }
        else
            popOperationFromQueue();
        
    }
    
    //Inicia operacao de IO
    public void startIO() {
        currentOperation = waitingOperations.getFirst();
        uso = true;
    }

    @Override
    public String toString() {
        return getName();
    }
    
    public double getBusyTime()
    {
        return busyTime;
    }
    
    public boolean isBusy()
    {
        return uso;
    }
    
    public OperationIO getOperation()
    {
        return currentOperation;
    }
}
