package edu.univ.erp;

import com.formdev.flatlaf.intellijthemes.FlatArcIJTheme;
import edu.univ.erp.ui.LoginWindow;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        try {
            FlatArcIJTheme.setup();
            System.out.println(" UI theme loaded: FlatArcIJTheme");
        } catch (Exception e) {
            System.err.println("⚠ Failed to apply FlatLaf theme: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> new LoginWindow().setVisible(true));

        System.out.println("=== Maintenance Service Test ===");
System.out.println("Initial flag: " + edu.univ.erp.service.MaintenanceService.isMaintenanceOn());

boolean result1 = edu.univ.erp.service.MaintenanceService.setMaintenance(true);
System.out.println("Set to true: " + result1);
System.out.println("After setting true: " + edu.univ.erp.service.MaintenanceService.isMaintenanceOn());

boolean result2 = edu.univ.erp.service.MaintenanceService.setMaintenance(false);
System.out.println("Set to false: " + result2);
System.out.println("After setting false: " + edu.univ.erp.service.MaintenanceService.isMaintenanceOn());
System.out.println("===============================");

    }
}
