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
        int margin = 40;
        int chartHeight = height - 2 * margin;
        int chartWidth = width - 2 * margin;
        int barWidth = chartWidth / (cities.size() * 2);

        // Draw title
        g2d.setFont(TITLE_FONT);
        g2d.setColor(new Color(244, 67, 54));
        g2d.drawString("Temperature Chart (°C)", 10, 25);

        // Find max and min temperature for scaling
        double maxTemp = -Double.MAX_VALUE;
        double minTemp = Double.MAX_VALUE;
        for (City city : cities) {
            maxTemp = Math.max(maxTemp, city.getCurrentTemperature());
            minTemp = Math.min(minTemp, city.getCurrentTemperature());
        }
        if (maxTemp == minTemp) maxTemp = minTemp + 1;

        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(margin, height - margin, width - margin, height - margin); // X-axis
        g2d.drawLine(margin, margin, margin, height - margin); // Y-axis

        // Draw Y-axis labels
        g2d.setFont(LABEL_FONT);
        int ySteps = 5;
        for (int i = 0; i <= ySteps; i++) {
            double tempValue = minTemp + (maxTemp - minTemp) * i / ySteps;
            int y = height - margin - (int) ((tempValue - minTemp) / (maxTemp - minTemp) * chartHeight);
            g2d.drawString(String.format("%.0f", tempValue), margin - 35, y + 5);
            g2d.drawLine(margin - 5, y, margin, y);
        }

        // Draw bars
        int barX = margin + 10;
        for (int i = 0; i < cities.size(); i++) {
            City city = cities.get(i);
            double temp = city.getCurrentTemperature();
            int barHeight = (int) ((temp - minTemp) / (maxTemp - minTemp) * chartHeight);
            int barY = height - margin - barHeight;

            // Choose color based on temperature
            Color barColor;
            if (temp < 0) {
                barColor = new Color(144, 202, 249); // Light blue for cold
            } else if (temp < 15) {
                barColor = new Color(76, 175, 80); // Green for cool
            } else if (temp < 25) {
                barColor = new Color(255, 193, 7); // Yellow for moderate
            } else {
                barColor = new Color(244, 67, 54); // Red for hot
            }

            g2d.setColor(barColor);
            g2d.fillRect(barX, barY, barWidth, barHeight);

            g2d.setColor(Color.BLACK);
            g2d.drawRect(barX, barY, barWidth, barHeight);

            // Draw temperature label on top of bar
            g2d.drawString(String.format("%.1f", temp), barX - 5, barY - 5);

            // Draw city name label on X-axis
            String cityName = city.getName().length() > 5 ? city.getName().substring(0, 5) : city.getName();
            g2d.drawString(cityName, barX - 10, height - margin + 20);

            barX += barWidth * 2;
        }
    }
}
