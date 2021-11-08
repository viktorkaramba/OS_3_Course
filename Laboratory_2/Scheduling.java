

import java.io.*;
import java.util.*;

public class Scheduling {

  private static int processNum = 5;
  private static int meanDev = 1000;
  private static int standardDev = 100;
  private static int quantum = 100;
  private static int runtime = 1000;
  private static Vector processVector = new Vector();
  private static Results result = new Results("null","null",0);
  private static String resultsFile = "Summary-Results";
  
  private static void Init(String file) {
    File f = new File(file);
    String line;
    String tmp;
    int cpuTime = 0;
    int ioBlocking = 0;
    int arrivalTime = 0;
    double X = 0.0;
    int i = 0;
    try {   
      //BufferedReader in = new BufferedReader(new FileReader(f));
      DataInputStream in = new DataInputStream(new FileInputStream(f));
      while ((line = in.readLine()) != null) {
        if (line.startsWith("numprocess")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          processNum = Common.s2i(st.nextToken());
        }
        if (line.startsWith("meandev")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          meanDev = Common.s2i(st.nextToken());
        }
        if (line.startsWith("standdev")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          standardDev = Common.s2i(st.nextToken());
        }
		if (line.startsWith("quantum")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          quantum = Common.s2i(st.nextToken());
        }
        if (line.startsWith("process")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          arrivalTime = Common.s2i(st.nextToken());
          ioBlocking = Common.s2i(st.nextToken());
          X = Common.R1();
          while (X == -1.0) {
            X = Common.R1();
          }
          X = X * standardDev;
          cpuTime = (int) X + meanDev;
          processVector.addElement(new Process(i, cpuTime, ioBlocking, arrivalTime, 0, 0, 0));
          i++;
        }
        if (line.startsWith("runtime")) {
          StringTokenizer st = new StringTokenizer(line);
          st.nextToken();
          runtime = Common.s2i(st.nextToken());
        }

      }
      in.close();
    } catch (IOException e) { /* Handle exceptions */ }
  }

  private static void debug() {
    int i = 0;
    System.out.println("processnum " + processNum);
    System.out.println("meandevm " + meanDev);
    System.out.println("standdev " + standardDev);
    int size = processVector.size();
    for (i = 0; i < size; i++) {
      Process process = (Process) processVector.elementAt(i);
      System.out.println("process " + i + " " + process.cpuTime + " " + process.ioBlocking + " " + process.cpuDone + " " + process.numBlocked);
    }
    System.out.println("runtime " + runtime);
  }

  public static void main(String[] args) {
    int i = 0;
    if (args.length != 1) {
      System.out.println("Usage: 'java Scheduling <INIT FILE>'");
      System.exit(-1);
    }
    File f = new File(args[0]);
    if (!(f.exists())) {
      System.out.println("Scheduling: error, file '" + f.getName() + "' does not exist.");
      System.exit(-1);
    }  
    if (!(f.canRead())) {
      System.out.println("Scheduling: error, read of " + f.getName() + " failed.");
      System.exit(-1);
    }
    System.out.println("Working...");
    Init(args[0]);
    if (processVector.size() < processNum) {
      i = 0;
      while (processVector.size() < processNum) {
          double X = Common.R1();
          while (X == -1.0) {
            X = Common.R1();
          }
          X = X * standardDev;
        int cputime = (int) X + meanDev;
        int arrivalTime = 100;
        processVector.addElement(new Process(i,cputime,i*100, i*100,0,0,0));
        i++;
      }
    }
    result = SchedulingAlgorithm.runRoundRobin(runtime, quantum, processVector, result);
    try {
      PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
      out.println("Scheduling Type: " + result.schedulingType);
      out.println("Scheduling Name: " + result.schedulingName);
      out.println("Simulation Run Time: " + result.compuTime);
      out.println("Mean: " + meanDev);
      out.println("Standard Deviation: " + standardDev);
      out.println("Quantum: " + quantum);
      out.println("Process #\tCPU Time\tIO Blocking\tCPU Completed\tCPU Blocked\t\tTAT\t\t\tWaiting Time");
      for (i = 0; i < processVector.size(); i++) {
        Process process = (Process) processVector.elementAt(i);
        out.print(Integer.toString(i));
        if (i < 100) { out.print("\t"); } else { out.print("\t"); }
        out.print(Integer.toString(process.cpuTime));
        if (process.cpuTime < 100) { out.print(" (ms)\t"); } else { out.print(" (ms)\t"); }
        out.print(Integer.toString(process.ioBlocking));
        if (process.ioBlocking < 100) { out.print(" (ms)\t\t"); } else { out.print(" (ms)\t\t"); }
        out.print(Integer.toString(process.cpuDone));
        if (process.cpuDone < 100) { out.print(" (ms)\t\t"); } else { out.print(" (ms)\t\t"); }
        out.print(process.numBlocked + " times\t\t");
        int tat = process.cpuTime - process.arrivalTime;
        out.print(tat + " (ms)\t");
        int wt = tat - process.cpuDone;
        out.println(wt + " (ms)\t");
      }
      out.close();
    } catch (IOException e) { /* Handle exceptions */ }
  System.out.println("Completed.");
  }
}

