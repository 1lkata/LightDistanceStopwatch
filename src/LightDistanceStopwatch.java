import javax.swing.*;
import java.awt.*;

// ChatGPT helped with finding good colors and fonts, error message logging, layout issues, dealing with Timer, and creating Number Field

// A class representing a time record
class TimeRecord {
    private final long hours;
    private final long minutes;
    private final long seconds;
    private final long milliseconds;
    private final double distance;
    private final String description;

    public TimeRecord(long hours, long minutes, long seconds, long milliseconds, double distance, String description) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.milliseconds = milliseconds;
        this.distance = distance;
        this.description = description;
    }

    public String formattedRecord() {
        return String.format("Time: %02d:%02d:%02d.%03d, Distance: %s, Description: %s",
                hours, minutes, seconds, milliseconds, convertDistance(distance), description);
    }

    public String convertDistance(double distanceInMeters) {
        String[] units = {"m", "km", "Mm", "Gm"};
        int unitIndex = 0;
        while (distanceInMeters >= 1000 && unitIndex < units.length - 1) {
            distanceInMeters /= 1000;
            unitIndex++;
        }
        return String.format("%.2f %s", distanceInMeters, units[unitIndex]);
    }
}

public class LightDistanceStopwatch extends JFrame {
    // Components and variables initialization
    private static final double SPEED_OF_LIGHT = 299792458; // Speed of light in meters/second

    private JLabel timeDisplay, distanceDisplay, headerLabel;
    private JTextField hoursField, minutesField, secondsField, millisField, descriptionField;
    private JButton startButton, stopButton, resetButton, logRecordButton, deleteRecordButton, logCurrentButton;
    private JList<String> recordList;
    private DefaultListModel<String> recordModel;
    private long startTime, elapsedTime;
    private boolean isRunning = false;
    private Timer timer;

    // Constructor
    public LightDistanceStopwatch() {
        setTitle("Light Distance Stopwatch");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        initUI();
        setupTimer();
    }

