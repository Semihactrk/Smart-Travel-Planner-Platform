package com.smarttravel.gui;

import com.smarttravel.City;
import com.smarttravel.WeatherState;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnhancedPieChartPanel extends JPanel {
    private List<City> cities;
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 11);

    private static final Color[] WEATHER_COLORS = {
            new Color(255, 193, 7),   // SUNNY - Yellow
            new Color(158, 158, 158), // CLOUDY - Gray
            new Color(33, 150, 243),  // RAINY - Blue
            new Color(144, 202, 249)  // SNOWY - Light Blue
    };

    public EnhancedPieChartPanel(List<City> cities) {
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

        // Count weather states
        Map<WeatherState, Integer> counts = new HashMap<>();
        for (WeatherState state : WeatherState.values()) {
            counts.put(state, 0);
        }
        for (City city : cities) {
            counts.put(city.getCurrentWeatherState(), counts.get(city.getCurrentWeatherState()) + 1);
        }

        // Draw title
        g2d.setFont(TITLE_FONT);
        g2d.setColor(new Color(25, 118, 210));
        g2d.drawString("Weather Distribution", 10, 25);

        int total = cities.size();
        int startAngle = 0;

        int pieSize = Math.min(width, height) - 120;
        int pieX = (width - pieSize) / 2;
        int pieY = 40 + (height - 80 - pieSize) / 2;

        // Draw pie slices
        g2d.setFont(LABEL_FONT);
        int colorIndex = 0;
        for (WeatherState state : WeatherState.values()) {
            int count = counts.get(state);
            if (count > 0) {
                int arcAngle = (int) Math.round((double) count / total * 360);

                g2d.setColor(WEATHER_COLORS[colorIndex % WEATHER_COLORS.length]);
                g2d.fillArc(pieX, pieY, pieSize, pieSize, startAngle, arcAngle);

                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawArc(pieX, pieY, pieSize, pieSize, startAngle, arcAngle);

                startAngle += arcAngle;
            }
            colorIndex++;
        }

        // Draw legend
        int legendX = 10;
        int legendY = pieY + pieSize + 20;
        colorIndex = 0;

        g2d.setFont(LABEL_FONT);
        for (WeatherState state : WeatherState.values()) {
            int count = counts.get(state);
            g2d.setColor(WEATHER_COLORS[colorIndex % WEATHER_COLORS.length]);
            g2d.fillRect(legendX, legendY, 12, 12);

            g2d.setColor(Color.BLACK);
            g2d.drawRect(legendX, legendY, 12, 12);
            g2d.drawString(state.name() + " (" + count + ")", legendX + 20, legendY + 10);

            legendY += 20;
            colorIndex++;
        }
    }
}
