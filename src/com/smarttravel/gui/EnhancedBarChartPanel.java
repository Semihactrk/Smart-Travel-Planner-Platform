package com.smarttravel.gui;

import com.smarttravel.City;
import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class EnhancedBarChartPanel extends JPanel {
    private List<City> cities;
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 9);
    private static final Color BAR_COLOR = new Color(244, 67, 54);
    private static final Color GRID_COLOR = new Color(200, 200, 200);
    private DecimalFormat tempFormat = new DecimalFormat("0.0");

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
        int chartWidth = width - 2 * margin;
        int chartHeight = height - 80;

        // Draw title
        g2d.setFont(TITLE_FONT);
        g2d.setColor(new Color(244, 67, 54));
        g2d.drawString("Temperature Overview", 10, 25);

        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(margin, height - margin, width - margin, height - margin); // X-axis
        g2d.drawLine(margin, height - margin, margin, margin); // Y-axis

        // Find min and max temperatures
        double maxTemp = 50;
        double minTemp = -20;

        // Draw grid lines and labels
        g2d.setColor(GRID_COLOR);
        g2d.setStroke(new BasicStroke(1));
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 8));

        for (int i = 0; i <= 7; i++) {
            double tempValue = minTemp + (maxTemp - minTemp) * i / 7;
            int y = (int) (height - margin - (tempValue - minTemp) / (maxTemp - minTemp) * chartHeight);
            g2d.drawLine(margin - 5, y, width - margin, y);
            g2d.setColor(Color.BLACK);
            g2d.drawString(tempFormat.format(tempValue) + "°", 5, y + 4);
            g2d.setColor(GRID_COLOR);
        }

        // Draw bars
        int barWidth = chartWidth / cities.size();
        g2d.setColor(BAR_COLOR);

        for (int i = 0; i < cities.size(); i++) {
            City city = cities.get(i);
            double temp = city.getCurrentTemperature();
            
            // Clamp temperature to display range
            double clampedTemp = Math.max(minTemp, Math.min(maxTemp, temp));
            
            int barHeight = (int) ((clampedTemp - minTemp) / (maxTemp - minTemp) * chartHeight);
            int barX = margin + i * barWidth + 5;
            int barY = height - margin - barHeight;

            // Draw bar with gradient effect
            g2d.fillRect(barX, barY, barWidth - 10, barHeight);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(barX, barY, barWidth - 10, barHeight);

            // Draw temperature value on top
            g2d.setFont(LABEL_FONT);
            g2d.setColor(Color.BLACK);
            g2d.drawString(tempFormat.format(temp) + "°", barX + 2, barY - 5);

            // Draw city name below
            String cityName = city.getName();
            if (cityName.length() > 4) {
                cityName = cityName.substring(0, 4);
            }
            g2d.drawString(cityName, barX + 2, height - margin + 15);
            
            g2d.setColor(BAR_COLOR);
        }

        // Draw axis labels
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2d.drawString("Cities", width / 2 - 20, height - 5);
        g2d.rotate(-Math.PI / 2);
        g2d.drawString("Temperature (°C)", -height / 2, 15);
        g2d.rotate(Math.PI / 2);
    }
}
