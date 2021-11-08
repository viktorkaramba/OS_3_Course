// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.*;
import java.io.*;

public class SchedulingAlgorithm {

  public static Results run(int runTime, Vector processVector, Results result) {
    int i = 0;
    int compTime = 0;
    int currentProcess = 0;
    int previousProcess = 0;
    int size = processVector.size();
    int completed = 0;
    String resultsFile = "Summary-Processes";
    result.schedulingType = "Batch (Nonpreemptive)";
    result.schedulingName = "First-Come First-Served"; 
    try {
      PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
      Process process = (Process) processVector.elementAt(currentProcess);
      out.println("Process: " + currentProcess + " registered... (" + process.cpuTime + " " + process.ioBlocking + " " + process.cpuDone + ")");
      while (compTime < runTime) {
        if (process.cpuDone == process.cpuTime) {
          completed++;
          out.println("Process: " + currentProcess + " completed... (" + process.cpuTime + " " + process.ioBlocking + " " + process.cpuDone + ")");
          if (completed == size) {
            result.compuTime = compTime;
            out.close();
            return result;
          }
          for (i = size - 1; i >= 0; i--) {
            process = (Process) processVector.elementAt(i);
            if (process.cpuDone < process.cpuTime) {
              currentProcess = i;
            }
          }
          process = (Process) processVector.elementAt(currentProcess);
          out.println("Process: " + currentProcess + " registered... (" + process.cpuTime + " " + process.ioBlocking + " " + process.cpuDone + ")");
        }      
        if (process.ioBlocking == process.ioNext) {
          out.println("Process: " + currentProcess + " I/O blocked... (" + process.cpuTime + " " + process.ioBlocking + " " + process.cpuDone + ")");
          process.numBlocked++;
          process.ioNext = 0;
          previousProcess = currentProcess;
          for (i = size - 1; i >= 0; i--) {
            process = (Process) processVector.elementAt(i);
            if (process.cpuDone < process.cpuTime && previousProcess != i) {
              currentProcess = i;
            }
          }
          process = (Process) processVector.elementAt(currentProcess);
          out.println("Process: " + currentProcess + " registered... (" + process.cpuTime + " " + process.ioBlocking + " " + process.cpuDone + ")");
        }        
        process.cpuDone++;
        if (process.ioBlocking > 0) {
          process.ioNext++;
        }
        compTime++;
      }
      out.close();
    } catch (IOException e) { /* Handle exceptions */ }
    result.compuTime = compTime;
    return result;
  }
  
  public static Results runRoundRobin(int runTime, int quantum, Vector processVector, Results result) {
    sortByArrivalTime(processVector);
    int compTime = 0;
    int size = processVector.size();
    int counter = 0;
    int completed = 0;
    Vector<Process>  gantChart = new Vector<>();
    // Additional process to add in start of ready queue;
    Process addLast = new Process();
    ArrayDeque<Process> readyQueue = new ArrayDeque<>();
    String resultsFile = "Summary-Processes";
    result.schedulingType = "Batch (Nonpreemptive)";
    result.schedulingName = "Round Robin";
    try {
      PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
      Process process;
      addLast = null;
      while (compTime < runTime) {
        //Counter is using to check is all process was start
        if(counter != size) {
          for (int i = counter; i < size; i++) {
            process = (Process) processVector.elementAt(i);
            process.cpuTime = 0;
            if (process.arrivalTime <= compTime) {
              readyQueue.addFirst(process);
              out.println("Process: " + process.id + " (" +process.cpuTime + ", " + process.cpuDone + ") " + " added to ready queue");
              counter++;
            }
          }
          //Used for adding process in start of queue
          if (addLast != null) {
            readyQueue.addFirst(addLast);
            addLast = null;
          }
        }
        //If all process started
        else {
          if (addLast != null) {
            readyQueue.addFirst(addLast);
            addLast = null;
          }
        }
        Process process1 = readyQueue.pollLast();
        if(process1.burstTime > quantum) {
          gantChart.add(process1);
          process1.ioNext+=quantum;
          //Checking if process need to block
          if(process1.ioBlocking < process1.ioNext){
            compTime += quantum-(process1.ioNext - process1.ioBlocking);
            process1.cpuDone += quantum-(process1.ioNext - process1.ioBlocking);
            process1.burstTime-=quantum-(process1.ioNext - process1.ioBlocking);
            process1.cpuTime = compTime;
            process1.numBlocked++;
            process1.ioNext = 0;
            out.println("Process: " + process1.id + " (" +process1.cpuTime + ", " + process1.cpuDone + ") " + " I/O blocked");
          }
          else if(process1.ioBlocking == process1.ioNext){
            compTime += quantum;
            process1.cpuDone+=quantum;
            process1.numBlocked++;
            process1.burstTime-=quantum;
            process1.cpuTime = compTime;
            process1.ioNext = 0;
          }
          else{
            compTime += quantum;
            process1.burstTime-=quantum;
            process1.cpuTime = compTime;
            process1.cpuDone+=quantum;
          }
          if(process1.cpuTime>0){
            addLast = process1;
          }
          out.println("Process: " + process1.id + " (" +process1.cpuTime + ", " + process1.cpuDone + ") "+ "added to gant chart");
        }
        else{
          gantChart.add(process1);
          compTime+=process1.burstTime;
          process1.ioNext+=process1.burstTime;
          if(process1.ioBlocking <= process1.ioNext){
            process1.numBlocked++;
          }
          process1.cpuDone+=process1.burstTime;
          process1.cpuTime = compTime;
          process1.ioNext = 0;
          out.println("Process: " + process1.id + " (" +process1.cpuTime + ", " + process1.cpuDone + ") "+ " added to gant chart");
          completed++;
          out.println("Process: " + process1.id + " (" +process1.cpuTime + ", " + process1.cpuDone + ") "+ " completed");
          if(completed==size){
            result.compuTime=compTime;
            out.print("Gant chart: ");
            for(int i =0; i<gantChart.size(); i++) {
              out.print("P" + gantChart.get(i).id + " ");
            }
            return result;
          }
        }
      }
      result.compuTime=compTime;
      out.close();
    } catch (IOException e) { /* Handle exceptions */ }
    return result;
  }

  public static void sortByArrivalTime(Vector processVector){
    ProcessComparator processComparator = new ProcessComparator();
    processVector.sort(processComparator);
  }
}


