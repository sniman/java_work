/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bt.util.db;

import com.bt.bean.DataBean;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 608761624
 */
public class HSQLDBManager {

    Connection connection = null;
    ResultSet resultSet = null;
    Statement statement = null;
    String createtablestr = "CREATE TABLE temp_rawdata (BROWSER varchar(255),PAGE_NAME varchar(255),PAGE_VIEW varchar(255));";
    String createtableFinalstr = "CREATE TABLE temp_finaldata (BROWSER varchar(255),TOTAL varchar(255));";
    String insertstr1 = "INSERT INTO temp_rawdata (BROWSER,PAGE_NAME,PAGE_VIEW) VALUES (?,?,?)";
    String insertstr2 = "INSERT INTO temp_finaldata (BROWSER,TOTAL) VALUES (?,?)";


    public void HSQLDBManager() {

    }

    public void init() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection("jdbc:hsqldb:file:C:/etractorData/java/omdb", "SA", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (connection == null) {
            System.out.println(" connection null");
            return;
        }
    }

    public void createTempTable() {
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(createtablestr);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            pstmt = connection.prepareStatement(createtableFinalstr);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void insertRawData(List<DataBean> databean) throws SQLException {
        PreparedStatement pstmt = null;
        //loop data in databean
        for (DataBean temp : databean) {
            pstmt = connection.prepareStatement(insertstr1);
            //System.out.println("###"+temp.getBrowser() +" - "+ temp.getPagename() +" - "+temp.getTotalviews());
            pstmt.setString(1, temp.getBrowser() == null ? "" : temp.getBrowser());
            pstmt.setString(2, temp.getPagename() == null ? "" : temp.getPagename());
            pstmt.setString(3, temp.getTotalviews() == null ? "" : temp.getTotalviews());
            pstmt.executeUpdate();
        }

    }

    public void insertDataFinal(String browser, int total) throws SQLException {
        PreparedStatement pstmt = null;
        pstmt = connection.prepareStatement(insertstr2);
        //System.out.println("###"+temp.getBrowser() +" - "+ temp.getPagename() +" - "+temp.getTotalviews());
        pstmt.setString(1, browser);
        pstmt.setString(2, "" + total);
        pstmt.executeUpdate();

    }

    public String getPageViewByBrowser(String browsername) throws SQLException {
        String ret = "";
        String sql = "select SUM(CAST(PAGE_VIEW AS INT)) as are from temp_rawdata where browser like '%" + browsername + "%'";
        PreparedStatement pstmt = null;
        pstmt = connection.prepareStatement(sql);
        //pstmt.setString(1,"'%"+browsername+"%'");
        ResultSet result = pstmt.executeQuery();
        while (result.next()) {
            ret = result.getString("are");
        }

        return ret == "" ? "0" : ret == null ? "0" : ret;

    }

    public String getTotalPageViews() throws SQLException {
        String ret = "";
        String sql = "select SUM(CAST(PAGE_VIEW AS INT)) as are from temp_rawdata";
        PreparedStatement pstmt = null;
        pstmt = connection.prepareStatement(sql);
        //pstmt.setString(1,"'%"+browsername+"%'");
        ResultSet result = pstmt.executeQuery();
        while (result.next()) {
            ret = result.getString("are");
        }
        return ret == "" ? "0" : ret == null ? "0" : ret;
    }


    public void droptempTable() {
        String sql = "DROP TABLE temp_rawdata ";
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void droptempTableFinal() {
        String sql = "DROP TABLE temp_finaldata ";
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(sql);
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showdata() throws SQLException {
        PreparedStatement pstmt = connection.prepareStatement("select * from temp_rawdata ");
        ResultSet result = pstmt.executeQuery();

        while (result.next()) {
            System.out.println(result.getString("BROWSER") + " - " + result.getString("PAGE_NAME") + " - " + result.getString("PAGE_VIEW"));
        }
    }

    public void showdatatest() throws SQLException {
        PreparedStatement pstmt = connection.prepareStatement("select count(browser) as a from temp_rawdata");
        ResultSet result = pstmt.executeQuery();

        while (result.next()) {
            System.out.println(result.getString("a") + " - ");
        }
    }

    public void summarizeData() {

        String sql = "select * from temp_finaldata order by CAST(TOTAL AS INT) desc";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet result = pstmt.executeQuery();
            FileWriter writer = new FileWriter("C:/etractorData/java/sum.csv");
            while (result.next()) {
                //System.out.println(result.getString("BROWSER") + " - " + result.getString("Total") );
                generateCSVoutput(writer, result.getString("BROWSER"), result.getString("Total"));

            }

	    //generate whatever data you want
            writer.flush();
            writer.close();
            System.out.println("Outfile created/updated at C:/etractorData/java/sum.csv");
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void generateCSVoutput(FileWriter writer, String browser, String total) throws IOException {
        writer.append(browser);
        writer.append(',');
        writer.append(total);
        writer.append('\n');

    }

}
