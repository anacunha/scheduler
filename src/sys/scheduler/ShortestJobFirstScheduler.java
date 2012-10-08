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
public class ShortestJobFirstScheduler extends Scheduler{
    
    public static StringBuilder results;
    
    public ShortestJobFirstScheduler(LinkedList<Process> futureProcesses, LinkedList<Device> devices)
    {
        super(futureProcesses, devices);
        results = new StringBuilder();
        checkFuture();  
    }
    
    public void startScheduler()
    {     
        do {          
            // Caso nao existam processos prontos, espera a chegada do proximo processo 
            if (readyProcesses.isEmpty() && !futureProcesses.isEmpty()) 
            {
                results.append("Time: ").append(time).append(" | Waiting...\n");
                
                //Trata processos que estao fazendo I/O
                verifyBlockedProcesses();
                
                time++;
                checkFuture();
            } 
            
            else 
            {               
                //Como os processos sao ordenados pelo tempo restante de processamento,
                //esta operacao sempre pega o proximo processo para execucao.
                Process p = readyProcesses.get(0);
                
                //Enquanto novos processos nao chegam, continua executando o atual
                while (!checkFuture() && p.getStatus() != Status.Blocked) 
                {
                
                    // Se o processo ainda nao acabaou
                    if (p.start() >= 0) 
                    {
                        //Alem do processo em execucao, trata processos que estao fazendo I/O
                        verifyBlockedProcesses();
                        
                        // Se houver IO nesse instante, execute-a
                        if (p.isThereOperationNow())
                        {
                            results.append("----------------\n");
                            results.append("| Time: ").append(time).append("\tProcess ").append(p.getId()).append(" is now blocked due to IO request.\n");
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
                            results.append("Time: ").append(time).append("\tProcess ").append(p.getId()).append("\n");                                                  
                            p.stop();
                            busyTime++;
                        }
                    } 
                    
                    else 
                    {     
                        finishedProcesses.add(p);
                        p.setEndTime(time);
                        p.setTurnaroundTime();
                        p.setWaitingTime();
                        readyProcesses.remove(p);
                        break;      
                    }
                    time++;
                }
            }
        } while (!readyProcesses.isEmpty() || !futureProcesses.isEmpty());   
    }
    
    //Metodo para verificar os processos que chegam num determinado tempo
    private synchronized boolean checkFuture()
    {
        boolean arrived = false;
        
        for(int i = 0; i < futureProcesses.size(); i++)
        {
            Process p = futureProcesses.get(i);
            
            if(p.getArrivalTime() <= time && p.getStatus() != Status.Blocked)
            {
                
                readyProcesses.add(p);
                futureProcesses.remove(p);
                arrived = true;
            }
        }
        
        //Ordena os processos por tempo restante de execucao
        Collections.sort(readyProcesses);
        return arrived;
    }
    
    //Metodo para imprimir os resultados
    public String getResuls() {
        summary();
        return results.toString();
    }
    
    private void summary()
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
    
    //Executa operacoe de IO
    private void verifyBlockedProcesses()
    {
        for(Process blockedProcess : blockedProcesses)
        {
            if(blockedProcess.getStatus() == Status.Blocked)
                blockedProcess.makeOperation(this);
        }
    }
}
