package instagram.robosoft.com.mytestapplication.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.Time;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import instagram.robosoft.com.mytestapplication.communicator.MediaDetailsDataCommunicatior;
import instagram.robosoft.com.mytestapplication.constant.AppData;
import instagram.robosoft.com.mytestapplication.model.MediaDetails;
import instagram.robosoft.com.mytestapplication.utils.Util;

/**
 * Created by deena on 25/2/16.
 */
public class MediaDetailsAsyncTask extends AsyncTask<String, Void, ArrayList<MediaDetails>> {
    private HttpURLConnection httpURLConnection = null;
    private String mediaId[];
    private MediaDetails mediaDetails;
    private Context mContext;
    private ArrayList<MediaDetails> mediaDetailseslist;
    private MediaDetailsDataCommunicatior mCallBack;
    private int mTotalNoOfFollowers;


    public MediaDetailsAsyncTask(Context mContext) {
        this.mContext = mContext;
        mCallBack = (MediaDetailsDataCommunicatior) mContext;
        mediaDetailseslist = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected ArrayList<MediaDetails> doInBackground(String... params) {
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
            mTotalNoOfFollowers = follwerId.length;
            Log.i("Test", "Total No OF followers: " + mTotalNoOfFollowers);

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

        return mediaDetailseslist;
    }

    private void convertStringIntoJavaObject(JSONArray data, Boolean flag) {
        mediaId = new String[data.length()];
        for (int i = 0; i < data.length(); i++) {
            mediaDetails = new MediaDetails();
            JSONObject jsonObject1;
            try {
                jsonObject1 = data.getJSONObject(i);

                //Log.i("Test", jsonObject1.toString());
                //get the media post date
                String createdTime = jsonObject1.getString("created_time");
                long foo = Long.parseLong(createdTime) * 1000;
                Date date = new Date(foo);
                DateFormat formatter = new SimpleDateFormat("MMMM dd,yyyy");

                mediaDetails.setCraetedTime(formatter.format(date));
                String imageurl = jsonObject1.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                String liked = jsonObject1.getJSONObject("likes").getString("count");
                String caption, totalComment, mediaidd;
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

                mediaDetailseslist.add(mediaDetails);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostExecute(ArrayList<MediaDetails> mediaDetailses) {
        mCallBack.getMediaDetails(mediaDetailses);
    }
}

