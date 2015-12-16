package com.bt.backup;

import org.apache.commons.net.io.CopyStreamAdapter;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by 608761587 on 09/11/2015.
 */
public class BackupMain {

    /**
     *
     */
    public static final String DEFAULT_VBOX_HOME = "C:\\Program Files\\Oracle\\VirtualBox";

    /**
     *
     */
    public static final String INFO_CONFIG_FILE_LOCATION = "Config file";

    /**
     *
     */
    public static final String INFO_IDE_LOCATION = "IDE";

    /**
     *
     */
    public static final String INFO_SATA_LOCATION = "SATA";

    /**
     *
     */
    public static final String INFO_SNAPSHOT_LOCATION = "Snapshot folder";

    /**
     *
     */
    public static final String INFO_SHARED_FOLDER = "Shared folders";

    /**
     *
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";

    /**
     *
     */
    public static final String DEFAULT_BACKUP_PATH = File.separator + "vm-backup";

    //register vm constant

    /**
     *
     */
        public static final String DEFAULT_VM_SNAPSHOT_LOCATION = System.getProperty("user.home") + "\\VirtualBox VMs\\Redmine\\Redmine.vbox";
    private String vboxHome;
    private String defaultBackupPath;

    private Set<String> targetVms = new HashSet<String>();
    private final List<VirtualBox> virtualBoxes = new ArrayList<VirtualBox>();
    private String url;
    private String username;
    private String password;
    private String sourceFile;
    private static boolean noArchive = false;
    private boolean allVm;
    private final UploadStats uploadStats = new UploadStats();
    private DefaultFtpClient ftpClient;

    private BackupMain(String url, String username, String password, String targetVm, String sourceFile) {
        this.vboxHome = (System.getenv("VBOX_HOME") != null && !System.getenv("VBOX_HOME").isEmpty()) ? System.getenv("VBOX_HOME") : DEFAULT_VBOX_HOME;
        this.defaultBackupPath = (System.getenv("USERPROFILE") != null) ? System.getenv("USERPROFILE") + DEFAULT_BACKUP_PATH : "C:\\backup" + DEFAULT_BACKUP_PATH;

        System.out.println("Source files passed :" + sourceFile);
        this.url = url;
        this.username = username;
        this.password = password;
        this.sourceFile = sourceFile;

        if (targetVm != null && targetVm.contains(",")) {
            String[] targets = targetVm.split(",");
            for (String target : targets) {
                targetVms.add(target);
            }
        } else if ("ALL".equalsIgnoreCase(targetVm)) {
            allVm = true;
        } else if (targetVm != null) {
            targetVms.add(targetVm);
        }
    }

