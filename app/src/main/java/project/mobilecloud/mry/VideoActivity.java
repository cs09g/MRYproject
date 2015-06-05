package project.mobilecloud.mry;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import project.mobilecloud.mry.ThumbnailHandler;

/**
 * Created by seulgi choi on 6/3/15.
 */
public class VideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    public static final String API_KEY = "AIzaSyD_QSrX7JHHYZ_4_vnpIQNNrgSWoWktUUA";

    String VIDEO_ID;
    ListView mListView = null;
    ListViewAdapter mAdapter = null;
    RecommendRequest recommendRequest;

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
        //TextView artist = (TextView) findViewById(R.id.song_artist);
        title.setText(intent.getStringExtra("TITLE"));
        //artist.setText(intent.getStringExtra("ARTIST"));

        /*
        @@ Make array for video list
         */
        mListView = (ListView) findViewById(R.id.video_list);
        mAdapter = new ListViewAdapter(this);
        mListView.setAdapter(mAdapter);

        /*
        It's going to be in AWS with python
         */
        String serverURL = "http://52.68.192.12";
        String function = "/soundnerd/music/recommend";

        //new HttpAsyncTask().onPostExecute(serverURL+function); // json data stored at.

        /*
        @@ Onclick event on list view item
         */

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                VideoItem mData = mAdapter.getListData().get(position);

                Intent intent = new Intent(VideoActivity.this, VideoActivity.class);
                intent.putExtra("URL", mData.getSongURL());
                intent.putExtra("TITLE", mData.getSongTitle());
                //intent.putExtra("ARTIST", mData.getSongArtist());
                intent.putExtra("TRACK_ID", mData.getTrackID());

                startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            VideoActivity.this.finish();
            System.out.println("finishing intent");
            //return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls){
            Intent intent = getIntent();

            recommendRequest = new RecommendRequest();
            recommendRequest.setTrackID(intent.getStringExtra("TRACK_ID"));
            /* test for Bonacell's recommend */
            //recommendRequest.setTrackID("XKuaZWfYcGfmugio");
            recommendRequest.setCount(10);

            return POST(urls[0], recommendRequest);
        }
        @Override
        protected void onPostExecute(String result){
            String jsonRes = doInBackground(result);

            try{
                JSONObject jsonObj = new JSONObject(jsonRes);
                JSONArray jsonData = jsonObj.getJSONArray("tracks");
                for(int i = 0; i < jsonData.length(); i++){
                    JSONObject eachData = jsonData.getJSONObject(i);
                    RecommendedVideo recommendedVideo = new RecommendedVideo();

                    recommendedVideo.setTrackID(eachData.getString("track_id"));
                    recommendedVideo.setArtist(eachData.getString("artist"));
                    recommendedVideo.setTitle(eachData.getString("title"));
                    recommendedVideo.setUrl(eachData.getString("url"));

                    ThumbnailHandler thumbnail = new ThumbnailHandler();

                    String thumbnailURL = thumbnail.getYoutubeThumbnailUrl(recommendedVideo.getUrl());
                    mAdapter.addItem(thumbnail.drawableFromUrl(thumbnailURL), recommendedVideo.getUrl(),
                                     recommendedVideo.getTitle(), recommendedVideo.getArtist(),
                                     recommendedVideo.getTrackID());
                    mAdapter.dataChange();
                }
            }
            catch(Exception e){
                Log.d("InputStream", e.getLocalizedMessage());
            }
        }
    }

    public static String POST(String url, RecommendRequest recommendRequest){
        InputStream inputStream;
        String result = "";
        try{
            HttpClient httpclient = new DefaultHttpClient();

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("track_id", recommendRequest.getTrackID());
            jsonObject.accumulate("count", recommendRequest.getCount());

            String json = jsonObject.toString();

            HttpPost httpPost = new HttpPost(url);

            ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();

            HttpParams params = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);

            post.add(new BasicNameValuePair("data", json));

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(post, "UTF-8");
            httpPost.setEntity(entity);

            HttpResponse httpResponse = httpclient.execute(httpPost);

            if(httpResponse != null){
                inputStream = httpResponse.getEntity().getContent();

                if(inputStream != null){
                    result = convertInputStreamToString(inputStream);
                }
                else
                    result = "Didn't work!";
            }
        }
        catch(Exception e){
            Log.d("InputStream", e.getLocalizedMessage());
        }
        System.out.println(result);
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while((line = bufferedReader.readLine()) != null){
            result += line;
        }

        inputStream.close();
        return result;
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
            player.loadVideo(VIDEO_ID); // auto-play
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
