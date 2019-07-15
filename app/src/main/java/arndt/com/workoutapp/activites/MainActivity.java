package arndt.com.workoutapp.activites;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.List;

import arndt.com.workoutapp.DataModel;
import arndt.com.workoutapp.MyAdapter;
import arndt.com.workoutapp.R;
import arndt.com.workoutapp.SQLLite;
import arndt.com.workoutapp.activites.workoutlist.MenuCreator;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 932;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AsyncTask.execute(()->SQLLite.getInstance(this));
        setContentView(R.layout.activity_main);

        result = MenuCreator.getDrawer(this, getResources().getString(R.string.my_workout));
        if(result.getCurrentSelection() != R.id.menu_1)
            result.setSelection(R.id.menu_1);

        mRecyclerView = findViewById(R.id.recycler_view);

        List<DataModel> dataModelList = SQLLite.getTodaysWorkout();
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

        // specify an adapter and pass in our data model list

        mAdapter = new MyAdapter(dataModelList, getLifecycle());
        mRecyclerView.setAdapter(mAdapter);
    }

    private Drawer result = null;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
