package project.mobilecloud.mry;

/**
 * Created by root on 5/16/15.
 */
public class VideoRequest {
    private String artist, title;
    private int start, count; // starting index of list, number of recommendations

    public void setArtist(String Artist){
        artist = Artist;
    }
    public void setTitle(String Title) { title = Title; }
    public void setStart(int Start) { start = Start; }
    public void setCount(int Cnt){
        count = Cnt;
    }

    public String getArtist(){
        return artist;
    }
    public String getTitle() { return title; }
    public int getStart() { return start; }
    public int getCount(){
        return count;
    }
}
