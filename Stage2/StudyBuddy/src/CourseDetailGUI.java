
import ai.controller.AppFacade;
import course.assessment.Assessment;
import course.material.Material;
import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class CourseDetailGUI extends JFrame {
    private final AppFacade appFacade;
    private final JTextArea outputArea;
    private final JTextField questionField;

    public CourseDetailGUI(AppFacade appFacade) {
        super(appFacade.getCourse().getName());
        this.appFacade = appFacade;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel leftPanel = new JPanel(new BorderLayout(8, 8));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Course Overview"));
        leftPanel.setPreferredSize(new Dimension(360, 0));

        JTextArea summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setText(buildCourseSummary());
        leftPanel.add(new JScrollPane(summaryArea), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JButton uploadButton = new JButton("Upload Material");
        JButton reportButton = new JButton("Generate Report");
        JButton notesButton = new JButton("Generate Notes");
        JButton quizButton = new JButton("Generate Quiz");
        JButton studyButton = new JButton("Study Plan");
        JButton deadlineButton = new JButton("Deadlines");
        JButton taskButton = new JButton("Tasks");
        JButton backButton = new JButton("Back to Courses");
        JButton addAssessmentButton = new JButton("Add Assessment");

        actionPanel.add(uploadButton);
        actionPanel.add(addAssessmentButton);
        actionPanel.add(reportButton);
        actionPanel.add(notesButton);
        actionPanel.add(quizButton);
        actionPanel.add(studyButton);
        actionPanel.add(deadlineButton);
        actionPanel.add(taskButton);
        actionPanel.add(backButton);

        leftPanel.add(actionPanel, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout(8, 8));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Assessments and Materials"));

        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JTextArea assessmentsArea = new JTextArea(buildAssessmentList());
        assessmentsArea.setEditable(false);
        assessmentsArea.setLineWrap(true);
        assessmentsArea.setWrapStyleWord(true);

        JTextArea materialsArea = new JTextArea(buildMaterialList());
        materialsArea.setEditable(false);
        materialsArea.setLineWrap(true);
        materialsArea.setWrapStyleWord(true);

        infoPanel.add(new JScrollPane(assessmentsArea));
        infoPanel.add(new JScrollPane(materialsArea));
        centerPanel.add(infoPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.setBorder(BorderFactory.createTitledBorder("AI Chat"));

        questionField = new JTextField();
        questionField.setText("Ask AI about this course...");
        JPanel askPanel = new JPanel(new BorderLayout(8, 8));
        JButton askButton = new JButton("Ask AI");
        askPanel.add(questionField, BorderLayout.CENTER);
        askPanel.add(askButton, BorderLayout.EAST);
        rightPanel.add(askPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        rightPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        uploadButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Study files", "txt", "md", "pdf", "csv", "ppt", "pptx", "key"));
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                appFacade.uploadFile(file.getAbsolutePath());
                outputArea.setText(appFacade.uploadFileAndAnalyze(file.getAbsolutePath()));

                System.out.println(appFacade.uploadFileAndAnalyze(file.getAbsolutePath()));

                summaryArea.setText(buildCourseSummary());
                assessmentsArea.setText(buildAssessmentList());
                materialsArea.setText(buildMaterialList());
            }
        });

        reportButton.addActionListener(e -> runAsync(() -> appFacade.generateReport(), "Could not generate report"));
        notesButton.addActionListener(e -> runAsync(() -> appFacade.generateNotes(questionField.getText()), "Could not generate notes"));
        quizButton.addActionListener(e -> runAsync(() -> appFacade.generateQuiz(questionField.getText()), "Could not generate quiz"));
        studyButton.addActionListener(e -> runAsync(() -> appFacade.generateStudyPlan(questionField.getText()), "Could not generate study plan"));
        deadlineButton.addActionListener(e -> runAsync(() -> appFacade.generateDeadlineSummary(), "Could not generate deadline summary"));
        taskButton.addActionListener(e -> runAsync(() -> appFacade.generateTaskList(), "Could not generate task list"));

        askButton.addActionListener(e -> {
            String q = questionField.getText();
            if (q == null || q.isBlank() || q.equals("Ask AI about this course...")) {
                outputArea.setText("Please enter a question first.");
                return;
            }
            runAsync(() -> appFacade.askQuestion(q), "AI request failed");
        });

        backButton.addActionListener(e -> {
            this.dispose();
            SwingUtilities.invokeLater(() -> new MainMenuGUI(new AppFacade(ai.factory.AIFactory.createDefault(), appFacade.getCourse())));
        });

        addAssessmentButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Assessment name:", "Add Assessment", JOptionPane.PLAIN_MESSAGE);
            if (name == null || name.trim().isEmpty()) {
                return;
            }

            String date = JOptionPane.showInputDialog(this, "Due date (yyyy-MM-dd or MM/dd/yyyy):", "Add Assessment", JOptionPane.PLAIN_MESSAGE);
            if (date == null || date.trim().isEmpty()) {
                return;
            }

            String weight = JOptionPane.showInputDialog(this, "Weight percent (e.g. 10):", "Add Assessment", JOptionPane.PLAIN_MESSAGE);
            String type = JOptionPane.showInputDialog(this, "Type (QUIZ, EXAM, ASSIGNMENT, PROJECT):", "Add Assessment", JOptionPane.PLAIN_MESSAGE);

            try {
                appFacade.addManualAssessment(name, date, weight == null ? "25" : weight, type);
                summaryArea.setText(buildCourseSummary());
                assessmentsArea.setText(buildAssessmentList());
                outputArea.setText("Added assessment: " + name + " due " + date);
            } catch (Exception ex) {
                outputArea.setText("Could not add assessment: " + ex.getMessage());
            }
        });

        setVisible(true);
    }

    private String buildCourseSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Course: ").append(appFacade.getCourse().getName()).append("\n\n");
        sb.append("Assessments: ").append(appFacade.getAssessments().size()).append("\n");
        sb.append("Materials: ").append(appFacade.getMaterials().size()).append("\n");
        sb.append("Average: ").append(String.format("%.2f", appFacade.getAssessments().stream().mapToDouble(Assessment::getGrade).average().orElse(0.0))).append("%\n");
        return sb.toString();
    }

    private String buildAssessmentList() {
        if (appFacade.getAssessments().isEmpty()) {
            return "No assessments yet.";
        }

        StringBuilder sb = new StringBuilder();
        for (Assessment a : appFacade.getAssessments()) {
            sb.append("- ").append(a.getName())
              .append(" (" ).append(a.getType()).append(")")
              .append(" | Weight: ").append(a.getWeight())
              .append(" | Due: ").append(a.getDeadline().toLocalDate())
              .append("\n");
        }
        return sb.toString();
    }

    private String buildMaterialList() {
        if (appFacade.getMaterials().isEmpty()) {
            return "No materials uploaded yet.";
        }

        StringBuilder sb = new StringBuilder();
        for (Material m : appFacade.getMaterials()) {
            sb.append("- ").append(m.getFilename())
              .append(" [").append(m.getType()).append("]")
              .append("\n").append(m.getFilepath()).append("\n");
        }
        return sb.toString();
    }

    private void runAsync(CheckedTask task, String fallbackMessage) {
        new Thread(() -> {
            try {
                outputArea.setText(task.run());
            } catch (Exception ex) {
                outputArea.setText(fallbackMessage + ": " + ex.getMessage());
            }
        }).start();
    }

    @FunctionalInterface
    private interface CheckedTask {
        String run() throws Exception;
    }
}
