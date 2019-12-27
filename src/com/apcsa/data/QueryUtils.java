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
    
    public static final String GET_COURSES =
    		"SELECT course_no FROM courses " +
    				"WHERE department_id = ?";
    
    public static final String GET_COURSE_ID_FROM_DEPARTMENT_ID = 
    		"SELECT course_id FROM courses " +
    				"WHERE course_no = ?";
    
    public static final String ADD_ASSIGNMENT = 
    		"INSERT INTO assignments " +
    	            "VALUES(?, ?, ?, ?, ?, ?, ?)";
    
    public static final String DELETE_ASSIGNMENT = 
    		"DELETE FROM assignments " +
    				"WHERE course_id = ?" +
    				"AND marking_period = ?" +
    				"AND title = ?";
    
    public static final String PREVIOUS_ASSIGNMENT_ID =
    		"SELECT count(*) FROM assignments ";
    
    public static final String GET_ASSIGNMENTS = 
    		"SELECT title FROM assignments " +
    				"WHERE course_id = ?" +
    				"AND marking_period = ?";
    
    public static final String GET_ASSIGNMENT_IDS =
    		"SELECT assignment_id FROM assignments ";
    
    public static final String GET_POINT_VALUE =
    		"SELECT point_value FROM assignments " +
    				"WHERE title = ?";
    
    public static final String GET_TEACHERS =
    		"SELECT first_name, last_name FROM teachers ";
    
    public static final String GET_DEPARTMENT_ID = 
    		"SELECT department_id FROM teachers " +
    			"WHERE first_name = ?";
    
    public static final String GET_DEPARTMENT_TITLE = 
    		"SELECT title FROM departments " +
    				"WHERE department_id = ?";
    
    public static final String GET_ALL_DEPARTMENT_TITLES =
    		"SELECT title FROM departments ";
    
    public static final String GET_TEACHERS_WITH_DEPARTMENT_ID =
    		"SELECT first_name, last_name FROM teachers " +
    				"WHERE department_id = ?";
    
    public static final String GET_STUDENTS =
    		"SELECT first_name, last_name, graduation FROM students ";
    
    public static final String GET_STUDENTS_BY_GRADE =
    		"SELECT first_name, last_name, gpa FROM students " +
    				"WHERE grade_level = ?";
    
    public static final String GET_ALL_COURSES = 
    		"SELECT course_no FROM courses ";
    
    public static final String GET_COURSE_ID_FROM_COURSE_NO =
    		"SELECT course_id FROM courses " +
    				"WHERE course_no = ?";
    
    public static final String GET_STUDENT_ID_FROM_COURSE_ID =
    		"SELECT student_id FROM course_grades " +
    				"WHERE course_id = ?";
    
    public static final String GET_STUDENTS_BY_STUDENT_ID = 
    		"SELECT first_name, last_name, gpa FROM students " +
    				"WHERE student_id = ?";
    
    public static final String UPDATE_PASSWORD_AND_TIME =
            "UPDATE users " +
                "SET auth = ? , last_login = ? " +
            "WHERE username = ?";
    
    public static final String GET_STUDENT_GRADE =
    		"SELECT grade FROM course_grades " +
    				"WHERE course_id = ? " +
    				"AND student_id = ?";
}