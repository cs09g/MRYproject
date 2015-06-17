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
import org.apache.http.client.methods.HttpGet;
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
    public static final String API_KEY = "AIzaSyDhJ9UPOZzjWHSY8-I-2L0qacZeoJAnBVk";
    private String serverURL = "http://52.68.192.12";
    private String recommend_func = "/soundnerd/music/recommend";
    private String ytsearch_func = "/soundnerd/music/ytsearch";
    private String search_func = "/soundnerd/music/search";
    private String youtubeURL = "https://www.youtube.com/watch?v=";

    String VIDEO_ID;
    ListView mListView = null;
    ListViewAdapter mAdapter = null;
    RecommendRequest recommendRequest;
    VideoIdRequest videoIdReqeust = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** attaching layout xml **/
        setContentView(R.layout.play_video);

        /** Initializing YouTube player view **/
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        if(youTubePlayerView != null)
            youTubePlayerView.initialize(API_KEY, this);

        Intent intent = getIntent();

        /* It's always from youtube so track id is 11 letters */
        String videoId = intent.getStringExtra("TRACK_ID");
        VIDEO_ID = videoId;
        videoIdReqeust = new VideoIdRequest();
        videoIdReqeust.setVideo_id(VIDEO_ID);

        if(videoId.length() > 11) {
            VIDEO_ID = getTrackIdFromVideoId(videoId);
        }
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

        /* Suggestion Logic
        1. Ask to Bonacell for recommendations
        2. If exist the list, then End.
           Else ask to Youtube for suggestions
         */

        /** Suggestions from Bonacell **/
        /*
        It's going to be in AWS with python
         */

        new HttpAsyncTask().onPostExecute(serverURL+recommend_func); // json data stored at.

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
                intent.putExtra("ARTIST", mData.getSongArtist());

                // current -- http://www.youtu.be/<VIDEO_ID>
                // past -- https://www.youtube.com/watch?v=<VIDEO_ID>
                intent.putExtra("TRACK_ID", mData.getSongURL().substring(32));

                startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            VideoActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    String getTrackIdFromVideoId(String videoId){
        InputStream inputStream;
        String result = "";
        try{
            HttpClient httpclient = new DefaultHttpClient();

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("video_id", videoId);

            String json = jsonObject.toString();

            HttpPost httpPost = new HttpPost(serverURL+ytsearch_func);

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
                    JSONObject jsonObj = new JSONObject(convertInputStreamToString(inputStream));
                    JSONArray jsonData = jsonObj.getJSONArray("tracks");
                    result = jsonData.getJSONObject(0).getString("track_id");
                }
                else
                    result = "Didn't work!";
            }
        }
        catch(Exception e){
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls){
            recommendRequest = new RecommendRequest();
            recommendRequest.setTrackID(videoIdReqeust.getVideo_id());
            recommendRequest.setCount(5);

            return POST(urls[0], recommendRequest);
        }

        String getTrackInfo(String url, VideoIdRequest videoIdRequest){
            InputStream inputStream;
            String result = "";
            try{
                HttpClient httpclient = new DefaultHttpClient();

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("video_id", videoIdRequest.getVideo_id());

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
            return result;
        }

        String doInBackgroundForGET(String... urls){
            /** for Youtube **/
            return GET2Youtube(urls[0]);
        }

        @Override
        protected void onPostExecute(String result){
            TextView recommender = (TextView)findViewById(R.id.recommender);
            recommender.setText("Suggestions from Bonacell");
            String jsonRes = getTrackInfo(serverURL+ytsearch_func, videoIdReqeust);
            //String jsonRes = doInBackground(result);

            try{
                JSONObject jsonObj = new JSONObject(jsonRes);
                JSONArray jsonData = jsonObj.getJSONArray("tracks");
                int loopCnt = jsonData.length();
                if(loopCnt==0){
                    /* when the result is empty from Bonacell */
                    /** Ask to Youtube **/
                    String youtubeUrlForSuggestions =
                            "https://www.googleapis.com/youtube/v3/search?"+
                            "relatedToVideoId="+videoIdReqeust.getVideo_id()+
                            "&part=snippet"+
                            "&type=video"+
                            "&key="+API_KEY+
                            "&videoDuration=medium&videoDuration=short"+
                            "&maxResults=10"+
                            "&fields=items(id(videoId),snippet(title,thumbnails(default)))";
                    onGetExecute(youtubeUrlForSuggestions);
                }
                else {
                    if(loopCnt > 2) loopCnt = 2;
                    for (int i = 0; i < loopCnt; i++) {
                        JSONObject eachData = jsonData.getJSONObject(i);
                        // result of ytsearch
                        videoIdReqeust.setVideo_id(eachData.getString("track_id"));
                        String recommendJson = doInBackground(result);

                        JSONObject recommendJsonObj = new JSONObject(recommendJson);
                        JSONArray recommendJsonData = recommendJsonObj.getJSONArray("tracks");
                        for(int j=0;j<recommendJsonData.length();j++) {
                            JSONObject eachRecommendedData = recommendJsonData.getJSONObject(j);
                            RecommendedVideo recommendedVideo = new RecommendedVideo();

                            recommendedVideo.setTrackID(eachRecommendedData.getString("track_id"));
                            recommendedVideo.setArtist(eachRecommendedData.getString("artist"));
                            recommendedVideo.setTitle(eachRecommendedData.getString("title"));
                            recommendedVideo.setUrl(eachRecommendedData.getString("url"));

                            ThumbnailHandler thumbnail = new ThumbnailHandler();

                            String thumbnailURL = thumbnail.getYoutubeThumbnailUrl(recommendedVideo.getUrl());
                            mAdapter.addItem(thumbnail.drawableFromUrl(thumbnailURL), recommendedVideo.getUrl(),
                                    recommendedVideo.getTitle(), recommendedVideo.getArtist(),
                                    recommendedVideo.getTrackID());
                            mAdapter.dataChange();
                        }
                    }
                }
            }
            catch(Exception e){
                Log.d("InputStream", e.getLocalizedMessage());
            }
        }

        public void onGetExecute(String result){
            TextView recommender = (TextView)findViewById(R.id.recommender);
            recommender.setText("Suggestions from Youtube");
            String jsonRes = doInBackgroundForGET(result);
            /** for Youtube **/
            try{
                JSONObject jsonObj = new JSONObject(jsonRes);
                JSONArray jsonData = jsonObj.getJSONArray("items");

                for(int i=0;i<jsonData.length();i++){
                    JSONObject eachData = jsonData.getJSONObject(i);

                    VideoItemFromYoutube videoResult = new VideoItemFromYoutube();

                    String videoID, title;
                    try {
                        videoID = eachData.getJSONObject("id").getString("videoId");
                        title = eachData.getJSONObject("snippet").getString("title");
                    }
                    catch(Exception e){
                        continue;
                    }
                    String url = youtubeURL+videoID;

                    videoResult.setTrackID(videoID);
                    videoResult.setTitle(title);
                    videoResult.setURL(url);

                    /*
                    Youtube api provides videos' thumbnails.
                    if speed of the service is getting slow, then change to use api's not getting from url.
                     */
                    ThumbnailHandler thumbnail = new ThumbnailHandler();
                    String thumbnailURL = thumbnail.getYoutubeThumbnailUrl(videoResult.getURL());

                    mAdapter.addItem(thumbnail.drawableFromUrl(thumbnailURL), videoResult.getURL(),
                            videoResult.getTitle(), "", videoResult.getTrackID());

                    mAdapter.dataChange();
                }
            }
            catch(Exception e){
                Log.d("InputStream", e.getLocalizedMessage());
            }
        }
    }

    public static String GET2Youtube(String url){
        InputStream inputStream;
        String result = "";

        try{
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpclient.execute(httpGet);

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
        return result;
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
        YouTubePlayer p = player;
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
