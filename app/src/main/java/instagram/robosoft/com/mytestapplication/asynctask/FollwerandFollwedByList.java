package instagram.robosoft.com.mytestapplication.asynctask;

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
import java.util.Map;

import instagram.robosoft.com.mytestapplication.constant.AppData;
import instagram.robosoft.com.mytestapplication.model.MediaDetails;
import instagram.robosoft.com.mytestapplication.utils.Util;

/**
 * Created by deena on 27/2/16.
 */
public class FollwerandFollwedByList extends AsyncTask<String, Void, Void> {
    private HttpURLConnection mHttpURLConnection = null;
    String MediaId[];
    private MediaDetails mediaDetails;

    public FollwerandFollwedByList(Map<String, MediaDetails> mediaDetailsMap) {
        this.mediaDetailsMap = mediaDetailsMap;
    }

    private Map<String,MediaDetails> mediaDetailsMap;


    @Override
    protected Void doInBackground(String... params) {
        try {

            //List of Follwers
            URL url = new URL(params[0]);
            mHttpURLConnection = (HttpURLConnection) url.openConnection();
            mHttpURLConnection.setRequestMethod("GET");
            String respose = String.valueOf(Util.covertInputStreamToString(mHttpURLConnection.getInputStream()));
            Log.i("Follws", respose);
            JSONObject follwesByJson = (JSONObject) new JSONTokener(respose).nextValue();
            JSONArray follwesByJsonJSONArray = follwesByJson.getJSONArray("data");
            String follwerId[] = new String[follwesByJsonJSONArray.length()];
            for (int i = 0; i < follwesByJsonJSONArray.length(); i++) {
                follwerId[i] = follwesByJsonJSONArray.getJSONObject(i).getString("id");
                Log.i("ID", follwerId[i]);
            }
            //get media details json data of follower
            for (int i = 0; i < follwerId.length; i++) {
                String media = "https://api.instagram.com/v1/users/" + follwerId[i] + "/media/recent/?access_token=" + AppData.accesstokn;
                url = new URL(media);
                mHttpURLConnection = (HttpURLConnection) url.openConnection();
                mHttpURLConnection.setRequestMethod("GET");
                respose = String.valueOf(Util.covertInputStreamToString(mHttpURLConnection.getInputStream()));
                Log.i("MediaDetails", respose);
                //get details of mediaobject
                JSONObject rootJsonObject = (JSONObject) new JSONTokener(respose).nextValue();
                JSONArray rootJsonArray = rootJsonObject.getJSONArray("data");
                for (int j = 0; j < rootJsonArray.length(); j++) {
                    mediaDetails = new MediaDetails();
                    JSONObject jsonObject = rootJsonArray.getJSONObject(j);
                    String imageurl = jsonObject.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                    String liked = jsonObject.getJSONObject("likes").getString("count");
                    String caption = jsonObject.getJSONObject("caption").getString("text");
                    String totalComment = jsonObject.getJSONObject("comments").getString("count");
                    String mediaidd = jsonObject.getString("id");
                    mediaDetailsMap.put(mediaidd, mediaDetails);
                    Log.i("FollwdBy mediaData", imageurl + "\n " + liked + "\n " + caption + "\n " + totalComment + "\n " + mediaidd);
                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}