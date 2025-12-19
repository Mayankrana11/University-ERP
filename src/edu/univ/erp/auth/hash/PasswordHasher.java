package edu.univ.erp.auth.hash;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    //  Hash password
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(10));
    }

    //  Check password
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (Exception e) {
            System.err.println("Password check error: " + e.getMessage());
            return false;
        }
    }
}
