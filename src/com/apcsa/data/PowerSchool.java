package com.apcsa.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import com.apcsa.controller.Utils;
import com.apcsa.model.Administrator;
import com.apcsa.model.Student;
import com.apcsa.model.Teacher;
import com.apcsa.model.User;

public class PowerSchool {

    private final static String PROTOCOL = "jdbc:sqlite:";
    private final static String DATABASE_URL = "data/powerschool.db";

    /**
     * Initializes the database if needed (or if requested).
     *
     * @param force whether or not to force-reset the database
     * @throws Exception
     */

    public static void initialize(boolean force) {
        if (force) {
            reset();    // force reset
        } else {
            boolean required = false;

            // check if all tables have been created and loaded in database

            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(QueryUtils.SETUP_SQL)) {

                while (rs.next()) {
                    if (rs.getInt("names") != 9) {
                        required = true;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // build database if needed

            if (required) {
                reset();
            }
        }
    }

    /**
     * Retrieves the User object associated with the requested login.
     *
     * @param username the username of the requested User
     * @param password the password of the requested User
     * @return the User object for valid logins; null for invalid logins
     */

    public static User login(String username, String password) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.LOGIN_SQL)) {

            stmt.setString(1, username);
            stmt.setString(2, Utils.getHash(password));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = new Timestamp(new Date().getTime());
                    int affected = PowerSchool.updateLastLogin(conn, username, ts);

                    if (affected != 1) {
                        System.err.println("Unable to update last login (affected rows: " + affected + ").");
                    }
                    return new User(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the administrator account associated with the user.
     *
     * @param user the user
     * @return the administrator account if it exists
     */

    public static User getAdministrator(User user) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_ADMIN_SQL)) {

            stmt.setInt(1, user.getUserId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Administrator(user, rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    /**
     * Returns the teacher account associated with the user.
     *
     * @param user the user
     * @return the teacher account if it exists
     */

    public static User getTeacher(User user) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_TEACHER_SQL)) {

            stmt.setInt(1, user.getUserId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Teacher(user, rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    /**
     * Returns the student account associated with the user.
     *
     * @param user the user
     * @return the student account if it exists
     */

    public static User getStudent(User user) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENT_SQL)) {

            stmt.setInt(1, user.getUserId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(user, rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    /*
     * Establishes a connection to the database.
     *
     * @return a database Connection object
     * @throws SQLException
     */

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(PROTOCOL + DATABASE_URL);
    }

    /*
     * Updates the last login time for the user.
     *
     * @param conn the current database connection
     * @param username the user's username
     * @param ts the current timestamp
     * @return the number of affected rows
     */

    private static int updateLastLogin(Connection conn, String username, Timestamp ts) {
        try (PreparedStatement stmt = conn.prepareStatement(QueryUtils.UPDATE_LAST_LOGIN_SQL)) {

            conn.setAutoCommit(false);
            stmt.setString(1, ts.toString());
            stmt.setString(2, username);

            if (stmt.executeUpdate() == 1) {
                conn.commit();
                return 1;
            } else {
                conn.rollback();

                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();

            return -1;
        }
    }

    /*
     * Builds the database. Executes a SQL script from a configuration file to
     * create the tables, setup the primary and foreign keys, and load sample data.
     */

    private static void reset() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             BufferedReader br = new BufferedReader(new FileReader(new File("config/setup.sql")))) {

            String line;
            StringBuffer sql = new StringBuffer();

            // read the configuration file line-by-line to get SQL commands

            while ((line = br.readLine()) != null) {
                sql.append(line);
            }

            // execute SQL commands one-by-one

            for (String command : sql.toString().split(";")) {
                if (!command.strip().isEmpty()) {
                    stmt.executeUpdate(command);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Unable to load SQL configuration file.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error: Unable to open and/or read SQL configuration file.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error: Unable to execute SQL script from configuration file.");
            e.printStackTrace();
        }
    }
    
    public static int updatePassword(String username, String newPassword) {
        try (Connection conn = getConnection();
        	 PreparedStatement stmt = conn.prepareStatement(QueryUtils.UPDATE_STUDENT_PASSWORD)) {

            conn.setAutoCommit(false);
            stmt.setString(1, Utils.getHash(newPassword));
            stmt.setString(2, username);

            if (stmt.executeUpdate() == 1) {
                conn.commit();
                return 1;
            } else {
                conn.rollback();
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();

            return -1;
        }
    }
    
    public static String getFirstName(User activeUser) {
    	try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENT_FIRSTNAME)) {

               stmt.setInt(1, activeUser.getUserId());

               try (ResultSet rs = stmt.executeQuery()) {
                   if (rs.next()) {
                       return rs.getString("first_name");
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }

           return "root";
    }
    
    public static ArrayList<Integer> getCourseId(User activeUser) {
    	ArrayList<Integer> resultList = new ArrayList<Integer>();
    	try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_COURSE_ID)) {
    		stmt.setInt(1, activeUser.getUserId());

    		try (ResultSet rs = stmt.executeQuery()) {
        	   while (rs.next()) {
        		   int result = rs.getInt("course_id");
        		   resultList.add(result);
        	   }
        	   return resultList;
        	   
    		}
    	} catch (SQLException e) {
    		e.printStackTrace();
        }
    	return resultList;
    }
    
    public static ArrayList<String> getCourseName(User activeUser, ArrayList<Integer> courseIds) {
    	ArrayList<String> courses = new ArrayList<String>();
     	try (Connection conn = getConnection();
     			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_COURSE_NAME)) {
     			for(int i = 0; i <= courseIds.size()-1; i++) {
     				stmt.setInt(1, courseIds.get(i));
     				
     				try (ResultSet rs = stmt.executeQuery()) {
     					while (rs.next()) {
                  		   String result = rs.getString("title");
                  		   courses.add(result);
     					}
     				}
     			}
     			return courses;
        } catch (SQLException e) {
            e.printStackTrace();
        }
     	return courses;
     }
    
    public static ArrayList<String> getCourseGrade(User activeUser, ArrayList<Integer> courseIds) {
    	ArrayList<String> courseGrades = new ArrayList<String>();
     	try (Connection conn = getConnection();
     			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_COURSE_GRADE)) {
     			for(int i = 0; i <= courseIds.size()-1; i++) {
     				stmt.setInt(1, courseIds.get(i));
     				stmt.setInt(2, activeUser.getUserId());
     				
     				try (ResultSet rs = stmt.executeQuery()) {
     					while (rs.next()) {
                  		   String result = rs.getString("grade");
                  		   if(result == null) {
                  			 courseGrades.add("--");
                  		   }else {
                  			 courseGrades.add(result);
                  		   }
     					}
     				}
     			}
     			return courseGrades;
        } catch (SQLException e) {
            e.printStackTrace();
        }
     	return courseGrades;
     }
    
    public static ArrayList<String> getCourseNumber(User activeUser, ArrayList<Integer> courseIds) {
    	ArrayList<String> courses = new ArrayList<String>();
     	try (Connection conn = getConnection();
     			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_COURSE_NUMBER)) {
     			for(int i = 0; i <= courseIds.size()-1; i++) {
     				stmt.setInt(1, courseIds.get(i));
     				
     				try (ResultSet rs = stmt.executeQuery()) {
     					while (rs.next()) {
                  		   String result = rs.getString("course_no");
                  		   courses.add(result);
     					}
     				}
     			}
     			return courses;
        } catch (SQLException e) {
            e.printStackTrace();
        }
     	return courses;
     }
    
    public static ArrayList<String> getAssignmentTitle(int assignmentSelection, ArrayList<Integer> courseIds) {
    	ArrayList<String> title = new ArrayList<String>();
     	try (Connection conn = getConnection();
     			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_ASSIGNMENT_TITLE)) {
     			for(int i = 0; i <= courseIds.size()-1; i++) {
     				stmt.setInt(1, courseIds.get(i));
     				
     				try (ResultSet rs = stmt.executeQuery()) {
     					while (rs.next()) {
                  		   String result = rs.getString("title");
                  		 title.add(result);
     					}
     				}
     			}
     			return title;
        } catch (SQLException e) {
            e.printStackTrace();
        }
     	return title;
     }
    
