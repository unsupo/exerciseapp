package arndt.com.workoutapp.activites.objects;

public class Days {
    boolean monday,tuesday,wednesday,thursday,friday,saturday,sunday;

    public Days() {
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public void setIndex(int i, boolean b) {
        switch (i){
            case 0: monday = b; break;
            case 1: tuesday = b; break;
            case 2: wednesday = b; break;
            case 3: thursday = b; break;
            case 4: friday = b; break;
            case 5: saturday = b; break;
            case 6: sunday = b; break;
        }
    }
}
