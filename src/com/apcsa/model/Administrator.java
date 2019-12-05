package com.apcsa.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.apcsa.model.User;

public class Administrator extends User {

	public Administrator(User user, ResultSet rs) throws SQLException {
		super(rs);
	}
	
	private int administratorId;
    private String firstName;
    private String lastName;
    private String jobTitle;
    
}