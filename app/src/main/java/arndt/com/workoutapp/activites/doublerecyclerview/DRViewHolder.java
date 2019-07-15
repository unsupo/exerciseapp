package arndt.com.workoutapp.activites.doublerecyclerview;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import arndt.com.workoutapp.activites.doublerecyclerview.subrecyclerview.SubDRAdapter;

public class DRViewHolder extends RecyclerView.ViewHolder {
    private RecyclerView.Adapter mAdapter;
    private RecyclerView subRecyclerView;
    int subViewId;
    Activity context;
    List<Integer> subClickIds;

    public DRViewHolder(int recyclerViewId, @NonNull View itemView, Activity context, int subViewId, List<Integer> subClickIds) {
        super(itemView);
        this.subViewId = subViewId;
        this.subClickIds = subClickIds;
        this.context = context;
        this.subRecyclerView = context.findViewById(recyclerViewId);
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    public <T> void bindViews(T dm) {
        subRecyclerView.setLayoutManager(new LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
        ));
        mAdapter = new SubDRAdapter(((DataModel)dm).getSubData(),subViewId,subClickIds);
        subRecyclerView.setAdapter(mAdapter);
    }
}
