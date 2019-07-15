package arndt.com.workoutapp.activites.workoutlist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arndt.com.workoutapp.R;
import arndt.com.workoutapp.SQLLite;
import arndt.com.workoutapp.activites.objects.Pair;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static arndt.com.workoutapp.activites.workoutlist.Filters.filterItems;

public class FilterActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_filter);
        createBackButton();
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter and pass in our data model list
        String del = "8392kjds;l3k2j";
        for(String s : Arrays.asList("Exercise","Classifications","Muscles")){
            HashMap<String,List<String>> options = new HashMap<>();
            for(Pair<String, String> item : SQLLite.getCategories(s.toLowerCase())) {
                String k = String.format("%s%s%s", s,del,item.getKey());
                List<String> l = new ArrayList<>();
                if (options.containsKey(k))
                    l=options.get(k);
                else options.put(k,l);
                l.add(item.getValue());
            }
            for(Map.Entry<String, List<String>> option : options.entrySet()) {
                String[] split = option.getKey().split(del);
                filterItems.add(new FilterItem(split[0],split[1], option.getValue()));
            }
        }

        mAdapter = new FilterAdapter(filterItems);
        mRecyclerView.setAdapter(mAdapter);
        ((FilterAdapter)mAdapter).getPositionClicks().subscribe(filterItem -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Select Filters")
                    .setMultiChoiceItems(
                            filterItem.getValues().toArray(new CharSequence[filterItem.getValues().size()]),
                            filterItem.selected,
                            (dialogInterface, i, b) -> {

                            })
                    .setPositiveButton("OK", (dialogInterface, i) ->
                            Filters.addFilter(filterItem,filterItem.selected))
                    .setNegativeButton("CANCEL", (dialogInterface, i) -> { /*DO NOTHING TODO REMOVE FILTERS*/})
                    .create()
                    .show();
        });

        findViewById(R.id.clear).setOnClickListener(view -> {
            Filters.removeAllFilters();
            finish();
        });

        findViewById(R.id.confirm).setOnClickListener(view -> finish());
    }

    private void createBackButton() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Exercise Filter");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(view -> finish());
    }

    public class FilterItem{
        private String title, subtitle;
        private List<String> values;
        private boolean[] selected;

        public FilterItem(String title, String subtitle, List<String> values) {
            this.title = title;
            this.subtitle = subtitle;
            this.values = values;
            this.selected = new boolean[values.size()];
        }

        public boolean[] getSelected() {
            return selected;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getValues() {
            return values;
        }
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTextView, subtitleTextView;
        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView    = itemView.findViewById(R.id.title);
            subtitleTextView = itemView.findViewById(R.id.subtitle);
        }
    }

    public class FilterAdapter extends RecyclerView.Adapter<FilterActivity.FilterViewHolder> {
        private List<FilterItem> dataModelList;
        private final PublishSubject<FilterItem> onClickSubject = PublishSubject.create();

        public FilterAdapter(List<FilterItem> modelList) {
            dataModelList = modelList;
        }

        @NonNull
        @Override
        public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list, parent, false);
            // Return a new view holder
            return new FilterViewHolder(view);
        }
        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            onClickSubject.onComplete(); //here we avoid memory leaks
        }

        @Override
        public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
            final FilterItem dm = dataModelList.get(position);
            holder.itemView.setOnClickListener(v -> onClickSubject.onNext(dm));
            holder.titleTextView.setText(dm.getTitle());
            holder.subtitleTextView.setText(dm.getSubtitle().replace("_"," "));
        }

        public Observable<FilterItem> getPositionClicks(){
            return onClickSubject.hide();
        }

        @Override
        public int getItemCount() {
            return dataModelList.size();
        }
    }
}
