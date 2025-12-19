package edu.univ.erp.auth.session;

import java.util.Map;

public class AppState {
    private static Map<String, Object> currentUser;

    public static void setCurrentUser(Map<String, Object> user) {
        currentUser = user;
    }

    public static Map<String, Object> getCurrentUser() {
        return currentUser;
    }

    public static String getUsername() {
        if (currentUser != null) {
            return (String) currentUser.get("username");
        }
        return null;
    }

    public static String getRole() {
        if (currentUser != null) {
            return (String) currentUser.get("role");
        }
        return null;
    }

    public static void clear() {
        currentUser = null;
    }
}
