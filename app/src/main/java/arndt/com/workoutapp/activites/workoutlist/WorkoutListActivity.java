package arndt.com.workoutapp.activites.workoutlist;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Filter;
import java.util.stream.Collectors;

import arndt.com.workoutapp.DataModel;
import arndt.com.workoutapp.MyAdapter;
import arndt.com.workoutapp.R;
import arndt.com.workoutapp.SQLLite;
import arndt.com.workoutapp.activites.objects.Pair;

public class WorkoutListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private TabLayout mTabLayout,mTabLayoutSub;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SearchView searchView;
    private int start=0,incr=50,end = incr;

    private boolean mLoading = false;
    Random r = new Random();
    final List<DataModel> allDataModels = new ArrayList<>(), tabModelList = new ArrayList<>(),
            dataModelList = new ArrayList<>(), filteredModelList = new ArrayList<>();
    int bID = r.nextInt(100);

    HashMap<String,String> tabFiltering = new HashMap<>();
    private void selectTab(){
        TabLayout.Tab t = mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition());
        // remove all subtabs
        mTabLayoutSub.removeAllTabs();
        if(!t.getText().toString().toLowerCase().equals("all")){
            // add subtabs for options in that category
            for(Pair<String, String> c : SQLLite.getCategories(t.getText().toString().toLowerCase())) {
                if(c.getKey().trim().isEmpty() || c.getKey().trim().toLowerCase().equals("null") ||
                        c.getValue().trim().isEmpty() || c.getValue().trim().toLowerCase().equals("null")) continue;
                mTabLayoutSub.addTab(mTabLayoutSub.newTab().setText(c.getValue().trim()));
                tabFiltering.put(c.getValue().trim(),c.getKey().trim());
            }
            // filter list based off of option
            selectSubTab();
            // set linearlayout height due to new tablayout
        }else { // remove all filters
            tabModelList.addAll(allDataModels);
            query("");
        }
    }

    private void selectSubTab() {
        TabLayout.Tab t = mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition());
        TabLayout.Tab subt = mTabLayoutSub.getTabAt(mTabLayoutSub.getSelectedTabPosition());
        // filter based on subtab
        String category = tabFiltering.get(subt.getText());
        tabModelList.addAll(_query(String.format("%s:%s", category,subt.getText())));
        query("");
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_workouts_tab);
        createBackButton();
        Drawer result = MenuCreator.getDrawer(this,getResources().getString(R.string.exercise_list));
        if(result.getCurrentSelection() != R.id.menu_2)
            result.setSelection(R.id.menu_2);
        Filters.filteredModelList = allDataModels;
        Filters.allDataModels = allDataModels;
        Filters.ListActivity = this;

        mRecyclerView = findViewById(R.id.recycler_view);
//        SQLLite s = new SQLLite(this);
        allDataModels.addAll(SQLLite.getAllWorkouts());
        filteredModelList.addAll(allDataModels);
        dataModelList.addAll(allDataModels.subList(start,end));
        // use this setting to improve performance if you know that changes

        // in content do not change the layout size of the RecyclerView

        mRecyclerView.setHasFixedSize(true);
//        listView.setFastScrollEnabled(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                scrollIt();
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItem = mLayoutManager.getItemCount();
                int lastVisibleItem = ((LinearLayoutManager) mLayoutManager).findFirstCompletelyVisibleItemPosition();

                if (!mLoading && lastVisibleItem == totalItem - incr*.1) {
                    mLoading = true;
                    // Scrolled to bottom. Do something here.
                    scrollIt();
                    mLoading = false;
                }
            }
            private void scrollIt(){
                int initialSize = dataModelList.size();
                start+=incr;
                if(start > filteredModelList.size())
                    start=Math.max(filteredModelList.size()-incr,filteredModelList.size());
                end=Math.min(incr+end,filteredModelList.size());
                dataModelList.addAll(filteredModelList.subList(start,end));
                int endSize = dataModelList.size();
                mAdapter.notifyItemRangeInserted(initialSize,endSize);
            }
        });

        // specify an adapter and pass in our data model list

        mAdapter = new MyAdapter(dataModelList, getLifecycle());
        mRecyclerView.setAdapter(mAdapter);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                query(query);
                return false;
            }
        });
        ImageView closeButton = searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(view ->{
            query("");
            //Find EditText view
            EditText et = findViewById(R.id.search_src_text);

            //Clear the text from EditText view
            et.setText("");

            //Clear query
            searchView.setQuery("", false);
            //Collapse the action view
            searchView.onActionViewCollapsed();
            //Collapse the search widget
            menu.findItem(R.id.search).collapseActionView();
        });
        findViewById(R.id.filter).setOnClickListener(view -> {
            Intent myIntent = new Intent(WorkoutListActivity.this, FilterActivity.class);
            WorkoutListActivity.this.startActivity(myIntent);
        });

        return super.onCreateOptionsMenu(menu);
    }
    public void filtered(List<DataModel> filteredModelList){
        this.filteredModelList.clear();
        this.filteredModelList.addAll(filteredModelList);
        start = 0;
        end = Math.min(incr, filteredModelList.size());
        dataModelList.clear();
        dataModelList.addAll(filteredModelList.subList(start, end));
        mAdapter.notifyDataSetChanged();
    }
    private void query(String query) {
        filteredModelList.clear();
        if(query.isEmpty())
            filteredModelList.addAll(tabModelList.subList(start,tabModelList.size() < end ?tabModelList.size() : end));
        else
            filteredModelList.addAll(_query(query));
        start = 0;
        end = Math.min(incr, filteredModelList.size());
        dataModelList.clear();
        dataModelList.addAll(filteredModelList.subList(start, end));
        mAdapter.notifyDataSetChanged();
    }
    private List<DataModel> _query(String query){
        List<Pair<String,String>> filters = new ArrayList<>();
        if(query.contains(":"))
            for(String s : query.split(",")) {
                if(!s.contains(":")) continue;
                String[] sp = s.split(":");
                filters.add(new Pair<>(sp[0].trim(),sp[1].trim()));
            }
        else
            return tabModelList.parallelStream()
                    .filter(a -> a.query(query))
                    .collect(Collectors.toList());
        return tabModelList.parallelStream()
                .filter(a->{
                    for(Pair<String, String> p : filters)
                        for(String s : a.getProperties().get(p.getKey()))
                            if(s.toLowerCase().equals(p.getValue().toLowerCase()))
                                return true;
                    return false;
                }).collect(Collectors.toList());
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createBackButton() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.workout_list_name);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(view -> finish());
    }
}
