package iot;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SmartHomeSimulator extends JFrame{
    private JLabel lightLabel;
    private JLabel doorLabel;
    private JLabel alarmLabel;

    public SmartHomeSimulator() {
        setTitle("Smart Home Simulator");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3,1));

        lightLabel = new JLabel("Light: OFF");
        doorLabel = new JLabel("Door: OFF");
        alarmLabel = new JLabel("Alarm: OFF");

        add(lightLabel);
        add(doorLabel);
        add(alarmLabel);

        startServer();
    }

    private void startServer() {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(5000);
                Socket socket = serverSocket.accept();

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                String line;

                while ((line = in.readLine()) != null) {
                    handleMessage(line);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleMessage(String msg) {
        SwingUtilities.invokeLater(() -> {

            String[] parts = msg.split(":");
            if (parts.length != 2) return;

            String device = parts[0];
            String state = parts[1];

            switch (device) {
                case "LIGHT":
                    lightLabel.setText("Light: " + state);
                    break;
                case "DOOR":
                    doorLabel.setText("Door: " + state);
                    break;
                case "ALARM":
                    alarmLabel.setText("Alarm: " + state);
                    break;
            }
        });
    }

    public static void main(String[] args) {
        new SmartHomeSimulator().setVisible(true);
    }
}
