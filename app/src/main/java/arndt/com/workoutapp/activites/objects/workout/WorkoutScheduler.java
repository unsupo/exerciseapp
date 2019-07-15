package arndt.com.workoutapp.activites.objects.workout;

public class WorkoutScheduler {
    public WorkoutScheduler() {
        loadSchedules();
    }

    private void loadSchedules() {
        // from sqllite load schedules created
        // default is
        // BRO SPLIT:  MTu = PUSH, ThF = PULL, W = Legs, Cardio
        // 1 hour at the gym,
        // 3 sets of 8 with 1 minute break between
        // --- assuming 30 seconds per set,
        // --- 1.5+1=2.5 minutes then: ~ ^10 minutes per workout
        // --- At max, 6 workouts, 5 is more reasonable
        // 5 workouts
        // Once week a month strength: 1st of the month
        // --- strength week has 3 sets of 3 with 2 minutes break
    }

    /**
     * A workout plan consists of all above details in individual units
     * for instance
     * M-W--Sa- (may or may not define a time)
     * -> these exercises (reps/sets)
     *
     * Object
     * WorkoutPlan
     *  boolean: Active
     *  String: name
     *  List<WorkoutGroup> workoutGroups
     *
     * WorkoutGroup
     *  String: name
     *  days: SuMTuWThFSa
     *  Even, Odd, Interval (every n days)
     *  GPS: AT (ie vasa location optional)
     *
     * option to export to google calander
     */
}
