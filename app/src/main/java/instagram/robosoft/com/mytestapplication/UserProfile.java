package instagram.robosoft.com.mytestapplication;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import instagram.robosoft.com.mytestapplication.constant.AppData;
import instagram.robosoft.com.mytestapplication.utils.Util;

public class UserProfile extends AppCompatActivity {
    private TextView txtTotalPosts, txtTotalFollwers, txtTotalFollowing, txtUserName;
    private ImageView mUserProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initializeView();
        String userId = getIntent().getStringExtra("userId");
        if (Util.isNwConnected(this)) {
            new UserDetailAsyncTask().execute(AppData.APIURL + "/users/" + userId + "/?access_token=" + AppData.accesstokn);
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences(AppData.USER_PROFILE_DETAILS, MODE_PRIVATE);
            txtUserName.setText(sharedPreferences.getString(AppData.USER_NAME, "NA"));
            txtTotalFollwers.setText(sharedPreferences.getString(AppData.FOLLOWED_BY, "0"));
            txtTotalFollowing.setText(sharedPreferences.getString(AppData.FOLLOWERS, "0"));
            txtTotalPosts.setText(sharedPreferences.getString(AppData.POSTED_MEDIA, "0"));
        }

    }

    private void initializeView() {
        txtTotalPosts = (TextView) findViewById(R.id.txttotalposts);
        txtTotalFollwers = (TextView) findViewById(R.id.txttotalfollwers);
        txtTotalFollowing = (TextView) findViewById(R.id.txttotalfollowing);
        txtUserName = (TextView) findViewById(R.id.txtuserName);
        mUserProfilePic = (ImageView) findViewById(R.id.userimage);
    }
    class UserDetailAsyncTask extends AsyncTask<String, Void, ArrayList<String>> {
        HttpURLConnection mHttpURLConnection = null;
        private ArrayList<String> mUserDetailsArrayList;
        private URL mUrl;
        private SharedPreferences mSharedPreferences;

        public UserDetailAsyncTask() {
            mUserDetailsArrayList = new ArrayList<>();
            mSharedPreferences = getSharedPreferences(AppData.USER_PROFILE_DETAILS, MODE_PRIVATE);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            try {
                mUrl = new URL(params[0]);
                mHttpURLConnection = (HttpURLConnection) mUrl.openConnection();
                String respose = String.valueOf(Util.covertInputStreamToString(mHttpURLConnection.getInputStream()));
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
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(AppData.USER_NAME, fullName);
                editor.putString(AppData.USER_PROFILE_PIC, profilePic);
                editor.putString(AppData.POSTED_MEDIA, String.valueOf(media));
                editor.putString(AppData.FOLLOWED_BY, String.valueOf(followed_by));
                editor.putString(AppData.FOLLOWERS, String.valueOf(follows));
                editor.apply();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mUserDetailsArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<String> mUserDetailsArrayList) {
            super.onPostExecute(mUserDetailsArrayList);
            Picasso.with(UserProfile.this).load(mUserDetailsArrayList.get(1)).into(mUserProfilePic);
            txtUserName.setText(mUserDetailsArrayList.get(2));
            txtTotalPosts.setText(mUserDetailsArrayList.get(3));
            txtTotalFollowing.setText(mUserDetailsArrayList.get(5));
            txtTotalFollwers.setText(mUserDetailsArrayList.get(4));
        }
    }
}
