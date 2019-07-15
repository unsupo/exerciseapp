package arndt.com.workoutapp.activites.objects.workout;

import java.util.List;

public class WorkoutPlan {
    /**
     *      * WorkoutPlan
     *      *  boolean: Active
     *      *  String: name
     *      *  List<WorkoutGroup> workoutGroups
     */
    boolean isActive;
    String name;
    List<WorkoutGroup> workoutGroups;
}
