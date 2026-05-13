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

    private EnhancedBarChartPanel barChartPanel;
    private EnhancedPieChartPanel pieChartPanel;

    private JTree planTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;

    private Map<City, ActivityPlan> cityPlans;
    private City currentSelectedCity;

    // --- NEXT-GEN SAAS COLOR PALETTE ---
    private final Color SIDEBAR_BG = new Color(15, 23, 42);     // Koyu Lacivert/Siyah
    private final Color MAIN_BG = new Color(241, 245, 249);     // Çok açık gri/mavi
    private final Color CARD_BG = new Color(255, 255, 255);     // Saf Beyaz
    private final Color TEXT_LIGHT = new Color(248, 250, 252);  // Sidebar metinleri
    private final Color TEXT_DARK = new Color(30, 41, 59);      // Ana metinler
    private final Color ACCENT_COLOR = new Color(59, 130, 246); // Parlak Mavi
    private final Color ACCENT_HOVER = new Color(37, 99, 235);  // Koyu Mavi

    public TravelPlannerGUI() {
        repository = CityRepository.getInstance();
        commandManager = new CommandManager();
        cityPlans = new HashMap<>();

        setTitle("SmartTravel Pro • Next-Gen Dashboard");
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
        // ==========================================
        // 1. SOL MENÜ (DARK SIDEBAR)
        // ==========================================
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        // Logo / Başlık Alanı
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

        // Kontroller (Sıralama, Filtre, Undo/Redo)
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
        
        controlsPanel.add(Box.createVerticalStrut(20)); // Boşluk

        JButton btnUndo = createGradientButton("Undo Action", false);
        JButton btnRedo = createGradientButton("Redo Action", false);
        btnUndo.addActionListener(e -> { commandManager.undo(); updateTreeDisplay(); });
        btnRedo.addActionListener(e -> { commandManager.redo(); updateTreeDisplay(); });
        controlsPanel.add(btnUndo);
        controlsPanel.add(btnRedo);

        sidebar.add(controlsPanel, BorderLayout.CENTER);
        add(sidebar, BorderLayout.WEST);

        // ==========================================
        // 2. ANA İÇERİK (MAIN CONTENT AREA)
        // ==========================================
        JPanel mainContent = new JPanel(new BorderLayout(25, 25));
        mainContent.setBackground(MAIN_BG);
        mainContent.setBorder(new EmptyBorder(25, 25, 25, 25));

        // ÜST KISIM: 3 Sütunlu Kartlar (Tüm Şehirler | Filtrelenmiş | Toolbox)
        JPanel topCards = new JPanel(new GridLayout(1, 3, 25, 0));
        topCards.setOpaque(false);
        topCards.setPreferredSize(new Dimension(0, 350));

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

        // Araç Kutusu
        JPanel plannerPanel = new JPanel(new GridLayout(7, 1, 0, 8));
        plannerPanel.setOpaque(false);
        plannerPanel.add(createGradientButton("Add Sub-Plan", true));
        plannerPanel.add(createToolButton("Add Museum Visit", new MuseumVisit(new BaseCityActivity(null))));
        plannerPanel.add(createToolButton("Add Shopping Mall", new ShoppingMallVisit(new BaseCityActivity(null))));
        plannerPanel.add(createToolButton("Add Park Walk", new ParkVisit(new BaseCityActivity(null))));
        plannerPanel.add(createToolButton("Add City Center", new CityCenterVisit(new BaseCityActivity(null))));
        
        JButton btnRemove = createDangerButton("Remove Selected");
        btnRemove.addActionListener(e -> removeSelectedComponent());
        plannerPanel.add(btnRemove);
        
        JButton btnClear = createDangerButton("Clear Entire Plan");
        btnClear.addActionListener(e -> clearCurrentPlan());
        plannerPanel.add(btnClear);

        topCards.add(createShadowCard("Activity Toolbox", plannerPanel));
        mainContent.add(topCards, BorderLayout.NORTH);

        // ORTA KISIM: Seyahat Planı Ağacı
        rootNode = new DefaultMutableTreeNode("Select a destination to start planning...");
        treeModel = new DefaultTreeModel(rootNode);
        planTree = new JTree(treeModel);
        setupModernTree(planTree);
        mainContent.add(createShadowCard("Interactive Travel Hierarchy", createModernScrollPane(planTree)), BorderLayout.CENTER);

        // ALT KISIM: Grafikler
        JPanel bottomCharts = new JPanel(new GridLayout(1, 2, 25, 0));
        bottomCharts.setOpaque(false);
        bottomCharts.setPreferredSize(new Dimension(0, 250));
        barChartPanel = new EnhancedBarChartPanel(repository.getCities());
        pieChartPanel = new EnhancedPieChartPanel(repository.getCities());
        
        // Grafikleri şeffaf sarmalayıcılara koyalım ki arka planları kartın beyazıyla uyumlu olsun
        JPanel barWrap = new JPanel(new BorderLayout()); barWrap.setOpaque(false); barWrap.add(barChartPanel);
        JPanel pieWrap = new JPanel(new BorderLayout()); pieWrap.setOpaque(false); pieWrap.add(pieChartPanel);

        bottomCharts.add(createShadowCard("Live Temperature Analytics", barWrap));
        bottomCharts.add(createShadowCard("Live Weather Distribution", pieWrap));
        
        mainContent.add(bottomCharts, BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);
    }

    // ==========================================
    // --- ÖZEL ÇİZİM VE STİL METOTLARI ---
    // ==========================================

    // Gerçekçi Drop Shadow (Gölge) Efekti Kartı
    private JPanel createShadowCard(String title, JComponent content) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // 5 Katmanlı Yumuşak Gölge Çizimi
                for (int i = 0; i < 5; i++) {
                    g2.setColor(new Color(0, 0, 0, 3 + (i*2)));
                    g2.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, 20, 20);
                }
                // Ana Beyaz Kart
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

    // Gradient ve Animasyonlu Buton
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
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
        
        if(isPrimaryAction) {
            btn.addActionListener(e -> addSubPlan());
        }
        return btn;
    }

    private JButton createToolButton(String text, CityActivity decorator) {
        JButton btn = createGradientButton(text, false);
        btn.setForeground(Color.WHITE);
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
        return btn;
    }

    private JButton createDangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(220, 38, 38) : new Color(239, 68, 68));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
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
        label.setForeground(new Color(100, 116, 139)); // Slate 500
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
        list.setSelectionBackground(new Color(239, 246, 255)); // Çok uçuk mavi
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

    // ==========================================
    // --- İŞ MANTIĞI (BUSINESS LOGIC) ---
    // ==========================================

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
        String planName = JOptionPane.showInputDialog(this, "Enter name for the new sub-plan (e.g., 'Day 1: Historic Tour'):");
        if (planName != null && !planName.trim().isEmpty()) {
            ActivityPlan currentPlan = cityPlans.get(currentSelectedCity);
            commandManager.executeCommand(new AddActivityCommand(currentPlan, new ActivityPlan(planName)));
            updateTreeDisplay();
        }
    }

    private void addActivityToSelected(CityActivity tailoredActivity) {
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