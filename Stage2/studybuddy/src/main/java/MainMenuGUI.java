import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import ai.controller.AppFacade;
import ai.factory.AIFactory;
import course.Course;

public class MainMenuGUI extends JFrame {
    private static final List<Course> SHARED_COURSES = new ArrayList<>();
    private static final Map<String, AppFacade> SHARED_FACADES = new HashMap<>();

    private final List<Course> courses;
    private final Map<String, AppFacade> courseFacades;
    private final List<JButton> courseButtons = new ArrayList<>();
    private final JPanel courseListPanel;
    private final Dimension btnSize = new Dimension(420, 80);

    public MainMenuGUI(AppFacade appFacade) {
        super("StudyBuddy");
        this.courses = SHARED_COURSES;
        this.courseFacades = SHARED_FACADES;

        if (appFacade != null && appFacade.getCourse() != null) {
            registerCourse(appFacade.getCourse());
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocation(40, 40);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("StudyBuddy AI Assistant", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel leftPanel = new JPanel(new BorderLayout(8, 8));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Courses"));
        leftPanel.setPreferredSize(new Dimension(450, 0));
        add(leftPanel, BorderLayout.WEST);

        courseListPanel = new JPanel();
        courseListPanel.setLayout(new BoxLayout(courseListPanel, BoxLayout.Y_AXIS));
        courseListPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(courseListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        leftPanel.add(scrollPane, BorderLayout.CENTER);

        JButton addCourseBtn = new JButton("+ Add Course");
        Dimension addBtnSize = new Dimension(420, 40);
        addCourseBtn.setPreferredSize(addBtnSize);
        addCourseBtn.setMinimumSize(addBtnSize);
        addCourseBtn.setMaximumSize(addBtnSize);
        addCourseBtn.addActionListener(e -> onAddCourseClicked());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        bottomPanel.add(addCourseBtn);
        leftPanel.add(bottomPanel, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Overview"));
        add(rightPanel, BorderLayout.CENTER);

        JTextArea overviewText = new JTextArea();
        overviewText.setEditable(false);
        overviewText.setLineWrap(true);
        overviewText.setWrapStyleWord(true);
        overviewText.setText("Select a course to view its materials, assessments, and AI study tools.");
        rightPanel.add(new JScrollPane(overviewText), BorderLayout.CENTER);

        String modeLabel = "AI Mode: " + (AIFactory.createDefault().getClass().getSimpleName().contains("Online") ? "cloud" : "local");
        JButton modeButton = new JButton(modeLabel);
        modeButton.addActionListener(e -> switchAIMode());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.add(modeButton);
        rightPanel.add(topBar, BorderLayout.NORTH);

        refreshCourseButtons();
        setVisible(true);
    }

    private void registerCourse(Course course) {
        if (course == null) {
            return;
        }

        if (courses.stream().anyMatch(existing -> existing.getId().equals(course.getId()) || existing.getName().equalsIgnoreCase(course.getName()))) {
            return;
        }

        courses.add(course);
        courseFacades.put(course.getId(), new AppFacade(AIFactory.createDefault(), course));
    }

    private void refreshCourseButtons() {
        courseListPanel.removeAll();
        courseButtons.clear();

        for (Course course : courses) {
            addCourseButton(course);
        }

        courseListPanel.revalidate();
        courseListPanel.repaint();
    }

    private void onAddCourseClicked() {
        String name = JOptionPane.showInputDialog(
                this,
                "Enter course name (e.g. EECS 3311):",
                "Add Course",
                JOptionPane.PLAIN_MESSAGE);

        if (name == null) return;
        name = name.trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Course name cannot be empty.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (Course existing : courses) {
            if (existing.getName().equalsIgnoreCase(name)) {
                JOptionPane.showMessageDialog(this,
                        "Course \"" + name + "\" already exists.",
                        "Duplicate Course", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String id = "course-" + (courses.size() + 1);
        Course newCourse = new Course(id, name);
        registerCourse(newCourse);
        refreshCourseButtons();
    }

    private void addCourseButton(Course course) {
        JButton btn = new JButton(course.getName());
        btn.setPreferredSize(btnSize);
        btn.setMinimumSize(btnSize);
        btn.setMaximumSize(btnSize);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> openCourse(course));

        courseButtons.add(btn);
        courseListPanel.add(btn);
        courseListPanel.add(Box.createVerticalStrut(8));
    }

    private void switchAIMode() {
        String current = System.getProperty("studybuddy.ai.mode", "auto");
        String next = "local".equalsIgnoreCase(current) ? "cloud" : "local";
        System.setProperty("studybuddy.ai.mode", next);
        JOptionPane.showMessageDialog(this, "AI mode set to: " + next + "\nNew course instances will use this mode.");
    }

    private void openCourse(Course course) {
        AppFacade facade = courseFacades.get(course.getId());
        if (facade == null) {
            facade = new AppFacade(AIFactory.createDefault(), course);
            courseFacades.put(course.getId(), facade);
        }

        this.dispose();
        new CourseDetailGUI(facade);
    }
}