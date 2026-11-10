package course.material;

import course.Course;
import course.CourseListener;
import java.util.List;

public class MaterialTracker implements CourseListener {

    @Override
    public void update(Course course) {
        if (course == null) {
            return;
        }

        List<Material> materials = course.getMaterials();
        if (materials.isEmpty()) {
            System.out.println("MaterialTracker: No materials for course " + course.getId());
            return;
        }

        for (Material material : materials) {
            System.out.println(
                "MaterialTracker: " + material.getFilename() + " | type=" + material.getType()
                + " | path=" + material.getFilepath()
            );
        }
    }
}
