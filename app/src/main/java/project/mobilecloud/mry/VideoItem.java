package project.mobilecloud.mry;

import android.graphics.drawable.Drawable;

/**
 * Created by algolab on 6/1/15.
 */
public class VideoItem {
    private Drawable thumbnail;
    private String song_title;
    private String song_artist;

    public VideoItem(Drawable thumbnail, String song_title, String song_artist){
        this.thumbnail = thumbnail;
        this.song_title = song_title;
        this.song_artist = song_artist;
    }

    public Drawable getThumbnail(){ return thumbnail; }
    public String getSongTitle(){ return song_title; }
    public String getSongArtist(){ return song_artist; }

    public void setThumbnail(Drawable url){ this.thumbnail = url; }
    public void setSongTitle(String title){ this.song_title = title; }
    public void setSongArtist(String artist){ this.song_artist = artist; }
}
