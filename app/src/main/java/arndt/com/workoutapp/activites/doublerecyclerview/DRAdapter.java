package arndt.com.workoutapp.activites.doublerecyclerview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import arndt.com.workoutapp.activites.objects.Triplet;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class DRAdapter<T> extends RecyclerView.Adapter<DRViewHolder> {
    private List<T> data;
    private int viewId,subViewId,subRecyclerViewId;
    private List<Integer> clickIds,subClickIds;
    private final PublishSubject<T> onSelfClickSubject = PublishSubject.create();
    private final PublishSubject<Triplet<DRViewHolder,Integer,Integer>> onClickSubjects = PublishSubject.create();
    private Activity context;

    /**
     * Use this if you want to create a horizontal list in a verticle list.
     * @param context this is the activity running this whole ting
     * @param subRecyclerViewId this is the view id the sub recycler view
     * @param data this is the list of data with each item having a list of sub data
     * @param viewId this is the view id of the list item to be inflated
     * @param clickIds this is a list of view ids that you want to subscribe to the click events
     * @param subViewId this is the view id of the sub list item you want to be inflated
     * @param subClickIds this is a list of view ids of sub items you want to subscirbe to the click events
     */
    public DRAdapter(Activity context,int subRecyclerViewId, List<T> data, int viewId, List<Integer> clickIds, int subViewId, List<Integer> subClickIds) {
        this.data = data;
        this.viewId = viewId;
        this.clickIds = clickIds;
        this.subClickIds = subClickIds;
        this.subViewId = subViewId;
        this.context = context;
        this.subRecyclerViewId = subRecyclerViewId;
    }

    @NonNull
    @Override
    public DRViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewId,parent,false);
        return new DRViewHolder(subRecyclerViewId,view,context,subViewId,subClickIds);
    }

    @Override
    public void onBindViewHolder(@NonNull DRViewHolder holder, int position) {
        final T dm = data.get(position);
        holder.bindViews(dm);
        holder.itemView.setOnClickListener(v -> onSelfClickSubject.onNext(dm));
        clickIds.forEach(id->
                holder.itemView.findViewById(id)
                    .setOnClickListener(v->onClickSubjects.onNext(new Triplet<>(holder,id,position)))
        );
    }

    public Observable<T> getSelfPositionClicks(){
        return onSelfClickSubject.hide();
    }
    public Observable<Triplet<DRViewHolder,Integer,Integer>> getPositionClicks(){
        return onClickSubjects.hide();
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

    }
}
