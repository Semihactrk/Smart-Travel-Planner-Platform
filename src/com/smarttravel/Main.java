package com.smarttravel;

import com.smarttravel.gui.TravelPlannerGUI;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TravelPlannerGUI gui = new TravelPlannerGUI();
            gui.setLocationRelativeTo(null);
            gui.setVisible(true);
        });
    }
}
