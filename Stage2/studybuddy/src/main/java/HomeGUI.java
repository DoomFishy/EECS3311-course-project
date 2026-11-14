
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import ai.controller.AppFacade;


public class HomeGUI extends JFrame {
    private final AppFacade appFacade;
    private final JTextField questionField;
    private final JTextArea outputArea;

    public HomeGUI(AppFacade appFacade) {
        super("StudyBuddy");
        this.appFacade = appFacade;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocation(40, 40);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("StudyBuddy AI Assistant", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel leftPanel = new JPanel(new BorderLayout(8, 8));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Course Overview"));
        leftPanel.setPreferredSize(new Dimension(320, 0));

        JLabel courseName = new JLabel(appFacade.getAssessments().isEmpty() ? "No course loaded" : "Course: " + appFacade.getAssessments().size() + " assessments");
        courseName.setFont(new Font("SansSerif", Font.PLAIN, 16));
        courseName.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.add(courseName, BorderLayout.NORTH);

        JTextArea summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setText(
                "Assessments: " + appFacade.getAssessments().size() + "\n" +
                "Materials: " + appFacade.getMaterials().size() + "\n" +
                "Average: " + String.format("%.2f", appFacade.getAssessments().stream().mapToDouble(a -> a.getGrade()).average().orElse(0.0)) + "%"
        );
        leftPanel.add(new JScrollPane(summaryArea), BorderLayout.CENTER);

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton uploadButton = new JButton("Upload Material");
        JButton reportButton = new JButton("Generate Report");
        JButton notesButton = new JButton("Generate Notes");
        JButton quizButton = new JButton("Generate Quiz");
        JButton studyButton = new JButton("Study Plan");
        JButton deadlineButton = new JButton("Deadlines");
        JButton taskButton = new JButton("Tasks");

        leftButtons.add(uploadButton);
        leftButtons.add(reportButton);
        leftButtons.add(notesButton);
        leftButtons.add(quizButton);
        leftButtons.add(studyButton);
        leftButtons.add(deadlineButton);
        leftButtons.add(taskButton);
        leftPanel.add(leftButtons, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.setBorder(BorderFactory.createTitledBorder("AI Chat"));

        questionField = new JTextField();
        questionField.setText("Ask the AI about your course...");
        questionField.setColumns(30);

        outputArea = new JTextArea();

        JButton askButton = new JButton("Ask AI");
        askButton.addActionListener(e -> {
            String q = questionField.getText();
            if (q == null || q.isBlank() || q.equals("Ask the AI about your course...")) {
                outputArea.setText("Please enter a question first.");
                return;
            }
            new Thread(() -> {
                try {
                    String result = appFacade.askQuestion(q);
                    outputArea.setText(result);
                } catch (Exception ex) {
                    outputArea.setText("AI request failed: " + ex.getMessage());
                }
            }).start();
        });

        JPanel inputPanel = new JPanel(new BorderLayout(8, 8));
        inputPanel.add(questionField, BorderLayout.CENTER);
        inputPanel.add(askButton, BorderLayout.EAST);
        rightPanel.add(inputPanel, BorderLayout.NORTH);

        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rightPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        add(rightPanel, BorderLayout.CENTER);

        uploadButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Text/Markdown/PDF files", "txt", "md", "pdf", "csv"));
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                appFacade.uploadFile(file.getAbsolutePath());
                outputArea.setText("Uploaded: " + file.getAbsolutePath());
            }
        });

        reportButton.addActionListener(e -> {
            new Thread(() -> {
                try {
                    String report = appFacade.generateReport();
                    outputArea.setText(report);
                } catch (Exception ex) {
                    outputArea.setText("Could not generate report: " + ex.getMessage());
                }
            }).start();
        });

        notesButton.addActionListener(e -> {
            new Thread(() -> {
                try {
                    String result = appFacade.generateNotes();
                    outputArea.setText(result);
                } catch (Exception ex) {
                    outputArea.setText("Could not generate notes: " + ex.getMessage());
                }
            }).start();
        });

        quizButton.addActionListener(e -> {
            new Thread(() -> {
                try {
                    String result = appFacade.generateQuiz();
                    outputArea.setText(result);
                } catch (Exception ex) {
                    outputArea.setText("Could not generate quiz: " + ex.getMessage());
                }
            }).start();
        });

        studyButton.addActionListener(e -> {
            new Thread(() -> {
                try {
                    String result = appFacade.generateStudyPlan();
                    outputArea.setText(result);
                } catch (Exception ex) {
                    outputArea.setText("Could not generate study plan: " + ex.getMessage());
                }
            }).start();
        });

        deadlineButton.addActionListener(e -> {
            new Thread(() -> {
                try {
                    String result = appFacade.generateDeadlineSummary();
                    outputArea.setText(result);
                } catch (Exception ex) {
                    outputArea.setText("Could not generate deadline summary: " + ex.getMessage());
                }
            }).start();
        });

        taskButton.addActionListener(e -> {
            new Thread(() -> {
                try {
                    String result = appFacade.generateTaskList();
                    outputArea.setText(result);
                } catch (Exception ex) {
                    outputArea.setText("Could not generate task list: " + ex.getMessage());
                }
            }).start();
        });

        setVisible(true);
    }
}