    private void execBackupVm(boolean showInfoOnly) {

        System.out.println("Zip is enable -" + noArchive);
        log("Getting list of VMs");
        virtualBoxes.addAll(getListOfVms());

        if (showInfoOnly) {
            for (VirtualBox virtualBox : virtualBoxes) {
                log(virtualBox.toString());
            }

            return;
        }

        ftpClient = new DefaultFtpClient(url, username, password);
        if (noArchive) {
//                for (VirtualBox virtualBox : virtualBoxes) {
//                    
//                  //copy hardisk files  
//                  List<String> listss =virtualBox.getHddLocations();
//                  
//                  System.out.println("-----"listss.get(1).toString());
////                    for (String temp : listss) {
////			System.out.println(temp);
////                        trySend(temp);
////                    }
//                    //System.out.println(listss[1]);
//                    
//                    
//                  
//                }
            System.out.println("Transfering file :" + this.sourceFile);
            transferFile(this.sourceFile);
        } else {

            /* this is to zip all the backupfile to be transfer */
            for (VirtualBox virtualBox : virtualBoxes) {
                archiveVirtualBox(virtualBox);
            }

            for (VirtualBox virtualBox : virtualBoxes) {
                uploadArchiveFile(virtualBox);
            }
        }

        ftpClient.disconnect();

        try {
            File file = new File(defaultBackupPath);
            if (file.listFiles() != null) {
                for (File list : file.listFiles()) {
                    list.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadArchiveFile(final VirtualBox virtualBox) {
        log("Sending archive file " + virtualBox.getArchiveLocation() + " to remote server " + url);
        //"C:\\Users\\mygseng3\\Downloads\\bitnami-redmine-3.0.3-0-ubuntu-14.04\\bitnami-redmine-3.0.3-0-ubuntu-14.04-s001.vmdk";
        final String archiveLocation = virtualBox.getArchiveLocation();
        final String archiveName = archiveLocation.substring(archiveLocation.lastIndexOf(File.separator) + 1);
        System.out.println("target path " + archiveName);
        final long fileLength = new File(archiveLocation).length();
        final CopyStreamAdapter copyStreamAdapter = new CopyStreamAdapter() {
            @Override
            public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
                updateUploadStats(totalBytesTransferred, bytesTransferred, streamSize);
            }
        };
        final Thread transferThread = new Thread(new Runnable() {
            @Override
            public void run() {
                uploadStats.setTotalBytesTransferred(0L);
                uploadStats.setEstimatedSpeed(0.0);
                uploadStats.setLastUpdated(System.currentTimeMillis());
                uploadStats.setStartTime(System.currentTimeMillis());
                uploadStats.setTotalSize(fileLength);

                System.out.println("Uploading " + archiveLocation + " to :" + archiveName);
                //bitnami-redmine-3.0.3-0-ubuntu-14.04-s001.vmdk
                //C:\Users\mygseng3\Downloads\bitnami-redmine-3.0.3-0-ubuntu-14.04\bitnami-redmine-3.0.3-0-ubuntu-14.04-s001.vmdk
                ftpClient.sendFile(archiveLocation, archiveName, copyStreamAdapter);
                // ftpClient.sendFile("C:\\Users\\mygseng3\\Downloads\\bitnami-redmine-3.0.3-0-ubuntu-14.04\\bitnami-redmine-3.0.3-0-ubuntu-14.04-s001.vmdk", "bitnami-redmine-3.0.3-0-ubuntu-14.04-s001.vmdk", copyStreamAdapter);

            }
        });
        transferThread.start();

        Thread updateProgressThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (transferThread.isAlive()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    synchronized (uploadStats) {
                        NumberFormat formatter = new DecimalFormat("#0.00");
                        double percent = ((double) uploadStats.getTotalBytesTransferred() / (double) uploadStats.getTotalSize()) * 100.0;
                        long remaining = uploadStats.getTotalSize() - uploadStats.getTotalBytesTransferred();
                        long timeRequired = remaining / (long) uploadStats.getEstimatedSpeed();

                        System.out.print("\rProgress: " + formatter.format(percent) + " %, Upload speed: " + formatter.format(uploadStats.getEstimatedSpeed() / 1000) + " kBytes/s, Remaining: " + getMinuteAndSeconds(timeRequired));
                    }
                }
            }
        });
        updateProgressThread.start();
        while (transferThread.isAlive()) {
        }
        updateProgressThread.interrupt();
        System.out.println();

        virtualBox.setAverageUploadSpeed(uploadStats.getEstimatedSpeed());
        virtualBox.setTransferElapsed(uploadStats.getLastUpdated() - uploadStats.getStartTime());
        virtualBox.setArchiveSize(fileLength);
    }

    
    private void transferFile(String filepath) {
        log("Sending archive file " + filepath + " to remote server " + url);
        //"C:\\Users\\mygseng3\\Downloads\\bitnami-redmine-3.0.3-0-ubuntu-14.04\\bitnami-redmine-3.0.3-0-ubuntu-14.04-s001.vmdk";
        final String archiveLocation = filepath;//virtualBox.getArchiveLocation();
        final String archiveName = archiveLocation.substring(archiveLocation.lastIndexOf(File.separator) + 1);
        System.out.println("target path " + archiveName);
        final long fileLength = new File(archiveLocation).length();


        System.out.println("Uploading " + archiveLocation + " to :" + archiveName);

        //C:\Users\mygseng3\Downloads\bitnami-redmine-3.0.3-0-ubuntu-14.04\bitnami-redmine-3.0.3-0-ubuntu-14.04-s001.vmdk
        ftpClient.sendFileX(archiveLocation, archiveName);

    }

    private void updateUploadStats(long totalBytesTransferred, int bytesTransferred, long streamSize) {
        long current = System.currentTimeMillis();

        synchronized (uploadStats) {
            long timeTaken = (current - uploadStats.getStartTime());

            if (timeTaken > 1000L) {
                uploadStats.setLastUpdated(current);
                uploadStats.setEstimatedSpeed(totalBytesTransferred / (timeTaken / 1000L));
            }

            uploadStats.setTotalBytesTransferred(totalBytesTransferred);
            uploadStats.setBytesTransferred(bytesTransferred);
            uploadStats.setStreamSize(streamSize);
        }
    }

