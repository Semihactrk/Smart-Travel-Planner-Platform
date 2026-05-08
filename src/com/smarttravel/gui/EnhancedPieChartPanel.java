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
    private static final Color[] COLORS = {
            new Color(255, 193, 7),   // SUNNY - Yellow
            new Color(158, 158, 158), // CLOUDY - Gray
            new Color(63, 81, 181),   // RAINY - Blue
            new Color(229, 229, 229)  // SNOWY - Light Gray
    };

    public EnhancedPieChartPanel(List<City> cities) {
        this.cities = cities;
        setPreferredSize(new Dimension(350, 250));
        setBackground(new Color(255, 255, 255));
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
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Map<WeatherState, Integer> counts = new HashMap<>();
        for (WeatherState state : WeatherState.values())
            counts.put(state, 0);
        for (City city : cities) {
            counts.put(city.getCurrentWeatherState(), counts.get(city.getCurrentWeatherState()) + 1);
        }

        int total = cities.size();
        int startAngle = 0;

        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height) - 100;
        int x = (width - size) / 2 + 20;
        int y = (height - size) / 2 - 10;

        // Draw pie slices
        int colorIdx = 0;
        for (WeatherState state : WeatherState.values()) {
            int count = counts.get(state);
            if (count == 0) {
                colorIdx++;
                continue;
            }
            
            int arcAngle = (int) Math.round((double) count / total * 360);

            // Draw slice with gradient
            g2d.setColor(COLORS[colorIdx % COLORS.length]);
            g2d.fillArc(x, y, size, size, startAngle, arcAngle);

            // Draw border
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawArc(x, y, size, size, startAngle, arcAngle);

            // Draw percentage label
            int middleAngle = startAngle + arcAngle / 2;
            double percentage = (double) count / total * 100;
            int labelRadius = size / 3;
            int labelX = x + size / 2 + (int) (Math.cos(Math.toRadians(middleAngle - 90)) * labelRadius);
            int labelY = y + size / 2 + (int) (Math.sin(Math.toRadians(middleAngle - 90)) * labelRadius);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            String percentText = String.format("%.0f%%", percentage);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(percentText, labelX - fm.stringWidth(percentText) / 2, labelY + fm.getAscent() / 2);

            startAngle += arcAngle;
            colorIdx++;
        }

        // Draw legend
        int legendX = x + size + 30;
        int legendY = y + 10;
        colorIdx = 0;
        for (WeatherState state : WeatherState.values()) {
            int count = counts.get(state);
            if (count == 0) {
                colorIdx++;
                continue;
            }

            g2d.setColor(COLORS[colorIdx % COLORS.length]);
            g2d.fillRect(legendX, legendY, 15, 15);

            g2d.setColor(new Color(33, 33, 33));
            g2d.setFont(new Font("Arial", Font.PLAIN, 11));
            g2d.drawString(getWeatherEmoji(state) + " " + state.name() + " (" + count + ")", legendX + 20, legendY + 12);

            legendY += 25;
            colorIdx++;
        }

        // Title
        g2d.setColor(new Color(33, 33, 33));
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        String title = "Weather Distribution";
        g2d.drawString(title, (width - fm.stringWidth(title)) / 2, 25);
    }

    private String getWeatherEmoji(WeatherState state) {
        switch (state) {
            case SUNNY:
                return "☀";
            case CLOUDY:
                return "☁";
            case RAINY:
                return "🌧";
            case SNOWY:
                return "❄";
            default:
                return "?";
        }
    }
}
