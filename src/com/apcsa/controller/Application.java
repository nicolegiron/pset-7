package com.apcsa.controller;

import java.util.ArrayList;
import java.util.Scanner;
import com.apcsa.data.PowerSchool;
import com.apcsa.model.User;

enum RootAction { PASSWORD, DATABASE, LOGOUT, SHUTDOWN }

public class Application {

    private Scanner in;
    private User activeUser;

    /**
     * Creates an instance of the Application class, which is responsible for interacting
     * with the user via the command line interface.
     */

    public Application() {
        this.in = new Scanner(System.in);

        try {
            PowerSchool.initialize(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the PowerSchool application.
     */

    public void startup() {
        System.out.println("PowerSchool -- now for students, teachers, and school administrators!");

        // continuously prompt for login credentials and attempt to login
        while (true) {
            System.out.print("\nUsername: ");
            String username = in.next();

            System.out.print("Password: ");
            String password = in.next();

            // if login is successful, update generic user to administrator, teacher, or student
            try {
            if (login(username, password)) {
                activeUser = activeUser.isAdministrator()
                    ? PowerSchool.getAdministrator(activeUser) : activeUser.isTeacher()
                    ? PowerSchool.getTeacher(activeUser) : activeUser.isStudent()
                    ? PowerSchool.getStudent(activeUser) : activeUser.isRoot()
                    ? activeUser : null;

                if (isFirstLogin() && !activeUser.isRoot()) {
                    System.out.print("Input a new password: ");
                    String newPassword = in.next();
                    
                    changePass(username, newPassword);
                	// first-time users need to change their passwords from the default provided
                }
                
               
        		
                createAndShowUI();
                // create and show the user interface
                //
                // remember, the interface will be difference depending on the type
                // of user that is logged in (root, administrator, teacher, student)
            } else {
                System.out.println("\nInvalid username and/or password.");
            }
        } catch (Exception e) {
        	shutdown(e);
        }
        }
    }
    
    public void createAndShowUI() {
    	System.out.println("\nHello, again, " + PowerSchool.getFirstName(activeUser) + "!\n");
    	
    	if(activeUser.isRoot()) {
    		showRootUI();
    	} else if (activeUser.isStudent()) {
             while (activeUser != null) {
     			switch (studentSelection()) {
     			case 1: courseGrades(); break;
     			case 2: assignment(); break;
     			case 3: newPassword(); break;
//     			case 4: logout(); break;
     			default: System.out.println("\nInvalid selection. \n"); break;
     			}
             }
    	}
    }
    
    private void showRootUI() {
    	while (activeUser != null) {
//    		switch (getRootMenuSelection()) {
//            case PASSWORD: resetPassword(); break;
//            case DATABASE: factoryReset(); break;
//            case LOGOUT: logout(); break;
//            case SHUTDOWN: shutdown(); break;
//            default: System.out.println("\nInvalid selection."); break;
//        	}
    	}
    }
    
    private RootAction getRootMenuSelection() {
    	System.out.println();
    	
    	System.out.println("[1] Reset user password.");
        System.out.println("[2] Factory reset database.");
        System.out.println("[3] Logout.");
        System.out.println("[4] Shutdown.");
        System.out.print("\n::: ");
        
        switch (Utils.getInt(in, -1)) {
            case 1: return RootAction.PASSWORD;
            case 2: return RootAction.DATABASE;
            case 3: return RootAction.LOGOUT;
            case 4: return RootAction.SHUTDOWN;
            default: return null;
        }
    }
    
    public void newPassword() {
    	in.nextLine();
    	System.out.println("\nEnter current password: ");
    	String currentPassword = in.nextLine();
    	System.out.println("Enter new password: ");
    	String newPassword = in.nextLine();
    	
    	String truePassword = PowerSchool.getPassword(activeUser, currentPassword);
    	
    	if(!Utils.getHash(currentPassword).equals(truePassword)) {
    		System.out.println("\nInvalid current password.\n");
    	}else {
    		changePass(activeUser.getUsername(), newPassword);
    	}
    }
    
    public void assignment() {
    	System.out.println("Choose a course.");
    	ArrayList<Integer> courseIds = PowerSchool.getCourseId(activeUser);
    	ArrayList<String> courses = PowerSchool.getCourseNumber(activeUser, courseIds);
    	System.out.println("");
    	for(int i = 0; i <= courses.size()-1; i++) {
    		System.out.println("[" + (i + 1) + "] " + courses.get(i));
    	}
    	int courseSelection = in.nextInt();
    	
    	System.out.println("Choose a marking period or exam status.\n");
    	System.out.println("[1] MP1 assignment.");
    	System.out.println("[2] MP2 assignment.");
    	System.out.println("[3] MP3 assignment.");
    	System.out.println("[4] MP4 assignment.");
    	System.out.println("[5] Midterm exam.");
    	System.out.println("[6] Final exam.");
    	int assignmentSelection = in.nextInt();
    	ArrayList<String> grades = PowerSchool.getAssignmentTitle(assignmentSelection, courseIds);
    	System.out.println("");
    	for(int i = 0; i <= grades.size()-1; i++) {
    		System.out.println("[" + (i + 1) + "] " + grades.get(i));
    	}
    	
    }
    
    public void courseGrades() {
    	ArrayList<Integer> courseIds = PowerSchool.getCourseId(activeUser);
    	ArrayList<String> courses = PowerSchool.getCourseName(activeUser, courseIds);
    	ArrayList<String> courseGrades = PowerSchool.getCourseGrade(activeUser, courseIds);
    	System.out.println("");
    	for(int i = 0; i <= courses.size()-1; i++) {
    		System.out.println((i + 1) + ". " + courses.get(i) + " / " + courseGrades.get(i));
    	}
    	System.out.println("");
    }
    
    public void changePass(String username, String newPassword) {
    	PowerSchool.updatePassword(username, newPassword);
    	System.out.println("\nSuccessfully changed password.\n");
    }
    
    /**
     * Logs in with the provided credentials.
     *
     * @param username the username for the requested account
     * @param password the password for the requested account
     * @return true if the credentials were valid; false otherwise
     */

    public boolean login(String username, String password) {
        activeUser = PowerSchool.login(username, password);

        return activeUser != null;
    }

    /**
     * Determines whether or not the user has logged in before.
     *
     * @return true if the user has never logged in; false otherwise
     */

    public boolean isFirstLogin() {
        return activeUser.getLastLogin().equals("0000-00-00 00:00:00.000");
    }
    
    public int studentSelection() {
    	System.out.println("[1] View course grades.");
    	System.out.println("[2] View assignment grades by course.");
    	System.out.println("[3] Change password.");
    	System.out.println("[4] Logout.");
    	int selection = in.nextInt();
		return selection;
    }
    
    /*
     * Shuts down the application after encountering an error.
     * 
     * @param e the error that initiated the shutdown sequence
     */

    private void shutdown(Exception e) {
        if (in != null) {
            in.close();
        }
        
        System.out.println("Encountered unrecoverable error. Shutting down...\n");
        System.out.println(e.getMessage());
                
        System.out.println("\nGoodbye!");
        System.exit(0);
    }

    /*
     * Releases all resources and kills the application.
     */

    private void shutdown() {        
        System.out.println();
            
        if (Utils.confirm(in, "Are you sure? (y/n) ")) {
            if (in != null) {
                in.close();
            }
            
            System.out.println("\nGoodbye!");
            System.exit(0);
        }
    }
    

    /////// MAIN METHOD ///////////////////////////////////////////////////////////////////

    /*
     * Starts the PowerSchool application.
     *
     * @param args unused command line argument list
     */

    public static void main(String[] args) {
        Application app = new Application();

        app.startup();
    }
}