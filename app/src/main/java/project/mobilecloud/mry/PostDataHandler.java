package project.mobilecloud.mry;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

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

/**
 * Created by algolab on 6/3/15.
 */
public class PostDataHandler {
    VideoRequest videoRequest;
    EditText search_query;
    ListViewAdapter mAdapter = null;

    public PostDataHandler(EditText search_query, ListViewAdapter mAdapter){
        this.search_query = search_query;
        this.mAdapter = mAdapter;
    }

    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
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
                                     videoResult.getTitle(), videoResult.getArtist(), "");
                    mAdapter.dataChange();
                }
            }
            catch(Exception e){
                Log.d("InputStream", e.getLocalizedMessage());
            }
        }
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
