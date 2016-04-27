package instagram.robosoft.com.mytestapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import instagram.robosoft.com.mytestapplication.adapter.RecyclerviewAdapter;
import instagram.robosoft.com.mytestapplication.asynctask.MediaDetailsAsyncTask;
import instagram.robosoft.com.mytestapplication.asynctask.RequestForCommentAsyncTask;
import instagram.robosoft.com.mytestapplication.communicator.CallBack;
import instagram.robosoft.com.mytestapplication.communicator.CommentDetailsCallBack;
import instagram.robosoft.com.mytestapplication.communicator.MediaDetailsDataCommunicatior;
import instagram.robosoft.com.mytestapplication.constant.AppData;
import instagram.robosoft.com.mytestapplication.model.CommentDetails;
import instagram.robosoft.com.mytestapplication.model.MediaDetails;
import instagram.robosoft.com.mytestapplication.utils.Util;

public class MainActivity extends AppCompatActivity implements CallBack, MediaDetailsDataCommunicatior, CommentDetailsCallBack, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private SharedPreferences mSharedPreferences;
    private WebView webView;
    private AlertDialog.Builder mAlertBuilder;
    private RecyclerviewAdapter mRecyclerviewAdapter;
    private FloatingActionButton mFloatingActionButton;
    private CoordinatorLayout mCoordinatorLayout;
    private LinearLayout mLinearLayout;
    private int mCommentCountDisplay = 0;
    private String[] mUserdetails;
    private ArrayList<MediaDetails> mMediaDetailseslist = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayoutManager mLinearLayoutManager;
    private Util mUtil;
    private ArrayList<String> nextUrlArrayList;
    // Variables for scroll listener
    private boolean mUserScrolled = true;
    private int mPastVisiblesItems, mVisibleItemCount, mTotalItemCount;
    private int currentOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentOrientation = getResources().getConfiguration().orientation;
        initializeView();

        if (!Util.isNwConnected(this)) {
            mFloatingActionButton.show();
        } else {
            mFloatingActionButton.hide();
        }
        if (savedInstanceState == null) {
            if (Util.isNwConnected(this)) {
                Util.lockOrientation(MainActivity.this);
                giveUrlToWebView();
                mLinearLayout.setVisibility(View.GONE);
            } else {
                reloadConnection();
            }
        } else {
            mMediaDetailseslist = savedInstanceState.getParcelableArrayList(AppData.SAVE_STATE_PARCELABLE_ARRAY_LIST);
            mUserdetails = savedInstanceState.getStringArray(AppData.USER_DETAILS_ARRAY);
            nextUrlArrayList = savedInstanceState.getStringArrayList("nexturl");
            mUtil = (Util) getLastCustomNonConfigurationInstance();
            if (mUserdetails[0] != null)
                getSupportActionBar().setTitle(mUserdetails[0]);
            passDataToAdapter(true);
        }

    }

    private void initializeView() {
        mLinearLayout = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.htab_toolbar);
        setSupportActionBar(toolbar);
        mAlertBuilder = new AlertDialog.Builder(this);
        mAlertBuilder.setTitle(R.string.setting);
        mAlertBuilder.setMessage(R.string.alertMessage);
        webView = (WebView) findViewById(R.id.webview);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mSharedPreferences = getSharedPreferences(AppData.SETTINGPREFRENCE, MODE_PRIVATE);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
        implementScrollListener();
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorlayout);

    }


    private void implementScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mUserScrolled = true;
                    //Log.i("test", "onScrollStateChanged()");
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Log.i("test","onScrolled()");
                if (dy > 0) {
                    mVisibleItemCount = mLinearLayoutManager.getChildCount();
                    mTotalItemCount = mLinearLayoutManager.getItemCount();
                    mPastVisiblesItems = mLinearLayoutManager.findFirstVisibleItemPosition();
                    if (mUserScrolled
                            && (mVisibleItemCount + mPastVisiblesItems) == mTotalItemCount) {
                        mUserScrolled = false;
                        if (Util.isNwConnected(MainActivity.this) && mUtil != null) {
                            nextUrlArrayList = mUtil.getNextUrlArrayList();
                            updateRecyclerView(nextUrlArrayList);
                        }

                    }

                }
            }
        });
    }

    private void updateRecyclerView(ArrayList<String> nextUrlArrayList) {
        Util.lockOrientation(this);
        new MediaDetailsAsyncTask(nextUrlArrayList, MainActivity.this).execute();
    }

    public void reloadConnection() {
        Snackbar.make(mCoordinatorLayout, R.string.snackBarMessage, Snackbar.LENGTH_LONG).setAction("ReTry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isNwConnected(MainActivity.this)) {
                    giveUrlToWebView();
                    mFloatingActionButton.hide();
                }
            }
        }).setActionTextColor(Color.RED).show();
    }

    private void giveUrlToWebView() {
        webView.setWebViewClient(new WebViewClient(this));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(AppData.authURLString);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(AppData.SAVE_STATE_PARCELABLE_ARRAY_LIST, mMediaDetailseslist);
        outState.putStringArray(AppData.USER_DETAILS_ARRAY, mUserdetails);
        outState.putStringArrayList("nexturl", nextUrlArrayList);
    }

    @Override
    public void getData(String s[]) {
        if (s[0] != null)
            getSupportActionBar().setTitle(s[0]);
        this.mUserdetails = s;
        Util.lockOrientation(this);
        new MediaDetailsAsyncTask(this).execute(AppData.USER_INFORMATION, AppData.FOLLWERS);
        mLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void getMediaDetails(ArrayList<MediaDetails> l, Util util, Boolean flag) {
        //mMediaDetailseslist = l;
        mMediaDetailseslist.addAll(l);
        passDataToAdapter(flag);
        mUtil = util;
        mSwipeRefreshLayout.setRefreshing(false);
        Util.unLockOrientation(MainActivity.this);
    }

    private void passDataToAdapter(Boolean flag) {
        webView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        if (flag) {
            mCommentCountDisplay = Integer.parseInt(mSharedPreferences.getString(AppData.SettingKey, AppData.defaultNoOfComment));
            mLinearLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerviewAdapter = new RecyclerviewAdapter(mMediaDetailseslist, this, mUserdetails[0], mCommentCountDisplay);
            mRecyclerView.setAdapter(mRecyclerviewAdapter);
        } else {
            mRecyclerviewAdapter.notifyDataSetChanged();
        }

        mLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                profileActictivity();
                break;
            case R.id.setting:
                settingForComment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void profileActictivity() {
        Intent intent = new Intent(MainActivity.this, UserProfile.class);
        Bundle bundle = new Bundle();
        bundle.putStringArray(AppData.USERDETAILS, mUserdetails);
        bundle.putParcelableArrayList(AppData.USER_COMMENT_DETAILS, mMediaDetailseslist);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void settingForComment() {
        View v = LayoutInflater.from(this).inflate(R.layout.inputdialog, null);
        mAlertBuilder.setView(v);
        mAlertBuilder.setIcon(R.drawable.ic_settings_black_24dp);
        final EditText editText = (EditText) v.findViewById(R.id.editText2);
        mCommentCountDisplay = Integer.parseInt(mSharedPreferences.getString(AppData.SettingKey, AppData.defaultNoOfComment));
        if (mCommentCountDisplay != 0) {
            editText.setText(mCommentCountDisplay + "");
        }
        mAlertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String input = editText.getText().toString();
                SharedPreferences.Editor editor = mSharedPreferences.edit();

                if (input.trim().length() != 0) {
                    editor.putString(AppData.SettingKey, input);
                    editor.commit();
                    new RequestForCommentAsyncTask(mMediaDetailseslist, MainActivity.this).execute();
                } else {
                    Snackbar.make(mCoordinatorLayout, R.string.snackBarMessageForEditText, Snackbar.LENGTH_LONG).show();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = mAlertBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        reloadConnection();
    }

    @Override
    public void onRefresh() {
        new MediaDetailsAsyncTask(this).execute(AppData.USER_INFORMATION, AppData.FOLLWERS);
    }

    @Override
    public void commentDetails(ArrayList<ArrayList<CommentDetails>> commentArrayList) {
        int indx = 0;
        for (ArrayList<CommentDetails> arrayList : commentArrayList) {
            mMediaDetailseslist.get(indx).setCommentDetailsArrayList(arrayList);
            indx++;
        }
        mRecyclerviewAdapter.notifyDataSetChanged();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mUtil;
    }
}













