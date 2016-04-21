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
import instagram.robosoft.com.mytestapplication.utils.ImageDownloader;

public class UserProfile extends AppCompatActivity implements ImageAsyncCallBack {
    private TextView txtTotalPosts, txtTotalFollwers, txtTotalFollowing;
    private ImageView mUserProfilePic;
    private String[] mUserDetails;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        txtTotalPosts = (TextView) findViewById(R.id.txttotalposts);
        txtTotalFollwers = (TextView) findViewById(R.id.txttotalfollwers);
        txtTotalFollowing = (TextView) findViewById(R.id.txttotalfollowing);
        mUserProfilePic = (ImageView) findViewById(R.id.userimages);

        Log.i("Testtt", "OnCreate()");

        Intent intent = getIntent();
        mUserDetails = intent.getStringArrayExtra(AppData.USERDETAILS);
        Log.i("Test", mUserDetails[0] + " UserName" + "  " + mUserDetails[1]);
        //if (savedInstanceState == null) {

        Picasso.with(this).load(mUserDetails[1]).into(mUserProfilePic);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray("userProfileInfo", mUserDetails);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mUserDetails = savedInstanceState.getStringArray("userProfileInfo");
    }

    @Override
    public void processFinish(Bitmap bitmap, String Url) {
        mUserProfilePic.setImageBitmap(bitmap);
    }
}
