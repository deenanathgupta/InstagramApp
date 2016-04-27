package instagram.robosoft.com.mytestapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import instagram.robosoft.com.mytestapplication.constant.AppData;
import instagram.robosoft.com.mytestapplication.model.CommentDetails;

/**
 * Created by deena on 25/2/16.
 */
public class Util {
    private ArrayList<String> nextUrlArrayList = new ArrayList<>();
    ;
    private HttpURLConnection httpURLConnection;

    public Util() {

    }

    public static StringBuffer covertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        StringBuffer data = new StringBuffer();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                data.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
        return data;
    }

    public static boolean isNwConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
        if (nwInfo != null && nwInfo.isAvailable()) {
            return true;
        }
        return false;
    }

    public JSONArray urlConnection(String Url) {
        String respose, nextUrl;
        JSONArray data = null;
        URL url;
        try {
            url = new URL(Url);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            respose = String.valueOf(Util.covertInputStreamToString(httpURLConnection.getInputStream()));
            JSONObject jsonObject = (JSONObject) new JSONTokener(respose).nextValue();
            if (jsonObject.has("pagination")) {
                JSONObject paginationJsonObject = jsonObject.getJSONObject("pagination");
                if (!paginationJsonObject.isNull("next_url")) {
                    nextUrl = paginationJsonObject.getString("next_url");
                    nextUrlArrayList.add(nextUrl);
                }
            }
            data = jsonObject.getJSONArray("data");
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
        return data;
    }

    public ArrayList<CommentDetails> getCommentDetails(String mediaId, int mCommentCountDisplay) {
        CommentDetails commentDetails;
        ArrayList<CommentDetails> arrayList = new ArrayList<>();
        int count = 0;
        int commentCount = mCommentCountDisplay;
        String recentComment = AppData.APIURL + "/media/" + mediaId + "/comments?access_token=" + AppData.accesstokn;
        try {
            JSONArray data = urlConnection(recentComment);
            if (commentCount > data.length())
                count = data.length();
            else if (commentCount < data.length())
                count = commentCount;
            int temp = data.length() - count;

            for (int i = 0; i < data.length(); i++) {
                if (i >= temp) {
                    commentDetails = new CommentDetails();
                    JSONObject object = data.getJSONObject(i);
                    String comment = object.getString("text");
                    String commentFromUser = object.getJSONObject("from").getString("username");
                    commentDetails.setCommentedBy(commentFromUser);
                    commentDetails.setComment(comment);
                    arrayList.add(commentDetails);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            httpURLConnection.disconnect();
        }
        return arrayList;
    }

    public ArrayList<String> getNextUrlArrayList() {
        return nextUrlArrayList;
    }

    public static void lockOrientation(Activity mActivity) {
        int currentOrienttion = mActivity.getResources().getConfiguration().orientation;
        if (currentOrienttion == Configuration.ORIENTATION_PORTRAIT) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    public static void unLockOrientation(Activity mActivity) {
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
}
