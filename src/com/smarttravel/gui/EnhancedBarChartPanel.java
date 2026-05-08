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
        setPreferredSize(new Dimension(300, 250));
        setBackground(Color.WHITE);
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
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 40;
        int graphHeight = height - 2 * padding;
        int graphWidth = width - 2 * padding;

        // Draw title
        g2d.setFont(TITLE_FONT);
        g2d.setColor(new Color(25, 118, 210));
        g2d.drawString("City Temperatures (°C)", 10, 25);

        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(padding, height - padding, width - padding, height - padding); // X-axis
        g2d.drawLine(padding, padding, padding, height - padding); // Y-axis

        // Find max and min temperature for scaling
        double maxTemp = -100;
        double minTemp = 100;
        for (City city : cities) {
            maxTemp = Math.max(maxTemp, city.getCurrentTemperature());
            minTemp = Math.min(minTemp, city.getCurrentTemperature());
        }
        double tempRange = maxTemp - minTemp;
        if (tempRange == 0) tempRange = 10;

        int barWidth = Math.max(15, graphWidth / (cities.size() * 2));

        // Draw bars
        g2d.setFont(LABEL_FONT);
        for (int i = 0; i < cities.size(); i++) {
            City city = cities.get(i);
            double normalizedTemp = (city.getCurrentTemperature() - minTemp) / tempRange;
            int barHeight = (int) (normalizedTemp * (graphHeight - 20));

            int x = padding + (i * (graphWidth / cities.size())) + (graphWidth / cities.size() - barWidth) / 2;
            int y = height - padding - barHeight;

            // Color based on temperature
            Color barColor;
            if (city.getCurrentTemperature() > 25) {
                barColor = new Color(244, 67, 54); // Red for hot
            } else if (city.getCurrentTemperature() > 15) {
                barColor = new Color(255, 193, 7); // Yellow for warm
            } else if (city.getCurrentTemperature() > 0) {
                barColor = new Color(33, 150, 243); // Blue for cold
            } else {
                barColor = new Color(144, 202, 249); // Light blue for freezing
            }

            g2d.setColor(barColor);
            g2d.fillRect(x, y, barWidth, barHeight);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, barWidth, barHeight);

            // Draw temperature value
            g2d.setColor(Color.BLACK);
            g2d.drawString(String.format("%.1f", city.getCurrentTemperature()), x - 5, y - 5);

            // Draw city name
            String shortName = city.getName().substring(0, Math.min(3, city.getName().length()));
            g2d.drawString(shortName, x + barWidth / 2 - 10, height - padding + 15);
        }

        // Draw Y-axis scale
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        int scaleSteps = 5;
        for (int i = 0; i <= scaleSteps; i++) {
            double temp = minTemp + (tempRange / scaleSteps) * i;
            int y = height - padding - (int) ((temp - minTemp) / tempRange * (graphHeight - 20));
            g2d.drawString(String.format("%.0f", temp), padding - 35, y + 5);
            g2d.drawLine(padding - 5, y, padding, y);
        }
    }
}
