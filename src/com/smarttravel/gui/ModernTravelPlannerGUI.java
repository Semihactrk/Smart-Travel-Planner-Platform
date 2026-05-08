package com.smarttravel.gui;

import com.smarttravel.City;
import com.smarttravel.WeatherState;
import com.smarttravel.command.*;
import com.smarttravel.composite.*;
import com.smarttravel.decorator.*;
import com.smarttravel.iterator.WeatherCityIterator;
import com.smarttravel.observer.WeatherObserver;
import com.smarttravel.observer.WeatherReportProvider;
import com.smarttravel.repository.CityRepository;
import com.smarttravel.strategy.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.text.DecimalFormat;
import java.util.*;

public class ModernTravelPlannerGUI extends JFrame implements WeatherObserver {
    private static final Color PRIMARY_COLOR = new Color(25, 118, 210);
    private static final Color SECONDARY_COLOR = new Color(244, 67, 54);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    private CityRepository repository;
    private CommandManager commandManager;
    private WeatherReportProvider weatherProvider;

    private DefaultListModel<City> allCitiesModel;
    private JList<City> allCitiesList;
    private DefaultListModel<City> weatherFilteredModel;
    private JList<City> weatherFilteredList;

    private JComboBox<String> sortComboBox;
    private JComboBox<String> weatherComboBox;

    private BarChartPanel barChartPanel;
    private PieChartPanel pieChartPanel;

    private JTree planTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;

    private JLabel budgetLabel;
    private JLabel durationLabel;
    private JLabel selectedCityLabel;

    private ActivityPlan rootActivityPlan;
    private Map<City, ActivityPlan> cityPlans;
    private City currentSelectedCity;

    private DecimalFormat moneyFormat = new DecimalFormat("$#,##0.00");
    private DecimalFormat timeFormat = new DecimalFormat("0.##");

    public ModernTravelPlannerGUI() {
        repository = CityRepository.getInstance();
        commandManager = new CommandManager();
        cityPlans = new HashMap<>();
        rootActivityPlan = new ActivityPlan("My Travel Plan");

        setTitle("✈ Smart Travel Planner Platform");
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        initComponents();

        weatherProvider = new WeatherReportProvider(repository.getCities());
        weatherProvider.addObserver(this);
        new Thread(weatherProvider).start();

        updateAllCitiesList(new SortByName());
        updateWeatherFilter();
    }