    public static String getPassword(User activeUser, String currentPassword) {
    	try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_PASSWORD)) {

               stmt.setInt(1, activeUser.getUserId());

               try (ResultSet rs = stmt.executeQuery()) {
                   if (rs.next()) {
                	   return rs.getString("auth");
                   }
                   
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }

    	return "other";
    }
    
    public static ArrayList<String> getCourses(int departmentId) {
    	ArrayList<String> courses = new ArrayList<String>();
     	try (Connection conn = getConnection();
     			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_COURSES)) {
     			stmt.setInt(1, departmentId);
     			
     			try (ResultSet rs = stmt.executeQuery()) {
     				while (rs.next()) {
                  	   String result = rs.getString("course_no");
                  	 courses.add(result);
     				}
     			}	
     		return courses;
        } catch (SQLException e) {
            e.printStackTrace();
        }
     	return courses;
     }
    
    public static int getCourseIdFromCourseNo(String courseNo){
    	try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_COURSE_ID_FROM_DEPARTMENT_ID)) {

               stmt.setString(1, courseNo);

               try (ResultSet rs = stmt.executeQuery()) {
                   if (rs.next()) {
                       return rs.getInt("course_id");
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }

           return 1;
    }
    
    public static int addAssignment(int courseId, int assignmentId, int markingPeriod, int isMidterm, int isFinal, String title, int pointValue) {
    	try (Connection conn = getConnection();
           	 PreparedStatement stmt = conn.prepareStatement(QueryUtils.ADD_ASSIGNMENT)) {
               
    		   conn.setAutoCommit(false);
               stmt.setInt(1, courseId);
               stmt.setInt(2, assignmentId);
               stmt.setInt(3, markingPeriod);
               stmt.setInt(4, isMidterm);
               stmt.setInt(5, isFinal);
               stmt.setString(6, title);
               stmt.setInt(7, pointValue);

               if (stmt.executeUpdate() == 1) {
                   conn.commit();
                   return 1;
               } else {
                   conn.rollback();
                   return -1;
               }
           } catch (SQLException e) {
               return -1;
           }
    }
    
    public static int assignmentRows() {
    	try (Connection conn = getConnection();
    			PreparedStatement stmt = conn.prepareStatement(QueryUtils.PREVIOUS_ASSIGNMENT_ID)) {
                  
    		try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count(*)");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 1;
    }
    
    public static ArrayList<String> getAssignments(int courseId, int markingPeriod) {
    	ArrayList<String> assignments = new ArrayList<String>();
    	try (Connection conn = getConnection();
     			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_ASSIGNMENTS)) {
     			stmt.setInt(1, courseId);
     			stmt.setInt(2, markingPeriod);
     			
     			try (ResultSet rs = stmt.executeQuery()) {
     				while (rs.next()) {
                  	   String result = rs.getString("title");
                  	 assignments.add(result);
     				}
     			}	
     		return assignments;
        } catch (SQLException e) {
            e.printStackTrace();
        }
     	return assignments;
    }
    
    public static int deleteAssignment(int courseId, int markingPeriod, String title) {
    	try (Connection conn = getConnection();
           	 PreparedStatement stmt = conn.prepareStatement(QueryUtils.DELETE_ASSIGNMENT)) {
               
    		   conn.setAutoCommit(false);
               stmt.setInt(1, courseId);
               stmt.setInt(2, markingPeriod);
               stmt.setString(3, title);

               if (stmt.executeUpdate() == 1) {
                   conn.commit();
                   return 1;
               } else {
                   conn.rollback();
                   return -1;
               }
           } catch (SQLException e) {
               return -1;
           }
    }
    
    public static int getPointValue(String title) {
    	try (Connection conn = getConnection();
    			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_POINT_VALUE)) {
                  
    		stmt.setString(1, title);
    		
    		try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("point_value");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 1;
    }
    
    public static ArrayList<String> getAssignmentIds() {
    	ArrayList<String> assignmentIds = new ArrayList<String>();
    	try (Connection conn = getConnection();
     			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_ASSIGNMENT_IDS)) {
     			
     			try (ResultSet rs = stmt.executeQuery()) {
     				while (rs.next()) {
                  	   String result = rs.getString("assignment_id");
                  	 assignmentIds.add(result);
     				}
     			}	
     		return assignmentIds;
        } catch (SQLException e) {
            e.printStackTrace();
        }
     	return assignmentIds;
    }
    
    public static ArrayList<String> getTeachers() {
    	ArrayList<String> teachers = new ArrayList<String>();
    	try (Connection conn = getConnection();
     			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_TEACHERS)) {
     			
     			try (ResultSet rs = stmt.executeQuery()) {
     				while (rs.next()) {
                  	   String result = rs.getString("first_name");
                  	   String result2 = rs.getString("last_name");
                  	 teachers.add(result);
                  	teachers.add(result2);
     				}
     			}	
     		return teachers;
        } catch (SQLException e) {
            e.printStackTrace();
        }
     	return teachers;
    }
    
    public static String getDepartmentId(String firstName) {
    	try (Connection conn = getConnection();
     			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_DEPARTMENT_ID)) {
     			
    			stmt.setString(1, firstName);
     			try (ResultSet rs = stmt.executeQuery()) {
     				while (rs.next()) {
                  	   return rs.getString("department_id");
     				}
     			}	
     			return "not working";
        } catch (SQLException e) {
            e.printStackTrace();
        }
    	return "not working";
    }
    
    public static String getDepartmentTitle(String departmentId) {
    	try (Connection conn = getConnection();
     			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_DEPARTMENT_TITLE)) {
     			
    			stmt.setString(1, departmentId);
     			try (ResultSet rs = stmt.executeQuery()) {
     				while (rs.next()) {
     					return rs.getString("title");
     				}
     			}	
     			return "not working";
        } catch (SQLException e) {
            e.printStackTrace();
        }
		return "not working";
    }
    
    public static ArrayList<String> getAllDepartmentTitles() {
    	ArrayList<String> departmentTitles = new ArrayList<String>();
    	try (Connection conn = getConnection();
     			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_ALL_DEPARTMENT_TITLES)) {
     			
     			try (ResultSet rs = stmt.executeQuery()) {
     				while (rs.next()) {
                  	   String result = rs.getString("title");
                  	 departmentTitles.add(result);
     				}
     			}	
     		return departmentTitles;
        } catch (SQLException e) {
            e.printStackTrace();
        }
     	return departmentTitles;
    }
    
    public static ArrayList<String> getTeachersWithDepartmentId(int department) {
    	ArrayList<String> teachers = new ArrayList<String>();
    	try (Connection conn = getConnection();
     			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_TEACHERS_WITH_DEPARTMENT_ID)) {
     			
    			stmt.setInt(1, department);
    			
     			try (ResultSet rs = stmt.executeQuery()) {
     				while (rs.next()) {
                  	   String result = rs.getString("first_name");
                  	   String result2 = rs.getString("last_name");
                  	   teachers.add(result);
                  	   teachers.add(result2);
     				}
     			}	
     		return teachers;
        } catch (SQLException e) {
            e.printStackTrace();
        }
     	return teachers;
    }
    
    public static ArrayList<String> getStudents() {
    	ArrayList<String> students = new ArrayList<String>();
    	try (Connection conn = getConnection();
     			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENTS)) {
     			
     			try (ResultSet rs = stmt.executeQuery()) {
     				while (rs.next()) {
                  	   String result = rs.getString("first_name");
                  	   String result2 = rs.getString("last_name");
                  	   students.add(result);
                  	   students.add(result2);
     				}
     			}	
     		return students;
        } catch (SQLException e) {
            e.printStackTrace();
        }
     	return students;
    }
    
    public static ArrayList<String> getGradYears() {
    	ArrayList<String> gradYears = new ArrayList<String>();
    	try (Connection conn = getConnection();
     			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENTS)) {
     			
     			try (ResultSet rs = stmt.executeQuery()) {
     				while (rs.next()) {
                  	   gradYears.add(rs.getString("graduation"));
     				}
     			}	
     		return gradYears;
        } catch (SQLException e) {
            e.printStackTrace();
        }
     	return gradYears;
    }
    
    public static ArrayList<String> getStudentsByGrade(int gradeLevel) {
    	ArrayList<String> students = new ArrayList<String>();
    	try (Connection conn = getConnection();
     			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENTS_BY_GRADE)) {
     			
    			stmt.setInt(1,  gradeLevel);
     			try (ResultSet rs = stmt.executeQuery()) {
     				while (rs.next()) {
                  	   students.add(rs.getString("first_name"));
                  	   students.add(rs.getString("last_name"));
                  	   students.add(rs.getString("gpa"));
     				}
     			}	
     		return students;
        } catch (SQLException e) {
            e.printStackTrace();
        }
     	return students;
    }
    
    public static ArrayList<String> getAllCourses() {
    	ArrayList<String> courses = new ArrayList<String>();
    	try (Connection conn = getConnection();
     			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_ALL_COURSES)) {
     			
     			try (ResultSet rs = stmt.executeQuery()) {
     				while (rs.next()) {
     					courses.add(rs.getString("course_no"));
     				}
     			}	
     		return courses;
        } catch (SQLException e) {
            e.printStackTrace();
        }
     	return courses;
    }
    
    public static String getCourseIdFromCourseNo2(String courseNo) {
    	try (Connection conn = getConnection();
     			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_COURSE_ID_FROM_COURSE_NO)) {
     			stmt.setString(1,  courseNo);
     			try (ResultSet rs = stmt.executeQuery()) {
     				while (rs.next()) {
     					return rs.getString("course_id");
     				}
     			}	
     		return "no";
        } catch (SQLException e) {
            e.printStackTrace();
        }
     	return "no";
    }
    
    public static ArrayList<String> getStudentId(String courseId) {
    	ArrayList<String> studentIds = new ArrayList<String>();
    	try (Connection conn = getConnection();
     			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENT_ID_FROM_COURSE_ID)) {
     			
    			stmt.setString(1, courseId);
     			try (ResultSet rs = stmt.executeQuery()) {
     				while (rs.next()) {
     					studentIds.add(rs.getString("student_id"));
     				}
     			}	
     		return studentIds;
        } catch (SQLException e) {
            e.printStackTrace();
        }
     	return studentIds;
    }
    
    public static ArrayList<String> getStudentsByStudentId(String studentIds) {
    	ArrayList<String> students = new ArrayList<String>();
    	try (Connection conn = getConnection();
     			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENTS_BY_STUDENT_ID)) {
     			
    			stmt.setString(1,  studentIds);
     			try (ResultSet rs = stmt.executeQuery()) {
     				while (rs.next()) {
                  	   students.add(rs.getString("first_name"));
                  	   students.add(rs.getString("last_name"));
                  	   students.add(rs.getString("gpa"));
     				}
     			}	
     		return students;
        } catch (SQLException e) {
            e.printStackTrace();
        }
     	return students;
    }
    
    public static int updatePasswordAndTime(String username) {
        try (Connection conn = getConnection();
        	 PreparedStatement stmt = conn.prepareStatement(QueryUtils.UPDATE_PASSWORD_AND_TIME)) {

            conn.setAutoCommit(false);
            stmt.setString(1, Utils.getHash(username));
            stmt.setString(2, "0000-00-00 00:00:00.000");
            stmt.setString(3, username);

            if (stmt.executeUpdate() == 1) {
                conn.commit();
                return 1;
            } else {
            	System.out.println("Here");
                conn.rollback();
                return -1;
            }
        } catch (SQLException e) {
        	System.out.println("Here 2");
            e.printStackTrace();

            return -1;
        }
    }
}
