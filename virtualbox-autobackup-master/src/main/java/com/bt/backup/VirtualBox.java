package com.bt.backup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 608761587 on 09/11/2015.
 */
public class VirtualBox {
    public enum VBOX_STATUS {RUNNING, POWEROFF, SAVEDSTATE};

    private String vmId;
    private String configLocation;
    private List<String> hddLocations = new ArrayList<String>();
    private VBOX_STATUS status;
    private String archiveLocation;
    private String uuid;
    private String snapshotLocation;
    private List<String> sharedFolderLocations = new ArrayList<String>();
    private String vmInfoOutput;
    private long archiveElapsed;
    private long transferElapsed;
    private double averageUploadSpeed;
    private long archiveSize;
    private long vmDownTime = 0L;

    public long getVmDownTime() {
        return vmDownTime;
    }

    public void setVmDownTime(long vmDownTime) {
        this.vmDownTime = vmDownTime;
    }

    public long getArchiveSize() {
        return archiveSize;
    }

    public void setArchiveSize(long archiveSize) {
        this.archiveSize = archiveSize;
    }

    public double getAverageUploadSpeed() {
        return averageUploadSpeed;
    }

    public void setAverageUploadSpeed(double averageUploadSpeed) {
        this.averageUploadSpeed = averageUploadSpeed;
    }

    public long getArchiveElapsed() {
        return archiveElapsed;
    }

    public void setArchiveElapsed(long archiveElapsed) {
        this.archiveElapsed = archiveElapsed;
    }

    public long getTransferElapsed() {
        return transferElapsed;
    }

    public void setTransferElapsed(long transferElapsed) {
        this.transferElapsed = transferElapsed;
    }

    public String getVmInfoOutput() {
        return vmInfoOutput;
    }

    public void setVmInfoOutput(String vmInfoOutput) {
        this.vmInfoOutput = vmInfoOutput;
    }

    public List<String> getSharedFolderLocations() {
        return sharedFolderLocations;
    }

    public void setSharedFolderLocations(List<String> sharedFolderLocations) {
        this.sharedFolderLocations = sharedFolderLocations;
    }

    public String getSnapshotLocation() {
        return snapshotLocation;
    }

    public void setSnapshotLocation(String snapshotLocation) {
        this.snapshotLocation = snapshotLocation;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getArchiveLocation() {
        return archiveLocation;
    }

    public void setArchiveLocation(String archiveLocation) {
        this.archiveLocation = archiveLocation;
    }

    public String getVmId() {
        return vmId;
    }

    public void setVmId(String vmId) {
        this.vmId = vmId;
    }

    public String getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    public List<String> getHddLocations() {
        return hddLocations;
    }

    public void setHddLocations(List<String> hddLocations) {
        this.hddLocations = hddLocations;
    }

    public VBOX_STATUS getStatus() {
        return status;
    }

    public void setStatus(VBOX_STATUS status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "VirtualBox{" +
                "vmId='" + vmId + '\'' +
                ", configLocation='" + configLocation + '\'' +
                ", hddLocations=" + hddLocations +
                ", status=" + status +
                ", archiveLocation='" + archiveLocation + '\'' +
                ", uuid='" + uuid + '\'' +
                ", snapshotLocation='" + snapshotLocation + '\'' +
                ", sharedFolderLocations=" + sharedFolderLocations +
                ", vmInfoOutput='" + vmInfoOutput + '\'' +
                ", archiveElapsed=" + archiveElapsed +
                ", transferElapsed=" + transferElapsed +
                ", averageUploadSpeed=" + averageUploadSpeed +
                ", archiveSize=" + archiveSize +
                ", vmDownTime=" + vmDownTime +
                '}';
    }
}
