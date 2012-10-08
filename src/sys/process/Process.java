package sys.process;

import java.util.LinkedList;
import sys.io.OperationIO;
import sys.scheduler.Scheduler;

/**
 *
 * @author Ana Luiza Cunha, Guilherme Kelling, Mauricio Zaquia
 */
public class Process implements Comparable<Process> {
    
    private int id;
    private int arrivalTime, processTime;
    private int remainingTime, endTime;
    private int turnaroundTime, waitingTime, blockedTime;  
    private LinkedList<OperationIO> operations;
    private OperationIO currentOperation; 
    private Status status;
    
    public Process(int id, int at, int pt) {
        this.id = id;
        arrivalTime = at;
        processTime = pt;
        remainingTime = processTime;
        status = Status.Ready;
        
        operations = new LinkedList<OperationIO>(); 
    }

    //Makes the process start running.
    public int start() {
        if (status == Status.Finished || remainingTime == 0) return -1;
        else if (status == Status.Ready) { status = Status.Running; return 1; }
        else return 0;
    }
    
    //Makes the process stop running
    public void stop() {
        remainingTime--;
        if(status != Status.Blocked)
        {
            if (remainingTime<=0) { 
                status = Status.Finished;
            }
            else
            {
                status = Status.Ready;
            }
        }
    }
    
    public boolean addOperation(OperationIO operation) throws Exception {
        
        for (OperationIO op : operations)
        {
            if (op.getDeviceName().equals(operation.getDeviceName())
                    && op.getStartTime() == operation.getStartTime())
                throw new Exception();
        }
        
        // If the addition make the operations list greater than total time, we can't add it.
        if (operations.size()+1 > processTime)
            return false;
        
        operations.add(operation);
        return true;
        
    }
    
    public void removeOperation(OperationIO o)
    {
        operations.remove(o);
    }
    
    //Verifica se ha uma operacao no tempo atual
    public boolean isThereOperationNow() {
        
        boolean yes = false;
        
        if (operations.isEmpty()) return false;
        
        for (OperationIO op : operations)
            if (op.startTime <= (processTime-remainingTime)+1)
                yes = true;
        
        return yes;
    }
    
    // Em algum instante, o método requestOperation é chamado para o processo.
    // nesse método, ele pega a primeira posição da fila de operações, bloqueia o processo 
    // e executa o método addOperation da classe OperationIO.
    public boolean requestOperation(Scheduler s) {
        
        if (status == Status.Finished || status == Status.Blocked)
            return false;
        
        // If there's operations to be done, pick the first one from the list.
        if (operations.size() > 0) {
            currentOperation = operations.removeFirst();
            
            // And execute it.
            status = Status.Blocked;
            remainingTime--;
            currentOperation.addOperation(s);
                
            return true;    
        }        
        return false; 
    }
    
    //Realiza operacao de IO (para um tick)
    public void makeOperation(Scheduler s) {
        currentOperation.processOperation(s);
    }
    
    public int getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getProcessTime() {
        return processTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public void increaseBlockedTime() {
        blockedTime++;
        turnaroundTime++;
    }
    
    public int getBlockedTime() {
        return blockedTime;
    }
    
    public OperationIO getCurrentOperation() {
        return currentOperation;
    }
    
    public void setEndTime(int t) {
        endTime = t;
    }
    
    //Calculates the turnaround time
    public void setTurnaroundTime() {
        turnaroundTime = endTime - arrivalTime;
    }
    
    public int getWaitingTime() {
        return waitingTime;
    }
    
    //Calculates the waiting time
    public void setWaitingTime() {
        waitingTime = getTurnaroundTime()-processTime-blockedTime;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status s) {
        status = s;
    }

    //Compare the processes by remaining time (useful for SJF)
    @Override
    public int compareTo(Process t) {
        Integer a = remainingTime;
        Integer b = t.getRemainingTime();
        return a.compareTo(b);
    }

    public LinkedList<OperationIO> getOperations() {
        return operations;
    }

    @Override
    public String toString() {
        return "Process "+id;
    }
    
    
    
}
