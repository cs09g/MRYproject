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
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeThumbnailView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParamBean;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by seulgi choi on 5/5/15.
 */

public class SearchActivity extends MainActivity {

    EditText search_query;
    ListView result_query;
    Button search_button;
    ArrayAdapter<String> result_adapter;
    VideoRequest videoRequest;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        search_query = (EditText) findViewById(R.id.video_search_query);

        /*
        @@ Make array for video list
         */
        result_query = (ListView) findViewById(R.id.video_list);
        result_adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
        result_query.setAdapter(result_adapter);

        /*
        @@ Search button
         */
        search_button = (Button) findViewById(R.id.search_button);
        search_button.setOnClickListener(this);

        /*
        project name : MRY
        public IP : 52.68.192.12
        search URL : soundnerd/music/search
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
        /*
        if(search_query != null){
            result_adapter.add(search_query.getText().toString());
        }
        */
        switch(view.getId()){
            case R.id.search_button:
                if(!validate()) {
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                }
                else {
                    /*
                    SERVER URL is not working yet.
                    It's going to be in AWS with python
                     */
                    new HttpAsyncTask().onPostExecute("http://52.68.192.12/soundnerd/music/search"); // json data stored at.
                }
            break;
        }

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls){
            videoRequest = new VideoRequest();
           // videoRequest.setTitle(search_query.getText().toString());
            videoRequest.setStart(1);
            videoRequest.setCount(5);

            return POST(urls[0], videoRequest);
        }
        @Override
        protected void onPostExecute(String result){
            result_adapter.add(doInBackground(result));
        }
    }
    private boolean validate() {
        if(search_query.getText().toString().trim().equals(""))
            return false;
        else
            return true;
    }

    public static String POST(String url, VideoRequest videoRequest){
        InputStream inputStream;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("artist", "김광석");
            jsonObject.accumulate("title", "사랑했지만");
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

            if(httpResponse != null) {
                inputStream = httpResponse.getEntity().getContent();

                if (inputStream != null)
                    result = convertInputStreamToString(inputStream);
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
