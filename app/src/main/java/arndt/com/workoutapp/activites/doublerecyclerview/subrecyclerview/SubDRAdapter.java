package arndt.com.workoutapp.activites.doublerecyclerview.subrecyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import arndt.com.workoutapp.activites.doublerecyclerview.DRViewHolder;
import arndt.com.workoutapp.activites.objects.Triplet;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class SubDRAdapter<T> extends RecyclerView.Adapter<SubDRViewHolder> {
    private List<T> data;
    private int viewId;
    private List<Integer> clickIds;
    private final PublishSubject<T> onSelfClickSubject = PublishSubject.create();
    private final PublishSubject<Triplet<SubDRViewHolder,Integer,Integer>> onClickSubjects = PublishSubject.create();

    public SubDRAdapter(List<T> data, int viewId,List<Integer> clickIds) {
        this.data = data;
        this.viewId = viewId;
        this.clickIds = clickIds;
    }

    @NonNull
    @Override
    public SubDRViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewId,parent,false);
        return new SubDRViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubDRViewHolder holder, int position) {
        final T dm = data.get(position);
        holder.itemView.setOnClickListener(v -> onSelfClickSubject.onNext(dm));
        clickIds.forEach(id->
                holder.itemView.findViewById(id)
                        .setOnClickListener(v->onClickSubjects.onNext(new Triplet<>(holder,id,position)))
        );
    }

    public Observable<T> getSelfPositionClicks(){
        return onSelfClickSubject.hide();
    }
    public Observable<Triplet<SubDRViewHolder,Integer,Integer>> getPositionClicks(){
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
