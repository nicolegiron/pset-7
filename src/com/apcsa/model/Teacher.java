package com.apcsa.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.apcsa.model.User;

public class Teacher extends User {

	private int teacherId;
    private int departmentId;
    private String firstName;
    private String lastName;
    private String departmentName;

    
    public Teacher(User user, ResultSet rs) throws SQLException {
        super(user);
        
        this.teacherId = rs.getInt("teacher_id");
        this.departmentId = rs.getInt("department_id");
        this.firstName = rs.getString("first_name");
        this.lastName = rs.getString("last_name");
        this.departmentId = rs.getInt("department_id");
    }
    
    public int getTeacherId() {
    	return teacherId;
    }
    
    public int getDepartmentId() {
    	return departmentId;
    }
    
    public String getFirstName() {
    	return firstName;
    }
    
    public String getLastName() {
    	return lastName;
    }
    
    public String getDepartmentName() {
    	return departmentName;
    }

    


}
