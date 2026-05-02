package com.smarttravel.gui;

import com.smarttravel.City;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BarChartPanel extends JPanel {
    private List<City> cities;

    public BarChartPanel(List<City> cities) {
        this.cities = cities;
        setPreferredSize(new Dimension(300, 200));
    }

    public void updateData(List<City> cities) {
        this.cities = cities;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (cities == null || cities.isEmpty())
            return;

        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        int barWidth = width / cities.size();

        double maxTemp = 50.0; // Assume max 50C for scaling

        for (int i = 0; i < cities.size(); i++) {
            City city = cities.get(i);
            int barHeight = (int) ((Math.max(0, city.getCurrentTemperature()) / maxTemp) * (height - 30));
            int x = i * barWidth;
            int y = height - barHeight - 20;

            g2d.setColor(new Color(255, 100, 100)); // Red for temp
            g2d.fillRect(x + 5, y, barWidth - 10, barHeight);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x + 5, y, barWidth - 10, barHeight);

            // Label
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString(String.format("%.1f", city.getCurrentTemperature()), x + 5, y - 5);
            g2d.drawString(city.getName().substring(0, Math.min(3, city.getName().length())), x + 5, height - 5);
        }
    }
}
