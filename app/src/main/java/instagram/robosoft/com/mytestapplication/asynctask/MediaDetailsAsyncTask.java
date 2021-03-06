package instagram.robosoft.com.mytestapplication.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.format.DateUtils;
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
import java.net.ProtocolException;
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
import instagram.robosoft.com.mytestapplication.model.CommentDetails;
import instagram.robosoft.com.mytestapplication.model.MediaDetails;
import instagram.robosoft.com.mytestapplication.utils.Util;

/**
 * Created by deena on 25/2/16.
 */
public class MediaDetailsAsyncTask extends AsyncTask<String, Void, Util> {
    private Boolean mFlagg = false;
    private String mediaId[];
    private MediaDetails mediaDetails;
    private Context mContext;
    private ArrayList<MediaDetails> mediaDetailseslist;
    private MediaDetailsDataCommunicatior mCallBack;
    private SharedPreferences mSharedPreference;
    private int mCommentCountDisplay;
    private Util util;
    private ArrayList<String> nextUrlArrayList;
    private Boolean mFlag = false, mFlagFromMainActivity;
    private int x = 0;
    //private ProgressDialog progressDialog;


    public MediaDetailsAsyncTask(Context mContext, Boolean flag) {
        mFlagg = true;
        mFlagFromMainActivity = flag;
        this.mContext = mContext;
        mCallBack = (MediaDetailsDataCommunicatior) mContext;
        mediaDetailseslist = new ArrayList<>();
        getSharedValue();
    }

    public MediaDetailsAsyncTask(ArrayList<String> nextUrlArrayList, Context mContext) {
        this.nextUrlArrayList = nextUrlArrayList;
        x = 1;
        Log.i("test", "Inside MediaDetailsAsyncTask(2 Parameter)");
        this.mContext = mContext;
        mCallBack = (MediaDetailsDataCommunicatior) mContext;
        mediaDetailseslist = new ArrayList<>();
        getSharedValue();
    }

    private void getSharedValue() {
        mSharedPreference = mContext.getSharedPreferences(AppData.SETTINGPREFRENCE, mContext.MODE_PRIVATE);
        mCommentCountDisplay = Integer.parseInt(mSharedPreference.getString(AppData.SettingKey, AppData.defaultNoOfComment));
        util = new Util();
    }

    @Override
    protected Util doInBackground(String... params) {
        JSONArray jsonArray;
        if (isCancelled()) {
            // progressDialog.dismiss();
        }
        if (mFlagg) {
            mFlag = true;
            try {
                jsonArray = util.urlConnection(params[0]);
                convertStringIntoJavaObject(jsonArray);
                //get media details of Followers
                jsonArray = util.urlConnection(params[1]);
                if (jsonArray != null) {
                    String follwerId[] = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        follwerId[i] = jsonArray.getJSONObject(i).getString("id");
                    }
                    //get media details json data of followers
                    for (int i = 0; i < follwerId.length; i++) {
                        String media = "";
                        if (mFlagFromMainActivity)
                            media = AppData.APIURL + "/users/" + follwerId[i] + "/media/recent/?access_token=" + AppData.accesstokn + "&count=" + AppData.DEFAULT_LOAD_DATA;
                        else
                            media = AppData.APIURL + "/users/" + follwerId[i] + "/media/recent/?access_token=" + AppData.accesstokn;
                        jsonArray = util.urlConnection(media);
                        convertStringIntoJavaObject(jsonArray);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mFlag = false;
            for (String string : nextUrlArrayList) {
                jsonArray = util.urlConnection(string);
                convertStringIntoJavaObject(jsonArray);
            }
        }

        return util;
    }

    private void convertStringIntoJavaObject(JSONArray data) {
        if (data != null) {
            mediaId = new String[data.length()];
            for (int i = 0; i < data.length(); i++) {
                mediaDetails = new MediaDetails();
                JSONObject jsonObject1;
                try {
                    jsonObject1 = data.getJSONObject(i);
                    Boolean user_has_liked = jsonObject1.getBoolean("user_has_liked");
                    //Log.i("test", "" + user_has_liked);
                    mediaDetails.setUser_has_liked(user_has_liked);
                    //get the media post date
                    String createdTime = jsonObject1.getString("created_time");
                    postDateOfFeed(createdTime);
                    String imageurl = jsonObject1.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                    //Log.i("test", "iMage Url: " + imageurl);
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
                    mediaDetails.setUserProfilePic(profile_picture);
                    mediaDetails.setUserName(username);

                    ArrayList<CommentDetails> arrayList = util.getCommentDetails(mediaId[i], mCommentCountDisplay);
                    mediaDetails.setCommentDetailsArrayList(arrayList);
                    mediaDetailseslist.add(mediaDetails);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void postDateOfFeed(String createdTime) {
        long postTime = Long.parseLong(createdTime) * 1000;
        long currenrTime = new Date().getTime();
        long diff = currenrTime - postTime;
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        long diffInDays = (diff) / (1000 * 60 * 60 * 24);

        if (diffInDays >= 1) {
            if (diffInDays >= 1 && diffInDays < 7)
                mediaDetails.setDateDiff(diffInDays + "d");
            else if (diffInDays >= 30 && diffInDays < 365)
                mediaDetails.setDateDiff((diffInDays / 30) + "M");
            else if (diffInDays < 30 && diffInDays >= 7)
                mediaDetails.setDateDiff((diffInDays / 7) + "w");
            else if (diffInDays >= 365)
                mediaDetails.setDateDiff((diffInDays / 365) + "y");
        } else if (diffHours >= 1 && diffHours <= 24) {
            mediaDetails.setDateDiff(diffHours + "h");
        } else if (diffMinutes <= 60 && diffMinutes >= 1) {
            mediaDetails.setDateDiff(diffMinutes + "m");
        } else {
            mediaDetails.setDateDiff(diffSeconds + "s");
        }
    }

    @Override
    protected void onPostExecute(Util util) {
        //progressDialog.dismiss();
        mCallBack.getMediaDetails(mediaDetailseslist, util, mFlag);


    }

}

