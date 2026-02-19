/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iot;

/**
 *
 * @author chahine
 */
import javax.swing.*;
import java.awt.*;

public class Dash extends JFrame {

    public Dash() {
        setTitle("Smart Home Dashboard");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel logo = new JLabel("HOME");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(30));

        String[] items = {"Dashboard", "Devices", "Scenes", "Analytics", "Security"};

        for (String item : items) {
            JButton btn = new JButton(item);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setFocusPainted(false);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(10));
        }

        return sidebar;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        main.add(createHeader(), BorderLayout.NORTH);
        main.add(createCenterGrid(), BorderLayout.CENTER);

        return main;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());

        JLabel welcome = new JLabel("Welcome, Chahine");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JTextField search = new JTextField(" Search home...");
        search.setPreferredSize(new Dimension(250, 35));

        header.add(welcome, BorderLayout.WEST);
        header.add(search, BorderLayout.EAST);

        return header;
    }

    private JPanel createCenterGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));

        grid.add(createCard("Current Load", "1.2 kW"));
        grid.add(createCard("Temperature", "22.4°C"));
        grid.add(createCard("Device Health", "Optimal"));
        grid.add(createCard("Security", "Active"));

        return grid;
    }

    private JPanel createCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 63, 65)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Dash().setVisible(true);
        });
    }
}
