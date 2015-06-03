package project.mobilecloud.mry;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by algolab on 6/3/15.
 */
public class ListViewAdapter extends BaseAdapter {
    private class ViewHolder{
        public ImageView thumbnail;
        public TextView title;
        public TextView artist;
    }

    private Context mContext = null;
    private ArrayList<VideoItem> mListData = new ArrayList<VideoItem>();

    public ListViewAdapter(Context mContext){
        super();
        this.mContext = mContext;
    }

    public ArrayList<VideoItem> getListData(){
        return mListData;
    }

    @Override
    public int getCount(){
        return mListData.size();
    }

    @Override
    public Object getItem(int position){
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);

            holder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
            holder.title = (TextView) convertView.findViewById(R.id.song_title);
            holder.artist = (TextView) convertView.findViewById(R.id.song_artist);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        VideoItem mData = mListData.get(position);

        if(mData.getThumbnail() != null){
            holder.thumbnail.setVisibility(View.VISIBLE);
            holder.thumbnail.setImageDrawable(mData.getThumbnail());
        }
        else{
            holder.thumbnail.setVisibility(View.GONE);
        }

        holder.title.setText(mData.getSongTitle());
        holder.artist.setText(mData.getSongArtist());

        return convertView;
    }

    public void addItem(Drawable thumbnail, String song_url, String title, String artist, String track_id){
        VideoItem addInfo = new VideoItem(thumbnail, song_url, title, artist, track_id);

        mListData.add(addInfo);
    }

    public void remove(int position){
        mListData.remove(position);
        dataChange();
    }

    public void dataChange(){
        this.notifyDataSetChanged();
    }

    public void removeAll(){
        int numOfItems = getCount();
        for(int i=0;i<numOfItems;i++){
            remove(0);
        }
    }
}
