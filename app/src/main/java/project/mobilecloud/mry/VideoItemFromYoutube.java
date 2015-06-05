package project.mobilecloud.mry;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by algolab on 6/5/15.
 */
public class VideoItemFromYoutube {
    Drawable thumbnail;
    String URL;
    String track_id;
    String title;

    public Drawable getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Drawable thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getTrackID() {
        return track_id;
    }

    public void setTrackID(String track_id) {
        this.track_id = track_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
