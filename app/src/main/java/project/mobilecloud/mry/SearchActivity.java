package project.mobilecloud.mry;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeThumbnailView;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by seulgi choi on 5/5/15.
 */

/*
    project name : MRY
    public IP : 52.68.192.12
    search URL : soundnerd/music/search
    recommend URL : soundnerd/music/recommend
 */
public class SearchActivity extends MainActivity{
    EditText search_query;
    Button search_button;
    VideoRequest videoRequest;
    ListView mListView = null;
    ListViewAdapter mAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        search_query = (EditText) findViewById(R.id.video_search_query);

        /*
        @@ Make array for video list
         */
        mListView = (ListView) findViewById(R.id.video_list);
        mAdapter = new ListViewAdapter(this);
        mListView.setAdapter(mAdapter);

        /*
        @@ Search button
         */
        search_button = (Button) findViewById(R.id.search_button);
        search_button.setOnClickListener(this);

        search_query.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search_button.performClick();
                    return true;
                }
                return false;
            }
        });

        /*
        @@ Onclick event on list view item
         */
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                VideoItem mData = mAdapter.mListData.get(position);

                Intent intent = new Intent(SearchActivity.this, VideoActivity.class);
                intent.putExtra("URL", mData.getSongURL());
                intent.putExtra("TITLE", mData.getSongTitle());
                intent.putExtra("ARTIST", mData.getSongArtist());
                startActivityForResult(intent, 1);
            }
        });
    }

    private class ViewHolder{
        public ImageView thumbnail;
        public TextView title;
        public TextView artist;
    }

    private class ListViewAdapter extends BaseAdapter{
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
    public void onClick(View view){
        // initialize the list
        mAdapter.removeAll();

        // show searching result
        switch(view.getId()){
            case R.id.search_button:
                if(!validate()){
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                }
                else{
                    /*
                    It's going to be in AWS with python
                     */
                    String serverURL = "http://52.68.192.12";
                    String function = "/soundnerd/music/search";

                    new HttpAsyncTask().onPostExecute(serverURL+function); // json data stored at.
                }
            break;
        }

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls){
            videoRequest = new VideoRequest();
            videoRequest.setTitle(search_query.getText().toString());
            videoRequest.setStart(1);
            videoRequest.setCount(5);
            return POST(urls[0], videoRequest);
        }
        @Override
        protected void onPostExecute(String result){
            String jsonRes = doInBackground(result);

            try{
                JSONObject jsonObj = new JSONObject(jsonRes);
                JSONArray jsonData = jsonObj.getJSONArray("tracks");

                for(int i = 0; i < jsonData.length(); i++){
                    JSONObject eachData = jsonData.getJSONObject(i);
                    VideoResult videoResult = new VideoResult();

                    videoResult.setTrackID(eachData.getString("track_id"));
                    videoResult.setArtist(eachData.getString("artist"));
                    videoResult.setTitle(eachData.getString("title"));
                    videoResult.setURL(eachData.getString("url"));

                    ThumbnailHandler thumbnail = new ThumbnailHandler();

                    String thumbnailURL = thumbnail.getYoutubeThumbnailUrl(videoResult.getURL());
                    mAdapter.addItem(thumbnail.drawableFromUrl(thumbnailURL), videoResult.getURL(),
                                     videoResult.getTitle(), videoResult.getArtist());
                    mAdapter.dataChange();
                }
            }
            catch(Exception e){
                Log.d("InputStream", e.getLocalizedMessage());
            }
        }
    }

    private boolean validate(){
        if(search_query.getText().toString().trim().equals(""))
            return false;
        else
            return true;
    }

    public static String POST(String url, VideoRequest videoRequest){
        InputStream inputStream;
        String result = "";
        try{
            HttpClient httpclient = new DefaultHttpClient();

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("title", videoRequest.getTitle());
            jsonObject.accumulate("start", videoRequest.getStart());
            jsonObject.accumulate("count", videoRequest.getCount());

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
}
