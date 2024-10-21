package Clock;

public class HybridLogicalClock {
    private long logicalTime;
    private long physicalTime;

    public HybridLogicalClock() {
        this.physicalTime = System.currentTimeMillis();
        this.logicalTime = 0;
    }

    public synchronized void update(long receivedPhysicalTime, long receivedLogicalTime) {
        this.physicalTime = Math.max(this.physicalTime, receivedPhysicalTime);
        if (this.physicalTime == receivedPhysicalTime) {
            this.logicalTime = Math.max(this.logicalTime, receivedLogicalTime + 1);
        } else {
            this.logicalTime = Math.max(this.logicalTime + 1, 0);
        }
    }

    public synchronized long[] getTime() {
        return new long[]{this.physicalTime, this.logicalTime};
    }
}
