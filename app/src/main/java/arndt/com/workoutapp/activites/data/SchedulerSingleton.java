package arndt.com.workoutapp.activites.data;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import arndt.com.workoutapp.activites.workoutscheduler.WorkoutSchedulerActivity;

public class SchedulerSingleton {
    public static List<WorkoutSchedulerActivity.SchedulerItem> dataModelList = new ArrayList<>();
    private static HashMap<String, WorkoutSchedulerActivity.SchedulerItem> map = new HashMap<>();
    public static WorkoutSchedulerActivity.SchedulerItem get(String key) {
        return map.get(key);
    }
    public static void add(WorkoutSchedulerActivity.SchedulerItem item){
        map.put(item.getUuid(),item);
        dataModelList.clear();
        dataModelList.addAll(map.values());
    }
    public static HashMap<String, RecyclerView.Adapter> adapterHashMap = new HashMap<>();
}
