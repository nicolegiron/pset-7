package com.apcsa.data;

public class QueryUtils {

    /////// QUERY CONSTANTS ///////////////////////////////////////////////////////////////
    
    /*
     * Determines if the default tables were correctly loaded.
     */
	
    public static final String SETUP_SQL =
        "SELECT COUNT(name) AS names FROM sqlite_master " +
            "WHERE type = 'table' " +
        "AND name NOT LIKE 'sqlite_%'";
    
    /*
     * Updates the last login timestamp each time a user logs into the system.
     */

    public static final String LOGIN_SQL =
        "SELECT * FROM users " +
            "WHERE username = ?" +
        "AND auth = ?";
    
    /*
     * Updates the last login timestamp each time a user logs into the system.
     */

    public static final String UPDATE_LAST_LOGIN_SQL =
        "UPDATE users " +
            "SET last_login = ? " +
        "WHERE username = ?";
    
    /*
     * Retrieves an administrator associated with a user account.
     */

    public static final String GET_ADMIN_SQL =
        "SELECT * FROM administrators " +
            "WHERE user_id = ?";
    
    /*
     * Retrieves a teacher associated with a user account.
     */

    public static final String GET_TEACHER_SQL =
        "SELECT * FROM teachers " +
            "WHERE user_id = ?";
    
    /*
     * Retrieves a student associated with a user account.
     */

    public static final String GET_STUDENT_SQL =
        "SELECT * FROM students " +
            "WHERE user_id = ?";
    
    public static final String UPDATE_STUDENT_PASSWORD = 
    		"UPDATE users " +
    	            "SET auth = ? " +
    	        "WHERE username = ?";
    
    public static final String GET_PASSWORD = 
    		"SELECT auth FROM users " +
    				"WHERE user_id = ?";
    
    public static final String GET_COURSE_ID =
    		"SELECT course_id FROM course_grades " +
    			"WHERE student_id = ?";
    
    public static final String GET_COURSE_NAME = 
    		"SELECT title FROM courses " + 
    				"WHERE course_id = ?";
    
    public static final String GET_COURSE_GRADE =
    		"SELECT grade FROM course_grades " +
    				"WHERE course_id = ?" +
    				"AND student_id = ?";
    
    public static final String GET_STUDENT_FIRSTNAME =
    		"SELECT first_name FROM students " +
    				"WHERE user_id = ?";
    
    public static final String GET_COURSE_NUMBER = 
    		"SELECT course_no FROM courses " +
    				"WHERE course_id = ?";
    
    public static final String GET_ASSIGNMENT_TITLE =
    		"SELECT title FROM assignments " +
    				"WHERE course_id = ?";
    
}