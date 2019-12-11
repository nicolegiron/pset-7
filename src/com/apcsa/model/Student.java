package com.apcsa.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.apcsa.model.User;

public class Student extends User {
	private int studentId;
    private int classRank;
    private int gradeLevel;
    private int graduationYear;
    private double gpa;
    private String firstName;
    private String lastName;
    
    
    public Student(User user, ResultSet rs) {
    	super(user.getUserId(), user.getAccountType(), user.getUsername(), user.getPassword(), user.getLastLogin());
    }
    
    public int getStudentId() {
    	return studentId;
    }
    
    public int getClassRank() {
    	return classRank;
    }
    
    public int getGradeLevel() {
    	return gradeLevel;
    }
    
    public int getGraduationYear() {
    	return graduationYear;
    }
    
    public double getGpa() {
    	return gpa;
    }
    
    public String getFirstName() {
    	return firstName;
    }
    
    public String getLastName() {
    	return lastName;
    }
    
    
	
}