    // Initialization Method
    private void initUI() {
        JPanel topPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        headerLabel = new JLabel("Light Distance Stopwatch", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Serif", Font.BOLD, 36));
        headerLabel.setForeground(new Color(30, 144, 255));

        timeDisplay = new JLabel("00:00:00.000", SwingConstants.CENTER);
        timeDisplay.setFont(new Font("Monospaced", Font.BOLD, 28));
        timeDisplay.setForeground(new Color(60, 60, 60));

        distanceDisplay = new JLabel("Distance light has traveled: 0 meters", SwingConstants.CENTER);
        distanceDisplay.setFont(new Font("SansSerif", Font.PLAIN, 16));

        topPanel.add(headerLabel);
        topPanel.add(timeDisplay);
        topPanel.add(distanceDisplay);

        JPanel middlePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        middlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        resetButton = new JButton("Reset");
        logCurrentButton = new JButton("Log Current Time");

        styleButton(startButton, new Color(0, 128, 0));
        styleButton(stopButton, new Color(255, 69, 0));
        styleButton(resetButton, new Color(70, 130, 180));
        styleButton(logCurrentButton, new Color(255, 140, 0));

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(logCurrentButton);

        JPanel recordPanel = new JPanel(new BorderLayout(5, 5));
        recordModel = new DefaultListModel<>();
        recordList = new JList<>(recordModel);
        recordList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        deleteRecordButton = new JButton("Delete Record");
        styleButton(deleteRecordButton, new Color(138, 43, 226));

        recordPanel.add(new JScrollPane(recordList), BorderLayout.CENTER);
        recordPanel.add(deleteRecordButton, BorderLayout.SOUTH);

        middlePanel.add(buttonPanel);
        middlePanel.add(recordPanel);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        hoursField = createNumberField(2);
        minutesField = createNumberField(2);
        secondsField = createNumberField(2);
        millisField = createNumberField(3);
        descriptionField = new JTextField(30);
        logRecordButton = new JButton("Log Time");
        styleButton(logRecordButton, new Color(255, 215, 0));

        bottomPanel.add(new JLabel("H:"));
        bottomPanel.add(hoursField);
        bottomPanel.add(new JLabel("M:"));
        bottomPanel.add(minutesField);
        bottomPanel.add(new JLabel("S:"));
        bottomPanel.add(secondsField);
        bottomPanel.add(new JLabel("MS:"));
        bottomPanel.add(millisField);
        bottomPanel.add(descriptionField);
        bottomPanel.add(logRecordButton);

        add(topPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        startButton.addActionListener(e -> startTimer());
        stopButton.addActionListener(e -> stopTimer());
        resetButton.addActionListener(e -> resetTimer());
        logRecordButton.addActionListener(e -> logRecord());
        deleteRecordButton.addActionListener(e -> deleteRecord());
        logCurrentButton.addActionListener(e -> logCurrentTimeAndDistance());

        stopButton.setEnabled(false);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
    }

    // Number Field Method
    private JTextField createNumberField(int columns) {
        JTextField field = new JTextField(columns);
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!(Character.isDigit(c) || c == java.awt.event.KeyEvent.VK_BACK_SPACE || c == java.awt.event.KeyEvent.VK_DELETE)) {
                    evt.consume();
                }
            }
        });
        return field;
    }

    // Timer Setup
    private void setupTimer() {
        timer = new Timer(10, e -> updateDisplay());
    }

    // Timer Start
    private void startTimer() {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime;
            timer.start();
            isRunning = true;
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        }
    }

    // Timer Stop
    private void stopTimer() {
        if (isRunning) {
            timer.stop();
            elapsedTime = System.currentTimeMillis() - startTime;
            isRunning = false;
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        }
    }

    // Record log method
    private void logRecord() {
        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a description", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (validateTimeFields()) {
            long totalMillis = Integer.parseInt(hoursField.getText().trim()) * 3600000L +
                    Integer.parseInt(minutesField.getText().trim()) * 60000L +
                    Integer.parseInt(secondsField.getText().trim()) * 1000L +
                    Integer.parseInt(millisField.getText().trim());
            double distance = SPEED_OF_LIGHT * (totalMillis / 1000.0);

            TimeRecord record = new TimeRecord(
                    Integer.parseInt(hoursField.getText().trim()),
                    Integer.parseInt(minutesField.getText().trim()),
                    Integer.parseInt(secondsField.getText().trim()),
                    Integer.parseInt(millisField.getText().trim()),
                    distance, description);

            recordModel.addElement(record.formattedRecord());
            clearFields();
        }
    }

    private boolean validateTimeFields() {
        if (hoursField.getText().trim().isEmpty() ||
                minutesField.getText().trim().isEmpty() ||
                secondsField.getText().trim().isEmpty() ||
                millisField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all time fields", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void deleteRecord() {
        int selectedIndex = recordList.getSelectedIndex();
        if (selectedIndex != -1) {
            recordModel.remove(selectedIndex);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a record to delete", "Selection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logCurrentTimeAndDistance() {
        if (!isRunning && elapsedTime == 0) {
            JOptionPane.showMessageDialog(this, "The timer is not running", "Action Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        long totalElapsed = isRunning ? (System.currentTimeMillis() - startTime) : elapsedTime;
        long hours = totalElapsed / 3600000;
        long minutes = (totalElapsed % 3600000) / 60000;
        long seconds = (totalElapsed % 60000) / 1000;
        long millis = totalElapsed % 1000;

        double distance = SPEED_OF_LIGHT * (totalElapsed / 1000.0);
        TimeRecord record = new TimeRecord(hours, minutes, seconds, millis, distance, "Current Time Record");
        recordModel.addElement(record.formattedRecord());
    }

    // Timer reset
    private void resetTimer() {
        timer.stop();
        isRunning = false;
        elapsedTime = 0;
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        updateDisplay();
        clearFields();
    }

    // Update Display - every 10 milliseconds
    private void updateDisplay() {
        long totalElapsed = isRunning ? (System.currentTimeMillis() - startTime) : elapsedTime;
        long hours = totalElapsed / 3600000;
        long minutes = (totalElapsed % 3600000) / 60000;
        long seconds = (totalElapsed % 60000) / 1000;
        long millis = totalElapsed % 1000;
        timeDisplay.setText(String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis));
        distanceDisplay.setText("Distance light has traveled: " +
                new TimeRecord(hours, minutes, seconds, millis, SPEED_OF_LIGHT * (totalElapsed / 1000.0), "")
                        .convertDistance(SPEED_OF_LIGHT * (totalElapsed / 1000.0)));
    }

    // Fields Clear
    private void clearFields() {
        hoursField.setText("");
        minutesField.setText("");
        secondsField.setText("");
        millisField.setText("");
        descriptionField.setText("");
    }

    // Main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LightDistanceStopwatch().setVisible(true));
    }
}
