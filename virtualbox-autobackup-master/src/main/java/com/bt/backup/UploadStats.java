package com.bt.backup;

/**
 * Created by 608761587 on 12/11/2015.
 */
public class UploadStats {
    private long totalBytesTransferred;
    private int bytesTransferred;
    private long streamSize;
    private long lastUpdated;
    private double estimatedSpeed;
    private long startTime;
    private long totalSize;

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public double getEstimatedSpeed() {
        return estimatedSpeed;
    }

    public void setEstimatedSpeed(double estimatedSpeed) {
        this.estimatedSpeed = estimatedSpeed;
    }

    public long getTotalBytesTransferred() {
        return totalBytesTransferred;
    }

    public void setTotalBytesTransferred(long totalBytesTransferred) {
        this.totalBytesTransferred = totalBytesTransferred;
    }

    public int getBytesTransferred() {
        return bytesTransferred;
    }

    public void setBytesTransferred(int bytesTransferred) {
        this.bytesTransferred = bytesTransferred;
    }

    public long getStreamSize() {
        return streamSize;
    }

    public void setStreamSize(long streamSize) {
        this.streamSize = streamSize;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
