import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlarmGUI {

    private static Clip activeClip;
    private static boolean isRinging = false;

    private static final List<LocalDateTime> alarms =
            new ArrayList<>();

    private static final DefaultListModel<String> alarmModel =
            new DefaultListModel<>();

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final String FILE_PATH =
        "C:\\Users\\darsh\\OneDrive\\Pictures\\Documents\\projectsinjava\\Take These Scars - Mireheart.wav";

    private static JLabel statusLabel;
    private static JButton stopButton;
    private static JButton snoozeButton;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(AlarmGUI::createGUI);

    }

    private static void createGUI() {

        // COLORS

        Color background = new Color(18, 24, 38);
        Color panelColor = new Color(30, 41, 59);
        Color accent = new Color(56, 189, 248);
        Color white = Color.WHITE;

        // MAIN FRAME

        JFrame frame = new JFrame("My Alarm Clock");

        frame.setSize(560, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.getContentPane().setBackground(background);
        frame.setLayout(new BorderLayout(10, 10));

        // TITLE

        JLabel title = new JLabel(
                "MY ALARM CLOCK",
                SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 27));
        title.setForeground(white);
        title.setBorder(
                BorderFactory.createEmptyBorder(20, 5, 10, 5)
        );

        frame.add(title, BorderLayout.NORTH);

        // DIGITAL CLOCK

        JLabel clockLabel = new JLabel(
                LocalTime.now().format(FORMATTER),
                SwingConstants.CENTER
        );

        clockLabel.setFont(new Font("Arial", Font.BOLD, 55));
        clockLabel.setForeground(accent);

        JPanel clockPanel = new JPanel(new BorderLayout());

        clockPanel.setBackground(background);
        clockPanel.add(clockLabel, BorderLayout.CENTER);

        frame.add(clockPanel, BorderLayout.CENTER);

        // ALARM INPUT

        JTextField alarmInput = new JTextField();

        alarmInput.setFont(new Font("Arial", Font.PLAIN, 18));
        alarmInput.setHorizontalAlignment(
                JTextField.CENTER
        );

        alarmInput.setBackground(panelColor);
        alarmInput.setForeground(white);
        alarmInput.setCaretColor(white);

        alarmInput.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(accent),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                )
        );

        alarmInput.setToolTipText("Enter time HH:mm:ss");

        // BUTTONS

        JButton addButton = new JButton("Add Alarm");

        JButton deleteButton = new JButton("Delete Alarm");

        stopButton = new JButton("Stop Alarm");

        snoozeButton = new JButton("Snooze 5 Min");

        styleButton(addButton, new Color(37, 99, 235));
        styleButton(deleteButton, new Color(217, 119, 6));
        styleButton(stopButton, new Color(220, 38, 38));
        styleButton(snoozeButton, new Color(5, 150, 105));

        stopButton.setEnabled(false);
        snoozeButton.setEnabled(false);

        // ALARM LIST

        JList<String> alarmList = new JList<>(alarmModel);

        alarmList.setFont(new Font("Arial", Font.PLAIN, 16));
        alarmList.setBackground(panelColor);
        alarmList.setForeground(white);
        alarmList.setSelectionBackground(accent);
        alarmList.setSelectionForeground(Color.BLACK);

        alarmList.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        // STATUS LABEL

        statusLabel = new JLabel(
                "Enter alarm time (HH:mm:ss)",
                SwingConstants.CENTER
        );

        statusLabel.setForeground(accent);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // ADD ALARM

        addButton.addActionListener(e -> {

            String input = alarmInput.getText().trim();

            try {

                LocalTime time =
                        LocalTime.parse(input, FORMATTER);

                LocalDateTime now = LocalDateTime.now();

                LocalDateTime alarm =
                        now.toLocalDate().atTime(time);

                if (!alarm.isAfter(now)) {

                    statusLabel.setText(
                            "Please enter a future time!"
                    );

                    return;
                }

                if (alarms.contains(alarm)) {

                    statusLabel.setText(
                            "Alarm already exists!"
                    );

                    return;
                }

                alarms.add(alarm);

                refreshAlarmList();

                statusLabel.setText(
                        "Alarm added successfully!"
                );

                alarmInput.setText("");

            }
            catch (DateTimeParseException ex) {

                statusLabel.setText(
                        "Invalid time! Use HH:mm:ss"
                );
            }

        });

        // DELETE ALARM

        deleteButton.addActionListener(e -> {

            int index = alarmList.getSelectedIndex();

            if (index == -1) {

                statusLabel.setText(
                        "Select an alarm to delete!"
                );

                return;
            }

            LocalDateTime selected = alarms.get(index);

            alarms.remove(selected);

            refreshAlarmList();

            statusLabel.setText("Alarm deleted!");

        });

        // STOP ALARM

        stopButton.addActionListener(e -> {

            if (!isRinging) {
                return;
            }

            stopSound();

            isRinging = false;

            stopButton.setEnabled(false);
            snoozeButton.setEnabled(false);

            statusLabel.setText("Alarm stopped!");

        });

        // SNOOZE ALARM

        snoozeButton.addActionListener(e -> {

            if (!isRinging) {
                return;
            }

            stopSound();

            isRinging = false;

            LocalDateTime snoozeTime =
                    LocalDateTime.now().plusMinutes(5);

            alarms.add(snoozeTime);

            refreshAlarmList();

            stopButton.setEnabled(false);
            snoozeButton.setEnabled(false);

            statusLabel.setText(
                    "Snoozed until " +
                    snoozeTime.format(FORMATTER)
            );

        });

        // INPUT PANEL

        JPanel inputPanel =
                new JPanel(new GridLayout(2, 1, 8, 8));

        inputPanel.setBackground(background);

        inputPanel.add(alarmInput);
        inputPanel.add(addButton);

        // LIST TITLE

        JLabel listTitle = new JLabel(
                "YOUR ALARMS",
                SwingConstants.CENTER
        );

        listTitle.setForeground(white);
        listTitle.setFont(new Font("Arial", Font.BOLD, 16));

        // SCROLL PANE

        JScrollPane scrollPane =
                new JScrollPane(alarmList);

        scrollPane.setPreferredSize(
                new Dimension(480, 120)
        );

        scrollPane.setBackground(panelColor);

        // BUTTON PANEL

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 8, 10)
        );

        buttonPanel.setBackground(background);

        buttonPanel.add(deleteButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(snoozeButton);

        // CONTROLS PANEL
        // BoxLayout keeps all controls in separate rows.

        JPanel controlsPanel = new JPanel();

        controlsPanel.setLayout(
                new BoxLayout(controlsPanel, BoxLayout.Y_AXIS)
        );

        controlsPanel.setBackground(background);

        controlsPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 20, 15, 20)
        );

        inputPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 85)
        );

        listTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 55)
        );

        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        controlsPanel.add(inputPanel);
        controlsPanel.add(Box.createVerticalStrut(15));
        controlsPanel.add(listTitle);
        controlsPanel.add(Box.createVerticalStrut(8));
        controlsPanel.add(scrollPane);
        controlsPanel.add(Box.createVerticalStrut(8));
        controlsPanel.add(buttonPanel);
        controlsPanel.add(Box.createVerticalStrut(5));
        controlsPanel.add(statusLabel);

        // ADD CONTROLS TO FRAME

        frame.add(controlsPanel, BorderLayout.SOUTH);

        // CHECK ALARMS EVERY SECOND

        Timer alarmTimer = new Timer(1000, e -> {

            LocalDateTime now = LocalDateTime.now();

            clockLabel.setText(
                    LocalTime.now().format(FORMATTER)
            );

            // Do not interrupt an alarm already ringing.
            // Other due alarms remain in the list.

            if (isRinging) {
                return;
            }

            for (int i = 0; i < alarms.size(); i++) {

                LocalDateTime alarm = alarms.get(i);

                if (!now.isBefore(alarm)) {

                    alarms.remove(i);

                    refreshAlarmList();

                    isRinging = true;

                    statusLabel.setText("ALARM RINGING!");

                    if (!playSound()) {

                        isRinging = false;

                        stopButton.setEnabled(false);
                        snoozeButton.setEnabled(false);

                    }

                    break;
                }
            }

        });

        alarmTimer.start();

        // CLEAN UP AUDIO WHEN WINDOW CLOSES

        frame.addWindowListener(
                new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(
                            java.awt.event.WindowEvent e) {

                        alarmTimer.stop();
                        stopSound();

                    }
                }
        );

        frame.setVisible(true);

    }

    // BUTTON STYLING

    private static void styleButton(
            JButton button, Color color) {

        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

    }

    // REFRESH ALARM LIST

    private static void refreshAlarmList() {

        Collections.sort(alarms);

        alarmModel.clear();

        for (LocalDateTime alarm : alarms) {

            alarmModel.addElement(
                    alarm.format(FORMATTER)
            );

        }

    }

    // PLAY SONG

    private static boolean playSound() {

        Clip newClip = null;

        try {

            File audioFile = new File(FILE_PATH);

            if (!audioFile.exists()) {

                statusLabel.setText(
                        "Audio file not found!"
                );

                return false;
            }

            newClip = AudioSystem.getClip();

            try (AudioInputStream audioStream =
                    AudioSystem.getAudioInputStream(audioFile)) {

                newClip.open(audioStream);

            }

            activeClip = newClip;

            activeClip.loop(Clip.LOOP_CONTINUOUSLY);

            stopButton.setEnabled(true);
            snoozeButton.setEnabled(true);

            statusLabel.setText("ALARM RINGING!");

            return true;

        }
        catch (UnsupportedAudioFileException |
               IOException |
               LineUnavailableException ex) {

            if (newClip != null) {
                newClip.close();
            }

            activeClip = null;

            statusLabel.setText(
                    "Unable to play audio! Check WAV format."
            );

            ex.printStackTrace();

            return false;

        }

    }

    // STOP SONG

    private static void stopSound() {

        if (activeClip != null) {

            activeClip.stop();
            activeClip.close();

            activeClip = null;

        }

    }

}