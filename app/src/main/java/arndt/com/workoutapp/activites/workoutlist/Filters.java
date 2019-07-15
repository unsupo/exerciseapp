package arndt.com.workoutapp.activites.workoutlist;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import arndt.com.workoutapp.DataModel;

public class Filters {
    public static WorkoutListActivity ListActivity;
    static List<DataModel> allDataModels = new ArrayList<>(), filteredModelList = new ArrayList<>();

    private Filters(){}
    public static List<FilterActivity.FilterItem> filterItems = new ArrayList<>();

    public static void addFilter(FilterActivity.FilterItem filterItem, boolean[] checkedItems) {
        List<DataModel> filtered = filteredModelList.parallelStream().filter(a -> {
            for (int i = 0; i < filterItem.getValues().size(); i++) {
                if (checkedItems[i])
                    if (!a.getProperties().containsKey(filterItem.getSubtitle()))
                        return false;
                    else {
                        boolean hasItem = false;
                        for (String v : a.getProperties().get(filterItem.getSubtitle()))
                            if (filterItem.getValues().get(i).equals(v))
                                return true;
                    }
            }
            return false;
        }).collect(Collectors.toList());
        ListActivity.filtered(filtered);
    }

    public static void removeAllFilters(){
        List<DataModel> filtered = new ArrayList<>();
        filtered.addAll(allDataModels);
        ListActivity.filtered(filtered);
    }

    public void getFilters(){

    }
}
