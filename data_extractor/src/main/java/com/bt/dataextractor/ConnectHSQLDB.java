/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bt.dataextractor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author 608761624
 */
public class ConnectHSQLDB {

    public static void main(String[] args) {
        Connection connection = null;
        ResultSet resultSet = null;
        Statement statement = null;
        String createtablestr = "CREATE TABLE employeedetails (EMPNAME varchar(20),EMPID varchar(20));";
        String insertstr1 = "INSERT INTO employeedetails (EMPNAME,EMPID) VALUES ('EMPNAME1','1')";
        String insertstr2 = "INSERT INTO employeedetails (EMPNAME,EMPID) VALUES ('EMPNAME2','1212')";
        String insertstr3 = "INSERT INTO employeedetails (EMPNAME,EMPID) VALUES ('EMPNAME3','12121')";
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection("jdbc:hsqldb:file:C:/temphsqldb/java/march112011aDB", "SA", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (connection == null) {
            System.out.println(" connection null");
            return;
        }
        try {
            statement = connection.createStatement();
            statement.executeUpdate(createtablestr);

            statement.executeUpdate(insertstr1);

            statement.executeUpdate(insertstr2);
            statement.executeUpdate(insertstr3);

            resultSet = statement.executeQuery("SELECT * FROM EMPLOYEEDETAILS");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            while (resultSet.next()) {
                System.out.println("EMPLOYEE NAME:" + resultSet.getString("EMPNAME") +" ID :"+ resultSet.getString("EMPID") );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}

