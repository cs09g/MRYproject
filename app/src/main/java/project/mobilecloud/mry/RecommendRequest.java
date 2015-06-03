package project.mobilecloud.mry;

/**
 * Created by algolab on 6/3/15.
 */
public class RecommendRequest {
    private String trackID;
    private int count;

    public String getTrackID() {
        return trackID;
    }

    public void setTrackID(String trackID) {
        this.trackID = trackID;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
