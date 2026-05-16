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
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private JTextField planNameField; // Hocanın istediği Plan Ismi kutusu

    private EnhancedBarChartPanel barChartPanel;
    private EnhancedPieChartPanel pieChartPanel;

    private JTree planTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;

    private Map<City, ActivityPlan> cityPlans;
    private City currentSelectedCity;

    // --- NEXT-GEN SAAS COLOR PALETTE ---
    private final Color SIDEBAR_BG = new Color(15, 23, 42);
    private final Color MAIN_BG = new Color(241, 245, 249);
    private final Color CARD_BG = new Color(255, 255, 255);
    private final Color TEXT_LIGHT = new Color(248, 250, 252);
    private final Color TEXT_DARK = new Color(30, 41, 59);
    private final Color ACCENT_COLOR = new Color(59, 130, 246);
    private final Color ACCENT_HOVER = new Color(37, 99, 235);

    public TravelPlannerGUI() {
        repository = CityRepository.getInstance();
        commandManager = new CommandManager();
        cityPlans = new HashMap<>();

        setTitle("SmartTravel Pro • Next-Gen Dashboard");
        setSize(1450, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();

        weatherProvider = new WeatherReportProvider(repository.getCities());
        weatherProvider.addObserver(this);
        new Thread(weatherProvider).start();

        updateAllCitiesList(new SortByName());
    }

    private void initComponents() {
        // --- 1. SOL MENÜ (DARK SIDEBAR) ---
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        JPanel logoPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        logoPanel.setOpaque(false);
        JLabel logoIcon = new JLabel("SmartTravel Platform");
        logoIcon.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoIcon.setForeground(Color.WHITE);
        JLabel subLogo = new JLabel("Pro Dashboard v2.0");
        subLogo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLogo.setForeground(new Color(148, 163, 184));
        logoPanel.add(logoIcon);
        logoPanel.add(subLogo);
        sidebar.add(logoPanel, BorderLayout.NORTH);

        JPanel controlsPanel = new JPanel(new GridLayout(8, 1, 0, 15));
        controlsPanel.setOpaque(false);
        controlsPanel.setBorder(new EmptyBorder(40, 0, 0, 0));

        sortComboBox = createSidebarCombo(new String[]{"Sort by Name", "Sort by Population", "Sort by Area"});
        sortComboBox.addActionListener(e -> applySort());

        weatherComboBox = createSidebarCombo(new String[]{"ALL", "SUNNY", "CLOUDY", "RAINY", "SNOWY"});
        weatherComboBox.addActionListener(e -> applyWeatherFilter());

        controlsPanel.add(createSidebarLabel("SORT STRATEGY"));
        controlsPanel.add(sortComboBox);
        controlsPanel.add(createSidebarLabel("WEATHER FILTER"));
        controlsPanel.add(weatherComboBox);
        controlsPanel.add(Box.createVerticalStrut(20)); 

        JButton btnUndo = createGradientButton("Undo Action", false);
        JButton btnRedo = createGradientButton("Redo Action", false);
        btnUndo.addActionListener(e -> { commandManager.undo(); updateTreeDisplay(); });
        btnRedo.addActionListener(e -> { commandManager.redo(); updateTreeDisplay(); });
        controlsPanel.add(btnUndo);
        controlsPanel.add(btnRedo);

        sidebar.add(controlsPanel, BorderLayout.CENTER);
        add(sidebar, BorderLayout.WEST);

        // --- 2. ANA İÇERİK (MAIN CONTENT AREA) ---
        JPanel mainContent = new JPanel(new BorderLayout(25, 25));
        mainContent.setBackground(MAIN_BG);
        mainContent.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel topCards = new JPanel(new GridLayout(1, 3, 25, 0));
        topCards.setOpaque(false);
        topCards.setPreferredSize(new Dimension(0, 360));

        allCitiesModel = new DefaultListModel<>();
        allCitiesList = new JList<>(allCitiesModel);
        setupModernList(allCitiesList);
        allCitiesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && allCitiesList.getSelectedValue() != null) {
                switchActiveCity(allCitiesList.getSelectedValue());
                weatherFilteredList.clearSelection();
            }
        });
        topCards.add(createShadowCard("Available Destinations", createModernScrollPane(allCitiesList)));

        weatherFilteredModel = new DefaultListModel<>();
        weatherFilteredList = new JList<>(weatherFilteredModel);
        setupModernList(weatherFilteredList);
        weatherFilteredList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && weatherFilteredList.getSelectedValue() != null) {
                switchActiveCity(weatherFilteredList.getSelectedValue());
                allCitiesList.clearSelection();
            }
        });
        topCards.add(createShadowCard("Weather Filtered", createModernScrollPane(weatherFilteredList)));

        // HOCANIN İSTEDİĞİ TOOLBOX (Aktivite Kutusu)
        JPanel plannerPanel = new JPanel(new BorderLayout(0, 10));
        plannerPanel.setOpaque(false);
        
        // Üst kısım: Plan ekleme (TextField + Buton)
        JPanel addPlanContainer = new JPanel(new BorderLayout(5, 0));
        addPlanContainer.setOpaque(false);
        planNameField = new JTextField("New Plan Name...");
        planNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        planNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JButton btnAddPlan = createGradientButton("Add Sub-Plan", true);
        btnAddPlan.addActionListener(e -> addSubPlan());
        addPlanContainer.add(planNameField, BorderLayout.CENTER);
        addPlanContainer.add(btnAddPlan, BorderLayout.EAST);

        // Orta kısım: Aktiviteler
        JPanel activityBtns = new JPanel(new GridLayout(4, 1, 0, 5));
        activityBtns.setOpaque(false);
        activityBtns.add(createToolButton("Add Museum Visit", new MuseumVisit(new BaseCityActivity(null))));
        activityBtns.add(createToolButton("Add Shopping Mall", new ShoppingMallVisit(new BaseCityActivity(null))));
        activityBtns.add(createToolButton("Add Park Walk", new ParkVisit(new BaseCityActivity(null))));
        activityBtns.add(createToolButton("Add City Center", new CityCenterVisit(new BaseCityActivity(null))));
        
        // Alt Kısım: Ağaç Düzenleme Butonları (Hocanın Mock-up'ındaki Move Up/Down)
        JPanel editBtns = new JPanel(new GridLayout(2, 2, 5, 5));
        editBtns.setOpaque(false);
        JButton btnMoveUp = createToolButton("Move Up", null);
        btnMoveUp.addActionListener(e -> moveSelected(-1));
        JButton btnMoveDown = createToolButton("Move Down", null);
        btnMoveDown.addActionListener(e -> moveSelected(1));
        JButton btnRemove = createDangerButton("Remove");
        btnRemove.addActionListener(e -> removeSelectedComponent());
        JButton btnClear = createDangerButton("Clear All");
        btnClear.addActionListener(e -> clearCurrentPlan());
        
        editBtns.add(btnMoveUp);
        editBtns.add(btnMoveDown);
        editBtns.add(btnRemove);
        editBtns.add(btnClear);

        plannerPanel.add(addPlanContainer, BorderLayout.NORTH);
        plannerPanel.add(activityBtns, BorderLayout.CENTER);
        plannerPanel.add(editBtns, BorderLayout.SOUTH);

        topCards.add(createShadowCard("Activity Toolbox", plannerPanel));
        mainContent.add(topCards, BorderLayout.NORTH);

        // --- ORTA KISIM: Ağaç ---
        rootNode = new DefaultMutableTreeNode("Select a destination to start planning...");
        treeModel = new DefaultTreeModel(rootNode);
        planTree = new JTree(treeModel);
        setupModernTree(planTree);
        mainContent.add(createShadowCard("Interactive Travel Hierarchy", createModernScrollPane(planTree)), BorderLayout.CENTER);

        // --- ALT KISIM: Grafikler ---
        JPanel bottomCharts = new JPanel(new GridLayout(1, 2, 25, 0));
        bottomCharts.setOpaque(false);
        bottomCharts.setPreferredSize(new Dimension(0, 250));
        barChartPanel = new EnhancedBarChartPanel(repository.getCities());
        pieChartPanel = new EnhancedPieChartPanel(repository.getCities());
        
        JPanel barWrap = new JPanel(new BorderLayout()); barWrap.setOpaque(false); barWrap.add(barChartPanel);
        JPanel pieWrap = new JPanel(new BorderLayout()); pieWrap.setOpaque(false); pieWrap.add(pieChartPanel);

        bottomCharts.add(createShadowCard("Live Temperature Analytics", barWrap));
        bottomCharts.add(createShadowCard("Live Weather Distribution", pieWrap));
        
        mainContent.add(bottomCharts, BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);
    }

    // --- UI YARDIMCILARI ---
    private JPanel createShadowCard(String title, JComponent content) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (int i = 0; i < 5; i++) {
                    g2.setColor(new Color(0, 0, 0, 3 + (i*2)));
                    g2.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, 20, 20);
                }
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 16, 16);
                g2.dispose();
            }
        };
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(20, 20, 25, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        wrapper.add(titleLabel, BorderLayout.NORTH);
        if (content instanceof JScrollPane) {
            content.setOpaque(false);
            ((JScrollPane) content).getViewport().setOpaque(false);
        }
        wrapper.add(content, BorderLayout.CENTER);
        return wrapper;
    }

    private JButton createGradientButton(String text, boolean isPrimaryAction) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setPaint(new GradientPaint(0, 0, ACCENT_HOVER, getWidth(), getHeight(), new Color(30, 64, 175)));
                } else {
                    g2.setPaint(new GradientPaint(0, 0, ACCENT_COLOR, getWidth(), getHeight(), ACCENT_HOVER));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createToolButton(String text, CityActivity decorator) {
        JButton btn = createGradientButton(text, false);
        btn.setForeground(Color.WHITE);
        if (decorator != null) {
            btn.addActionListener(e -> {
                if(currentSelectedCity != null) {
                    CityActivity tailoredActivity = null;
                    if(decorator instanceof MuseumVisit) tailoredActivity = new MuseumVisit(new BaseCityActivity(currentSelectedCity));
                    else if(decorator instanceof ShoppingMallVisit) tailoredActivity = new ShoppingMallVisit(new BaseCityActivity(currentSelectedCity));
                    else if(decorator instanceof ParkVisit) tailoredActivity = new ParkVisit(new BaseCityActivity(currentSelectedCity));
                    else if(decorator instanceof CityCenterVisit) tailoredActivity = new CityCenterVisit(new BaseCityActivity(currentSelectedCity));
                    addActivityToSelected(tailoredActivity);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a destination first!");
                }
            });
        }
        return btn;
    }

    private JButton createDangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(220, 38, 38) : new Color(239, 68, 68));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel createSidebarLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(new Color(100, 116, 139)); 
        return label;
    }

    private JComboBox<String> createSidebarCombo(String[] items) {
        JComboBox<String> box = new JComboBox<>(items);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        box.setBackground(new Color(30, 41, 59));
        box.setForeground(TEXT_LIGHT);
        box.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        box.setFocusable(false);
        return box;
    }

    private void setupModernList(JList<?> list) {
        list.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        list.setFixedCellHeight(38);
        list.setBackground(CARD_BG);
        list.setForeground(TEXT_DARK);
        list.setSelectionBackground(new Color(239, 246, 255)); 
        list.setSelectionForeground(ACCENT_COLOR);
        list.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private void setupModernTree(JTree tree) {
        tree.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tree.setBackground(CARD_BG);
        tree.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tree.setRowHeight(30);
        
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setBackgroundNonSelectionColor(CARD_BG);
        renderer.setBackgroundSelectionColor(new Color(239, 246, 255));
        renderer.setTextSelectionColor(ACCENT_COLOR);
        renderer.setTextNonSelectionColor(TEXT_DARK);
        renderer.setBorderSelectionColor(new Color(239, 246, 255));
        renderer.setLeafIcon(null);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
        tree.setCellRenderer(renderer);
    }

    private JScrollPane createModernScrollPane(JComponent view) {
        JScrollPane scroll = new JScrollPane(view);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(CARD_BG);
        scroll.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { this.thumbColor = new Color(203, 213, 225); this.trackColor = CARD_BG; }
            @Override protected JButton createDecreaseButton(int orientation) { return createZeroBtn(); }
            @Override protected JButton createIncreaseButton(int orientation) { return createZeroBtn(); }
            private JButton createZeroBtn() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); return b; }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle bounds) {
                if(bounds.isEmpty()) return;
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(thumbColor);
                g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 8, 8);
                g2.dispose();
            }
        });
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        return scroll;
    }

    // --- MANTIK (BUSINESS LOGIC) ---

    private void switchActiveCity(City city) {
        currentSelectedCity = city;
        if (!cityPlans.containsKey(city)) {
            cityPlans.put(city, new ActivityPlan(city.getName() + " Master Itinerary"));
        }
        updateTreeDisplay();
    }

    private void addSubPlan() {
        if (currentSelectedCity == null) {
            JOptionPane.showMessageDialog(this, "Please select a destination from the lists first!");
            return;
        }
        
        String planName = planNameField.getText();
        if (planName == null || planName.trim().isEmpty() || planName.equals("New Plan Name...")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid plan name.");
            return;
        }

        // BUG DÜZELTİLDİ: Artık ağaçta seçili olan (aktif) klasöre alt plan ekliyor [cite: 85, 86]
        ActivityPlan targetPlan = cityPlans.get(currentSelectedCity);
        TreePath selectionPath = planTree.getSelectionPath();
        if (selectionPath != null) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
            if (selectedNode.getUserObject() instanceof ActivityPlanHolder) {
                targetPlan = ((ActivityPlanHolder) selectedNode.getUserObject()).plan;
            }
        }

        commandManager.executeCommand(new AddActivityCommand(targetPlan, new ActivityPlan(planName)));
        planNameField.setText("New Plan Name..."); // Kutuyu sıfırla
        updateTreeDisplay();
    }

    private void addActivityToSelected(CityActivity tailoredActivity) {
        if (currentSelectedCity == null) return;
        ActivityPlan targetPlan = cityPlans.get(currentSelectedCity);
        TreePath selectionPath = planTree.getSelectionPath();
        if (selectionPath != null) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
            if (selectedNode.getUserObject() instanceof ActivityPlanHolder) {
                targetPlan = ((ActivityPlanHolder) selectedNode.getUserObject()).plan;
            }
        }
        commandManager.executeCommand(new AddActivityCommand(targetPlan, new ActivityLeaf(tailoredActivity)));
        updateTreeDisplay();
    }

    private void moveSelected(int direction) {
        if (currentSelectedCity == null || planTree.getSelectionPath() == null) return;
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) planTree.getSelectionPath().getLastPathComponent();
        if (selectedNode == rootNode) return; // Kök dizin taşınamaz

        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
        ActivityPlan parentPlan = (parentNode == rootNode) ? cityPlans.get(currentSelectedCity) : ((ActivityPlanHolder) parentNode.getUserObject()).plan;
        ActivityComponent componentToMove = (selectedNode.getUserObject() instanceof ActivityPlanHolder) ? ((ActivityPlanHolder) selectedNode.getUserObject()).plan : ((ActivityLeafHolder) selectedNode.getUserObject()).leaf;
        
        commandManager.executeCommand(new MoveCommand(parentPlan, componentToMove, direction));
        
        // Taşıdıktan sonra seçimi kaybetmemek için ağacı güncelliyoruz
        TreePath currentPath = planTree.getSelectionPath();
        updateTreeDisplay();
        planTree.setSelectionPath(currentPath);
    }

    private void removeSelectedComponent() {
        if (currentSelectedCity == null || planTree.getSelectionPath() == null) return;
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) planTree.getSelectionPath().getLastPathComponent();
        if (selectedNode == rootNode) {
            JOptionPane.showMessageDialog(this, "Cannot remove the Root Plan!");
            return;
        }
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
        ActivityPlan parentPlan = (parentNode == rootNode) ? cityPlans.get(currentSelectedCity) : ((ActivityPlanHolder) parentNode.getUserObject()).plan;
        ActivityComponent componentToRemove = (selectedNode.getUserObject() instanceof ActivityPlanHolder) ? ((ActivityPlanHolder) selectedNode.getUserObject()).plan : ((ActivityLeafHolder) selectedNode.getUserObject()).leaf;
        
        commandManager.executeCommand(new RemoveActivityCommand(parentPlan, componentToRemove));
        updateTreeDisplay();
    }

    private void clearCurrentPlan() {
        if (currentSelectedCity != null) {
            commandManager.executeCommand(new ClearPlanCommand(cityPlans.get(currentSelectedCity)));
            updateTreeDisplay();
        }
    }

    private void applySort() {
        int index = sortComboBox.getSelectedIndex();
        SortStrategy strategy = (index == 1) ? new SortByPopulation() : (index == 2) ? new SortByArea() : new SortByName();
        updateAllCitiesList(strategy);
    }

    private void applyWeatherFilter() {
        String selected = (String) weatherComboBox.getSelectedItem();
        weatherFilteredModel.clear();
        if (selected == null || selected.equals("ALL")) {
            for (City c : repository.getCities()) weatherFilteredModel.addElement(c);
            return;
        }
        WeatherState state = WeatherState.valueOf(selected);
        Iterator<City> iterator = new WeatherCityIterator(repository.getCities(), state);
        while (iterator.hasNext()) weatherFilteredModel.addElement(iterator.next());
    }

    private void updateAllCitiesList(SortStrategy strategy) {
        List<City> list = new ArrayList<>(repository.getCities());
        strategy.sort(list);
        allCitiesModel.clear();
        for (City city : list) allCitiesModel.addElement(city);
        applyWeatherFilter();
    }

    private void updateTreeDisplay() {
        rootNode.removeAllChildren();
        if (currentSelectedCity == null) {
            rootNode.setUserObject("Select a destination to start planning...");
        } else {
            ActivityPlan activePlan = cityPlans.get(currentSelectedCity);
            rootNode.setUserObject(activePlan.getName() + "   (Total: $" + activePlan.getCost() + " | " + activePlan.getRequiredTime() + "h)");
            buildTree(rootNode, activePlan);
        }
        treeModel.reload();
        for (int i = 0; i < planTree.getRowCount(); i++) planTree.expandRow(i);
    }

    private void buildTree(DefaultMutableTreeNode treeNode, ActivityComponent component) {
        for (int i = 0;; i++) {
            try {
                ActivityComponent child = component.getChild(i);
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(
                    (child instanceof ActivityPlan) ? new ActivityPlanHolder((ActivityPlan) child) : new ActivityLeafHolder((ActivityLeaf) child)
                );
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

    private static class ActivityPlanHolder {
        ActivityPlan plan;
        ActivityPlanHolder(ActivityPlan plan) { this.plan = plan; }
        @Override public String toString() { return plan.getName() + "   [$" + plan.getCost() + " | " + plan.getRequiredTime() + "h]"; }
    }

    private static class ActivityLeafHolder {
        ActivityLeaf leaf;
        ActivityLeafHolder(ActivityLeaf leaf) { this.leaf = leaf; }
        @Override public String toString() { return "  -  " + leaf.getName() + "   [$" + leaf.getCost() + " | " + leaf.getRequiredTime() + "h]"; }
    }
}