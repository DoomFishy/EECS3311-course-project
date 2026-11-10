import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import ai.controller.AppFacade;
import course.assessment.Assessment;
import course.material.Material;

public class CourseDetailGUI extends JFrame {
    private final AppFacade appFacade;
    private final JTextArea outputArea;
    private final JTextField questionField;
    private final JTextArea summaryArea;
    private final JTextArea assessmentsArea;
    
    // Replaced materialsArea with the panel of checkboxes
    private final JPanel materialSelectionPanel; 
    
    private final JPanel taskPanel;
    private final JPanel deadlinePanel;

    public CourseDetailGUI(AppFacade appFacade) {
        super(appFacade.getCourse().getName());
        this.appFacade = appFacade;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1300, 850); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ==========================================
        // --- LEFT PANEL (Overview, Deadlines, Tasks) ---
        // ==========================================
        JPanel leftPanel = new JPanel(new BorderLayout(8, 8));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftPanel.setPreferredSize(new Dimension(300, 0)); // Made narrower

        summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setText(buildCourseSummary());
        JPanel summaryContainer = new JPanel(new BorderLayout());
        summaryContainer.setBorder(BorderFactory.createTitledBorder("Course Overview"));
        summaryContainer.add(new JScrollPane(summaryArea), BorderLayout.CENTER);

        deadlinePanel = new JPanel(new BorderLayout());
        deadlinePanel.setBorder(BorderFactory.createTitledBorder("Deadlines"));
        deadlinePanel.add(new JScrollPane(new JTextArea(appFacade.generateDeadlineSummary())), BorderLayout.CENTER);

        taskPanel = new JPanel(new BorderLayout());
        taskPanel.setBorder(BorderFactory.createTitledBorder("Tasks"));
        taskPanel.add(new JScrollPane(new JTextArea(appFacade.generateTaskList())), BorderLayout.CENTER);

        // Stack Deadlines and Tasks under the Overview
        JPanel leftBottomPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        leftBottomPanel.add(deadlinePanel);
        leftBottomPanel.add(taskPanel);

        JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, summaryContainer, leftBottomPanel);
        leftSplit.setResizeWeight(0.4); // 40% to overview, 60% to deadlines/tasks
        leftSplit.setDividerLocation(250);
        leftPanel.add(leftSplit, BorderLayout.CENTER);
        
        add(leftPanel, BorderLayout.WEST);

        // ==========================================
        // --- CENTER PANEL (AI Chat) ---
        // ==========================================
        JPanel centerPanel = new JPanel(new BorderLayout(8, 8));
        centerPanel.setBorder(BorderFactory.createTitledBorder("AI Chat"));
        
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        centerPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        questionField = new JTextField();
        questionField.setText("Ask AI about this course...");
        JPanel askPanel = new JPanel(new BorderLayout(8, 8));
        JButton askButton = new JButton("Ask AI");
        askPanel.add(questionField, BorderLayout.CENTER);
        askPanel.add(askButton, BorderLayout.EAST);
        
        JPanel askContainer = new JPanel(new BorderLayout());
        askContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        askContainer.add(askPanel, BorderLayout.CENTER);
        centerPanel.add(askContainer, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // ==========================================
        // --- RIGHT PANEL (Materials, Assessments, Actions) ---
        // ==========================================
        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        rightPanel.setPreferredSize(new Dimension(380, 0));

        // Combined Materials & Target Materials Panel
        materialSelectionPanel = new JPanel();
        materialSelectionPanel.setLayout(new BoxLayout(materialSelectionPanel, BoxLayout.Y_AXIS));
        materialSelectionPanel.setBorder(BorderFactory.createTitledBorder("Materials (Check to Target)"));
        rebuildMaterialSelection(); // Populates the checkboxes
        JScrollPane materialScroll = new JScrollPane(materialSelectionPanel);

        // Assessments Panel
        assessmentsArea = new JTextArea(buildAssessmentList());
        assessmentsArea.setEditable(false);
        assessmentsArea.setLineWrap(true);
        assessmentsArea.setWrapStyleWord(true);
        JPanel assessmentsContainer = new JPanel(new BorderLayout());
        assessmentsContainer.setBorder(BorderFactory.createTitledBorder("Assessments"));
        assessmentsContainer.add(new JScrollPane(assessmentsArea), BorderLayout.CENTER);

        // Split Materials and Assessments vertically
        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, materialScroll, assessmentsContainer);
        rightSplit.setResizeWeight(0.5); // Equal space
        rightPanel.add(rightSplit, BorderLayout.CENTER);

        // Action Buttons at the bottom of Right Panel
        JPanel actionPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JButton uploadButton = new JButton("Upload Material");
        JButton reportButton = new JButton("Generate Report");
        JButton notesButton = new JButton("Generate Notes");
        JButton quizButton = new JButton("Generate Quiz");
        JButton studyButton = new JButton("Study Plan");
        JButton deadlineButton = new JButton("Refresh Deadlines");
        JButton taskButton = new JButton("Refresh Tasks");
        JButton backButton = new JButton("Back to Courses");
        JButton addAssessmentButton = new JButton("Add Assessment");
        JButton setGoalButton = new JButton("Set Grade Goal");
        JButton updateGradeButton = new JButton("Update Grade");

        actionPanel.add(uploadButton);
        actionPanel.add(addAssessmentButton);
        actionPanel.add(reportButton);
        actionPanel.add(notesButton);
        actionPanel.add(quizButton);
        actionPanel.add(studyButton);
        actionPanel.add(setGoalButton);
        actionPanel.add(updateGradeButton);
        actionPanel.add(deadlineButton);
        actionPanel.add(taskButton);
        actionPanel.add(backButton);

        rightPanel.add(actionPanel, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);

        // ==========================================
        // --- LISTENERS ---
        // ==========================================
        uploadButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Study files", "txt", "md", "pdf", "csv", "ppt", "pptx", "key"));
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                appFacade.uploadFile(file.getAbsolutePath());
                outputArea.setText(appFacade.uploadFileAndAnalyze(file.getAbsolutePath()));
                refreshViews();
            }
        });

        reportButton.addActionListener(e -> runAsync(() -> appFacade.generateReport(), "Could not generate report"));
        notesButton.addActionListener(e -> runAsync(() -> appFacade.generateNotes(questionField.getText()), "Could not generate notes"));
        quizButton.addActionListener(e -> runAsync(() -> appFacade.generateQuiz(questionField.getText()), "Could not generate quiz"));
        studyButton.addActionListener(e -> runAsync(() -> appFacade.generateStudyPlan(questionField.getText()), "Could not generate study plan"));
        
        deadlineButton.addActionListener(e -> refreshViews());
        taskButton.addActionListener(e -> refreshViews());

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
            if (name == null || name.trim().isEmpty()) return;

            String date = JOptionPane.showInputDialog(this, "Due date (yyyy-MM-dd or MM/dd/yyyy):", "Add Assessment", JOptionPane.PLAIN_MESSAGE);
            if (date == null || date.trim().isEmpty()) return;

            String weight = JOptionPane.showInputDialog(this, "Weight percent (e.g. 10):", "Add Assessment", JOptionPane.PLAIN_MESSAGE);
            String type = JOptionPane.showInputDialog(this, "Type (QUIZ, EXAM, ASSIGNMENT, PROJECT):", "Add Assessment", JOptionPane.PLAIN_MESSAGE);

            try {
                appFacade.addManualAssessment(name, date, weight == null ? "25" : weight, type);
                refreshViews();
                outputArea.setText("Added assessment: " + name + " due " + date);
            } catch (Exception ex) {
                outputArea.setText("Could not add assessment: " + ex.getMessage());
            }
        });

        setGoalButton.addActionListener(e -> {
            String value = JOptionPane.showInputDialog(this, "Target course grade (%)", String.valueOf(appFacade.getGradeGoal()));
            if (value == null || value.isBlank()) return;
            try {
                float goal = Float.parseFloat(value.trim());
                appFacade.setGradeGoal(goal);
                refreshViews();
                outputArea.setText("Grade goal set to " + goal + "%");
            } catch (NumberFormatException ex) {
                outputArea.setText("Please enter a valid number percentage, e.g. 85");
            }
        });

        updateGradeButton.addActionListener(e -> {
            List<Assessment> assessments = appFacade.getAssessments();
            if (assessments.isEmpty()) {
                outputArea.setText("No assessments to update.");
                return;
            }

            String[] options = assessments.stream().map(Assessment::getName).toArray(String[]::new);
            String selected = (String) JOptionPane.showInputDialog(this, "Choose assessment:", "Update Assessment Grade", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (selected == null) return;

            Assessment target = assessments.stream().filter(a -> a.getName().equals(selected)).findFirst().orElse(null);
            if (target == null) return;

            String scoreText = JOptionPane.showInputDialog(this, "Enter score out of 100 for " + target.getName() + ":", "Update Assessment Grade", JOptionPane.PLAIN_MESSAGE);
            if (scoreText == null || scoreText.isBlank()) return;

            try {
                float score = Float.parseFloat(scoreText.trim());
                target.setScore(score);
                target.setCompleted(true);
                appFacade.getCourse().setGradeReport(appFacade.getGradeGoal());
                refreshViews();
                outputArea.setText("Updated " + target.getName() + " score to " + score + "/100");
            } catch (NumberFormatException ex) {
                outputArea.setText("Please enter a valid score between 0 and 100.");
            }
        });

        setVisible(true);
    }

    // Rebuilds the checkboxes in the combined Materials/Target panel
    private void rebuildMaterialSelection() {
        materialSelectionPanel.removeAll();
        if (appFacade.getMaterials().isEmpty()) {
            JLabel empty = new JLabel("No materials uploaded yet.");
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            materialSelectionPanel.add(empty);
        } else {
            for (Material material : appFacade.getMaterials()) {
                JCheckBox checkBox = new JCheckBox(material.getFilename(), appFacade.getCourse().isMaterialSelected(material.getId()));
                checkBox.addActionListener(e -> appFacade.getCourse().toggleMaterialSelection(material.getId(), checkBox.isSelected()));
                checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
                materialSelectionPanel.add(checkBox);
            }
        }
        materialSelectionPanel.revalidate();
        materialSelectionPanel.repaint();
    }

    private void refreshViews() {
        summaryArea.setText(buildCourseSummary());
        assessmentsArea.setText(buildAssessmentList());
        rebuildMaterialSelection(); // Refresh checkboxes
        
        deadlinePanel.removeAll();
        deadlinePanel.add(new JScrollPane(new JTextArea(appFacade.generateDeadlineSummary())), BorderLayout.CENTER);
        deadlinePanel.revalidate();
        deadlinePanel.repaint();

        taskPanel.removeAll();
        taskPanel.add(new JScrollPane(new JTextArea(appFacade.generateTaskList())), BorderLayout.CENTER);
        taskPanel.revalidate();
        taskPanel.repaint();
    }

    private String buildCourseSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Course: ").append(appFacade.getCourse().getName()).append("\n\n");
        sb.append("Assessments: ").append(appFacade.getAssessments().size()).append("\n");
        sb.append("Materials: ").append(appFacade.getMaterials().size()).append("\n");
        sb.append("Target Grade Goal: ").append(String.format("%.2f", appFacade.getGradeGoal())).append("%\n");
        
        double currentAvg = appFacade.getAssessments().stream().mapToDouble(Assessment::getGrade).average().orElse(0.0);
        sb.append("Current average: ").append(String.format("%.2f", currentAvg)).append("%\n");

        double goal = appFacade.getGradeGoal();
        double totalWeight = 0;
        double completedWeight = 0;
        double completedPoints = 0;

        for (Assessment a : appFacade.getAssessments()) {
            totalWeight += a.getWeight();
            if (a.isCompleted()) {
                completedWeight += a.getWeight();
                completedPoints += (a.getScore() / 100.0) * a.getWeight();
            }
        }

        double remainingWeight = totalWeight - completedWeight;
        
        if (goal <= 0) {
             sb.append("Required on remaining: Set a Grade Goal\n");
        } else if (remainingWeight <= 0.001) { 
            sb.append("Required on remaining: All done!\n");
        } else {
            double currentGoalPoints = goal / 100.0;
            double neededPoints = currentGoalPoints - completedPoints;
            double requiredScore = (neededPoints / remainingWeight) * 100.0;

            if (requiredScore <= 0) {
                sb.append("Required on remaining: 0% (Goal achieved!)\n");
            } else if (requiredScore > 100) {
                sb.append("Required on remaining: >100% (Impossible)\n");
            } else {
                sb.append(String.format("Required on remaining: %.2f%%\n", requiredScore));
            }
        }

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
              .append(" | Score: ").append(String.format("%.0f", a.getScore()))
              .append(" | Completed: ").append(a.isCompleted() ? "Yes" : "No")
              .append("\n");
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