public class Process {
  public int id;
  public int burstTime;
  public int cpuTime;
  public int ioBlocking;
  public int arrivalTime;
  public int cpuDone;
  public int ioNext;
  public int numBlocked;

  public Process() {
    this.id = 0;
    this.burstTime = 0;
    this.cpuTime = 0;
    this.ioBlocking = 0;
    this.arrivalTime = 0;
    this.cpuDone = 0;
    this.ioNext = 0;
    this.numBlocked = 0;
  }

  public Process(int id,int cpuTime, int ioBlocking, int  arrivalTime, int cpuDone, int ioNext, int numBlocked) {
    this.id = id;
    this.burstTime = cpuTime;
    this.cpuTime = cpuTime;
    this.ioBlocking = ioBlocking;
    this.arrivalTime = arrivalTime;
    this.cpuDone = cpuDone;
    this.ioNext = ioNext;
    this.numBlocked = numBlocked;
  }
}
