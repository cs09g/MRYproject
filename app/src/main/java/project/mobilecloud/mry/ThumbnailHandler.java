package project.mobilecloud.mry;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;

/**
 * Created by algolab on 6/3/15.
 */
public class ThumbnailHandler {
    /*
    @@ Make drawable object from URL

    http://stackoverflow.com/questions/3375166/android-drawable-images-from-url
     */
    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(x);
    }

    /*
    @@ Get youtube video's thumbnail from URL

    https://androidsnippets.wordpress.com/2012/08/16/how-to-get-thumbnail-image-icon-for-a-youtube-video/
     */
    public static String getYoutubeThumbnailUrl(String youtubeUrl){
        String thumbImageUrl = "http://img.youtube.com/vi/noimagefound/default.jpg";

        if(youtubeUrl!=null && youtubeUrl.trim().length()>0 && youtubeUrl.startsWith("http") && youtubeUrl.contains("youtube")){
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            try{
                youtubeUrl = URLDecoder.decode(youtubeUrl, "UTF-8");

                if(youtubeUrl.indexOf('?')>0){
                    String array[] = youtubeUrl.split("\\?");

                    int equalsFilterIndex = array.length - 1;
                    String equalsString = array[equalsFilterIndex];

                    if(equalsString.indexOf('&')>0){
                        String ampersandArray[] = equalsString.split("&");
                        for(String parameter : ampersandArray){
                            String keyvaluePair[] = parameter.split("=");
                            params.put(URLDecoder.decode(keyvaluePair[0],"UTF-8"),URLDecoder.decode(keyvaluePair[1],"UTF-8"));
                        }
                    }
                    else{
                        String v[] = equalsString.split("=");
                        params.put(URLDecoder.decode(v[0],"UTF-8"),URLDecoder.decode(v[1],"UTF-8"));
                    }
                }

                int size = params.size();

                if(size==0 || !params.containsKey("v")){
                    if(size>0)
                        youtubeUrl = youtubeUrl.substring(0, youtubeUrl.indexOf("?",0));

                    String vtoSplit = "/v/";
                    int index = youtubeUrl.indexOf(vtoSplit,0);

                    int fromIndex = index + vtoSplit.length();
                    int lastIndex = youtubeUrl.indexOf("?", 0);

                    if(lastIndex==-1)
                        lastIndex = youtubeUrl.length();

                    String v = youtubeUrl.substring(fromIndex,lastIndex);
                    thumbImageUrl = "http://img.youtube.com/vi/" + v + "/default.jpg";
                }
                else{
                    String v = params.get("v");
                    thumbImageUrl = "http://img.youtube.com/vi/" + v + "/default.jpg";
                }
            }
            catch(Exception e){
                if(e!=null)
                    e.printStackTrace();
            }
        }

        return thumbImageUrl;
    }
}
