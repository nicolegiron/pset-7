package com.apcsa.controller;

import java.util.Scanner;
import com.apcsa.data.PowerSchool;
import com.apcsa.model.User;

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
                
                System.out.println("\nHello, again, " + activeUser.getUsername() + "!\n");
                
                boolean validLogin = true;
                while(validLogin) {
        			switch (studentSelection()) {
        			case 1: courseGrades(); break;
//        			case 2: assignment(); break;
//        			case 3: changePassword(); break;
//        			case 4: logout(); break;
        			default: System.out.println("\nInvalid selection. \n"); break;
        			}
        		}
                
                // create and show the user interface
                //
                // remember, the interface will be difference depending on the type
                // of user that is logged in (root, administrator, teacher, student)
            } else {
                System.out.println("\nInvalid username and/or password.");
            }
        }
    }
    public void courseGrades() {
    	int courseId = PowerSchool.getCourseId(activeUser);
    	System.out.println(courseId);
    }
    
    public void changePass(String username, String newPassword) {
    	PowerSchool.updatePassword(username, newPassword);
    	System.out.println("Successfully changed password.");
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