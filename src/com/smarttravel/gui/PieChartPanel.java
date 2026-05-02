package com.smarttravel.gui;

import com.smarttravel.City;
import com.smarttravel.WeatherState;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PieChartPanel extends JPanel {
    private List<City> cities;

    public PieChartPanel(List<City> cities) {
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
        int size = Math.min(width, height) - 40;
        int x = (width - size) / 2;
        int y = (height - size) / 2;

        Color[] colors = { Color.YELLOW, Color.LIGHT_GRAY, Color.GRAY, Color.CYAN };
        int cIdx = 0;

        int legendY = 20;

        for (WeatherState state : WeatherState.values()) {
            int count = counts.get(state);
            int arcAngle = (int) Math.round((double) count / total * 360);

            g2d.setColor(colors[cIdx % colors.length]);
            g2d.fillArc(x - 50, y, size, size, startAngle, arcAngle);

            // Legend
            g2d.fillRect(width - 100, legendY, 10, 10);
            g2d.setColor(Color.BLACK);
            g2d.drawString(state.name() + " (" + count + ")", width - 85, legendY + 10);
            legendY += 20;

            startAngle += arcAngle;
            cIdx++;
        }
    }
}
