package instagram.robosoft.com.mytestapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import instagram.robosoft.com.mytestapplication.asynctask.FollwerandFollwedByList;
import instagram.robosoft.com.mytestapplication.asynctask.GetAccessToken;
import instagram.robosoft.com.mytestapplication.asynctask.GetComment;
import instagram.robosoft.com.mytestapplication.asynctask.ImageRequestAsyncTask;
import instagram.robosoft.com.mytestapplication.communicator.CallBack;
import instagram.robosoft.com.mytestapplication.communicator.CommentDetailsCallBack;
import instagram.robosoft.com.mytestapplication.communicator.MediaDetailsDataCommunicatior;
import instagram.robosoft.com.mytestapplication.constant.AppData;
import instagram.robosoft.com.mytestapplication.model.CommentDetails;
import instagram.robosoft.com.mytestapplication.model.MediaDetails;
import instagram.robosoft.com.mytestapplication.model.PostDetailsOfInstagram;

public class MainActivity extends AppCompatActivity implements CallBack, MediaDetailsDataCommunicatior, CommentDetailsCallBack {

    private RecyclerView mRecyclerView;
    private SharedPreferences sharedPreferences;
    //private List<MediaDetails> mMediaDetailses;
    private Map<String,MediaDetails> mediaDetailsMap=new HashMap<>();
    private Map mCommentDetailsList;
    private Map<String, PostDetailsOfInstagram> instagramMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instagramMap = new HashMap<>();
        WebView webView = (WebView) findViewById(R.id.webview);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        sharedPreferences = getSharedPreferences(AppData.MYPREFERENCE, MODE_PRIVATE);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new WebViewClient(this));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(AppData.authURLString);

    }

    @Override
    public void getData(String s) {
        getSupportActionBar().setTitle(s);
        String name = sharedPreferences.getString(AppData.Id, "");
        Log.i("Name", name);
        new ImageRequestAsyncTask(this).execute(AppData.USER_INFORMATION);
        //new GetComment().execute(AppData.recentComment);
    }

    @Override
    public void getMediaDetails(Map<String,MediaDetails> l) {
        mediaDetailsMap = l;
        new GetComment(mediaDetailsMap, this).execute();
    }

    @Override
    public void commentDetails(Map commentDetailses) {
        mCommentDetailsList = commentDetailses;
        new FollwerandFollwedByList().execute("https://api.instagram.com/v1/users/self/follows?access_token="+AppData.accesstokn,
                "https://api.instagram.com/v1/users/self/followed-by?access_token="+AppData.accesstokn);
    }
}
/* //TODO
        int i = 0;
        Iterator iterator = commentDetailses.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String mediaId = (String) entry.getKey();
            List<CommentDetails> list = (List<CommentDetails>) entry.getValue();
            MediaDetails mediaDetails = mMediaDetailses.get(i);
            PostDetailsOfInstagram postDetailsOfInstagram = new PostDetailsOfInstagram();
            postDetailsOfInstagram.setMediaUrl(mediaDetails.getMediaUrl());
            postDetailsOfInstagram.setMediaId(mediaDetails.getMediaId());
            postDetailsOfInstagram.setPostDescription(mediaDetails.getPostDescription());
            postDetailsOfInstagram.setTotalLike(mediaDetails.getTotalLike());
            postDetailsOfInstagram.setTotlaNoOfComment(mediaDetails.getTotlaNoOfComment());
            postDetailsOfInstagram.setCommentDetailsList(list);
            instagramMap.put(mediaId, postDetailsOfInstagram);
        }
        mCommentDetailsList = null;
        mMediaDetailses = null;
        Log.i("MediaSize", instagramMap.size() + "");
        //finish();
        //startActivity(new Intent(this, InstagramHomePage.class));
        //create a hahmap which will contain all data (mediadetails+comment);
        //Log.i("MediaSize", mMediaDetailses.size() + "");
        //Log.i("CommentSize", mCommentDetailsList.size() + "");*/