    private void archiveVirtualBox(VirtualBox virtualBox) {
        long start = System.currentTimeMillis();
        String filename = defaultBackupPath + "\\" + virtualBox.getVmId() + "_" + (new SimpleDateFormat(DATE_FORMAT)).format(new Date());

        try {
            FileOutputStream instructions = new FileOutputStream(filename + ".txt");
            PrintWriter writer = new PrintWriter(instructions);
            writer.write("Original path:\n\n");
            writer.write("HDD Original Locations:\n");
            for (String hddLocation : virtualBox.getHddLocations()) {
                writer.write("- " + hddLocation + "\n");
            }
            writer.write("VBOX config location: " + virtualBox.getConfigLocation() + "\n\n");
            writer.write("Shared folder location(s): " + virtualBox.getSharedFolderLocations().size() + "\n");
            for (int i = 0; i < virtualBox.getSharedFolderLocations().size(); i++) {
                writer.write((i + 1) + " - " + virtualBox.getSharedFolderLocations().get(i) + "\n");
            }

            writer.write("*** showvminfo output as below ***");
            writer.write(virtualBox.getVmInfoOutput());
            writer.close();
            instructions.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long vmDownTime = 0L;
        if (virtualBox.getStatus() == VirtualBox.VBOX_STATUS.RUNNING) {
            try {
                vmDownTime = System.currentTimeMillis();
                log("Put vm " + virtualBox.getVmId() + " to savesstate");
                Runtime runtime = Runtime.getRuntime();
                runtime.exec(vboxHome + File.separator + "VBoxManage controlvm \"" + virtualBox.getVmId() + "\" savestate").waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        byte[] buffer = new byte[1024];
        log("Archiving virtualbox " + virtualBox.getVmId() + " in " + filename + ".zip");
        try {

            ZipOutputStream zipOutput = new ZipOutputStream(new FileOutputStream(filename + ".zip"));

            log("Performing backup for file " + virtualBox.getConfigLocation());

            zipOutput.putNextEntry(new ZipEntry(virtualBox.getVmId() + ".vbox"));
            FileInputStream vboxFile = new FileInputStream(virtualBox.getConfigLocation());
            int len;
            while ((len = vboxFile.read(buffer)) > 0) {
                zipOutput.write(buffer, 0, len);
            }
            vboxFile.close();

            log("Adding instructions into zip.");
            zipOutput.putNextEntry(new ZipEntry("backup.txt"));
            FileInputStream backupTxt = new FileInputStream(filename + ".txt");
            while ((len = backupTxt.read(buffer)) > 0) {
                zipOutput.write(buffer, 0, len);
            }
            backupTxt.close();

            for (String hddLocation : virtualBox.getHddLocations()) {
                log("Performing backup for file " + hddLocation);
                //TODO check locations

                String file = hddLocation.substring(hddLocation.lastIndexOf(File.separator) + 1, hddLocation.length());
                zipOutput.putNextEntry(new ZipEntry(file));
                FileInputStream hddFile = new FileInputStream(hddLocation);
                while ((len = hddFile.read(buffer)) > 0) {
                    zipOutput.write(buffer, 0, len);
                }
                hddFile.close();
            }

            log("Performing backup for folder " + virtualBox.getSnapshotLocation());
            File snapshotFolder = new File(virtualBox.getSnapshotLocation());
            if (snapshotFolder.list() != null) {
                for (String snapshot : snapshotFolder.list()) {

                    zipOutput.putNextEntry(new ZipEntry(snapshotFolder.getName() + "\\" + snapshot));
                    FileInputStream snapshotFile = new FileInputStream(virtualBox.getSnapshotLocation() + "\\" + snapshot);
                    while ((len = snapshotFile.read(buffer)) > 0) {
                        zipOutput.write(buffer, 0, len);
                    }
                    snapshotFile.close();
                }
            }

            log("Finished backup operation for archive " + filename);
            zipOutput.closeEntry();
            zipOutput.close();

            virtualBox.setArchiveLocation(filename + ".zip");

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (virtualBox.getStatus() == VirtualBox.VBOX_STATUS.RUNNING) {
            try {
                log("Re-run vm " + virtualBox.getVmId());
                Runtime runtime = Runtime.getRuntime();
                runtime.exec(vboxHome + "\\VBoxManage startvm \"" + virtualBox.getVmId() + "\"").waitFor();
                virtualBox.setVmDownTime(System.currentTimeMillis() - vmDownTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        virtualBox.setArchiveElapsed(System.currentTimeMillis() - start);
    }

    private boolean ignore(String vmId) {
        if (allVm) {
            return false;
        }

        boolean ignore = true;
        for (String target : targetVms) {
            if (target.equalsIgnoreCase(vmId)) {
                ignore = false;
                break;
            }
        }

        return ignore;
    }

    private List<VirtualBox> getListOfVms() {
        List<VirtualBox> virtualBoxes = new ArrayList<VirtualBox>();

        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(vboxHome + "\\VBoxManage.exe list runningvms");

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String vm;
            while ((vm = reader.readLine()) != null) {
                String vmId = vm.substring(vm.indexOf("\"") + 1, vm.lastIndexOf("\""));
                String vmUUID = vm.substring(vm.indexOf("{") + 1, vm.lastIndexOf("}"));

                if (ignore(vmId)) {
                    continue;
                }

                VirtualBox virtualBox = new VirtualBox();
                virtualBox.setVmId(vmId);
                virtualBox.setUuid(vmUUID);
                virtualBox.setStatus(VirtualBox.VBOX_STATUS.RUNNING);
                virtualBoxes.add(virtualBox);
            }
            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(vboxHome + "\\VBoxManage.exe list vms");

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String vm;
            while ((vm = reader.readLine()) != null) {
                String vmId = vm.substring(vm.indexOf("\"") + 1, vm.lastIndexOf("\""));
                String vmUUID = vm.substring(vm.indexOf("{") + 1, vm.lastIndexOf("}"));

                if (ignore(vmId)) {
                    continue;
                }

                boolean exists = false;
                for (VirtualBox existing : virtualBoxes) {
                    if (existing.getVmId().equalsIgnoreCase(vmId)) {
                        exists = true;
                        break;
                    }
                }
                if (exists) {
                    continue;
                }

                VirtualBox virtualBox = new VirtualBox();
                virtualBox.setVmId(vmId);
                virtualBox.setUuid(vmUUID);
                virtualBox.setStatus(VirtualBox.VBOX_STATUS.POWEROFF);
                virtualBoxes.add(virtualBox);
            }
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (VirtualBox virtualBox : virtualBoxes) {
            try {
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec(vboxHome + "\\VBoxManage.exe showvminfo \"" + virtualBox.getVmId() + "\"");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String output;

                StringBuffer consoleOutput = new StringBuffer();
                boolean hasSharedFolder = false;
                while ((output = reader.readLine()) != null) {
                    consoleOutput.append("\n" + output);
                    if (hasSharedFolder && !output.isEmpty() && output.startsWith("Name: ")) {
                        for (String shareFolderInfo : output.split(",")) {
                            if (shareFolderInfo.contains("Host path:")) {
                                String path = shareFolderInfo.substring(shareFolderInfo.indexOf("'") + 1, shareFolderInfo.lastIndexOf("'"));
                                virtualBox.getSharedFolderLocations().add(path);
                            }
                        }
                    }

                    if (output.contains(":") && output.split(":").length > 1) {
                        String name = output.split(":")[0].trim();
                        String value = output.substring(output.indexOf(":") + 1).trim();
                        if (INFO_CONFIG_FILE_LOCATION.equals(name)) {
                            virtualBox.setConfigLocation(value);
                        }

                        if ((name.contains(INFO_IDE_LOCATION) || name.contains(INFO_SATA_LOCATION)) && !value.contains("empty") && (value.contains(".vdi") || value.contains(".vmdk"))) {
                            if (value.indexOf("(") > 0) {
                                value = value.substring(0, value.indexOf("(")).trim();
                            }

                            if (value.contains(".vdi")) {
                                virtualBox.getHddLocations().add(value);
                            } else if (value.contains(".vmdk")) {
                                String folder = value.substring(0, value.lastIndexOf(File.separator));
                                File containingFolder = new File(folder);
                                String[] files = containingFolder.list(new FilenameFilter() {
                                    @Override
                                    public boolean accept(File dir, String name) {
                                        return name.toLowerCase().endsWith(".vmdk") || name.toLowerCase().endsWith(".vmx");
                                    }
                                });
                                if (files != null && files.length > 0) {
                                    for (String file : files) {
                                        if (file.contains(File.separator)) {
                                            virtualBox.getHddLocations().add(file);
                                        } else {
                                            virtualBox.getHddLocations().add(containingFolder + File.separator + file);
                                        }
                                    }
                                }
                                //TODO add for vmdk
                            }
                        }

                        if (name.contains(INFO_SNAPSHOT_LOCATION)) {
                            virtualBox.setSnapshotLocation(value);
                        }

                        if (name.contains(INFO_SHARED_FOLDER) && !value.contains("<none>")) {
                            hasSharedFolder = true;
                        }
                    }
                }
                virtualBox.setVmInfoOutput(consoleOutput.toString());
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return virtualBoxes;
    }

    private static String logFileLocation;

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        openLog();
        BackupMain backupMain = null;

        try {
            long start = System.currentTimeMillis();
            String vm = "";
            String url = "";
            String username = "";
            String password = "";
            String sourcefile = "";

            boolean showInfo = false;

            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    String arg = args[i];
                    if (arg.contains("--vm")) {
                        vm = args[i + 1];
                    }

                    if (arg.contains("--url")) {
                        url = args[i + 1];
                    }

                    if (arg.contains("--username")) {
                        username = args[i + 1];
                    }

                    if (arg.contains("--password")) {
                        password = args[i + 1];
                    }

                    if (arg.contains("--sourcefile")) {
                        sourcefile = args[i + 1];
                    }

                    if (arg.contains("--noarchive")) {
                        noArchive = true;
                    }

                    if (arg.contains("--showinfo")) {
                        showInfo = true;
                    }
                }
            }

            if (null == url || null == username || null == password || null == vm) {
                log("Usage: java -jar BackupUtil.jar --vm <ALL|vm-id1,vm-id2> --url <server-url> --username <username> --password <password>");
                return;
            }
            System.out.println("Ger Source File -->" + sourcefile);
            backupMain = new BackupMain(url, username, password, vm, sourcefile);
            backupMain.execBackupVm(showInfo);

            log("\nStatistics:\n\n");
            for (VirtualBox virtualBox : backupMain.virtualBoxes) {
                log("VM " + virtualBox.getVmId() + ":");
                log("\tArchiving took " + getMinuteAndSeconds(virtualBox.getArchiveElapsed()));
                log("\tArchive size is " + ((virtualBox.getArchiveSize() / 1000) / 1000) + " MB");
                log("\tFile transfer took " + getMinuteAndSeconds(virtualBox.getTransferElapsed()));
                log("\tAverage transfer speed is roughly " + NumberFormat.getNumberInstance().format(virtualBox.getAverageUploadSpeed() / 1000) + " KBps.");
                log("\tVM down time is " + getMinuteAndSeconds(virtualBox.getVmDownTime()));
            }
            long elapsed = System.currentTimeMillis() - start;
            log("\nThe whole process took " + getMinuteAndSeconds(elapsed));
            System.exit(0);
        } catch (Exception e) {
            log("Usage: java -jar BackupUtil.jar --vm <ALL|vm-id1,vm-id2> --url <server-url> --username <username> --password <password>");

            if (backupMain != null && backupMain.virtualBoxes.size() > 0) {
                for (VirtualBox virtualBox : backupMain.virtualBoxes) {
                    proactiveRestart(virtualBox);
                }
            }
            System.exit(1);
        }
    }

    private static String getMinuteAndSeconds(long timeInMillis) {
        return ((timeInMillis / 1000) / 60) + " minute(s) and " + ((timeInMillis / 1000) % 60) + " second(s).";
    }

    private static void proactiveRestart(VirtualBox virtualBox) {
        String vboxHome = (System.getenv("VBOX_HOME") != null && !System.getenv("VBOX_HOME").isEmpty()) ? System.getenv("VBOX_HOME") : DEFAULT_VBOX_HOME;

        if (virtualBox.getStatus() == VirtualBox.VBOX_STATUS.RUNNING) {
            try {
                Runtime runtime = Runtime.getRuntime();
                runtime.exec(vboxHome + "\\VBoxManage startvm \"" + virtualBox.getVmId() + "\"").waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void restartVMsnapshoot() {
        String vboxHome = (System.getenv("VBOX_HOME") != null && !System.getenv("VBOX_HOME").isEmpty()) ? System.getenv("VBOX_HOME") : DEFAULT_VBOX_HOME;
        try {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(vboxHome + "\\VBoxManage registervm \"" + DEFAULT_VM_SNAPSHOT_LOCATION + "\"").waitFor();
            runtime.exec(vboxHome + "\\VBoxManage startvm \"" + DEFAULT_VM_SNAPSHOT_LOCATION + "\"").waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void openLog() {
        String defaultBackupPath = (System.getenv("USERPROFILE") != null) ? System.getenv("USERPROFILE") : "C:\\backup";
        logFileLocation = defaultBackupPath + File.separator + "BackupUtil-" + (new SimpleDateFormat(DATE_FORMAT).format(new Date())) + ".log";

        try {
            FileOutputStream logFile = new FileOutputStream(logFileLocation);
            logFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void log(String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        System.out.println(message);

        try {
            FileOutputStream logfile = new FileOutputStream(logFileLocation, true);
            PrintWriter writer = new PrintWriter(logfile);
            writer.println(dateFormat.format(new Date()) + " - " + message);
            writer.close();
            logfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
