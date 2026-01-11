/*
 * ╔═══════════════════════════════════════════════════════════════════════════════╗
 * ║           Cat's Default SDK Launcher v0.1 - Mojang Style Edition              ║
 * ║              Authentic Minecraft Java Launcher Experience                      ║
 * ║                    Team Flames / Samsoft / Cat OS                              ║
 * ╠═══════════════════════════════════════════════════════════════════════════════╣
 * ║  Features: Green Play Button, Dark Theme, Modern UI, Full Library Support     ║
 * ╚═══════════════════════════════════════════════════════════════════════════════╝
 *
 * Compile: javac CatsDefaultSDKLauncher.java
 * Run: java CatsDefaultSDKLauncher
 */

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.List;
import javax.net.ssl.*;

public class CatsDefaultSDKLauncher extends JFrame {
    
    // ═══════════════════════════════════════════════════════════════════════════════
    // CONSTANTS
    // ═══════════════════════════════════════════════════════════════════════════════
    private static final String LAUNCHER_NAME = "Cat's Default SDK Launcher";
    private static final String LAUNCHER_VERSION = "0.1";
    private static final String LAUNCHER_DIR = System.getProperty("user.home") + "/.catslauncher";
    private static final String VERSIONS_DIR = LAUNCHER_DIR + "/versions";
    private static final String LIBRARIES_DIR = LAUNCHER_DIR + "/libraries";
    private static final String JAVA_DIR = LAUNCHER_DIR + "/java";
    private static final String ASSETS_DIR = LAUNCHER_DIR + "/assets";
    private static final String VERSION_MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
    
    private static final int MAX_RETRIES = 5;
    private static final int DOWNLOAD_TIMEOUT = 60000;
    private static final int RATE_LIMIT_DELAY = 100;
    
    // ═══════════════════════════════════════════════════════════════════════════════
    // MOJANG THEME COLORS
    // ═══════════════════════════════════════════════════════════════════════════════
    
    // Main backgrounds
    private static final Color BG_DARK = new Color(0x1D1D1D);
    private static final Color BG_DARKER = new Color(0x141414);
    private static final Color BG_PANEL = new Color(0x2D2D2D);
    private static final Color BG_CARD = new Color(0x383838);
    private static final Color BG_HOVER = new Color(0x404040);
    
    // Mojang Green (Play Button)
    private static final Color GREEN_PRIMARY = new Color(0x3C8527);
    private static final Color GREEN_HOVER = new Color(0x4A9E30);
    private static final Color GREEN_PRESSED = new Color(0x2E6B1E);
    private static final Color GREEN_GLOW = new Color(0x50B030);
    
    // Text colors
    private static final Color TEXT_WHITE = new Color(0xFFFFFF);
    private static final Color TEXT_GRAY = new Color(0xB0B0B0);
    private static final Color TEXT_DIM = new Color(0x707070);
    private static final Color TEXT_TITLE = new Color(0xE0E0E0);
    
    // Accent colors
    private static final Color ACCENT_BLUE = new Color(0x3391D4);
    private static final Color BORDER_COLOR = new Color(0x484848);
    private static final Color INPUT_BG = new Color(0x3C3C3C);
    private static final Color INPUT_BORDER = new Color(0x505050);
    
    // ═══════════════════════════════════════════════════════════════════════════════
    // UI COMPONENTS
    // ═══════════════════════════════════════════════════════════════════════════════
    private JTextField usernameInput;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> versionCombo;
    private JSlider ramSlider;
    private JLabel ramLabel;
    private JLabel statusLabel;
    private JLabel progressLabel;
    private JProgressBar progressBar;
    private MojangPlayButton playButton;
    private JPanel mainContentPanel;
    private JPanel newsPanel;
    
    // ═══════════════════════════════════════════════════════════════════════════════
    // DATA
    // ═══════════════════════════════════════════════════════════════════════════════
    private Map<String, String> versions = new HashMap<>();
    private Map<String, List<String>> versionCategories = new LinkedHashMap<>();
    
    // ═══════════════════════════════════════════════════════════════════════════════
    // CONSTRUCTOR
    // ═══════════════════════════════════════════════════════════════════════════════
    public CatsDefaultSDKLauncher() {
        initVersionCategories();
        initUI();
        
        // Fetch versions after UI is ready
        SwingUtilities.invokeLater(this::fetchVersions);
    }
    
