package arndt.com.workoutapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private Lifecycle lifecycle;
    private List<DataModel> dataModelList;
    private List<DataModel> filteredDataModelList;

    public MyAdapter(List<DataModel> modelList, Lifecycle lifecycle) {
        dataModelList = modelList;
        this.lifecycle = lifecycle;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate out card list item
//        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) LayoutInflater.from(parent.getContext())
//                .inflate(R.id.imageView, parent, false);
//        lifecycle.addObserver(youTubePlayerView);

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        // Return a new view holder

        return new MyViewHolder(view);
    }
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        // Bind data for the item at position
//
//        holder.bindData(dataModelList.get(position), mContext);
//    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int position) {
        DataModel dm = dataModelList.get(position);
        viewHolder.cueVideo(dm.video);
        viewHolder.titleTextView.setText(dm.getTitle());
        viewHolder.subTitleTextView.setText(dm.getSubTitle());
    }

    @Override
    public int getItemCount() {
        // Return the total number of items

        return dataModelList.size();
    }

    // View holder class whose objects represent each list item

    public class MyViewHolder extends RecyclerView.ViewHolder {
//        public ImageView cardImageView;
        private YouTubePlayerView youTubePlayerView;
        private YouTubePlayer youTubePlayer;
        private String currentVideoId;

        public TextView titleTextView;
        public TextView subTitleTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            youTubePlayerView = itemView.findViewById(R.id.imageView);
            lifecycle.addObserver(youTubePlayerView);
            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer initializedYouTubePlayer) {
                    youTubePlayer = initializedYouTubePlayer;
                    youTubePlayer.cueVideo(currentVideoId, 0);
                }

                @Override
                public void onError(YouTubePlayer youTubePlayer, PlayerConstants.PlayerError error) {
                    super.onError(youTubePlayer, error);
                    Log.e("",error.toString());
                }
            });

            titleTextView = itemView.findViewById(R.id.card_title);
            subTitleTextView = itemView.findViewById(R.id.card_subtitle);
        }

        void cueVideo(String videoId) {
            currentVideoId = videoId;

            if(youTubePlayer == null)
                return;

            youTubePlayer.cueVideo(videoId, 0);
        }
        public void bindData(DataModel dataModel, Context context) {
//            cardImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.list_image));
            titleTextView.setText(dataModel.getTitle());
            subTitleTextView.setText(dataModel.getSubTitle());
        }

    }

}