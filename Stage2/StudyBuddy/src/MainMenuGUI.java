
import ai.controller.AppFacade;
import ai.factory.AIFactory;
import course.Course;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;


public class MainMenuGUI extends JFrame {
    private final List<Course> courses = new ArrayList<>();
    private final Map<String, AppFacade> courseFacades = new HashMap<>();
    private final List<JButton> courseButtons = new ArrayList<>();
    private final JPanel courseListPanel;   // holds just the course buttons
    private final Dimension btnSize = new Dimension(420, 80);

    public MainMenuGUI(AppFacade appFacade) {
        super("StudyBuddy");

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

        // ---- Left panel wrapper ----
        // ---- Left panel wrapper ----
        JPanel leftPanel = new JPanel(new BorderLayout(8, 8));   // ← BorderLayout, NOT BoxLayout
        leftPanel.setBorder(BorderFactory.createTitledBorder("Courses"));
        leftPanel.setPreferredSize(new Dimension(450, 0));
        add(leftPanel, BorderLayout.WEST);

        // The panel that holds course buttons (goes INSIDE the scroll pane only)
        courseListPanel = new JPanel();
        courseListPanel.setLayout(new BoxLayout(courseListPanel, BoxLayout.Y_AXIS));
        courseListPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Wrap it in the scroll pane
        JScrollPane scrollPane = new JScrollPane(courseListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        leftPanel.add(scrollPane, BorderLayout.CENTER);   // ← scroll pane in CENTER

        // ---- "Add Course" button pinned at bottom ----
        JButton addCourseBtn = new JButton("+ Add Course");
        Dimension addBtnSize = new Dimension(420, 40);
        addCourseBtn.setPreferredSize(addBtnSize);
        addCourseBtn.setMinimumSize(addBtnSize);
        addCourseBtn.setMaximumSize(addBtnSize);
        addCourseBtn.addActionListener(e -> onAddCourseClicked());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        bottomPanel.add(addCourseBtn);
        leftPanel.add(bottomPanel, BorderLayout.SOUTH);   // ← pinned at bottom

        // ---- Right panel ----
        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Overview"));
        add(rightPanel, BorderLayout.CENTER);

        JTextArea overviewText = new JTextArea();
        overviewText.setEditable(false);
        overviewText.setLineWrap(true);
        overviewText.setWrapStyleWord(true);
        overviewText.setText("Select a course to view its materials, assessments, and AI study tools.");
        rightPanel.add(new JScrollPane(overviewText), BorderLayout.CENTER);

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

        // User cancelled or left it blank
        if (name == null) return;
        name = name.trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Course name cannot be empty.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Prevent duplicates
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

        // Clicking a course button -> open that course (hook into appFacade)
        btn.addActionListener(e -> openCourse(course));

        courseButtons.add(btn);
        courseListPanel.add(btn);
        courseListPanel.add(Box.createVerticalStrut(8));
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