    private void initVersionCategories() {
        versionCategories.put("Latest Release", new ArrayList<>());
        versionCategories.put("Latest Snapshot", new ArrayList<>());
        versionCategories.put("Release", new ArrayList<>());
        versionCategories.put("Snapshot", new ArrayList<>());
        versionCategories.put("Old Beta", new ArrayList<>());
        versionCategories.put("Old Alpha", new ArrayList<>());
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════
    // UI INITIALIZATION
    // ═══════════════════════════════════════════════════════════════════════════════
    private void initUI() {
        setTitle(LAUNCHER_NAME + " " + LAUNCHER_VERSION);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setMinimumSize(new Dimension(900, 550));
        setLocationRelativeTo(null);
        
        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_DARK);
        
        // Header bar
        mainPanel.add(createHeader(), BorderLayout.NORTH);
        
        // Center content with sidebar
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BG_DARK);
        centerPanel.add(createSidebar(), BorderLayout.WEST);
        centerPanel.add(createMainContent(), BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom bar with play button
        mainPanel.add(createBottomBar(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_DARKER);
        header.setPreferredSize(new Dimension(0, 55));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        
        // Left: Logo and title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        leftPanel.setOpaque(false);
        
        // Minecraft-style cube icon
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw a simple Minecraft grass block icon
                int size = 28;
                int x = 2, y = 2;
                
                // Top (grass - green)
                g2.setColor(new Color(0x5D9B47));
                g2.fillRect(x, y, size, size/3);
                
                // Bottom (dirt - brown)
                g2.setColor(new Color(0x8B5A2B));
                g2.fillRect(x, y + size/3, size, size * 2/3);
                
                // Border
                g2.setColor(new Color(0x3D3D3D));
                g2.drawRect(x, y, size, size);
            }
        };
        iconLabel.setPreferredSize(new Dimension(32, 32));
        leftPanel.add(iconLabel);
        
        JPanel titleStack = new JPanel();
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Minecraft: Java Edition");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_WHITE);
        titleStack.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel(LAUNCHER_NAME + " v" + LAUNCHER_VERSION);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subtitleLabel.setForeground(TEXT_DIM);
        titleStack.add(subtitleLabel);
        
        leftPanel.add(titleStack);
        header.add(leftPanel, BorderLayout.WEST);
        
        // Right: Controls
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        rightPanel.setOpaque(false);
        
        // Settings button
        JButton settingsBtn = createHeaderButton("⚙");
        settingsBtn.addActionListener(e -> showSettings());
        rightPanel.add(settingsBtn);
        
        // Minimize
        JButton minBtn = createHeaderButton("—");
        minBtn.addActionListener(e -> setState(Frame.ICONIFIED));
        rightPanel.add(minBtn);
        
        // Close
        JButton closeBtn = createHeaderButton("✕");
        closeBtn.addActionListener(e -> System.exit(0));
        rightPanel.add(closeBtn);
        
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
    
