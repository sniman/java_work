package com.norirman.rest.service.dao;

import com.norirman.rest.service.model.Employee;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class EmployeeDao 
{
	
	public Employee getEmployeeDetails(String employeeId)
	{
            Employee emp = new Employee();
            try {
                
                Context initContext = new InitialContext();
                Context envContext = (Context) initContext.lookup("java:/comp/env");
                DataSource ds = (DataSource) envContext.lookup("jdbc/TestDB");
                Connection conn = ds.getConnection();

                String sql = "SELECT * FROM customer where customer_id=?";
                PreparedStatement ps = conn.prepareCall(sql);
                ps.setString(1,employeeId);
                ResultSet rd = ps.executeQuery();
                while (rd.next()) {
                   System.out.println("--" + rd.getString("email"));                   
                   emp.setDateOfJoining(rd.getString("fax"));
                   emp.setDepartment(rd.getString("discount_code"));
                   emp.setEmail(rd.getString("email"));
                   emp.setEmployeeId(rd.getString("customer_id"));
                   emp.setFirstName(rd.getString("name"));
                   emp.setLastName(rd.getString("city"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
		
		return emp;
	}
        
        public List<Employee> getAllEmployee(){
              List<Employee> emp = new ArrayList<Employee>();
            try {
                
                Context initContext = new InitialContext();
                Context envContext = (Context) initContext.lookup("java:/comp/env");
                DataSource ds = (DataSource) envContext.lookup("jdbc/TestDB");
                Connection conn = ds.getConnection();

                String sql = "SELECT * FROM customer ";
                PreparedStatement ps = conn.prepareCall(sql);
                ResultSet rd = ps.executeQuery();
                while (rd.next()) {
                   Employee eadd= new Employee();
                   System.out.println("--" + rd.getString("email"));                   
                   eadd.setDateOfJoining(rd.getString("fax"));
                   eadd.setDepartment(rd.getString("discount_code"));
                   eadd.setEmail(rd.getString("email"));
                   eadd.setEmployeeId(rd.getString("customer_id"));
                   eadd.setFirstName(rd.getString("name"));
                   eadd.setLastName(rd.getString("city"));
                   emp.add(eadd);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return emp;
        }
}
