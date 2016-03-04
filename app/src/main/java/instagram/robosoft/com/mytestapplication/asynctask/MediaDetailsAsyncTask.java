package instagram.robosoft.com.mytestapplication.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import instagram.robosoft.com.mytestapplication.communicator.MediaDetailsDataCommunicatior;
import instagram.robosoft.com.mytestapplication.constant.AppData;
import instagram.robosoft.com.mytestapplication.model.MediaDetails;
import instagram.robosoft.com.mytestapplication.utils.Util;

/**
 * Created by deena on 25/2/16.
 */
public class MediaDetailsAsyncTask extends AsyncTask<String, Void, Map<String, MediaDetails>> {
    HttpURLConnection httpURLConnection = null;
    String mediaId[];
    private Map<String, MediaDetails> mediaDetailsMap = new HashMap<>();
    MediaDetails mediaDetails;
    private Context mContext;
    private MediaDetailsDataCommunicatior mCallBack;

    public MediaDetailsAsyncTask(Context mContext) {
        this.mContext = mContext;
        mCallBack = (MediaDetailsDataCommunicatior) mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Map doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            String respose = String.valueOf(Util.covertInputStreamToString(httpURLConnection.getInputStream()));
            JSONObject jsonObject = (JSONObject) new JSONTokener(respose).nextValue();
            JSONArray data = jsonObject.getJSONArray("data");
            convertStringIntoJavaObject(data, false);
            //get media details of Followers
            url = new URL(params[1]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            respose = String.valueOf(Util.covertInputStreamToString(httpURLConnection.getInputStream()));
            //Log.i("Follws", respose);
            JSONObject follwesByJson = (JSONObject) new JSONTokener(respose).nextValue();
            JSONArray follwesByJsonJSONArray = follwesByJson.getJSONArray("data");
            String follwerId[] = new String[follwesByJsonJSONArray.length()];
            for (int i = 0; i < follwesByJsonJSONArray.length(); i++) {
                follwerId[i] = follwesByJsonJSONArray.getJSONObject(i).getString("id");
            }
            //get media details json data of follower
            for (int i = 0; i < follwerId.length; i++) {
                String media = "https://api.instagram.com/v1/users/" + follwerId[i] + "/media/recent/?access_token=" + AppData.accesstokn;
                url = new URL(media);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                respose = String.valueOf(Util.covertInputStreamToString(httpURLConnection.getInputStream()));
                Log.i("MediaDetails", respose);
                //get details of mediaobject
                JSONObject rootJsonObject = (JSONObject) new JSONTokener(respose).nextValue();
                JSONArray rootJsonArray = rootJsonObject.getJSONArray("data");
                convertStringIntoJavaObject(rootJsonArray, true);
            }

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

    private void convertStringIntoJavaObject(JSONArray data, Boolean flag) {
        mediaId = new String[data.length()];
        for (int i = 0; i < data.length(); i++) {
            mediaDetails = new MediaDetails();
            JSONObject jsonObject1 = null;
            try {
                jsonObject1 = data.getJSONObject(i);

                String imageurl = jsonObject1.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                String liked = jsonObject1.getJSONObject("likes").getString("count");
                String caption = null, totalComment = null, mediaidd = null;
                if (!jsonObject1.isNull("caption")) {
                    caption = jsonObject1.getJSONObject("caption").getString("text");
                    mediaDetails.setPostDescription(caption);
                }
                if (!jsonObject1.isNull("comments")) {
                    totalComment = data.getJSONObject(i).getJSONObject("comments").getString("count");
                    mediaDetails.setTotlaNoOfComment(totalComment);
                }
                String profile_picture = jsonObject1.getJSONObject("user").getString("profile_picture");
                String username = jsonObject1.getJSONObject("user").getString("username");
                mediaidd = jsonObject1.getString("id");
                mediaId[i] = mediaidd;
                mediaDetails.setMediaUrl(imageurl);
                mediaDetails.setTotalLike(liked);
                mediaDetails.setMediaId(mediaId[i]);
                //ChangesHere
                mediaDetails.setUserProfilePic(profile_picture);
                mediaDetails.setUserName(username);

                //mediaDetailseslist.add(mediaDetails);
                mediaDetailsMap.put(mediaidd, mediaDetails);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostExecute(Map<String, MediaDetails> mediaDetailses) {
        mCallBack.getMediaDetails(mediaDetailses);
    }
}

