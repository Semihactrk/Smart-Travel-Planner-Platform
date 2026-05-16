package com.smarttravel.gui;

import com.smarttravel.City;
import com.smarttravel.WeatherState;
import com.smarttravel.command.*;
import com.smarttravel.composite.ActivityComponent;
import com.smarttravel.composite.ActivityLeaf;
import com.smarttravel.composite.ActivityPlan;
import com.smarttravel.decorator.*;
import com.smarttravel.iterator.WeatherCityIterator;
import com.smarttravel.observer.WeatherObserver;
import com.smarttravel.observer.WeatherReportProvider;
import com.smarttravel.repository.CityRepository;
import com.smarttravel.strategy.SortByArea;
import com.smarttravel.strategy.SortByName;
import com.smarttravel.strategy.SortByPopulation;
import com.smarttravel.strategy.SortStrategy;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TravelPlannerGUI extends JFrame implements WeatherObserver {
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

    // City-based plan management
    private Map<City, ActivityPlan> cityPlans = new HashMap<>();
    private City selectedCity = null;
    private ActivityPlan currentRootPlan = null;

    public TravelPlannerGUI() {
        repository = CityRepository.getInstance();
        commandManager = new CommandManager();

        setTitle("Smart Travel Planner Platform");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();

        weatherProvider = new WeatherReportProvider(repository.getCities());
        weatherProvider.addObserver(this);
        new Thread(weatherProvider).start();

        updateAllCitiesList(new SortByName());
    }

    private void initComponents() {
        // --- TOP: Controls ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        sortComboBox = new JComboBox<>(new String[] { "Sort by Name", "Sort by Population", "Sort by Area" });
        sortComboBox.addActionListener(e -> applySort());

        weatherComboBox = new JComboBox<>(new String[] { "ALL", "SUNNY", "CLOUDY", "RAINY", "SNOWY" });
        weatherComboBox.addActionListener(e -> applyWeatherFilter());

        controlPanel.add(new JLabel("Sort :"));
        controlPanel.add(sortComboBox);
        controlPanel.add(new JLabel("Filter by Weather:"));
        controlPanel.add(weatherComboBox);

        // Undo/Redo
        JButton btnUndo = new JButton("Undo");
        JButton btnRedo = new JButton("Redo");
        btnUndo.addActionListener(e -> {
            commandManager.undo();
            updateTree();
        });
        btnRedo.addActionListener(e -> {
            commandManager.redo();
            updateTree();
        });
        controlPanel.add(btnUndo);
        controlPanel.add(btnRedo);

        add(controlPanel, BorderLayout.NORTH);

        // --- CENTER: Lists and Planners ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 4, 10, 0));

        // 1. All Cities List
        allCitiesModel = new DefaultListModel<>();
        allCitiesList = new JList<>(allCitiesModel);
        allCitiesList.addListSelectionListener(e -> {
            selectedCity = allCitiesList.getSelectedValue();
            if (selectedCity != null) {
                if (!cityPlans.containsKey(selectedCity)) {
                    cityPlans.put(selectedCity, new ActivityPlan(selectedCity.getName() + " Plan"));
                }
                currentRootPlan = cityPlans.get(selectedCity);
                updateTree();
            }
        });
        centerPanel.add(createTitledPanel("All Cities", new JScrollPane(allCitiesList)));

        // 2. Weather Filtered List
        weatherFilteredModel = new DefaultListModel<>();
        weatherFilteredList = new JList<>(weatherFilteredModel);
        centerPanel.add(createTitledPanel("Cities By Weather", new JScrollPane(weatherFilteredList)));

        // 3. Planner Panel
        JPanel plannerPanel = new JPanel(new BorderLayout());
        JPanel plannerBtns = new JPanel(new GridLayout(10, 1, 5, 5));
        
        // Add City Button
        JButton btnAddCity = new JButton("Add City to Trip");
        btnAddCity.addActionListener(e -> {
            City selected = allCitiesList.getSelectedValue();
            if (selected != null) {
                commandManager.executeCommand(new AddCityCommand(new ArrayList<>(cityPlans.keySet()), selected));
                updateTree();
            }
        });
        plannerBtns.add(btnAddCity);

        // Remove City Button
        JButton btnRemoveCity = new JButton("Remove City from Trip");
        btnRemoveCity.addActionListener(e -> {
            if (selectedCity != null) {
                commandManager.executeCommand(new RemoveCityCommand(new ArrayList<>(cityPlans.keySet()), selectedCity));
                cityPlans.remove(selectedCity);
                selectedCity = null;
                currentRootPlan = null;
                updateTree();
            }
        });
        plannerBtns.add(btnRemoveCity);

        // Add Activity Plan Node
        JButton btnAddPlanNode = new JButton("Add Activity Plan Node");
        btnAddPlanNode.addActionListener(e -> {
            if (currentRootPlan != null) {
                String nodeName = JOptionPane.showInputDialog(this, "Enter plan node name:", "Day 1");
                if (nodeName != null && !nodeName.isEmpty()) {
                    ActivityPlan newPlan = new ActivityPlan(nodeName);
                    commandManager.executeCommand(new AddPlanNodeCommand(currentRootPlan, newPlan));
                    updateTree();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a city first!");
            }
        });
        plannerBtns.add(btnAddPlanNode);

        // Activity Buttons
        JButton btnAddMuseum = new JButton("Add Museum Visit");
        btnAddMuseum.addActionListener(e -> addActivityToSelectedNode(new MuseumVisit(new BaseCityActivity(selectedCity))));
        plannerBtns.add(btnAddMuseum);

        JButton btnAddPark = new JButton("Add Park Visit");
        btnAddPark.addActionListener(e -> addActivityToSelectedNode(new ParkVisit(new BaseCityActivity(selectedCity))));
        plannerBtns.add(btnAddPark);

        JButton btnAddShopping = new JButton("Add Shopping Mall Visit");
        btnAddShopping.addActionListener(e -> addActivityToSelectedNode(new ShoppingMallVisit(new BaseCityActivity(selectedCity))));
        plannerBtns.add(btnAddShopping);

        JButton btnAddCityCenter = new JButton("Add City Center Visit");
        btnAddCityCenter.addActionListener(e -> addActivityToSelectedNode(new CityCenterVisit(new BaseCityActivity(selectedCity))));
        plannerBtns.add(btnAddCityCenter);

        // Add Custom Activity
        JButton btnAddCustom = new JButton("Add Custom Activity");
        btnAddCustom.addActionListener(e -> {
            String actName = JOptionPane.showInputDialog(this, "Activity name (e.g., Cinema, Dinner):");
            if (actName != null && !actName.isEmpty()) {
                String costStr = JOptionPane.showInputDialog(this, "Cost ($):", "15.0");
                String timeStr = JOptionPane.showInputDialog(this, "Time (hours):", "2.0");
                try {
                    double cost = Double.parseDouble(costStr);
                    double time = Double.parseDouble(timeStr);
                    ActivityLeaf leaf = new ActivityLeaf(actName, cost, time);
                    commandManager.executeCommand(new AddActivityCommand(currentRootPlan, leaf));
                    updateTree();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid cost or time!");
                }
            }
        });
        plannerBtns.add(btnAddCustom);

        // Move buttons
        JButton btnMoveUp = new JButton("Move Activity Up");
        btnMoveUp.addActionListener(e -> moveActivityUp());
        plannerBtns.add(btnMoveUp);

        JButton btnMoveDown = new JButton("Move Activity Down");
        btnMoveDown.addActionListener(e -> moveActivityDown());
        plannerBtns.add(btnMoveDown);

        // Clear button
        JButton btnClear = new JButton("Clear Plan");
        btnClear.addActionListener(e -> {
            if (currentRootPlan != null) {
                commandManager.executeCommand(new ClearPlanCommand(currentRootPlan));
                updateTree();
            }
        });
        plannerBtns.add(btnClear);

        plannerPanel.add(plannerBtns, BorderLayout.CENTER);
        centerPanel.add(createTitledPanel("Planner Settings", new JScrollPane(plannerPanel)));

        // 4. Tree
        rootNode = new DefaultMutableTreeNode("Travel Plans");
        treeModel = new DefaultTreeModel(rootNode);
        planTree = new JTree(treeModel);
        planTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) planTree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                Object userObject = selectedNode.getUserObject();
                System.out.println("Selected: " + userObject);
            }
        });
        centerPanel.add(createTitledPanel("Activity Tree", new JScrollPane(planTree)));

        add(centerPanel, BorderLayout.CENTER);

        // --- BOTTOM: Charts ---
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
        barChartPanel = new BarChartPanel(repository.getCities());
        pieChartPanel = new PieChartPanel(repository.getCities());
        bottomPanel.add(createTitledPanel("City Temperatures", barChartPanel));
        bottomPanel.add(createTitledPanel("Weather Distribution", pieChartPanel));
        bottomPanel.setPreferredSize(new Dimension(1400, 250));

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addActivityToSelectedNode(CityActivity activity) {
        if (selectedCity == null) {
            JOptionPane.showMessageDialog(this, "Select a city first!");
            return;
        }
        if (currentRootPlan == null) {
            JOptionPane.showMessageDialog(this, "Select a city first!");
            return;
        }
        ActivityLeaf leaf = new ActivityLeaf(activity);
        commandManager.executeCommand(new AddActivityCommand(currentRootPlan, leaf));
        updateTree();
    }

    private void moveActivityUp() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) planTree.getLastSelectedPathComponent();
        if (selectedNode != null && currentRootPlan != null) {
            Object userObject = selectedNode.getUserObject();
            // This is a simplified approach - in production, track actual components
            JOptionPane.showMessageDialog(this, "Move feature - select activity in tree");
        }
    }

    private void moveActivityDown() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) planTree.getLastSelectedPathComponent();
        if (selectedNode != null && currentRootPlan != null) {
            JOptionPane.showMessageDialog(this, "Move feature - select activity in tree");
        }
    }

    private JPanel createTitledPanel(String title, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private void applySort() {
        int index = sortComboBox.getSelectedIndex();
        SortStrategy strategy;
        if (index == 1)
            strategy = new SortByPopulation();
        else if (index == 2)
            strategy = new SortByArea();
        else
            strategy = new SortByName();

        updateAllCitiesList(strategy);
    }

    private void applyWeatherFilter() {
        String selected = (String) weatherComboBox.getSelectedItem();
        weatherFilteredModel.clear();
        if (selected == null || selected.equals("ALL")) {
            for (City c : repository.getCities())
                weatherFilteredModel.addElement(c);
            return;
        }

        WeatherState state = WeatherState.valueOf(selected);
        Iterator<City> iterator = new WeatherCityIterator(repository.getCities(), state);
        while (iterator.hasNext()) {
            weatherFilteredModel.addElement(iterator.next());
        }
    }

    private void updateAllCitiesList(SortStrategy strategy) {
        List<City> list = new ArrayList<>(repository.getCities());
        strategy.sort(list);
        allCitiesModel.clear();
        for (City city : list) {
            allCitiesModel.addElement(city);
        }
        applyWeatherFilter();
    }

    private void updateTree() {
        rootNode.removeAllChildren();

        if (currentRootPlan != null) {
            rootNode.setUserObject(currentRootPlan.getName() + " [Cost: $" + String.format("%.2f", currentRootPlan.getCost())
                    + ", Time: " + String.format("%.1f", currentRootPlan.getRequiredTime()) + "h]");
            buildTree(rootNode, currentRootPlan);
        } else {
            rootNode.setUserObject("Travel Plans");
            // Show all city plans
            for (Map.Entry<City, ActivityPlan> entry : cityPlans.entrySet()) {
                DefaultMutableTreeNode cityNode = new DefaultMutableTreeNode(
                        entry.getKey().getName() + " [Cost: $" + String.format("%.2f", entry.getValue().getCost())
                                + ", Time: " + String.format("%.1f", entry.getValue().getRequiredTime()) + "h]");
                buildTree(cityNode, entry.getValue());
                rootNode.add(cityNode);
            }
        }

        treeModel.reload();
        for (int i = 0; i < planTree.getRowCount(); i++) {
            planTree.expandRow(i);
        }
    }

    private void buildTree(DefaultMutableTreeNode treeNode, ActivityComponent component) {
        for (int i = 0; i < component.getComponentCount(); i++) {
            try {
                ActivityComponent child = component.getChild(i);
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(
                        child.getName() + " [Cost: $" + String.format("%.2f", child.getCost()) + ", Time: "
                                + String.format("%.1f", child.getRequiredTime()) + "h]");
                treeNode.add(childNode);
                buildTree(childNode, child);
            } catch (IndexOutOfBoundsException | UnsupportedOperationException e) {
                break;
            }
        }
    }

    @Override
    public void update(List<City> cities) {
        SwingUtilities.invokeLater(() -> {
            allCitiesList.repaint();
            weatherFilteredList.repaint();
            barChartPanel.updateData(cities);
            pieChartPanel.updateData(cities);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TravelPlannerGUI gui = new TravelPlannerGUI();
            gui.setLocationRelativeTo(null);
            gui.setVisible(true);
        });
    }
}
