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
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font LEGEND_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    private static final Color[] WEATHER_COLORS = {
            new Color(250, 204, 21),   // SUNNY 
            new Color(148, 163, 184),  // CLOUDY 
            new Color(56, 189, 248),   // RAINY 
            new Color(224, 242, 254)   // SNOWY 
    };

    public EnhancedPieChartPanel(List<City> cities) {
        this.cities = cities;
        setOpaque(false);
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

        Map<WeatherState, Integer> counts = new HashMap<>();
        for (WeatherState state : WeatherState.values()) counts.put(state, 0);
        for (City city : cities) {
            counts.put(city.getCurrentWeatherState(), counts.get(city.getCurrentWeatherState()) + 1);
        }

        int total = cities.size();
        int width = getWidth();
        int height = getHeight();

        int pieSize = Math.min(width / 2, height - 30);
        int pieX = (width / 2 - pieSize) / 2;
        int pieY = (height - pieSize) / 2;

        int startAngle = 0;
        int colorIndex = 0;

        int legendX = width / 2 + 20;
        int legendY = (height - (WeatherState.values().length * 30)) / 2;

        for (WeatherState state : WeatherState.values()) {
            int count = counts.get(state);
            
            if (count > 0) {
                int arcAngle = (int) Math.round((double) count / total * 360);

                g2d.setColor(WEATHER_COLORS[colorIndex % WEATHER_COLORS.length]);
                g2d.fillArc(pieX, pieY, pieSize, pieSize, startAngle, arcAngle);

                double angle = Math.toRadians(startAngle + arcAngle / 2.0);
                int labelX = (int) (pieX + pieSize / 2 + Math.cos(angle) * (pieSize / 2.6));
                int labelY = (int) (pieY + pieSize / 2 - Math.sin(angle) * (pieSize / 2.6));

                int percentage = (int) Math.round((double) count / total * 100);
                String pctText = percentage + "%";

                g2d.setColor(Color.DARK_GRAY);
                g2d.setFont(LABEL_FONT);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(pctText, labelX - fm.stringWidth(pctText) / 2, labelY + fm.getAscent() / 2);

                startAngle += arcAngle;
            }

            g2d.setColor(WEATHER_COLORS[colorIndex % WEATHER_COLORS.length]);
            g2d.fillRoundRect(legendX, legendY, 15, 15, 4, 4);
            g2d.setColor(new Color(30, 41, 59));
            g2d.setFont(LEGEND_FONT);
            g2d.drawString(state.name() + " (" + count + ")", legendX + 25, legendY + 13);

            legendY += 30; 
            colorIndex++;
        }
    }
}