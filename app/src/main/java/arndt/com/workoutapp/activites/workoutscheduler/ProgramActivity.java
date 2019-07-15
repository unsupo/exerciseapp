package arndt.com.workoutapp.activites.workoutscheduler;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

import arndt.com.workoutapp.R;
import arndt.com.workoutapp.activites.data.SchedulerSingleton;
import arndt.com.workoutapp.activites.objects.Days;

public class ProgramActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<WorkoutSchedulerActivity.SubSchedulerItem> dataModelList;
    WorkoutSchedulerActivity.SchedulerItem program;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_scheduler);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            program = SchedulerSingleton.get(extras.getString("key"));
        createBackButton("Program: "+program.getTitle());
        dataModelList = program.getSubSchedulerItems();
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
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new WorkoutSchedulerActivity.SubSchedulerAdapter(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mLayoutManager = new LinearLayoutManager(this);
        ((WorkoutSchedulerActivity.SubSchedulerAdapter) mAdapter).setDataModelList(dataModelList);

        ((WorkoutSchedulerActivity.SubSchedulerAdapter)mAdapter).onClickView.subscribe(triplet -> {
            List<WorkoutSchedulerActivity.SubSchedulerItem> subDataModelList = ((WorkoutSchedulerActivity.SubSchedulerAdapter) mAdapter).dataModelList;
            switch (triplet.getSecond().intValue()){
                case R.id.subdelete:
                    new MaterialAlertDialogBuilder(this)
                            .setTitle("Delete Program")
                            .setMessage("Are You sure you want to delete workout schedule: "+subDataModelList.get(triplet.getThird()).getTitle()+"?")
                            .setPositiveButton("YES",(dialogInterface, i) -> {
                                subDataModelList.remove(triplet.getThird().intValue());
                                mAdapter.notifyDataSetChanged();
                                mAdapter.notifyItemRemoved(triplet.getThird().intValue());
                                if (subDataModelList.isEmpty()) {
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
                    WorkoutSchedulerActivity.SubSchedulerItem m = subDataModelList.get(triplet.getThird().intValue());
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
                            .setBackgroundTintList(ContextCompat.getColorStateList(this, !isDay ? R.color.myCustomColor: R.color.default_day));
                    subDataModelList.get(triplet.getThird().intValue()).getDays().setIndex(i,!isDay);
                    break;
            }
        });
        ((WorkoutSchedulerActivity.SubSchedulerAdapter)mAdapter).getPositionClicks().subscribe(subschedulerItem -> {
            EditText t = new EditText(this);
            t.setHint("New Name");
            t.setText(subschedulerItem.getTitle());
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Modify Sub Schedule")
                    .setView(t)
                    .setNegativeButton("Cancel", (dialogInterface, i) -> { })
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        subschedulerItem.setTitle(t.getText().toString());
                        mAdapter.notifyDataSetChanged();
                    })
                    .show();
        });
        findViewById(R.id.floating_action_button).setOnClickListener(view -> {
            EditText txt = new EditText(this);
            new MaterialAlertDialogBuilder(this)
                .setTitle("Add a Sub Schedule")
                .setView(txt)
                .setNegativeButton("Cancel", (dialogInterface, ii) -> {
                })
                .setPositiveButton("OK", (dialogInterface, ii) -> {
                    dataModelList.add(new WorkoutSchedulerActivity.SubSchedulerItem(txt.getText().toString()));
                    ((WorkoutSchedulerActivity.SubSchedulerAdapter) mAdapter).setDataModelList(dataModelList);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    for(RecyclerView.Adapter a : SchedulerSingleton.adapterHashMap.values())
                        a.notifyDataSetChanged();
                })
                .show();
        });
    }

    private void createBackButton(String name) {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(name);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(view -> finish());
    }
}
