package instagram.robosoft.com.mytestapplication.asynctask;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import instagram.robosoft.com.mytestapplication.UserProfile;
import instagram.robosoft.com.mytestapplication.utils.Util;

/**
 * Created by deena on 2/5/16.
 */
public class UserDetailAsyncTask extends AsyncTask<String, Void, Void> {
    HttpURLConnection mHttpURLConnection = null;
    private Context mContext;
    private ArrayList<String> mUserDetailsArrayList;
    Util util;
    URL mUrl;

    public UserDetailAsyncTask(Context mContext) {
        this.mContext = mContext;
        mUserDetailsArrayList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            mUrl = new URL(params[0]);
            mHttpURLConnection = (HttpURLConnection) mUrl.openConnection();
            String respose = String.valueOf(Util.covertInputStreamToString(mHttpURLConnection.getInputStream()));
            Log.i("test", "RES " + respose);
            JSONObject jsonObject = (JSONObject) new JSONTokener(respose).nextValue();
            JSONObject dataJsonObject = jsonObject.getJSONObject("data");
            String userName = dataJsonObject.getString("username");
            String profilePic = dataJsonObject.getString("profile_picture");
            String fullName = dataJsonObject.getString("full_name");
            JSONObject countJsonObject = dataJsonObject.getJSONObject("counts");
            int media = countJsonObject.getInt("media");
            int followed_by = countJsonObject.getInt("followed_by");
            int follows = countJsonObject.getInt("follows");
            mUserDetailsArrayList.add(userName);
            mUserDetailsArrayList.add(profilePic);
            mUserDetailsArrayList.add(fullName);
            mUserDetailsArrayList.add(String.valueOf(media));
            mUserDetailsArrayList.add(String.valueOf(followed_by));
            mUserDetailsArrayList.add(String.valueOf(follows));
            mUserDetailsArrayList.add(userName);
            Log.d("test", "Size of ArrayaUserDetails+++" + mUserDetailsArrayList.size());
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
        Bundle bundle = new Bundle();
        Intent intent = new Intent(mContext, UserProfile.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }
}
