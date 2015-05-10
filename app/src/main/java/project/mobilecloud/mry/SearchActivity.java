package project.mobilecloud.mry;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeThumbnailView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by algolab on 5/5/15.
 */
public class SearchActivity extends MainActivity {

    SearchView search_query, result_query;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search_query = (android.support.v7.widget.SearchView) findViewById(R.id.video_search_query);

        /*
        Intent videoActive = getIntent();
        if(Intent.ACTION_SEARCH.equals(videoActive.getAction())){
            String query = videoActive.getStringExtra(SearchManager.QUERY);
            searchVideos(query);
        }
        */
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.video_search_query:
                if(!validate())
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                    new HttpAsyncTask().onPostExecute("http://hmkcode.appspot.com/jsonservlet"); // json data stored at.
                break;
        }
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls){
            result_query = search_query;
            return POST(urls[0], result_query);
        }
        @Override
        protected void onPostExecute(String result){
            Toast.makeText(getBaseContext(), "Data Sent!!", Toast.LENGTH_LONG).show();
        }
    }
    private boolean validate() {
        if(search_query.getQuery().toString().trim().equals(""))
            return false;
        else
            return true;
    }

    public static String POST(String url, SearchView query){
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            String json = "";

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("video_search_query", query);

            json = jsonObject.toString();

            StringEntity se = new StringEntity(json);

            httppost.setEntity(se);
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httppost);
            inputStream = httpResponse.getEntity().getContent();

            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Didn't work!";
        }
        catch(Exception e){
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null){
            result += line;
        }

        inputStream.close();
        return result;
    }

    private void searchVideos(String query) {


    }


}
