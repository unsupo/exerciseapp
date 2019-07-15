package arndt.com.workoutapp.activites.workoutscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.mikepenz.materialdrawer.Drawer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

import arndt.com.workoutapp.R;
import arndt.com.workoutapp.activites.data.SchedulerSingleton;
import arndt.com.workoutapp.activites.objects.Days;
import arndt.com.workoutapp.activites.objects.Triplet;
import arndt.com.workoutapp.activites.workoutlist.MenuCreator;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static arndt.com.workoutapp.activites.data.SchedulerSingleton.dataModelList;

/**
 * These are workout Programs.  You can one or more programs active at once.
 * This displays the list of programs
 *
 * each program has a schedule, each schedule consists of:
 *
 */
public class WorkoutSchedulerActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private WorkoutSchedulerActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_scheduler);
        context = this;

        Drawer result = MenuCreator.getDrawer(this, getResources().getString(R.string.workout_programs));
        if(result.getCurrentSelection() != R.id.menu_3)
            result.setSelection(R.id.menu_3);

        mRecyclerView = findViewById(R.id.recycler_view);

        TextView emptyView = findViewById(R.id.empty_view);

        if (dataModelList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        // use this setting to improve performance if you know that changes

        // in content do not change the layout size of the RecyclerView

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(true);
        // specify an adapter and pass in our data model list

        mAdapter = new WorkoutSchedulerActivity.SchedulerAdapter(dataModelList);
        mRecyclerView.setAdapter(mAdapter);
        ((SchedulerAdapter)mAdapter).onClickView.subscribe(triplet -> {
            switch (triplet.getSecond().intValue()){
                case R.id.delete:
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Delete Program")
                            .setMessage("Are You sure you want to delete workout program: "+dataModelList.get(triplet.getThird().intValue()).getTitle()+"?")
                            .setPositiveButton("YES",(dialogInterface, i) -> {
                                dataModelList.remove(triplet.getThird().intValue());
                                mAdapter.notifyDataSetChanged();
                                if (dataModelList.isEmpty()) {
                                    mRecyclerView.setVisibility(View.GONE);
                                    emptyView.setVisibility(View.VISIBLE);
                                }
                                else {
                                    mRecyclerView.setVisibility(View.VISIBLE);
                                    emptyView.setVisibility(View.GONE);
                                }
                            })
                            .setNegativeButton("NO",(dialogInterface, i) -> {})
                            .show();
                    break;
                case R.id.switchMaterial2: break;
                case R.id.add_schedule:
                    EditText txt = new EditText(context);
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("Add a Sub Schedule")
                            .setView(txt)
                            .setNegativeButton("Cancel", (dialogInterface, ii) -> {
                            })
                            .setPositiveButton("OK", (dialogInterface, ii) -> {
                                SchedulerViewHolder holder = triplet.getFirst();
                                List<SubSchedulerItem> sublist = dataModelList.get(triplet.getThird().intValue()).getSubSchedulerItems();
                                sublist.add(new SubSchedulerItem(txt.getText().toString()));
                                ((SubSchedulerAdapter) holder.nAdapter).setDataModelList(sublist);
                                holder.nAdapter.notifyDataSetChanged();
                                holder.nRecyclerView.setVisibility(View.VISIBLE);
                                holder.subemptyView.setVisibility(View.GONE);
                            })
                            .show();
                    break;
            }
        });
        ((SchedulerAdapter)mAdapter).getPositionClicks().subscribe(schedulerItem -> {
            View v = getLayoutInflater().inflate(R.layout.add_schedule_dialog, null);
            TextInputEditText t = v.findViewById(R.id.name);
            t.setOnKeyListener((view, i, keyEvent) -> positiveAction(schedulerItem,t,v)); //TODO this doesn't work for some reason
            t.setHint("New Name");
            t.setText(schedulerItem.getTitle());
            updateCheckBox(v,schedulerItem.getCheckbox(),Arrays.asList(R.id.Fst,R.id.Snd,R.id.Trd,R.id.Fth));
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Modify Schedule")
                    .setView(v)
                    .setNegativeButton("Cancel",(dialogInterface, i) -> {})
                    .setPositiveButton("Save",(dialogInterface, i) -> {
                        positiveAction(schedulerItem, t,v);
                    })
                    .show();
        });

        findViewById(R.id.floating_action_button).setOnClickListener(view -> {
            EditText txt = new EditText(context);
            new MaterialAlertDialogBuilder(this)
                    .setMessage("Input Name of Program")
                    .setView(txt)
                    .setNegativeButton("Cancel", (dialogInterface, i) -> { })
                    .setPositiveButton("OK", (dialogInterface, i) -> {
//                        SchedulerItem si = new SchedulerItem(
//                                ((TextInputEditText)v.findViewById(R.id.name)).getText().toString(),
//                                getCheckBoxString(v,Arrays.asList(R.id.Fst,R.id.Snd,R.id.Trd,R.id.Fth)),
//                                getCheckBoxValues(v,Arrays.asList(R.id.Fst,R.id.Snd,R.id.Trd,R.id.Fth)));
                        SchedulerItem si = new SchedulerItem(txt.getText().toString());
                        SchedulerSingleton.add(si);
                        Intent intent = new Intent(WorkoutSchedulerActivity.this, ProgramActivity.class);
                        intent.putExtra("key",si.uuid);
                        startActivity(intent);
//                        View v = getLayoutInflater().inflate(R.layout.add_schedule_dialog, null);
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    })
                    .show();
        });
    }

    private boolean positiveAction(SchedulerItem schedulerItem, TextInputEditText t, View v) {
        schedulerItem.setTitle(t.getText().toString());
        schedulerItem.setFrequency(getCheckBoxString(v,Arrays.asList(R.id.Fst,R.id.Snd,R.id.Trd,R.id.Fth)));
        schedulerItem.setCheckbox(getCheckBoxValues(v,Arrays.asList(R.id.Fst,R.id.Snd,R.id.Trd,R.id.Fth)));
        mAdapter.notifyDataSetChanged();
        return true;
    }

    private void updateCheckBox(View v, boolean[] checkbox, List<Integer> asList) {
        for (int i = 0; i < checkbox.length; i++)
            ((MaterialCheckBox)v.findViewById(asList.get(i)))
                    .setChecked(checkbox[i]);
    }

    private boolean[] getCheckBoxValues(View v, List<Integer> asList) {
        boolean[] b = new boolean[asList.size()];
        for (int i = 0; i < asList.size(); i++) {
            MaterialCheckBox checkBox = v.findViewById(asList.get(i));
            if (checkBox.isChecked())
                b[i] = true;
        }
        return b;
    }


    private String getCheckBoxString(View v, List<Integer> asList) {
        boolean[] b = new boolean[asList.size()];
        String s = "Weeks: ";
        int c = 0;
        for (int i = 0; i < asList.size(); i++) {
            MaterialCheckBox checkBox = v.findViewById(asList.get(i));
            if (checkBox.isChecked()){
                c++;
                b[i]=true;
                s+=checkBox.getText().toString().replace(" Week","")+",";
            }
        }
        if(c==1)
            s=s.replace("Weeks:","Week: ");
        return c == 0 ? "No Weeks" : c == asList.size() ? "All Weeks" : s.substring(0,s.length()-" , ".length());
    }

    /**
     * Schedule
     */
    public class SchedulerItem implements Serializable {
        private String title, frequency, uuid;
        private boolean[] checkbox;
        private boolean checked = true;
        private List<SubSchedulerItem> subSchedulerItems = new ArrayList<>();

        public SchedulerItem(String title) {
            this.title = title;
            uuid = UUID.randomUUID().toString();
        }

        public String getUuid() {
            return uuid;
        }

        public SchedulerItem(String title, String frequency, boolean[] checkbox) {
            this.title = title;
            this.frequency = frequency;
            this.checkbox = checkbox;
        }

        public String getTitle() {
            return title;
        }

        public String getFrequency() {
            return frequency;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }

        public boolean[] getCheckbox() {
            return checkbox;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public void setSubSchedulerItems(List<SubSchedulerItem> subSchedulerItems) {
            this.subSchedulerItems = subSchedulerItems;
        }

        public void setCheckbox(boolean[] checkbox) {
            this.checkbox = checkbox;
        }

        public List<SubSchedulerItem> getSubSchedulerItems() {
            return subSchedulerItems;
        }

        public boolean isChecked() {
            return checked;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SchedulerItem item = (SchedulerItem) o;
            return Objects.equals(uuid, item.uuid);
        }

        @Override
        public int hashCode() {
            return Objects.hash(uuid);
        }
    }

    public class SchedulerViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTextView, frequencyTextView;
//        private WeakReference<ClickListener> listenerRef;
        public SwitchMaterial switchMaterial;
        TextView subemptyView;

        public RecyclerView nRecyclerView;
        private RecyclerView.Adapter nAdapter;
        private RecyclerView.LayoutManager nLayoutManager;

        public SchedulerViewHolder(@NonNull View itemView) {//, ClickListener listener
            super(itemView);
            switchMaterial = itemView.findViewById(R.id.switchMaterial2);
            titleTextView    = itemView.findViewById(R.id.card_title);
            frequencyTextView = itemView.findViewById(R.id.frequency);

            nRecyclerView = itemView.findViewById(R.id.recycler_view);

            subemptyView = itemView.findViewById(R.id.empty_view);

            nAdapter = new SubSchedulerAdapter(context);
            SchedulerSingleton.adapterHashMap.put(WorkoutSchedulerActivity.class.getName()+UUID.randomUUID().toString(),mAdapter);//problem
            
            if (((SubSchedulerAdapter) nAdapter).dataModelList.isEmpty()) {
                nRecyclerView.setVisibility(View.GONE);
                subemptyView.setVisibility(View.VISIBLE);
            }
            else {
                nRecyclerView.setVisibility(View.VISIBLE);
                subemptyView.setVisibility(View.GONE);
            }

            nRecyclerView.setHasFixedSize(true);

            // use a linear layout manager

            nRecyclerView.setAdapter(nAdapter);
            nRecyclerView.setLayoutManager(nLayoutManager);
            nRecyclerView.setNestedScrollingEnabled(true);
            nLayoutManager = new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false);
            nRecyclerView.setHorizontalScrollBarEnabled(true);
            nRecyclerView.canScrollHorizontally(RecyclerView.HORIZONTAL);
            // specify an adapter and pass in our data model list

            ((SubSchedulerAdapter)nAdapter).onClickView.subscribe(triplet -> {
                List<SubSchedulerItem> subDataModelList = ((SubSchedulerAdapter) nAdapter).dataModelList;
                switch (triplet.getSecond().intValue()){
                    case R.id.subdelete:
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("Delete Program")
                            .setMessage("Are You sure you want to delete workout schedule: "+subDataModelList.get(triplet.getThird()).getTitle()+"?")
                            .setPositiveButton("YES",(dialogInterface, i) -> {
                                subDataModelList.remove(triplet.getThird().intValue());
                                nAdapter.notifyDataSetChanged();
                                nAdapter.notifyItemRemoved(triplet.getThird().intValue());
                                if (subDataModelList.isEmpty()) {
                                    nRecyclerView.setVisibility(View.GONE);
                                    subemptyView.setVisibility(View.VISIBLE);
                                }
                                else {
                                    nRecyclerView.setVisibility(View.VISIBLE);
                                    subemptyView.setVisibility(View.GONE);
                                }
                            })
                            .setNegativeButton("NO",(dialogInterface, i) -> {})
                            .show();
                    break;
                    case R.id.switchMaterial:
                        boolean checked = ((SwitchMaterial)triplet.getFirst().findViewById(triplet.getSecond().intValue())).isChecked();
                        subDataModelList.get(triplet.getThird().intValue()).setChecked(checked);
                    break;
                    case R.id.M:
                    case R.id.Tu:
                    case R.id.W:
                    case R.id.Th:
                    case R.id.F:
                    case R.id.Sa:
                    case R.id.Su:
                        SubSchedulerItem m = subDataModelList.get(triplet.getThird().intValue());
                        Days d = m.getDays();
                        boolean isDay = false;
                        int i = 0;
                        switch (triplet.getSecond().intValue()){
                            case R.id.M: isDay=d.isMonday(); break;
                            case R.id.Tu: isDay=d.isTuesday(); i=1; break;
                            case R.id.W: isDay=d.isWednesday(); i=2; break;
                            case R.id.Th: isDay=d.isThursday(); i=3; break;
                            case R.id.F: isDay=d.isFriday(); i=4; break;
                            case R.id.Sa: isDay=d.isSaturday(); i=5; break;
                            case R.id.Su: isDay=d.isSunday(); i=6; break;
                        }
                        triplet.getFirst().findViewById(triplet.getSecond().intValue())
                                .setBackgroundTintList(ContextCompat.getColorStateList(context, !isDay ? R.color.myCustomColor: R.color.default_day));
                        subDataModelList.get(triplet.getThird().intValue()).getDays().setIndex(i,!isDay);
                    break;
                }
            });
            ((SubSchedulerAdapter)nAdapter).getPositionClicks().subscribe(subschedulerItem -> {
                EditText t = new EditText(context);
                t.setHint("New Name");
                t.setText(subschedulerItem.getTitle());
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Modify Sub Schedule")
                        .setView(t)
                        .setNegativeButton("Cancel", (dialogInterface, i) -> { })
                        .setPositiveButton("OK", (dialogInterface, i) -> {
                            subschedulerItem.setTitle(t.getText().toString());
                            nAdapter.notifyDataSetChanged();
                        })
                        .show();
            });
        }
    }

    public class SchedulerAdapter extends RecyclerView.Adapter<WorkoutSchedulerActivity.SchedulerViewHolder> {
        private List<WorkoutSchedulerActivity.SchedulerItem> subdataModelList;
        private final PublishSubject<WorkoutSchedulerActivity.SchedulerItem> onClickSubject = PublishSubject.create();
        private final PublishSubject<Triplet<SchedulerViewHolder, Integer, Integer>> onClickView = PublishSubject.create();
        SchedulerViewHolder holder;

        public SchedulerAdapter(List<WorkoutSchedulerActivity.SchedulerItem> modelList) {
            this.subdataModelList = modelList;
        }

        public SchedulerViewHolder getHolder() {
            return holder;
        }

        @NonNull
        @Override
        public WorkoutSchedulerActivity.SchedulerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.workout_schedule, parent, false);
            holder = new SchedulerViewHolder(view);//,listener
            return holder;
        }
        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            onClickSubject.onComplete(); //here we avoid memory leaks
            onClickView.onComplete();
        }

        @Override
        public void onBindViewHolder(@NonNull WorkoutSchedulerActivity.SchedulerViewHolder holder, int position) {
            final WorkoutSchedulerActivity.SchedulerItem dm = dataModelList.get(position);
            holder.itemView.setOnClickListener(v -> onClickSubject.onNext(dm));
            for(int id : Arrays.asList(R.id.switchMaterial2,R.id.delete,R.id.add_schedule))
                holder.itemView.findViewById(id).setOnClickListener(v -> onClickView.onNext(new Triplet<>(holder,id, position)));
            holder.titleTextView.setText(dm.getTitle());
            holder.frequencyTextView.setText(dm.getFrequency());
            holder.switchMaterial.setChecked(dm.isChecked());
            ((SubSchedulerAdapter)holder.nAdapter).setDataModelList(dm.getSubSchedulerItems());
            holder.nAdapter.notifyDataSetChanged();
        }

        public Observable<SchedulerItem> getPositionClicks(){
            return onClickSubject.hide();
        }

        @Override
        public int getItemCount() {
            return subdataModelList.size();
        }

    }


    /**
     * Sub Schedule
     */
    public static class SubSchedulerItem implements Serializable{
        private String title, frequency;
        boolean checked = true;
        private Days days = new Days();

        public SubSchedulerItem(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public String getFrequency() {
            return frequency;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setDays(Days days) {
            this.days = days;
        }

        public Days getDays() {
            return days;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }
    }

    public static class SubSchedulerViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTextView, frequencyTextView;
        HashMap<Integer,Object> viewObjs = new HashMap<>();

        public SubSchedulerViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView    = itemView.findViewById(R.id.card_title);
            frequencyTextView = itemView.findViewById(R.id.frequency);
            for(int id : Arrays.asList(R.id.subdelete,R.id.M,R.id.Tu,R.id.W,R.id.Th,R.id.F,
                    R.id.Sa,R.id.Su,R.id.workouts,R.id.switchMaterial))
                viewObjs.put(id,itemView.findViewById(id));
        }

        public HashMap<Integer, Object> getViewObjs() {
            return viewObjs;
        }
    }

    public static class SubSchedulerAdapter extends RecyclerView.Adapter<SubSchedulerViewHolder> {
        public List<SubSchedulerItem> dataModelList = new ArrayList<>();
        private final PublishSubject<SubSchedulerItem> onClickSubject = PublishSubject.create();
        public final PublishSubject<Triplet<View, Integer, Integer>> onClickView = PublishSubject.create();
        private AppCompatActivity context;
        public SubSchedulerAdapter(AppCompatActivity context) {
//            subdataModelList = modelList;
            this.context = context;
        }

        public List<SubSchedulerItem> getDataModelList() {
            return dataModelList;
        }

        public void setDataModelList(List<SubSchedulerItem> dataModelList) {
            this.dataModelList = dataModelList;
        }

        @NonNull
        @Override
        public SubSchedulerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.schedule, parent,false);
            return new SubSchedulerViewHolder(view);
        }
        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            onClickSubject.onComplete(); //here we avoid memory leaks
            onClickView.onComplete();
