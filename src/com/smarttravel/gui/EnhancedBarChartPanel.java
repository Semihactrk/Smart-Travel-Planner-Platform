package com.smarttravel.gui;

import com.smarttravel.City;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EnhancedBarChartPanel extends JPanel {
    private List<City> cities;
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 10);

    public EnhancedBarChartPanel(List<City> cities) {
        this.cities = cities;
        setOpaque(false); // Kartın arka planının (beyaz) görünmesi için şeffaf yapıyoruz
    }

    public void updateData(List<City> cities) {
        this.cities = cities;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (cities == null || cities.isEmpty()) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 30;
        int graphHeight = height - 2 * padding;
        int graphWidth = width - 2 * padding;

        // Eksen Çizgileri
        g2d.setColor(new Color(200, 200, 200));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(padding, height - padding, width - padding, height - padding); // X ekseni

        double maxTemp = -100;
        double minTemp = 100;
        for (City city : cities) {
            maxTemp = Math.max(maxTemp, city.getCurrentTemperature());
            minTemp = Math.min(minTemp, city.getCurrentTemperature());
        }
        double tempRange = maxTemp - minTemp;
        if (tempRange == 0) tempRange = 10;

        int barWidth = Math.max(15, graphWidth / (cities.size() * 2));

        g2d.setFont(LABEL_FONT);
        for (int i = 0; i < cities.size(); i++) {
            City city = cities.get(i);
            double normalizedTemp = (city.getCurrentTemperature() - minTemp) / tempRange;
            int barHeight = (int) (normalizedTemp * (graphHeight - 20));

            int x = padding + (i * (graphWidth / cities.size())) + (graphWidth / cities.size() - barWidth) / 2;
            int y = height - padding - barHeight;

            // Modern Renkler
            Color barColor;
            if (city.getCurrentTemperature() > 25) barColor = new Color(239, 68, 68);      // Sıcak (Kırmızı)
            else if (city.getCurrentTemperature() > 15) barColor = new Color(250, 204, 21); // Ilık (Sarı)
            else if (city.getCurrentTemperature() > 0) barColor = new Color(56, 189, 248);  // Soğuk (Mavi)
            else barColor = new Color(224, 242, 254);                                       // Dondurucu (Açık Mavi)

            g2d.setColor(barColor);
            g2d.fillRoundRect(x, y, barWidth, barHeight, 6, 6); // Yuvarlak hatlı çubuklar

            g2d.setColor(new Color(30, 41, 59));
            g2d.drawString(String.format("%.1f", city.getCurrentTemperature()), x - 2, y - 5);

            String shortName = city.getName().substring(0, Math.min(4, city.getName().length()));
            g2d.drawString(shortName, x + barWidth / 2 - 12, height - padding + 15);
        }
    }
}