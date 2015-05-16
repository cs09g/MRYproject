package project.mobilecloud.mry;

/**
 * Created by root on 5/16/15.
 */
public class VideoResult {
    private String track_id, artist, title, url;

    private void setTrackID(String trackID){
        track_id = trackID;
    }
    private void setArtist(String Artist){
        artist = Artist;
    }
    private void setTitle(String Title){
        title = Title;
    }
    private void setURL(String URL){
        url = URL;
    }

    private String getTrackID(){
        return track_id;
    }
    private String getArtist(){
        return artist;
    }
    private String getTitle() {
        return title;
    }
    private String getURL(){
        return url;
    }
}
