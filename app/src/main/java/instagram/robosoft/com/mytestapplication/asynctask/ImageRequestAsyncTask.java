package instagram.robosoft.com.mytestapplication.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import instagram.robosoft.com.mytestapplication.communicator.CallBack;
import instagram.robosoft.com.mytestapplication.communicator.MediaDetailsDataCommunicatior;
import instagram.robosoft.com.mytestapplication.constant.AppData;
import instagram.robosoft.com.mytestapplication.model.MediaDetails;
import instagram.robosoft.com.mytestapplication.model.PostDetailsOfInstagram;
import instagram.robosoft.com.mytestapplication.utils.Util;

/**
 * Created by deena on 25/2/16.
 */
public class ImageRequestAsyncTask extends AsyncTask<String, Void, Map<String,MediaDetails>> {
    HttpURLConnection httpURLConnection = null;
    String mediaId[];
    //List<MediaDetails> mediaDetailseslist = new ArrayList<>();
    private Map<String, MediaDetails> mediaDetailsMap = new HashMap<>();
    MediaDetails mediaDetails;
    private Context mContext;
    private MediaDetailsDataCommunicatior mCallBack;

    public ImageRequestAsyncTask(Context mContext) {
        this.mContext = mContext;
        mCallBack = (MediaDetailsDataCommunicatior) mContext;
    }

    @Override
    protected Map doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            String respose = String.valueOf(Util.covertInputStreamToString(httpURLConnection.getInputStream()));
            Log.i("ImageUrl", respose);
            JSONObject jsonObject = (JSONObject) new JSONTokener(respose).nextValue();
            JSONArray data = jsonObject.getJSONArray("data");
            mediaId = new String[data.length()];
            for (int i = 0; i < data.length(); i++) {
                mediaDetails = new MediaDetails();
                JSONObject jsonObject1 = data.getJSONObject(i);
                String imageurl = jsonObject1.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                String liked = jsonObject1.getJSONObject("likes").getString("count");
                String caption = data.getJSONObject(i).getString("caption");
                String totalComment = data.getJSONObject(i).getJSONObject("comments").getString("count");
                String mediaidd = jsonObject1.getString("id");
                mediaId[i] = mediaidd;
                mediaDetails.setMediaUrl(imageurl);
                mediaDetails.setTotalLike(liked);
                mediaDetails.setMediaId(mediaId[i]);
                mediaDetails.setPostDescription(caption);
                mediaDetails.setTotlaNoOfComment(totalComment);
                //mediaDetailseslist.add(mediaDetails);
                mediaDetailsMap.put(mediaidd, mediaDetails);
            }
           /*for (int j = 0; j < mediaId.length; j++) {

                String recentComment = AppData.APIURL + "/media/" + mediaId[j] + "/comments?access_token=" + AppData.accesstokn;
                url = new URL(recentComment);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                respose = String.valueOf(Util.covertInputStreamToString(httpURLConnection.getInputStream()));
                Log.i("CommentList", respose);
                jsonObject = (JSONObject) new JSONTokener(respose).nextValue();
                data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject object = data.getJSONObject(i);
                    String comment = object.getString("text");
                    String commentFromUser = object.getJSONObject("from").getString("username");
                    Log.i("Comment:", comment);
                    Log.i("CommentFrom", commentFromUser);
                }
                Log.i("CommentFrom", "----------------------------------");
            }*/
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }

        return mediaDetailsMap;
    }

    @Override
    protected void onPostExecute(Map<String,MediaDetails> mediaDetailses) {
        mCallBack.getMediaDetails(mediaDetailses);
    }
}

