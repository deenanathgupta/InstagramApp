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

import instagram.robosoft.com.mytestapplication.constant.AppData;
import instagram.robosoft.com.mytestapplication.utils.Util;

/**
 * Created by deena on 27/2/16.
 */
public class FollwerandFollwedByList extends AsyncTask<String, Void, Void> {
    private HttpURLConnection mHttpURLConnection = null;
    String MediaId[];



    @Override
    protected Void doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            mHttpURLConnection = (HttpURLConnection) url.openConnection();
            mHttpURLConnection.setRequestMethod("GET");
            String respose = String.valueOf(Util.covertInputStreamToString(mHttpURLConnection.getInputStream()));
            Log.i("Follws", respose);
            url = new URL(params[1]);
            mHttpURLConnection = (HttpURLConnection) url.openConnection();
            mHttpURLConnection.setRequestMethod("GET");
            respose = String.valueOf(Util.covertInputStreamToString(mHttpURLConnection.getInputStream()));
            Log.i("FollwsBy", respose);

            JSONObject jsonObject = (JSONObject) new JSONTokener(respose).nextValue();
            JSONArray followdByDataArray = jsonObject.getJSONArray("data");
            MediaId = new String[followdByDataArray.length()];
            for (int i = 0; i < followdByDataArray.length(); i++) {
                MediaId[i] = followdByDataArray.getJSONObject(i).getString("id");
            }
            for (int i = 0; i < MediaId.length; i++) {
                String media = "https://api.instagram.com/v1/users/" + MediaId[i] + "/media/recent/?access_token=" + AppData.accesstokn;
                url = new URL(media);
                mHttpURLConnection = (HttpURLConnection) url.openConnection();
                mHttpURLConnection.setRequestMethod("GET");
                respose = String.valueOf(Util.covertInputStreamToString(mHttpURLConnection.getInputStream()));
                Log.i("MediaDetails", respose);
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
}
