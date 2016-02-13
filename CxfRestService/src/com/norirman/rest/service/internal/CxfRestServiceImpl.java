package com.norirman.rest.service.internal;

import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import com.norirman.rest.service.CxfRestService;
import com.norirman.rest.service.dao.ApplicationDao;
import com.norirman.rest.service.dao.EmployeeDao;
import com.norirman.rest.service.model.Employee;
import java.util.List;
import javax.ws.rs.core.GenericEntity;

public class CxfRestServiceImpl implements CxfRestService 
{
	@Autowired
	private EmployeeDao employeeDao; 
        
        @Autowired
        private ApplicationDao appDao;
       

	@Override
	public Response getEmployeeDetail(String employeeId) {
		if(employeeId == null){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}		
		return Response.ok(employeeDao.getEmployeeDetails(employeeId)).build();
	}

        @Override
        public Response getVersion() {
            return Response.ok(appDao.getVersion()).build();
        }

    @Override
    public Response getAllEmployee() {
       List<Employee> Employee=employeeDao.getAllEmployee();
       GenericEntity<List<Employee>> list = new GenericEntity<List<Employee>>(Employee) {
        };
       return Response.ok(list).build();
       //return Response.ok(new GenericEntity<List<String>>wmp).build();
    }


}
