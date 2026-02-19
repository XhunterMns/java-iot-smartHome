/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iot;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class SmartHomeDashboard extends JFrame {

    private JLabel connectionStatus;
    private JLabel tempValue;
    private JLabel humValue;
    private JComboBox<String> ipComboBox;
    private JTextField portField;
    private JButton connectButton; // bouton de connexion
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;


    public SmartHomeDashboard() {
        setTitle("Smart Home Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initUI();
    }



    private void initUI() {


        // 🌑 Global Dark Background
        getContentPane().setBackground(new Color(24, 26, 27));

        // ================= HEADER =================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(34, 40, 49));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Smart Home Dashboard");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        connectionStatus = new JLabel("● Disconnected");
        connectionStatus.setForeground(Color.RED);
        connectionStatus.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JPanel connectionPanel = new JPanel();
        connectionPanel.setBackground(new Color(34, 40, 49));

        ipComboBox = new JComboBox<>();
        for (String ip : getAvailableIpAddresses()) {
            ipComboBox.setEditable(true);
            ipComboBox.addItem(ip.toString());
        }
        ipComboBox.setBackground(new Color(24, 26, 27));
        ipComboBox.setForeground(Color.WHITE);

        portField = new JTextField("5000", 5);
        portField.setBackground(new Color(24, 26, 27));
        portField.setForeground(Color.WHITE);
        portField.setCaretColor(Color.WHITE);

        connectButton = new JButton("Connect");
        connectButton.setBackground(new Color(0, 173, 181));
        connectButton.setForeground(Color.WHITE);
        connectButton.setFocusPainted(false);
        connectButton.addActionListener(e -> connectToServer());

        JLabel ipLabel = new JLabel("IP:");
        ipLabel.setForeground(Color.WHITE);
        JLabel portLabel = new JLabel("Port:");
        portLabel.setForeground(Color.WHITE);

        connectionPanel.add(ipLabel);
        connectionPanel.add(ipComboBox);
        connectionPanel.add(portLabel);
        connectionPanel.add(portField);
        connectionPanel.add(connectButton);

        header.add(title, BorderLayout.WEST);
        header.add(connectionPanel, BorderLayout.CENTER);
        header.add(connectionStatus, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ================= CENTER =================
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerPanel.setBackground(new Color(24, 26, 27));

        centerPanel.add(createControlPanel());
        centerPanel.add(createSensorPanel());

        add(centerPanel, BorderLayout.CENTER);
        add(createIpListPanel(), BorderLayout.EAST);

        // ================= LOG PANEL =================
        JTextArea logs = new JTextArea();
        logs.setBackground(new Color(34, 40, 49));
        logs.setForeground(Color.WHITE);
        logs.setFont(new Font("Consolas", Font.PLAIN, 14));
        logs.setEditable(false);
        logs.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JScrollPane scrollPane = new JScrollPane(logs);
        scrollPane.setPreferredSize(new Dimension(0,120));

        add(scrollPane, BorderLayout.SOUTH);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(new Color(24, 26, 27));

        panel.add(createDeviceCard("Light"));
        panel.add(createDeviceCard("Door"));
        panel.add(createDeviceCard("Alarm"));

        return panel;
    }

    private JPanel createSensorPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0)); 
        panel.setBackground(new Color(24, 26, 27));

        panel.add(createSensorCard("Temperature", "23°C")); //var yji mel simulateur
        panel.add(createSensorCard("Humidity", "65%")); //  var yji mel simulateur

        return panel;
    }

    private List<String> getAvailableIpAddresses() {
        List<String> ips = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface ni : Collections.list(interfaces)) {
                if (ni.isLoopback() || !ni.isUp()) continue;
                for (InetAddress addr : Collections.list(ni.getInetAddresses())) {
                    if (!addr.isLoopbackAddress() && addr.getHostAddress().indexOf(':') == -1) {
                        ips.add(addr.getHostAddress());
                    }
                }
            }
        } catch (Exception e) {
            ips.add("Error: " + e.getMessage());
        }
        return ips;
    }

    private JPanel createIpListPanel() {
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(34, 40, 49));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(60, 70, 80)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(200, 0));

        JLabel title = new JLabel("Available IPs");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        DefaultListModel<String> model = new DefaultListModel<>();
        for (String ip : getAvailableIpAddresses()) {
            model.addElement(ip);
        }
        JList<String> ipList = new JList<>(model);
        ipList.setBackground(new Color(24, 26, 27));
        ipList.setForeground(new Color(0, 200, 120));
        ipList.setFont(new Font("Consolas", Font.PLAIN, 13));
        ipList.setSelectionBackground(new Color(0, 173, 181, 80));
        ipList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scroll = new JScrollPane(ipList);
        scroll.setBorder(null);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDeviceCard(String titleText) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(34, 40, 49));
        card.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel title = new JLabel(titleText);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton button = new JButton("OFF");
        button.setBackground(new Color(0, 173, 181));
        button.setForeground(Color.WHITE);

        button.addActionListener(e -> {
            if (socket == null || socket.isClosed()) return;

            boolean isOn = button.getText().equals("ON");
            String newState = isOn ? "OFF" : "ON";
            button.setText(newState);

            out.println(titleText.toUpperCase() + ":" + newState);
        });

        card.add(title, BorderLayout.NORTH);
        card.add(button, BorderLayout.CENTER);

        return card;
    }


    private JPanel createSensorCard(String titleText, String valueText) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(new Color(34, 40, 49));
        card.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel title = new JLabel(titleText);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel value = new JLabel(valueText);
        value.setForeground(new Color(0, 200, 120));
        value.setFont(new Font("Segoe UI", Font.BOLD, 32));

        card.add(title, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        return card;
    }

    private void connectToServer() {
        String ip = (String) ipComboBox.getSelectedItem();
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException e) {
            connectionStatus.setText("● Invalid port");
            connectionStatus.setForeground(Color.RED);
            return;
        }

        try {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            connectionStatus.setText("● Connected");
            connectionStatus.setForeground(new Color(0, 200, 120));

            startListeningThread();

        } catch (Exception ex) {
            connectionStatus.setText("● Disconnected");
            connectionStatus.setForeground(Color.RED);
            ex.printStackTrace();
        }
    }

    private void startListeningThread() {
        Thread listener = new Thread(() -> {
            try {
                String line;
                while (socket != null && socket.isConnected() && (line = in.readLine()) != null) {
                    final String msg = line;
                    SwingUtilities.invokeLater(() -> {
                        // Handle incoming messages (e.g. update sensors, append to logs)
                        System.out.println("Server: " + msg);
                    });
                }
            } catch (Exception e) {
                if (socket != null && !socket.isClosed()) {
                    SwingUtilities.invokeLater(() -> {
                        connectionStatus.setText("● Disconnected");
                        connectionStatus.setForeground(Color.RED);
                    });
                }
            }
        });
        listener.setDaemon(true);
        listener.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SmartHomeDashboard().setVisible(true);
        });
    }
}
