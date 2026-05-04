package iot;

import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class CameraSimulator {

    static {
        System.load("C:\\opencv\\build\\java\\x64\\opencv_java4130.dll");
    }

    public static void main(String[] args) throws Exception {

        ServerSocket server = new ServerSocket(5000);
        System.out.println("Camera Simulator waiting on port 5000...");
        Socket socket = server.accept();
        System.out.println("Dashboard connected.");

        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            System.out.println("Camera not detected.");
            return;
        }

        Mat frame = new Mat();
        MatOfByte buffer = new MatOfByte();

        while (true) {

            camera.read(frame);

            if (frame.empty()) {
                continue;
            }

            Imgcodecs.imencode(".jpg", frame, buffer);
            byte[] imageBytes = buffer.toArray();

            out.writeInt(imageBytes.length);
            out.write(imageBytes);
            out.flush();

            Thread.sleep(33);
        }
    }
}