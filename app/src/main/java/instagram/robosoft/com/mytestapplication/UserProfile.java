package instagram.robosoft.com.mytestapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import instagram.robosoft.com.mytestapplication.communicator.ImageAsyncCallBack;
import instagram.robosoft.com.mytestapplication.constant.AppData;

public class UserProfile extends AppCompatActivity {
    private TextView txtTotalPosts, txtTotalFollwers, txtTotalFollowing, txtUserName;
    private ImageView mUserProfilePic;
    private ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        txtTotalPosts = (TextView) findViewById(R.id.txttotalposts);
        txtTotalFollwers = (TextView) findViewById(R.id.txttotalfollwers);
        txtTotalFollowing = (TextView) findViewById(R.id.txttotalfollowing);
        txtUserName = (TextView) findViewById(R.id.txtuserName);
        mUserProfilePic = (ImageView) findViewById(R.id.userimage);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            arrayList = bundle.getStringArrayList(AppData.UserDetailKeyBundle);
            Picasso.with(this).load(arrayList.get(1)).into(mUserProfilePic);
            txtUserName.setText(arrayList.get(2));
            txtTotalPosts.setText(arrayList.get(3));
            txtTotalFollowing.setText(arrayList.get(5));
            txtTotalFollwers.setText(arrayList.get(4));
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences(AppData.USER_PROFILE_DETAILS, MODE_PRIVATE);
            txtUserName.setText(sharedPreferences.getString(AppData.USER_NAME, ""));
            txtTotalFollwers.setText(sharedPreferences.getString(AppData.FOLLOWED_BY, "0"));
            txtTotalFollowing.setText(sharedPreferences.getString(AppData.FOLLOWERS, "0"));
            txtTotalPosts.setText(sharedPreferences.getString(AppData.POSTED_MEDIA, "0"));
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(AppData.USER_PROFILE_DETAILS, arrayList);
    }
}
