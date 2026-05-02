package com.smarttravel.gui;

import com.smarttravel.City;
import com.smarttravel.WeatherState;
import com.smarttravel.command.AddActivityCommand;
import com.smarttravel.command.ClearPlanCommand;
import com.smarttravel.command.CommandManager;
import com.smarttravel.composite.ActivityComponent;
import com.smarttravel.composite.ActivityLeaf;
import com.smarttravel.composite.ActivityPlan;
import com.smarttravel.decorator.BaseCityActivity;
import com.smarttravel.decorator.MuseumVisit;
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
import java.util.ArrayList;
import java.util.Iterator;
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

    private ActivityPlan rootActivityPlan;

    public TravelPlannerGUI() {
        repository = CityRepository.getInstance();
        commandManager = new CommandManager();
        rootActivityPlan = new ActivityPlan("My Travel Plan");

        setTitle("Smart Travel Planner Platform");
        setSize(1200, 800);
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
        centerPanel.add(createTitledPanel("All Cities", new JScrollPane(allCitiesList)));

        // 2. Weather Filtered List
        weatherFilteredModel = new DefaultListModel<>();
        weatherFilteredList = new JList<>(weatherFilteredModel);
        centerPanel.add(createTitledPanel("Cities By Weather", new JScrollPane(weatherFilteredList)));

        // 3. Planner
        JPanel plannerPanel = new JPanel(new BorderLayout());
        JButton btnAddMuseum = new JButton("Add Museum Visit");
        btnAddMuseum.addActionListener(e -> {
            City selected = allCitiesList.getSelectedValue();
            if (selected != null) {
                ActivityLeaf leaf = new ActivityLeaf(new MuseumVisit(new BaseCityActivity(selected)));
                commandManager.executeCommand(new AddActivityCommand(rootActivityPlan, leaf));
                updateTree();
            } else {
                JOptionPane.showMessageDialog(this, "Select a city first!");
            }
        });

        JButton btnClear = new JButton("Clear Plan");
        btnClear.addActionListener(e -> {
            commandManager.executeCommand(new ClearPlanCommand(rootActivityPlan));
            updateTree();
        });

        JPanel plannerBtns = new JPanel(new GridLayout(2, 1));
        plannerBtns.add(btnAddMuseum);
        plannerBtns.add(btnClear);
        plannerPanel.add(plannerBtns, BorderLayout.NORTH);
        centerPanel.add(createTitledPanel("Planner Settings", plannerPanel));

        // 4. Tree
        rootNode = new DefaultMutableTreeNode(rootActivityPlan.getName());
        treeModel = new DefaultTreeModel(rootNode);
        planTree = new JTree(treeModel);
        centerPanel.add(createTitledPanel("Activity Tree", new JScrollPane(planTree)));

        add(centerPanel, BorderLayout.CENTER);

        // --- BOTTOM: Charts ---
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
        barChartPanel = new BarChartPanel(repository.getCities());
        pieChartPanel = new PieChartPanel(repository.getCities());
        bottomPanel.add(createTitledPanel("City Temperatures", barChartPanel));
        bottomPanel.add(createTitledPanel("Weather Distribution", pieChartPanel));
        bottomPanel.setPreferredSize(new Dimension(1200, 250));

        add(bottomPanel, BorderLayout.SOUTH);
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
        rootNode.setUserObject(rootActivityPlan.getName() + " [Cost: $" + rootActivityPlan.getCost() + ", Time: "
                + rootActivityPlan.getRequiredTime() + "h]");

        // Very basic Tree building for leaves just to demonstrate Composite updates
        buildTree(rootNode, rootActivityPlan);

        treeModel.reload();
        for (int i = 0; i < planTree.getRowCount(); i++) {
            planTree.expandRow(i);
        }
    }

    private void buildTree(DefaultMutableTreeNode treeNode, ActivityComponent component) {
        for (int i = 0;; i++) {
            try {
                ActivityComponent child = component.getChild(i);
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(
                        child.getName() + " [Cost: $" + child.getCost() + ", Time: " + child.getRequiredTime() + "h]");
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
            allCitiesList.repaint(); // repaints city toString updates
            weatherFilteredList.repaint();
            barChartPanel.updateData(cities);
            pieChartPanel.updateData(cities);
        });
    }
}