    private JButton createHeaderButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(TEXT_GRAY);
        btn.setBackground(BG_DARKER);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(TEXT_WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(TEXT_GRAY);
            }
        });
        
        return btn;
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_PANEL);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));
        
        // Tabs at top
        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabsPanel.setBackground(BG_DARKER);
        tabsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tabsPanel.setPreferredSize(new Dimension(260, 40));
        
        JButton playTab = createTabButton("Play", true);
        JButton installTab = createTabButton("Installations", false);
        JButton skinsTab = createTabButton("Skins", false);
        
        tabsPanel.add(playTab);
        tabsPanel.add(installTab);
        tabsPanel.add(skinsTab);
        sidebar.add(tabsPanel);
        
        // Profile section
        JPanel profileSection = new JPanel();
        profileSection.setLayout(new BoxLayout(profileSection, BoxLayout.Y_AXIS));
        profileSection.setBackground(BG_PANEL);
        profileSection.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        profileSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        
        // Profile header
        JLabel profileHeader = new JLabel("PLAYER");
        profileHeader.setFont(new Font("Segoe UI", Font.BOLD, 10));
        profileHeader.setForeground(TEXT_DIM);
        profileHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        profileSection.add(profileHeader);
        profileSection.add(Box.createVerticalStrut(8));
        
        // Username field with player head style
        JPanel usernamePanel = new JPanel(new BorderLayout(10, 0));
        usernamePanel.setBackground(INPUT_BG);
        usernamePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(INPUT_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        usernamePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        // Steve head icon
        JLabel headIcon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Simple Steve head
                g2.setColor(new Color(0xFFD5B4)); // Skin
                g2.fillRect(2, 2, 20, 20);
                g2.setColor(new Color(0x4A3728)); // Hair
                g2.fillRect(2, 2, 20, 6);
                g2.setColor(new Color(0x3D3D3D)); // Eyes
                g2.fillRect(5, 10, 4, 3);
                g2.fillRect(14, 10, 4, 3);
            }
        };
        headIcon.setPreferredSize(new Dimension(24, 24));
        usernamePanel.add(headIcon, BorderLayout.WEST);
        
        usernameInput = new JTextField("Player");
        usernameInput.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        usernameInput.setBackground(INPUT_BG);
        usernameInput.setForeground(TEXT_WHITE);
        usernameInput.setCaretColor(TEXT_WHITE);
        usernameInput.setBorder(null);
        usernamePanel.add(usernameInput, BorderLayout.CENTER);
        
        profileSection.add(usernamePanel);
        sidebar.add(profileSection);
        
        // Separator
        sidebar.add(createSeparator());
        
        // Version section
        JPanel versionSection = new JPanel();
        versionSection.setLayout(new BoxLayout(versionSection, BoxLayout.Y_AXIS));
        versionSection.setBackground(BG_PANEL);
        versionSection.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        versionSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        
        JLabel versionHeader = new JLabel("VERSION");
        versionHeader.setFont(new Font("Segoe UI", Font.BOLD, 10));
        versionHeader.setForeground(TEXT_DIM);
        versionHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        versionSection.add(versionHeader);
        versionSection.add(Box.createVerticalStrut(8));
        
        // Category dropdown
        categoryCombo = createStyledCombo(versionCategories.keySet().toArray(new String[0]));
        categoryCombo.setSelectedItem("Latest Release");
        categoryCombo.addActionListener(e -> updateVersionList());
        versionSection.add(categoryCombo);
        versionSection.add(Box.createVerticalStrut(10));
        
        // Version dropdown
        versionCombo = createStyledCombo(new String[]{});
        versionSection.add(versionCombo);
        
        sidebar.add(versionSection);
        
        // Separator
        sidebar.add(createSeparator());
        
        // Memory section
        JPanel memorySection = new JPanel();
        memorySection.setLayout(new BoxLayout(memorySection, BoxLayout.Y_AXIS));
        memorySection.setBackground(BG_PANEL);
        memorySection.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        memorySection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JPanel memHeader = new JPanel(new BorderLayout());
        memHeader.setOpaque(false);
        memHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        memHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        
        JLabel memLabel = new JLabel("MEMORY");
        memLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        memLabel.setForeground(TEXT_DIM);
        memHeader.add(memLabel, BorderLayout.WEST);
        
        ramLabel = new JLabel("4 GB");
        ramLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        ramLabel.setForeground(GREEN_PRIMARY);
        memHeader.add(ramLabel, BorderLayout.EAST);
        
        memorySection.add(memHeader);
        memorySection.add(Box.createVerticalStrut(10));
        
        // Custom styled slider
        ramSlider = new JSlider(1, 16, 4) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int trackY = getHeight() / 2 - 3;
                int trackWidth = getWidth() - 20;
                
                // Track background
                g2.setColor(INPUT_BG);
                g2.fillRoundRect(10, trackY, trackWidth, 6, 3, 3);
                
                // Filled portion
                int fillWidth = (int) ((getValue() - getMinimum()) / (double) (getMaximum() - getMinimum()) * trackWidth);
                g2.setColor(GREEN_PRIMARY);
                g2.fillRoundRect(10, trackY, fillWidth, 6, 3, 3);
                
                // Thumb
                int thumbX = 10 + fillWidth - 8;
                g2.setColor(TEXT_WHITE);
                g2.fillOval(thumbX, trackY - 5, 16, 16);
                g2.setColor(GREEN_PRIMARY);
                g2.fillOval(thumbX + 2, trackY - 3, 12, 12);
            }
        };
        ramSlider.setOpaque(false);
        ramSlider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        ramSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        ramSlider.addChangeListener(e -> ramLabel.setText(ramSlider.getValue() + " GB"));
        memorySection.add(ramSlider);
        
        sidebar.add(memorySection);
        
        // Push remaining content to bottom
        sidebar.add(Box.createVerticalGlue());
        
        // Refresh button at bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(BG_PANEL);
        bottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JButton refreshBtn = new JButton("↻ Refresh Versions");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        refreshBtn.setForeground(TEXT_GRAY);
        refreshBtn.setBackground(BG_CARD);
        refreshBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> fetchVersions());
        refreshBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                refreshBtn.setBackground(BG_HOVER);
                refreshBtn.setForeground(TEXT_WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                refreshBtn.setBackground(BG_CARD);
                refreshBtn.setForeground(TEXT_GRAY);
            }
        });
        bottomPanel.add(refreshBtn);
        
        sidebar.add(bottomPanel);
        
        return sidebar;
    }
    
    private JButton createTabButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setForeground(active ? TEXT_WHITE : TEXT_DIM);
        btn.setBackground(BG_DARKER);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, active ? 2 : 0, 0, GREEN_PRIMARY),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private JComboBox<String> createStyledCombo(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBackground(INPUT_BG);
        combo.setForeground(TEXT_WHITE);
        combo.setBorder(BorderFactory.createLineBorder(INPUT_BORDER));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return combo;
    }
    
    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
    
    private JPanel createMainContent() {
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(BG_DARK);
        
        // News/Welcome panel
        JPanel contentArea = new JPanel();
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));
        contentArea.setBackground(BG_DARK);
        contentArea.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Welcome header
        JLabel welcomeLabel = new JLabel("Welcome to Minecraft");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(TEXT_WHITE);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentArea.add(welcomeLabel);
        
        contentArea.add(Box.createVerticalStrut(5));
        
        JLabel subLabel = new JLabel("Java Edition • " + LAUNCHER_NAME);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLabel.setForeground(TEXT_GRAY);
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentArea.add(subLabel);
        
        contentArea.add(Box.createVerticalStrut(30));
        
        // News cards
        contentArea.add(createNewsCard("🎮 Ready to Play", 
            "Select a version from the sidebar and click PLAY to start your adventure!",
            GREEN_PRIMARY));
        contentArea.add(Box.createVerticalStrut(15));
        
        contentArea.add(createNewsCard("📦 Offline Mode", 
            "This launcher works in offline mode. Perfect for single-player gaming!",
            ACCENT_BLUE));
        contentArea.add(Box.createVerticalStrut(15));
        
        contentArea.add(createNewsCard("⚡ Full Library Support", 
            "All required libraries are automatically downloaded for seamless gameplay.",
            new Color(0x9B59B6)));
        
        contentArea.add(Box.createVerticalGlue());
        
        // Progress section (hidden by default)
        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.setBackground(BG_DARK);
        progressPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        progressLabel = new JLabel(" ");
        progressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        progressLabel.setForeground(TEXT_GRAY);
        progressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressPanel.add(progressLabel);
        progressPanel.add(Box.createVerticalStrut(5));
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(400, 8));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressBar.setBackground(INPUT_BG);
        progressBar.setForeground(GREEN_PRIMARY);
        progressBar.setBorderPainted(false);
        progressBar.setVisible(false);
        progressPanel.add(progressBar);
        
        contentArea.add(progressPanel);
        
        mainContentPanel.add(contentArea, BorderLayout.CENTER);
        
        return mainContentPanel;
    }
    
    private JPanel createNewsCard(String title, String description, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        // Accent bar
        JPanel accentBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
            }
        };
        accentBar.setPreferredSize(new Dimension(4, 50));
        accentBar.setOpaque(false);
        card.add(accentBar, BorderLayout.WEST);
        
        // Text content
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(TEXT_WHITE);
        textPanel.add(titleLbl);
        textPanel.add(Box.createVerticalStrut(5));
        
        JLabel descLbl = new JLabel("<html><body style='width: 400px'>" + description + "</body></html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLbl.setForeground(TEXT_GRAY);
        textPanel.add(descLbl);
        
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createBottomBar() {
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(BG_DARKER);
        bottomBar.setPreferredSize(new Dimension(0, 70));
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        
        // Status label (left)
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 25));
        statusPanel.setOpaque(false);
        
        statusLabel = new JLabel("Ready to play");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(TEXT_DIM);
        statusPanel.add(statusLabel);
        
        bottomBar.add(statusPanel, BorderLayout.WEST);
        
        // Play button (center-right)
        JPanel playPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 10));
        playPanel.setOpaque(false);
        
        playButton = new MojangPlayButton();
        playButton.addActionListener(e -> prepareAndLaunch());
        playPanel.add(playButton);
        
        bottomBar.add(playPanel, BorderLayout.EAST);
        
        // Footer text
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        
        JLabel footerLabel = new JLabel(LAUNCHER_NAME + " • Team Flames / Samsoft");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        footerLabel.setForeground(TEXT_DIM);
        footerPanel.add(footerLabel);
        
        bottomBar.add(footerPanel, BorderLayout.CENTER);
        
        return bottomBar;
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════
    // VERSION MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════════════
    private void fetchVersions() {
        statusLabel.setText("Fetching versions...");
        
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    setupSSL();
                    
                    URL url = URI.create(VERSION_MANIFEST_URL).toURL();
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(30000);
                    conn.setReadTimeout(30000);
                    
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    parseVersionManifest(response.toString());
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Error: " + e.getMessage());
                        JOptionPane.showMessageDialog(CatsDefaultSDKLauncher.this, 
                            "Failed to fetch versions:\n" + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
            
            @Override
            protected void done() {
                updateVersionList();
                statusLabel.setText("Loaded " + versions.size() + " versions • Ready to play");
            }
        }.execute();
    }
    
    private void setupSSL() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
            }
        };
        
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }
    
    private void parseVersionManifest(String json) {
        versions.clear();
        for (List<String> list : versionCategories.values()) {
            list.clear();
        }
        
        String latestRelease = extractJsonValue(json, "release");
        String latestSnapshot = extractJsonValue(json, "snapshot");
        
        int versionsStart = json.indexOf("\"versions\"");
        if (versionsStart == -1) return;
        
        int arrayStart = json.indexOf("[", versionsStart);
        int arrayEnd = findMatchingBracket(json, arrayStart);
        String versionsArray = json.substring(arrayStart, arrayEnd + 1);
        
        int pos = 0;
        while ((pos = versionsArray.indexOf("{", pos)) != -1) {
            int objEnd = findMatchingBrace(versionsArray, pos);
            String versionObj = versionsArray.substring(pos, objEnd + 1);
            
            String id = extractJsonValue(versionObj, "id");
            String type = extractJsonValue(versionObj, "type");
            String versionUrl = extractJsonValue(versionObj, "url");
            
            if (id != null && versionUrl != null) {
                versions.put(id, versionUrl);
                
                if (id.equals(latestRelease)) {
                    versionCategories.get("Latest Release").add(id);
                }
                if (id.equals(latestSnapshot)) {
                    versionCategories.get("Latest Snapshot").add(id);
                }
                
                if ("release".equals(type)) {
                    versionCategories.get("Release").add(id);
                } else if ("snapshot".equals(type)) {
                    versionCategories.get("Snapshot").add(id);
                } else if ("old_beta".equals(type)) {
                    versionCategories.get("Old Beta").add(id);
                } else if ("old_alpha".equals(type)) {
                    versionCategories.get("Old Alpha").add(id);
                }
            }
            
            pos = objEnd + 1;
        }
    }
    
    private String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\"";
        int keyPos = json.indexOf(pattern);
        if (keyPos == -1) return null;
        
        int colonPos = json.indexOf(":", keyPos);
        if (colonPos == -1) return null;
        
        int valueStart = colonPos + 1;
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }
        
        if (valueStart >= json.length()) return null;
        
        if (json.charAt(valueStart) == '"') {
            int valueEnd = json.indexOf("\"", valueStart + 1);
            if (valueEnd == -1) return null;
            return json.substring(valueStart + 1, valueEnd);
        }
        
        return null;
    }
    
    private int findMatchingBracket(String s, int start) {
        int count = 0;
        for (int i = start; i < s.length(); i++) {
            if (s.charAt(i) == '[') count++;
            else if (s.charAt(i) == ']') count--;
            if (count == 0) return i;
        }
        return s.length() - 1;
    }
    
    private int findMatchingBrace(String s, int start) {
        int count = 0;
        for (int i = start; i < s.length(); i++) {
            if (s.charAt(i) == '{') count++;
            else if (s.charAt(i) == '}') count--;
            if (count == 0) return i;
        }
        return s.length() - 1;
    }
    
    private void updateVersionList() {
        String category = (String) categoryCombo.getSelectedItem();
        if (category == null) return;
        
        List<String> vers = versionCategories.get(category);
        versionCombo.removeAllItems();
        
        if (vers != null) {
            for (String v : vers) {
                versionCombo.addItem(v);
            }
            if (!vers.isEmpty()) {
                versionCombo.setSelectedIndex(0);
            }
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════
    // LAUNCH LOGIC
    // ═══════════════════════════════════════════════════════════════════════════════
    private void prepareAndLaunch() {
        createGameDirectories();
        downloadAndLaunch();
    }
    
    private void createGameDirectories() {
        new File(LAUNCHER_DIR).mkdirs();
        new File(VERSIONS_DIR).mkdirs();
        new File(LIBRARIES_DIR).mkdirs();
        new File(ASSETS_DIR).mkdirs();
        new File(JAVA_DIR).mkdirs();
    }
    
    private void downloadAndLaunch() {
        String version = (String) versionCombo.getSelectedItem();
        if (version == null || version.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No version selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String username = validateUsername(usernameInput.getText());
        int ram = ramSlider.getValue();
        String versionUrl = versions.get(version);
        
        if (versionUrl == null) {
            JOptionPane.showMessageDialog(this, "Version URL not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        playButton.setEnabled(false);
        playButton.setText("LAUNCHING...");
        progressBar.setVisible(true);
        progressBar.setValue(0);
        progressLabel.setText("Preparing " + version + "...");
        statusLabel.setText("Downloading game files...");
        
        new SwingWorker<Boolean, Integer>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return downloadVersionFiles(version, versionUrl);
            }
            
            @Override
            protected void done() {
                try {
                    if (get()) {
                        String nativesDir = VERSIONS_DIR + "/" + version + "/natives";
                        launchGame(version, username, ram, nativesDir);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(CatsDefaultSDKLauncher.this,
                        "Failed to launch:\n" + e.getMessage(),
                        "Launch Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    playButton.setEnabled(true);
                    playButton.setText("▶  PLAY");
                }
            }
        }.execute();
    }
    
    private boolean downloadVersionFiles(String version, String versionUrl) {
        String versionDir = VERSIONS_DIR + "/" + version;
        new File(versionDir).mkdirs();
        new File(versionDir + "/natives").mkdirs();
        
        try {
            setupSSL();
            
            // Download version JSON
            String jsonPath = versionDir + "/" + version + ".json";
            if (!new File(jsonPath).exists()) {
                SwingUtilities.invokeLater(() -> {
                    progressLabel.setText("Downloading version info...");
                    statusLabel.setText("Fetching " + version + " metadata...");
                });
                downloadFile(versionUrl, jsonPath);
            }
            
            SwingUtilities.invokeLater(() -> progressBar.setValue(10));
            
            // Read version data
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonPath)));
            
            // Download client JAR
            String jarUrl = extractNestedJsonValue(jsonContent, "downloads", "client", "url");
            String jarPath = versionDir + "/" + version + ".jar";
            if (jarUrl != null && !new File(jarPath).exists()) {
                SwingUtilities.invokeLater(() -> {
                    progressLabel.setText("Downloading " + version + ".jar...");
                    statusLabel.setText("Downloading Minecraft client...");
                });
                downloadFile(jarUrl, jarPath);
            }
            
            SwingUtilities.invokeLater(() -> progressBar.setValue(30));
            
            // Download ALL libraries
            SwingUtilities.invokeLater(() -> {
                progressLabel.setText("Downloading libraries...");
                statusLabel.setText("Downloading game libraries...");
            });
            downloadLibraries(jsonContent);
            
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(100);
                progressLabel.setText("Download complete!");
                statusLabel.setText("Ready to launch!");
            });
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                progressLabel.setText("Error: " + e.getMessage());
                statusLabel.setText("Download failed");
                JOptionPane.showMessageDialog(CatsDefaultSDKLauncher.this,
                    "Failed to download:\n" + e.getMessage(),
                    "Download Error", JOptionPane.ERROR_MESSAGE);
            });
            return false;
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════
    // LIBRARY DOWNLOAD
    // ═══════════════════════════════════════════════════════════════════════════════
    private void downloadLibraries(String jsonContent) {
        int libStart = jsonContent.indexOf("\"libraries\"");
        if (libStart == -1) return;
        
        int arrayStart = jsonContent.indexOf("[", libStart);
        if (arrayStart == -1) return;
        int arrayEnd = findMatchingBracket(jsonContent, arrayStart);
        String librariesArray = jsonContent.substring(arrayStart, arrayEnd + 1);
        
        List<String[]> libsToDownload = new ArrayList<>();
        
        int pos = 0;
        while ((pos = librariesArray.indexOf("{", pos)) != -1) {
            int objEnd = findMatchingBrace(librariesArray, pos);
            String libObj = librariesArray.substring(pos, objEnd + 1);
            
            int downloadsPos = libObj.indexOf("\"downloads\"");
            if (downloadsPos != -1) {
                int artifactPos = libObj.indexOf("\"artifact\"", downloadsPos);
                if (artifactPos != -1) {
                    String artifactPath = extractJsonValue(libObj.substring(artifactPos), "path");
                    String artifactUrl = extractJsonValue(libObj.substring(artifactPos), "url");
                    
                    if (artifactPath != null && artifactUrl != null) {
                        libsToDownload.add(new String[]{artifactPath, artifactUrl});
                    }
                }
                
                int classifiersPos = libObj.indexOf("\"classifiers\"", downloadsPos);
                if (classifiersPos != -1) {
                    String osName = getOsName();
                    String nativeKey = "natives-" + osName;
                    
                    int nativePos = libObj.indexOf("\"" + nativeKey + "\"", classifiersPos);
                    if (nativePos != -1) {
                        String nativePath = extractJsonValue(libObj.substring(nativePos), "path");
                        String nativeUrl = extractJsonValue(libObj.substring(nativePos), "url");
                        
                        if (nativePath != null && nativeUrl != null) {
                            libsToDownload.add(new String[]{nativePath, nativeUrl});
                        }
                    }
                }
            }
            
            pos = objEnd + 1;
        }
        
        int total = libsToDownload.size();
        int current = 0;
        
        for (String[] lib : libsToDownload) {
            String path = lib[0];
            String url = lib[1];
            String fullPath = LIBRARIES_DIR + "/" + path;
            
            File libFile = new File(fullPath);
            if (!libFile.exists()) {
                try {
                    libFile.getParentFile().mkdirs();
                    downloadFile(url, fullPath);
                    Thread.sleep(RATE_LIMIT_DELAY);
                } catch (Exception e) {
                    System.err.println("Failed to download: " + path + " - " + e.getMessage());
                }
            }
            
            current++;
            final int progress = 30 + (60 * current / total);
            final int cur = current;
            final int tot = total;
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(progress);
                progressLabel.setText("Downloading libraries... (" + cur + "/" + tot + ")");
                statusLabel.setText("Library " + cur + " of " + tot);
            });
        }
    }
    
    private String getOsName() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) return "windows";
        if (os.contains("mac")) return "osx";
        return "linux";
    }
    
    private void downloadFile(String urlStr, String destPath) throws Exception {
        URL url = URI.create(urlStr).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(DOWNLOAD_TIMEOUT);
        conn.setReadTimeout(DOWNLOAD_TIMEOUT);
        conn.setRequestProperty("User-Agent", LAUNCHER_NAME + "/" + LAUNCHER_VERSION);
        
        new File(destPath).getParentFile().mkdirs();
        
        try (InputStream in = conn.getInputStream();
             FileOutputStream out = new FileOutputStream(destPath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
    
    private String extractNestedJsonValue(String json, String... keys) {
        String current = json;
        for (int i = 0; i < keys.length - 1; i++) {
            int keyPos = current.indexOf("\"" + keys[i] + "\"");
            if (keyPos == -1) return null;
            int bracePos = current.indexOf("{", keyPos);
            if (bracePos == -1) return null;
            int braceEnd = findMatchingBrace(current, bracePos);
            current = current.substring(bracePos, braceEnd + 1);
        }
        return extractJsonValue(current, keys[keys.length - 1]);
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════
    // LAUNCH GAME
    // ═══════════════════════════════════════════════════════════════════════════════
    private void launchGame(String version, String username, int ram, String nativesDir) {
        try {
            String versionDir = VERSIONS_DIR + "/" + version;
            String jsonPath = versionDir + "/" + version + ".json";
            String jarPath = versionDir + "/" + version + ".jar";
            
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonPath)));
            String mainClass = extractJsonValue(jsonContent, "mainClass");
            if (mainClass == null) mainClass = "net.minecraft.client.main.Main";
            
            // Build FULL classpath
            List<String> classpathList = new ArrayList<>();
            classpathList.add(jarPath);
            addLibrariesToClasspath(jsonContent, classpathList);
            
            String sep = System.getProperty("os.name").toLowerCase().contains("win") ? ";" : ":";
            String classpath = String.join(sep, classpathList);
            
            // Build command
            List<String> cmd = new ArrayList<>();
            cmd.add("java");
            cmd.add("-Xmx" + ram + "G");
            
            addJvmArgs(jsonContent, cmd, nativesDir);
            
            // Offline mode
            cmd.add("-Dminecraft.api.auth.host=http://0.0.0.0");
            cmd.add("-Dminecraft.api.account.host=http://0.0.0.0");
            cmd.add("-Dminecraft.api.session.host=http://0.0.0.0");
            cmd.add("-Dminecraft.api.services.host=http://0.0.0.0");
            
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                if (!cmd.contains("-XstartOnFirstThread")) {
                    cmd.add("-XstartOnFirstThread");
                }
            }
            
            cmd.add("-cp");
            cmd.add(classpath);
            cmd.add(mainClass);
            
            String uuid = generateOfflineUUID(username);
            String assetIndex = extractNestedJsonValue(jsonContent, "assetIndex", "id");
            if (assetIndex == null) assetIndex = "legacy";
            
            cmd.add("--username"); cmd.add(username);
            cmd.add("--version"); cmd.add(version);
            cmd.add("--gameDir"); cmd.add(LAUNCHER_DIR);
            cmd.add("--assetsDir"); cmd.add(ASSETS_DIR);
            cmd.add("--assetIndex"); cmd.add(assetIndex);
            cmd.add("--uuid"); cmd.add(uuid);
            cmd.add("--accessToken"); cmd.add("0");
            cmd.add("--userType"); cmd.add("legacy");
            
            statusLabel.setText("Launching Minecraft " + version + "...");
            System.out.println("🚀 Launching: " + String.join(" ", cmd));
            
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.directory(new File(LAUNCHER_DIR));
            pb.inheritIO();
            pb.start();
            
            progressLabel.setText("Game launched!");
            statusLabel.setText("Minecraft " + version + " is running");
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error launching:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addLibrariesToClasspath(String jsonContent, List<String> classpathList) {
        int libStart = jsonContent.indexOf("\"libraries\"");
        if (libStart == -1) return;
        
        int arrayStart = jsonContent.indexOf("[", libStart);
        if (arrayStart == -1) return;
        int arrayEnd = findMatchingBracket(jsonContent, arrayStart);
        String librariesArray = jsonContent.substring(arrayStart, arrayEnd + 1);
        
        int pos = 0;
        while ((pos = librariesArray.indexOf("{", pos)) != -1) {
            int objEnd = findMatchingBrace(librariesArray, pos);
            String libObj = librariesArray.substring(pos, objEnd + 1);
            
            int downloadsPos = libObj.indexOf("\"downloads\"");
            if (downloadsPos != -1) {
                int artifactPos = libObj.indexOf("\"artifact\"", downloadsPos);
                if (artifactPos != -1) {
                    String artifactPath = extractJsonValue(libObj.substring(artifactPos), "path");
                    if (artifactPath != null) {
                        String fullPath = LIBRARIES_DIR + "/" + artifactPath;
                        if (new File(fullPath).exists()) {
                            classpathList.add(fullPath);
                        }
                    }
                }
            }
            
            pos = objEnd + 1;
        }
    }
    
    private void addJvmArgs(String jsonContent, List<String> cmd, String nativesDir) {
        int argsPos = jsonContent.indexOf("\"arguments\"");
        if (argsPos != -1) {
            int jvmPos = jsonContent.indexOf("\"jvm\"", argsPos);
            if (jvmPos != -1) {
                int arrayStart = jsonContent.indexOf("[", jvmPos);
                if (arrayStart != -1) {
                    int arrayEnd = findMatchingBracket(jsonContent, arrayStart);
                    String jvmArray = jsonContent.substring(arrayStart + 1, arrayEnd);
                    
                    int strPos = 0;
                    while ((strPos = jvmArray.indexOf("\"", strPos)) != -1) {
                        int strEnd = jvmArray.indexOf("\"", strPos + 1);
                        if (strEnd == -1) break;
                        
                        String arg = jvmArray.substring(strPos + 1, strEnd);
                        
                        if (!arg.isEmpty() && (arg.startsWith("-") || arg.startsWith("$"))) {
                            arg = arg.replace("${natives_directory}", nativesDir);
                            arg = arg.replace("${launcher_name}", LAUNCHER_NAME);
                            arg = arg.replace("${launcher_version}", LAUNCHER_VERSION);
                            arg = arg.replace("${classpath}", "");
                            
                            if (!arg.contains("${") && !arg.isEmpty() && !arg.equals("-cp")) {
                                cmd.add(arg);
                            }
                        }
                        
                        strPos = strEnd + 1;
                    }
                }
            }
        }
        
        boolean hasNativePath = false;
        for (String arg : cmd) {
            if (arg.contains("-Djava.library.path=")) {
                hasNativePath = true;
                break;
            }
        }
        if (!hasNativePath) {
            cmd.add("-Djava.library.path=" + nativesDir);
        }
    }
    
    private String validateUsername(String username) {
        if (username == null || !username.matches("^[a-zA-Z0-9_]+$")) {
            return "Player";
        }
        return username;
    }
    
    private String generateOfflineUUID(String username) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(("OfflinePlayer:" + username).getBytes());
            
            digest[6] = (byte) ((digest[6] & 0x0f) | 0x30);
            digest[8] = (byte) ((digest[8] & 0x3f) | 0x80);
            
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            String hex = sb.toString();
            
            return hex.substring(0, 8) + "-" + hex.substring(8, 12) + "-" +
                   hex.substring(12, 16) + "-" + hex.substring(16, 20) + "-" +
                   hex.substring(20, 32);
                   
        } catch (Exception e) {
            return "00000000-0000-0000-0000-000000000000";
        }
    }
    
    private void showSettings() {
        JDialog dialog = new JDialog(this, "Settings", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BG_PANEL);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLbl = new JLabel("Settings");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLbl.setForeground(TEXT_WHITE);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(titleLbl);
        content.add(Box.createVerticalStrut(20));
        
        JLabel infoLbl = new JLabel("<html><body style='width: 300px'>" +
            "Game Directory: " + LAUNCHER_DIR + "<br><br>" +
            "Libraries: " + LIBRARIES_DIR + "<br><br>" +
            "Versions: " + VERSIONS_DIR +
            "</body></html>");
        infoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLbl.setForeground(TEXT_GRAY);
        infoLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(infoLbl);
        
        content.add(Box.createVerticalGlue());
        
        JButton closeBtn = new JButton("Close");
        closeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        closeBtn.addActionListener(e -> dialog.dispose());
        content.add(closeBtn);
        
        dialog.setContentPane(content);
        dialog.setVisible(true);
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════
    // MOJANG PLAY BUTTON
    // ═══════════════════════════════════════════════════════════════════════════════
    static class MojangPlayButton extends JButton {
        private boolean isHovered = false;
        private boolean isPressed = false;
        private String displayText = "▶  PLAY";
        
        public MojangPlayButton() {
            super("▶  PLAY");
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setForeground(TEXT_WHITE);
            setPreferredSize(new Dimension(180, 50));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) {
                        isHovered = true;
                        repaint();
                    }
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
                
                @Override
                public void mousePressed(MouseEvent e) {
                    if (isEnabled()) {
                        isPressed = true;
                        repaint();
                    }
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    repaint();
                }
            });
        }
        
        public void setText(String text) {
            this.displayText = text;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            
            // Button color based on state
            Color bgColor;
            if (!isEnabled()) {
                bgColor = new Color(0x4A4A4A);
            } else if (isPressed) {
                bgColor = GREEN_PRESSED;
            } else if (isHovered) {
                bgColor = GREEN_HOVER;
            } else {
                bgColor = GREEN_PRIMARY;
            }
            
            // Shadow
            g2.setColor(new Color(0, 0, 0, 60));
            g2.fillRoundRect(2, 4, w - 4, h - 4, 6, 6);
            
            // Main button
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, w - 2, h - 4, 6, 6);
            
            // Gradient overlay for depth
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(255, 255, 255, 30),
                0, h / 2, new Color(255, 255, 255, 0)
            );
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, w - 2, h / 2 - 2, 6, 6);
            
            // Border
            g2.setColor(new Color(0, 0, 0, 80));
            g2.drawRoundRect(0, 0, w - 3, h - 5, 6, 6);
            
            // Text
            g2.setColor(isEnabled() ? TEXT_WHITE : TEXT_DIM);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int textX = (w - fm.stringWidth(displayText)) / 2;
            int textY = (h - 4 + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(displayText, textX, textY);
            
            g2.dispose();
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════════════════
    // MAIN
    // ═══════════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════════════════╗");
        System.out.println("║           Cat's Default SDK Launcher v0.1 - Mojang Style              ║");
        System.out.println("║                    Team Flames / Samsoft / Cat OS                     ║");
        System.out.println("║   Authentic Minecraft Java Launcher Experience with Full Libraries    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════╝");
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Use default
        }
        
        SwingUtilities.invokeLater(() -> {
            CatsDefaultSDKLauncher launcher = new CatsDefaultSDKLauncher();
            launcher.setVisible(true);
        });
    }
}
