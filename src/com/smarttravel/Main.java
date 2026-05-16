package com.smarttravel;

import com.smarttravel.gui.TravelPlannerGUI;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

public class Main {
    public static void main(String[] args) {
        try {
            // Hata veren FlatLaf yerine, Java'nın gömülü modern teması olan Nimbus'u kullanıyoruz.
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            
            // Nimbus temasının renklerini "Modern Dashboard" (Beyaz ve açık gri) stiline uyarlıyoruz
            UIManager.put("control", new Color(245, 245, 245)); // Arka plan
            UIManager.put("info", new Color(255, 255, 255));
            UIManager.put("nimbusBase", new Color(225, 225, 225)); // Buton kenarlıkları
            UIManager.put("nimbusLightBackground", new Color(255, 255, 255)); // Paneller
            UIManager.put("nimbusSelectionBackground", new Color(70, 130, 180)); // Seçili öğe rengi
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13)); // Modern ve okunaklı font

        } catch (Exception e) {
            // Eğer bir sorun olursa işletim sisteminin kendi görünümüne dön
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Arayüzü Başlat
        SwingUtilities.invokeLater(() -> {
            TravelPlannerGUI gui = new TravelPlannerGUI();
            gui.setLocationRelativeTo(null); // Ekranın tam ortasında aç
            gui.setVisible(true);
        });
    }
}