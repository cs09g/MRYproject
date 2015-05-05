package project.mobilecloud.mry;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by algolab on 5/5/15.
 */
public class SearchActivity extends MainActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent videoActive = getIntent();
        if(Intent.ACTION_SEARCH.equals(videoActive.getAction())){
            String query = videoActive.getStringExtra(SearchManager.QUERY);
            searchVideos(query);
        }
    }

    private void searchVideos(String query) {

    }


}
