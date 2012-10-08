package sys.io;

import sys.process.Process;
import sys.process.Status;
import sys.scheduler.RoundRobinScheduler;
import sys.scheduler.Scheduler;
import sys.scheduler.ShortestJobFirstScheduler;

/**
 *
 * @author Ana Luiza Cunha, Guilherme Kelling, Maurício Zaquia
 */
public class OperationIO {
    
    public Device device;
    public Process process;
    public int startTime;
    public int remainingTime;

    public OperationIO(Device device, int startTime, Process process) {
        
        if (device != null) {        
            this.device = device;
            this.startTime = startTime;
            remainingTime = this.device.getDeviceTime();
            this.process = process;
        }
        
    }

    public int getProcessId() {
        return process.getId();
    }
    
    public String getDeviceName() {
        return device.getName();
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int getStartTime() {
        return startTime;
    }
    
    public void increaseBlockedTime() {
        process.increaseBlockedTime();
    }
    
    // O processo chama este método, que executa uma operacao de I/O para um tick.
    public void processOperation(Scheduler s) {
        if (remainingTime == 0) 
            return;
       
        if(remainingTime != 0 && (device.getOperation().equals(this)))
        {
            device.makeIO(s);   
            
            //depois de fazer IO, verifica se tempo de operacao ja terminou
            if(remainingTime == 0)
            {
                //se já terminou, marca o processo como pronto
                process.setStatus(Status.Ready);
                if(s instanceof ShortestJobFirstScheduler)
                {
                    ShortestJobFirstScheduler.results.append("| Process ").append(process.getId()).append(" is now unblocked.\n");
                }
                else
                {
                    RoundRobinScheduler.results.append("| Process ").append(process.getId()).append(" is now unblocked.\n");
                }
                device.popOperationFromQueue();
            }
        }
    }
    
    // O processo chama este método, que vai adicionar a operação na lista do dispositivo escolhido
    // usando o método addOperationToQueue, da classe Device.
    public void addOperation(Scheduler s) {
        device.addOperationToQueue(this);
        
        if(s instanceof ShortestJobFirstScheduler)
            ShortestJobFirstScheduler.results.append("| Operation added to ").append(device.getName()).append(" queue.\n");
        else
            RoundRobinScheduler.results.append("| Operation added to ").append(device.getName()).append(" queue.\n");
        
        device.startIO();
    }

    @Override
    public String toString() {
        return "Operation with "+device.getName();
    }
    
    
      
}
