package project.mobilecloud.mry;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;

/**
 * Created by seulgi choi on 6/3/15.
 */
public class VideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    public static final String API_KEY = "AIzaSyBrWf4JCHSUoxs6YTPpyiEO7ZMz6TEXaf8";

    String VIDEO_ID;//"7hokQ-M-l4I";
    ListView mListView = null;
    ListViewAdapter mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** attaching layout xml **/
        setContentView(R.layout.play_video);

        /** Initializing YouTube player view **/
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        youTubePlayerView.initialize(API_KEY, this);

        Intent intent = getIntent();
        // current -- http://www.youtu.be/<VIDEO_ID>
        // past -- https://www.youtube.com/watch?v=<VIDEO_ID>
        VIDEO_ID = intent.getStringExtra("URL").substring(32);

        TextView title = (TextView) findViewById(R.id.song_title);
        TextView artist = (TextView) findViewById(R.id.song_artist);
        title.setText(intent.getStringExtra("TITLE"));
        artist.setText(intent.getStringExtra("ARTIST"));

        /*
        @@ Make array for video list
         */
        mListView = (ListView) findViewById(R.id.video_list);
        mAdapter = new ListViewAdapter(this);
        mListView.setAdapter(mAdapter);
    }

    private class ViewHolder{
        public ImageView thumbnail;
        public TextView title;
        public TextView artist;
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<VideoItem> mListData = new ArrayList<VideoItem>();

        public ListViewAdapter(Context mContext){
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount(){
            return mListData.size();
        }

        @Override
        public Object getItem(int position){
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            ViewHolder holder;
            if(convertView == null){
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item, null);

                holder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
                holder.title = (TextView) convertView.findViewById(R.id.song_title);
                holder.artist = (TextView) convertView.findViewById(R.id.song_artist);

                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }

            VideoItem mData = mListData.get(position);

            if(mData.getThumbnail() != null){
                holder.thumbnail.setVisibility(View.VISIBLE);
                holder.thumbnail.setImageDrawable(mData.getThumbnail());
            }
            else{
                holder.thumbnail.setVisibility(View.GONE);
            }

            holder.title.setText(mData.getSongTitle());
            holder.artist.setText(mData.getSongArtist());

            return convertView;
        }

        public void addItem(Drawable thumbnail, String song_url, String title, String artist){
            VideoItem addInfo = new VideoItem(thumbnail, song_url, title, artist);

            mListData.add(addInfo);
        }

        public void remove(int position){
            mListData.remove(position);
            dataChange();
        }

        public void dataChange(){
            mAdapter.notifyDataSetChanged();
        }

        public void removeAll(){
            int numOfItems = getCount();
            for(int i=0;i<numOfItems;i++){
                remove(0);
            }
        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult result) {
        Toast.makeText(this, "Failured to Initialize!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {

        /** add listeners to YouTubePlayer instance **/
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);

        /** Start buffering **/
        if (!wasRestored) {
            //player.cueVideo(VIDEO_ID);
            player.loadVideo(VIDEO_ID);
        }
    }

    private PlaybackEventListener playbackEventListener = new PlaybackEventListener() {

        @Override
        public void onBuffering(boolean arg0) {
        }

        @Override
        public void onPaused() {
        }

        @Override
        public void onPlaying() {
        }

        @Override
        public void onSeekTo(int arg0) {
        }

        @Override
        public void onStopped() {
        }

    };

    private PlayerStateChangeListener playerStateChangeListener = new PlayerStateChangeListener() {

        @Override
        public void onAdStarted() {
        }

        @Override
        public void onError(ErrorReason arg0) {
        }

        @Override
        public void onLoaded(String arg0) {
        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {
        }

        @Override
        public void onVideoStarted() {
        }
    };
}
