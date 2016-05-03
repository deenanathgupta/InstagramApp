package instagram.robosoft.com.mytestapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import instagram.robosoft.com.mytestapplication.communicator.ImageAsyncCallBack;
import instagram.robosoft.com.mytestapplication.constant.AppData;

public class UserProfile extends AppCompatActivity {
    private TextView txtTotalPosts, txtTotalFollwers, txtTotalFollowing, txtUserName;
    private ImageView mUserProfilePic;
    private String[] mUserDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        txtTotalPosts = (TextView) findViewById(R.id.txttotalposts);
        txtTotalFollwers = (TextView) findViewById(R.id.txttotalfollwers);
        txtTotalFollowing = (TextView) findViewById(R.id.txttotalfollowing);
        txtUserName = (TextView) findViewById(R.id.txtuserName);
        mUserProfilePic = (ImageView) findViewById(R.id.userimage);

        /*if (savedInstanceState != null) {
            mUserDetails = savedInstanceState.getStringArray(AppData.USER_PROFILE_DETAILS);
        } else {
            Intent intent = getIntent();
            mUserDetails = intent.getStringArrayExtra(AppData.USERDETAILS);
        }
        if (!mUserDetails.equals(null)) {
            txtUserName.setText(mUserDetails[0]);
            Picasso.with(this).load(mUserDetails[1]).into(mUserProfilePic);
        }*/
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putStringArray(AppData.USER_PROFILE_DETAILS, mUserDetails);
    }
}