    private void initComponents() {
        // TOP PANEL - Control Section
        add(createControlPanel(), BorderLayout.NORTH);

        // CENTER PANEL - Main Content
        add(createCenterPanel(), BorderLayout.CENTER);

        // BOTTOM PANEL - Info Section
        add(createInfoPanel(), BorderLayout.SOUTH);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Left side - Sorting and Filtering
        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftControls.setOpaque(false);

        JLabel sortLabel = new JLabel("📊 Sort:");
        sortLabel.setFont(LABEL_FONT);
        sortLabel.setForeground(Color.WHITE);

        sortComboBox = new JComboBox<>(new String[]{"Sort by Name", "Sort by Population", "Sort by Area"});
        sortComboBox.setFont(LABEL_FONT);
        sortComboBox.addActionListener(e -> applySort());
        customizeComboBox(sortComboBox);

        JLabel filterLabel = new JLabel("🌤 Weather Filter:");
        filterLabel.setFont(LABEL_FONT);
        filterLabel.setForeground(Color.WHITE);

        weatherComboBox = new JComboBox<>(new String[]{"ALL", "☀ SUNNY", "☁ CLOUDY", "🌧 RAINY", "❄ SNOWY"});
        weatherComboBox.setFont(LABEL_FONT);
        weatherComboBox.addActionListener(e -> applyWeatherFilter());
        customizeComboBox(weatherComboBox);

        leftControls.add(sortLabel);
        leftControls.add(sortComboBox);
        leftControls.add(filterLabel);
        leftControls.add(weatherComboBox);

        // Right side - Undo/Redo
        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightControls.setOpaque(false);

        JButton btnUndo = createStyledButton("↶ Undo", new Color(76, 175, 80));
        btnUndo.addActionListener(e -> {
            commandManager.undo();
            updateTree();
        });

        JButton btnRedo = createStyledButton("↷ Redo", new Color(76, 175, 80));
        btnRedo.addActionListener(e -> {
            commandManager.redo();
            updateTree();
        });

        rightControls.add(btnUndo);
        rightControls.add(btnRedo);

        panel.add(leftControls, BorderLayout.WEST);
        panel.add(rightControls, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel mainPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // 1. All Cities List
        allCitiesModel = new DefaultListModel<>();
        allCitiesList = new JList<>(allCitiesModel);
        allCitiesList.setFont(LABEL_FONT);
        allCitiesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        allCitiesList.addListSelectionListener(e -> onCitySelected());
        JScrollPane citiesScroll = new JScrollPane(allCitiesList);
        mainPanel.add(createTitledPanel("🌍 All Cities", citiesScroll, new Color(33, 150, 243)));

        // 2. Weather Filtered Cities
        weatherFilteredModel = new DefaultListModel<>();
        weatherFilteredList = new JList<>(weatherFilteredModel);
        weatherFilteredList.setFont(LABEL_FONT);
        weatherFilteredList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        weatherFilteredList.addListSelectionListener(e -> {
            City selected = weatherFilteredList.getSelectedValue();
            if (selected != null) {
                allCitiesList.setSelectedValue(selected, true);
            }
        });
        JScrollPane filteredScroll = new JScrollPane(weatherFilteredList);
        mainPanel.add(createTitledPanel("🔍 Filtered Cities", filteredScroll, new Color(156, 39, 176)));

        // 3. Activity Planner Panel
        JPanel plannerPanel = createActivityPlannerPanel();
        mainPanel.add(createTitledPanel("🎯 Activity Planner", plannerPanel, new Color(255, 152, 0)));

        // 4. Travel Plan Tree
        rootNode = new DefaultMutableTreeNode("📋 My Travel Plan");
        treeModel = new DefaultTreeModel(rootNode);
        planTree = new JTree(treeModel);
        planTree.setFont(LABEL_FONT);
        planTree.setRowHeight(25);
        JScrollPane treeScroll = new JScrollPane(planTree);
        mainPanel.add(createTitledPanel("🗺 Travel Plan", treeScroll, new Color(76, 175, 80)));

        // 5. Temperature Bar Chart
        barChartPanel = new EnhancedBarChartPanel(repository.getCities());
        mainPanel.add(createTitledPanel("🌡 Temperature Chart", barChartPanel, new Color(244, 67, 54)));

        // 6. Weather Distribution Pie Chart
        pieChartPanel = new EnhancedPieChartPanel(repository.getCities());
        mainPanel.add(createTitledPanel("📈 Weather Distribution", pieChartPanel, new Color(103, 58, 183)));

        return mainPanel;
    }

    private JPanel createActivityPlannerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Activity buttons in a grid
        JPanel buttonsPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        buttonsPanel.setBackground(Color.WHITE);

        JButton btnMuseum = createActivityButton("🏛 Museum", new Color(63, 81, 181));
        btnMuseum.addActionListener(e -> addActivity(new MuseumVisit(new BaseCityActivity(currentSelectedCity))));

        JButton btnMall = createActivityButton("🛍 Shopping Mall", new Color(63, 81, 181));
        btnMall.addActionListener(e -> addActivity(new ShoppingMallVisit(new BaseCityActivity(currentSelectedCity))));

        JButton btnPark = createActivityButton("🌳 Park", new Color(63, 81, 181));
        btnPark.addActionListener(e -> addActivity(new ParkVisit(new BaseCityActivity(currentSelectedCity))));

        JButton btnCenter = createActivityButton("🏰 City Center", new Color(63, 81, 181));
        btnCenter.addActionListener(e -> addActivity(new CityCenterVisit(new BaseCityActivity(currentSelectedCity))));

        JButton btnCinema = createActivityButton("🎬 Cinema", new Color(63, 81, 181));
        btnCinema.addActionListener(e -> addActivity(new ActivityLeaf("Cinema Visit", 15.0, 3.0)));

        JButton btnDinner = createActivityButton("🍽 Dinner", new Color(63, 81, 181));
        btnDinner.addActionListener(e -> addActivity(new ActivityLeaf("Dinner", 45.0, 2.0)));

        JButton btnClear = createActivityButton("🗑 Clear", new Color(244, 67, 54));
        btnClear.addActionListener(e -> clearPlan());

        JButton btnAddPlan = createActivityButton("📝 Add Plan", new Color(76, 175, 80));
        btnAddPlan.addActionListener(e -> addActivityPlan());

        buttonsPanel.add(btnMuseum);
        buttonsPanel.add(btnMall);
        buttonsPanel.add(btnPark);
        buttonsPanel.add(btnCenter);
        buttonsPanel.add(btnCinema);
        buttonsPanel.add(btnDinner);
        buttonsPanel.add(btnClear);
        buttonsPanel.add(btnAddPlan);

        panel.add(buttonsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        selectedCityLabel = new JLabel("📍 Selected City: None");
        selectedCityLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        selectedCityLabel.setForeground(Color.WHITE);

        budgetLabel = new JLabel("💰 Total Budget: $0.00");
        budgetLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        budgetLabel.setForeground(Color.WHITE);

        durationLabel = new JLabel("⏱ Total Duration: 0.0 hours");
        durationLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        durationLabel.setForeground(Color.WHITE);

        panel.add(selectedCityLabel);
        panel.add(budgetLabel);
        panel.add(durationLabel);

        return panel;
    }

    private JPanel createTitledPanel(String title, JComponent component, Color titleColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        TitledBorder border = new TitledBorder(title);
        border.setTitleFont(TITLE_FONT);
        border.setTitleColor(titleColor);
        panel.setBorder(border);

        panel.add(component, BorderLayout.CENTER);

        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(LABEL_FONT);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 35));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });

        return btn;
    }

    private JButton createActivityButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(LABEL_FONT);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });

        return btn;
    }

    private void customizeComboBox(JComboBox<String> comboBox) {
        comboBox.setForeground(Color.WHITE);
        comboBox.setBackground(SECONDARY_COLOR);
    }

    private void onCitySelected() {
        City selected = allCitiesList.getSelectedValue();
        if (selected != null) {
            currentSelectedCity = selected;
            selectedCityLabel.setText("📍 Selected City: " + selected.getName());

            if (!cityPlans.containsKey(selected)) {
                cityPlans.put(selected, new ActivityPlan(selected.getName() + " Plan"));
            }

            updateTree();
            updateBudgetAndDuration();
        }
    }

    private void addActivity(ActivityComponent activity) {
        if (currentSelectedCity == null) {
            JOptionPane.showMessageDialog(this, "Please select a city first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ActivityPlan cityPlan = cityPlans.get(currentSelectedCity);
        if (cityPlan != null) {
            commandManager.executeCommand(new AddActivityCommand(cityPlan, activity));
            updateTree();
            updateBudgetAndDuration();
        }
    }

    private void addActivityPlan() {
        if (currentSelectedCity == null) {
            JOptionPane.showMessageDialog(this, "Please select a city first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String planName = JOptionPane.showInputDialog(this, "Enter plan name:", "New Activity Plan", JOptionPane.PLAIN_MESSAGE);
        if (planName != null && !planName.trim().isEmpty()) {
            ActivityPlan subPlan = new ActivityPlan(planName);
            ActivityPlan cityPlan = cityPlans.get(currentSelectedCity);
            if (cityPlan != null) {
                commandManager.executeCommand(new AddActivityCommand(cityPlan, subPlan));
                updateTree();
            }
        }
    }

    private void clearPlan() {
        if (currentSelectedCity == null) {
            JOptionPane.showMessageDialog(this, "Please select a city first!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int response = JOptionPane.showConfirmDialog(this, "Clear the entire plan for " + currentSelectedCity.getName() + "?", 
                "Confirm Clear", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            ActivityPlan cityPlan = cityPlans.get(currentSelectedCity);
            if (cityPlan != null) {
                commandManager.executeCommand(new ClearPlanCommand(cityPlan));
                updateTree();
                updateBudgetAndDuration();
            }
        }
    }

    private void updateAllCitiesList(SortStrategy strategy) {
        List<City> cities = new ArrayList<>(repository.getCities());
        strategy.sort(cities);
        allCitiesModel.clear();
        for (City city : cities) {
            allCitiesModel.addElement(city);
        }
    }

    private void applySort() {
        SortStrategy strategy;
        switch (sortComboBox.getSelectedIndex()) {
            case 1:
                strategy = new SortByPopulation();
                break;
            case 2:
                strategy = new SortByArea();
                break;
            default:
                strategy = new SortByName();
        }
        updateAllCitiesList(strategy);
    }

    private void applyWeatherFilter() {
        WeatherState filter = null;
        int index = weatherComboBox.getSelectedIndex();
        if (index > 0) {
            filter = WeatherState.values()[index - 1];
        }

        weatherFilteredModel.clear();
        for (City city : repository.getCities()) {
            if (filter == null || city.getCurrentWeatherState() == filter) {
                weatherFilteredModel.addElement(city);
            }
        }
    }

    private void updateWeatherFilter() {
        applyWeatherFilter();
    }

    private void updateTree() {
        rootNode.removeAllChildren();

        if (currentSelectedCity != null && cityPlans.containsKey(currentSelectedCity)) {
            ActivityPlan cityPlan = cityPlans.get(currentSelectedCity);
            buildTree(rootNode, cityPlan);
        }

        treeModel.reload(rootNode);
    }

    private void buildTree(DefaultMutableTreeNode parent, ActivityPlan plan) {
        DefaultMutableTreeNode planNode = new DefaultMutableTreeNode(plan.getName());
        parent.add(planNode);

        for (int i = 0; i < plan.getComponents().size(); i++) {
            ActivityComponent component = plan.getComponents().get(i);
            if (component instanceof ActivityPlan) {
                buildTree(planNode, (ActivityPlan) component);
            } else if (component instanceof ActivityLeaf) {
                ActivityLeaf leaf = (ActivityLeaf) component;
                planNode.add(new DefaultMutableTreeNode(leaf.getName() + " [" + moneyFormat.format(leaf.getCost()) + ", " + timeFormat.format(leaf.getRequiredTime()) + "h]"));
            }
        }
    }

    private void updateBudgetAndDuration() {
        if (currentSelectedCity != null && cityPlans.containsKey(currentSelectedCity)) {
            ActivityPlan cityPlan = cityPlans.get(currentSelectedCity);
            budgetLabel.setText("💰 Total Budget: " + moneyFormat.format(cityPlan.getCost()));
            durationLabel.setText("⏱ Total Duration: " + timeFormat.format(cityPlan.getRequiredTime()) + " hours");
        } else {
            budgetLabel.setText("💰 Total Budget: $0.00");
            durationLabel.setText("⏱ Total Duration: 0.0 hours");
        }
    }

    @Override
    public void update(List<City> cities) {
        SwingUtilities.invokeLater(() -> {
            barChartPanel.updateData(cities);
            pieChartPanel.updateData(cities);
            updateWeatherFilter();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ModernTravelPlannerGUI().setVisible(true);
        });
    }
}
