package sys.scheduler;

import java.util.Collections;
import java.util.LinkedList;
import sys.io.Device;
import sys.process.Process;
import sys.process.Status;

/**
 *
 * @author Ana Luiza Cunha, Guilherme Kelling, Mauricio Zaquia
 */
public class RoundRobinScheduler extends Scheduler{
    
    public static StringBuilder results;
    
    public RoundRobinScheduler(LinkedList<Process> futureProcesses, LinkedList<Device> devices)
    {
        super(futureProcesses, devices);
        results = new StringBuilder();
        checkFuture(); 
    }
   
    //Verifica se algum novo processo chegou no tempo atual.
    private boolean checkFuture() 
    {
        LinkedList<Process> futureCopy = copyList(futureProcesses);
        boolean arrived = false;
        
        for(int i = 0; i < futureCopy.size(); i++)
        {
            Process p = futureCopy.get(i);
            
            if (p.getArrivalTime() <= time && p.getStatus() != Status.Blocked) 
            {
                readyProcesses.add(p);
                futureProcesses.remove(p);
                arrived = true;
            }
        }
        
        return arrived;
    }
    
    public void startScheduler(int quantum) {
    
        do 
        {     
            //Executa cada processo pronto
            for (int i = 0; i < readyProcesses.size(); i++) 
            {
                Process p = readyProcesses.get(i);
                int countQuantum = 0;
                
                //O processo faz uso do processador pelo tempo de quantum,
                //desde que ele nao seja bloqueado durante sua execucao
                while(countQuantum < quantum && p.getStatus() != Status.Blocked) 
                {
                    //Checa se o processo ainda nao terminou
                    if (p.start() >= 0 && p.getStatus() != Status.Blocked) 
                    {
                        //Alem do processo em execucao, trata processos que estao fazendo I/O
                        verifyBlockedProcesses();
                        
                        // Se houver IO nesse instante, execute-a
                        if (p.isThereOperationNow())
                        {
                            results.append("| Time: ").append(time).append("\t Process ").append(p.getId()).append(" is now blocked due to IO request.\n");
                            p.requestOperation(this);
                            
                            // O processo é bloqueado, logo, perde o escalonador, 
                            // saindo da lista de prontos, indo para lista de bloqueados
                            // e voltando para a de futuros.
                            futureProcesses.add(p);
                            blockedProcesses.add(p);
                            readyProcesses.remove(p); 
                            Collections.reverse(blockedProcesses);
                        }
                        //Se nao houver IO, executa o processo normalmente
                        else
                        {
                            results.append("Time: ").append(time).append(" | Process ").append(p.getId()).append("\n"); 
                            p.stop();
                            busyTime++;
                        }                      
                    } 
                    time++;
                    if(p.start() == -1)
                    {
                        //Se o processo terminou, vai para lista de prontos,
                        //calculamos seu waiting e turnaroudn time,
                        //e removemos da lista de prontos.
                        finishedProcesses.add(p);
                        p.setEndTime(time);
                        p.setTurnaroundTime();
                        p.setWaitingTime();
                        readyProcesses.remove(p);
                         
                        break;
                    }
                    //time++; 
                    countQuantum++;
                    checkFuture();
                }
            }
            
            //Se nao ha nenhum processo pronto, espera pelo o proximo processo
            if(readyProcesses.isEmpty() && !futureProcesses.isEmpty()) 
            {
                results.append("Time: ").append(time).append(" | Waiting...\n");
                
                //Execucao das operacoes de IO
                verifyBlockedProcesses();
                
                time++;
                checkFuture();
            }

        }while(!readyProcesses.isEmpty() || !futureProcesses.isEmpty());
    }
        
    public String getResuls() {
        summary();
        return results.toString();
    }
    
    public void summary()
    {     
        results.append("\n+===========+\n");
        results.append("|| PROCESSES INFO ||\n");
        results.append("+===========+\n");

        results.append("\nPROCESS    PT      AT      WT      BT      TT");
        double tt = 0;
        
        for(Process p : finishedProcesses)
        {
            tt = tt + p.getTurnaroundTime();
            results.append(String.format("\n      %2d         %02d      %02d      %02d      %02d      %02d", p.getId(), p.getProcessTime(), p.getArrivalTime(), p.getWaitingTime(), p.getBlockedTime(), p.getTurnaroundTime()));
        }

        results.append("\n\nAverage Turnaround: ").append(tt/finishedProcesses.size()).append(" u.t.\n");
        results.append("\nTotal Time: ").append(time-1);
        results.append("\nCPU Busy Time: ").append(busyTime);
        results.append("\n% CPU Usage: ").append(String.format("%.2f", (busyTime/(time-1))*100)).append("%");
        
        for(Device d : devices)
        {
            results.append("\n\n").append(d.getName()).append(" Busy Time: ").append(d.getBusyTime());
            results.append("\n% ").append(d.getName()).append(" Usage: ").append(String.format("%.2f", (d.getBusyTime()/(time-1))*100)).append("%");
        }
        results.append("\n\n+========+\n");
        results.append("||       END      ||\n");
        results.append("+========+\n");
    }
    
    private LinkedList<Process> copyList(LinkedList<Process> original)
    {
        LinkedList<Process> copy = new LinkedList<Process>();
        for(Process p : original)
            copy.add(p);
        return copy;
    }
    
    private void verifyBlockedProcesses()
    {
        for(Process blockedProcess : blockedProcesses)
        {
            if(blockedProcess.getStatus() == Status.Blocked)
                blockedProcess.makeOperation(this);
        }
    }
}
