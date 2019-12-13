package com.apcsa.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.apcsa.model.User;

public class Teacher extends User {

	private int teacherId;
    private int departmentId;
    private String firstName;
    private String lastName;
<<<<<<< HEAD
    
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
    
    public int departmentId() {
    	return departmentId;
    }
    
    public String getFirstName() {
    	return firstName;
    }
    
    public String getLastName() {
    	return lastName;
    }
=======
    private String departmentName;
>>>>>>> a162d68ab58ec0748c29f2066d300967bc53b871

}
