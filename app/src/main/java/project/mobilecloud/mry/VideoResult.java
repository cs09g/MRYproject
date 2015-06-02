package project.mobilecloud.mry;

/**
 * Created by root on 5/16/15.
 */
public class VideoResult {
    private String track_id, artist, title, url;

    public void setTrackID(String trackID){
        this.track_id = trackID;
    }
    public void setArtist(String Artist){
        this.artist = Artist;
    }
    public void setTitle(String Title){
        this.title = Title;
    }
    public void setURL(String URL){
        this.url = URL;
    }

    public String getTrackID(){
        return track_id;
    }
    public String getArtist(){
        return artist;
    }
    public String getTitle() {
        return title;
    }
    public String getURL(){
        return url;
    }
}
