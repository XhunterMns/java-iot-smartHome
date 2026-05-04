package iot;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class SmartHomeSimulator extends JFrame {

    // ----------------- DEVICE STATES -----------------
    private boolean lightOn = false;
    private float lightAlpha = 0f;

    private boolean doorOpen = false;
    private double doorAngle = 0; // 0 = closed

    private boolean alarmOn = false;
    private boolean alarmFlash = false;

    private int temperature = 20;
    private int humidity = 50;

    // ----------------- ANIMATION TIMERS -----------------
    private Timer lightTimer;
    private Timer doorTimer;
    private Timer alarmTimer;
    private Timer sensorTimer;

    // ----------------- NETWORK -----------------
    private Socket clientSocket;
    private PrintWriter out;

    public SmartHomeSimulator() {
        setTitle("Smart Home Simulator - Animated");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Panel for drawing devices
        SimulatorPanel simulatorPanel = new SimulatorPanel();
        add(simulatorPanel);

        // ----------------- LIGHT FADE -----------------
        lightTimer = new Timer(40, e -> {
            if (lightOn && lightAlpha < 1f) lightAlpha += 0.05f;
            else if (!lightOn && lightAlpha > 0f) lightAlpha -= 0.05f;
            simulatorPanel.repaint();
        });

        // ----------------- DOOR ROTATION -----------------
        doorTimer = new Timer(20, e -> {
            if (doorOpen && doorAngle < 70) doorAngle += 2;
            else if (!doorOpen && doorAngle > 0) doorAngle -= 2;
            simulatorPanel.repaint();
        });

        // ----------------- ALARM FLASH -----------------
        alarmTimer = new Timer(400, e -> {
            if (alarmOn) {
                alarmFlash = !alarmFlash;
                simulatorPanel.repaint();
            }
        });

        // ----------------- SENSOR SIMULATION -----------------
        sensorTimer = new Timer(5000, e -> {
            Random rand = new Random();
            temperature = 18 + rand.nextInt(11); // 18-28°C
            humidity = 40 + rand.nextInt(41);   // 40-80%
            if (out != null) {
                out.println("TEMP:" + temperature);
                out.println("HUM:" + humidity);
            }
            simulatorPanel.repaint();
        });

        sensorTimer.start();

        // ----------------- START SERVER -----------------
        startServer();
    }

    // ================= NETWORK SERVER =================
    private void startServer() {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(5000);
                System.out.println("Simulator waiting for connection on port 5000...");
                clientSocket = serverSocket.accept();
                System.out.println("Dashboard connected!");

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                String line;
                while ((line = in.readLine()) != null) {
                    handleMessage(line);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ================= HANDLE DASHBOARD COMMANDS =================
    private void handleMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            String[] parts = msg.split(":");
            if (parts.length != 2) return;

            String device = parts[0].toUpperCase();
            String state = parts[1].toUpperCase();

            switch (device) {
                case "LIGHT":
                    setLight(state.equals("ON"));
                    break;
                case "DOOR":
                    setDoor(state.equals("ON"));
                    break;
                case "ALARM":
                    setAlarm(state.equals("ON"));
                    break;
            }
        });
    }

    // ================= DEVICE METHODS =================
    public void setLight(boolean on) {
        lightOn = on;
        lightTimer.start();
    }

    public void setDoor(boolean open) {
        doorOpen = open;
        doorTimer.start();
    }

    public void setAlarm(boolean on) {
        alarmOn = on;
        if (on) alarmTimer.start();
        else {
            alarmTimer.stop();
            alarmFlash = false;
        }
    }

    // ================= DRAWING PANEL =================
    class SimulatorPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // ----------------- BACKGROUND -----------------
            g2.setColor(new Color(15, 23, 42));
            g2.fillRect(0, 0, getWidth(), getHeight());

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            // ----------------- LIGHT -----------------
            if (lightAlpha > 0f) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, lightAlpha));
                g2.setColor(Color.YELLOW);
                g2.fillOval(centerX - 50, centerY - 150, 100, 100);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }

            // ----------------- DOOR -----------------
            AffineTransform old = g2.getTransform();
            g2.translate(centerX - 200, centerY + 50);
            g2.rotate(Math.toRadians(-doorAngle));
            g2.setColor(new Color(139, 69, 19));
            g2.fillRect(0, 0, 80, 150);
            g2.setTransform(old);

            // ----------------- ALARM -----------------
            g2.setColor(alarmOn && alarmFlash ? Color.RED : Color.GRAY);
            g2.fillOval(centerX + 150, centerY - 150, 50, 50);

            // ----------------- SENSOR INFO -----------------
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
            g2.drawString("Temperature: " + temperature + "°C", 50, 50);
            g2.drawString("Humidity: " + humidity + "%", 50, 80);
        }
    }

    // ================= MAIN =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SmartHomeSimulator sim = new SmartHomeSimulator();
            sim.setVisible(true);
        });
    }
}