//            super.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onBindViewHolder(@NonNull SubSchedulerViewHolder holder, int position) {
            final SubSchedulerItem dm = dataModelList.get(position);
            holder.itemView.setOnClickListener(v -> onClickSubject.onNext(dm));
            for(int id : Arrays.asList(R.id.subdelete,R.id.M,R.id.Tu,R.id.W,R.id.Th,R.id.F,R.id.Sa,R.id.Su,R.id.workouts,R.id.switchMaterial)) {
                holder.itemView.findViewById(id).setOnClickListener(v -> onClickView.onNext(new Triplet<>(holder.itemView, id, position)));
                switch (id){
                    case R.id.switchMaterial: ((SwitchMaterial)holder.viewObjs.get(id)).setChecked(dm.checked); break;
                    case R.id.M: ((MaterialButton)holder.viewObjs.get(id)).
                            setBackgroundTintList(ContextCompat
                                    .getColorStateList(context, dm.days.isMonday() ? R.color.myCustomColor: R.color.default_day));break;
                    case R.id.Tu: ((MaterialButton)holder.viewObjs.get(id)).
                            setBackgroundTintList(ContextCompat
                                    .getColorStateList(context, dm.days.isTuesday() ? R.color.myCustomColor: R.color.default_day));break;
                    case R.id.W: ((MaterialButton)holder.viewObjs.get(id)).
                            setBackgroundTintList(ContextCompat
                                    .getColorStateList(context, dm.days.isWednesday() ? R.color.myCustomColor: R.color.default_day));break;
                    case R.id.Th: ((MaterialButton)holder.viewObjs.get(id)).
                            setBackgroundTintList(ContextCompat
                                    .getColorStateList(context, dm.days.isThursday() ? R.color.myCustomColor: R.color.default_day));break;
                    case R.id.F: ((MaterialButton)holder.viewObjs.get(id)).
                            setBackgroundTintList(ContextCompat
                                    .getColorStateList(context, dm.days.isFriday() ? R.color.myCustomColor: R.color.default_day));break;
                    case R.id.Sa: ((MaterialButton)holder.viewObjs.get(id)).
                            setBackgroundTintList(ContextCompat
                                    .getColorStateList(context, dm.days.isSaturday() ? R.color.myCustomColor: R.color.default_day));break;
                    case R.id.Su: ((MaterialButton)holder.viewObjs.get(id)).
                            setBackgroundTintList(ContextCompat
                                    .getColorStateList(context, dm.days.isSunday() ? R.color.myCustomColor: R.color.default_day));break;
                }
            }
            holder.titleTextView.setText(dm.getTitle());
        }

        public Observable<SubSchedulerItem> getPositionClicks(){
            return onClickSubject.hide();
        }

        @Override
        public int getItemCount() {
            System.out.println(dataModelList.size());
            return dataModelList.size();
        }
    }
}
