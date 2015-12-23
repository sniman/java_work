/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bt.dataextractor;

import com.bt.bean.DataBean;
import com.bt.util.db.HSQLDBManager;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author 608761624
 */
public class Runner {

    public static void main(String[] args) {

        String ff = "";
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("JComboBox Test");
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton button = new JButton("Select File");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {

                        System.out.println(selectedFile.getCanonicalPath().toString());
                        String infile = selectedFile.getCanonicalPath().toString();
                        //processing code
                        BufferedReader bufferreader = null;
                        String inputFile = "";

                        try {
                            String dataLine;
                            bufferreader = new BufferedReader(new FileReader(infile));

                            // How to read file in java line by line?
                            int row = 0;
                            List<DataBean> list = new ArrayList<DataBean>();
                            DataBean data = null;// new DataBean();
                            while ((dataLine = bufferreader.readLine()) != null) {
                                data = new DataBean();
                                if (row == 0) {
                                    //skip
                                } else {
                                    String[] param = dataLine.split(",");
                                    data.setBrowser(param[0]);
                                    data.setPagename(param[1]);
                                    data.setTotalviews(param[2]);
                                    list.add(data);
                                }

                                row++;
                            }

                            System.out.println("Total records " + list.size());

                            //save to hsqldb
                            HSQLDBManager manager = new HSQLDBManager();

                            //manager.droptempTable();
                            manager.init();
                            manager.droptempTable();
                            manager.droptempTableFinal();

                            manager.createTempTable();
                            try {
                                manager.insertRawData(list);

                                int ie11total = Integer.parseInt(manager.getPageViewByBrowser("internet explorer 11.0")) + Integer.parseInt(manager.getPageViewByBrowser("microsoft internet explorer 11"));
                                int amzonsilk = Integer.parseInt(manager.getPageViewByBrowser("amazon silk 3.68")) + Integer.parseInt(manager.getPageViewByBrowser("amazon silk 44.2.49")) + Integer.parseInt(manager.getPageViewByBrowser("amazon silk 46.2.33"));
                                int androidbrowser = Integer.parseInt(manager.getPageViewByBrowser("android browser 4.0"));
                                int blackberry = Integer.parseInt(manager.getPageViewByBrowser("blackberry browser")) + Integer.parseInt(manager.getPageViewByBrowser("blackberry browser 1.0.7")) + Integer.parseInt(manager.getPageViewByBrowser("blackberry browser 10.0"));
                                int mobilesafari = Integer.parseInt(manager.getPageViewByBrowser("mobile safari"));
                                int chromemobile = Integer.parseInt(manager.getPageViewByBrowser("chrome mobile"));
                                int edge = Integer.parseInt(manager.getPageViewByBrowser("microsoft edge"));
                                int googlechrome = Integer.parseInt(manager.getPageViewByBrowser("google chrome"));
                                int ie10total = Integer.parseInt(manager.getPageViewByBrowser("internet explorer mobile 10.0")) + Integer.parseInt(manager.getPageViewByBrowser("microsoft internet explorer 10"));
                                int ie9total = Integer.parseInt(manager.getPageViewByBrowser("internet explorer mobile 9")) + Integer.parseInt(manager.getPageViewByBrowser("microsoft internet explorer 9"));
                                int ie8total = Integer.parseInt(manager.getPageViewByBrowser("internet explorer mobile 8")) + Integer.parseInt(manager.getPageViewByBrowser("microsoft internet explorer 8"));
                                int ie7total = Integer.parseInt(manager.getPageViewByBrowser("internet explorer mobile 7")) + Integer.parseInt(manager.getPageViewByBrowser("microsoft internet explorer 7"));
                                int mozilafx = Integer.parseInt(manager.getPageViewByBrowser("mozilla firefox"));
                                int safari = Integer.parseInt(manager.getPageViewByBrowser("safari"));
                                int iemobile = Integer.parseInt(manager.getPageViewByBrowser("internet explorer mobile"));
                                int msn = Integer.parseInt(manager.getPageViewByBrowser("microsoft msn explorer"));
                                int allPageview = Integer.parseInt(manager.getTotalPageViews());

                                System.out.println("Total page views " + allPageview);
                                int others = allPageview - (ie11total + amzonsilk + blackberry + mobilesafari
                                        + chromemobile + edge + googlechrome + ie10total + ie9total + ie8total + ie7total + mozilafx + safari + iemobile + msn);

                                /*
                                 System.out.println("Internet Explorer 11--->" + ie11total);
                                 System.out.println("Amazon Silk--->" + amzonsilk);
                                 System.out.println("Blackberry browser--->" + blackberry);
                                 System.out.println("Chrome mobile--->" + chromemobile);
                                 System.out.println("Mobile Safari--->" + mobilesafari);
                                 System.out.println("Microfost Edge--->" + edge);
                                 System.out.println("google chrome--->" + googlechrome);
                                 System.out.println("Internet Explorer 10--->" + ie10total);
                                 System.out.println("Internet Explorer 9--->" + ie9total);
                                 System.out.println("Internet Explorer 8--->" + ie8total);
                                 System.out.println("Internet Explorer 7-->" + ie7total);
                                 System.out.println("Mozila firefox-->" + mozilafx);
                                 System.out.println("Safari-->" + safari);
                                 System.out.println("IE mobile-->" + iemobile);
                                 System.out.println("MSN-->" + msn);
                                 System.out.println("Other -->"+others);
                                 */
                                manager.insertDataFinal("Internet Explorer 11", ie11total);
                                manager.insertDataFinal("Amazon Silk", amzonsilk);
                                manager.insertDataFinal("Blackberry browser", blackberry);
                                manager.insertDataFinal("Mobile Safari", mobilesafari);
                                manager.insertDataFinal("Chrome Mobile", chromemobile);
                                manager.insertDataFinal("Edge", edge);
                                manager.insertDataFinal("Google Chrome", googlechrome);
                                manager.insertDataFinal("IE 10", ie10total);
                                manager.insertDataFinal("IE 9", ie9total);
                                manager.insertDataFinal("IE 8", ie8total);
                                manager.insertDataFinal("IE 7", ie7total);
                                manager.insertDataFinal("Mozila Firefox", mozilafx);
                                manager.insertDataFinal("Safari", safari);
                                manager.insertDataFinal("IE mobile", iemobile);
                                manager.insertDataFinal("MSN", msn);
                                manager.insertDataFinal("Others", others);

                                manager.summarizeData();

                            } catch (SQLException ex) {
                                Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } catch (IOException ex) {
                        Logger.getLogger(Runner.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        frame.add(button);
        frame.pack();
        frame.setVisible(true);

    }
    

}
