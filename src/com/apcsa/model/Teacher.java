package com.apcsa.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.apcsa.model.User;

public class Teacher extends User {

	public Teacher(User user, ResultSet rs) throws SQLException {
		super(rs);
	}
	private int teacherId;
    private int departmentId;
    private String firstName;
    private String lastName;

}