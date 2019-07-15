package arndt.com.workoutapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExerciseObj {
    String name,eid;
//    String name,type,group_name,videourl,gifurl,utility,mechanics,force,function,intensity,
//                        preparation,execution,easier,comments,muscle_name,link,muscles_type;
//    int eid,cid,iid,mid;
    HashMap<String, List<String>> properties = new HashMap<>();
    public ExerciseObj(String name, String eid) {
        this.name = name;
        this.eid = eid;
    }

    public ExerciseObj addProperties(HashMap<String,String> properties){
        for(Map.Entry<String,String> e : properties.entrySet())
            if(this.properties.containsKey(e.getKey()))
                this.properties.get(e.getKey()).add(e.getValue());
            else this.properties.put(e.getKey(),new ArrayList<>(new HashSet<String>(Arrays.asList(e.getValue()))));
        return this;
    }

    @Override
    public String toString() {
        return "ExerciseObj{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExerciseObj that = (ExerciseObj) o;
        return eid == that.eid &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, eid);
    }
}
