package instagram.robosoft.com.mytestapplication;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import instagram.robosoft.com.mytestapplication.adapter.RecyclerviewAdapter;
import instagram.robosoft.com.mytestapplication.asynctask.MediaDetailsAsyncTask;
import instagram.robosoft.com.mytestapplication.communicator.CallBack;
import instagram.robosoft.com.mytestapplication.communicator.MediaDetailsDataCommunicatior;
import instagram.robosoft.com.mytestapplication.constant.AppData;
import instagram.robosoft.com.mytestapplication.model.CommentDetails;
import instagram.robosoft.com.mytestapplication.model.MediaDetails;
import instagram.robosoft.com.mytestapplication.model.PostDetailsOfInstagram;

public class MainActivity extends AppCompatActivity implements CallBack, MediaDetailsDataCommunicatior {

    private RecyclerView mRecyclerView;
    private SharedPreferences sharedPreferences;
    //private List<MediaDetails> mMediaDetailses;
    private Map<String, MediaDetails> mediaDetailsMap;
    private Map<String, List<CommentDetails>> mCommentDetailsList = null;
    private Map<String, PostDetailsOfInstagram> instagramMap;
    private ViewGroup mViewGroup;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instagramMap = new HashMap<>();
        mViewGroup = (ViewGroup) findViewById(R.id.rootviewGroup);
        webView = (WebView) findViewById(R.id.webview);
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
        //Log.i("Name", name);
        new MediaDetailsAsyncTask(this).execute(AppData.USER_INFORMATION, AppData.FOLLWERS);
    }

    @Override
    public void getMediaDetails(Map<String, MediaDetails> l) {
        mediaDetailsMap = l;
        webView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new RecyclerviewAdapter(mediaDetailsMap, this, mViewGroup));
    }

}













