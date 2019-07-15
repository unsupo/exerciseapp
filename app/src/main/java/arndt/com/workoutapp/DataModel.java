package arndt.com.workoutapp;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DataModel {
    public String video;
    private int imageDrawable;
    private String title;
    private String subTitle;
    private HashMap<String, List<String>> properties;

    public DataModel(int id,String video) {
        imageDrawable = R.drawable.list_image;
        title = String.format(Locale.ENGLISH, "Title %d Goes Here", id);
        subTitle = String.format(Locale.ENGLISH, "Sub title %d goes here", id);
        this.video = video;
    }

    public DataModel(HashMap<String, List<String>> row, String video, String title, String subTitle) {
        this.properties = row;
        imageDrawable = R.drawable.list_image;
        this.video = video;
        this.title = title;
        this.subTitle = subTitle;
    }

    public HashMap<String, List<String>> getProperties() {
        return properties;
    }

    public int getImageDrawable() {
        return imageDrawable;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public boolean query(String query) {
        String q = query.toLowerCase();
        if(getTitle().toLowerCase().contains(q) ||
                getSubTitle().toLowerCase().contains(q))
            return true;
        if(properties != null)
            for(List<String> pl: properties.values())
                for(String p : pl)
                    if(p.toLowerCase().contains(q))
                        return true;
        return false;
    }
}