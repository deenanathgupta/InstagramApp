package instagram.robosoft.com.mytestapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import instagram.robosoft.com.mytestapplication.adapter.RecyclerviewAdapter;
import instagram.robosoft.com.mytestapplication.asynctask.MediaDetailsAsyncTask;
import instagram.robosoft.com.mytestapplication.communicator.CallBack;
import instagram.robosoft.com.mytestapplication.communicator.MediaDetailsDataCommunicatior;
import instagram.robosoft.com.mytestapplication.constant.AppData;
import instagram.robosoft.com.mytestapplication.model.MediaDetails;
import instagram.robosoft.com.mytestapplication.utils.Util;

public class MainActivity extends AppCompatActivity implements CallBack, MediaDetailsDataCommunicatior, View.OnClickListener {

    private RecyclerView mRecyclerView;
    private SharedPreferences mSharedPreferences;
    private String mUsername;
    private WebView webView;
    private AlertDialog.Builder mAlertBuilder;
    private RecyclerviewAdapter mRecyclerviewAdapter;
    private FloatingActionButton mFab;
    private CoordinatorLayout mCoordinatorLayout;
    private LinearLayout linearLayout;
    private int commentCountDisplay = 0;
    private ArrayList<MediaDetails> mediaDetailseslist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

        Toolbar toolbar = (Toolbar) findViewById(R.id.htab_toolbar);
        setSupportActionBar(toolbar);

        mAlertBuilder = new AlertDialog.Builder(this);
        mAlertBuilder.setTitle("Setting.");
        mAlertBuilder.setMessage("Enter a value For comment to show");

        webView = (WebView) findViewById(R.id.webview);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        mSharedPreferences = getSharedPreferences(AppData.SETTINGPREFRENCE, MODE_PRIVATE);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorlayout);
        if (savedInstanceState == null) {
            if (Util.isNwConnected(this)) {
                giveUrlToWebView();
                linearLayout.setVisibility(View.GONE);
            } else {
                reloadConnection();
            }
        }
        if (!Util.isNwConnected(this)) {
            mFab.show();
        } else {
            mFab.hide();
        }
    }

    private void reloadConnection() {
        Snackbar.make(mCoordinatorLayout, "Internet connection is not Available", Snackbar.LENGTH_LONG).setAction("ReTry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isNwConnected(MainActivity.this))
                    giveUrlToWebView();
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
        outState.putParcelableArrayList("Data", mediaDetailseslist);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mediaDetailseslist = savedInstanceState.getParcelableArrayList("Data");
        passDataToAdapter();

    }


    @Override
    public void getData(String s) {
        getSupportActionBar().setTitle(s);
        this.mUsername = s;
        new MediaDetailsAsyncTask(this).execute(AppData.USER_INFORMATION, AppData.FOLLWERS);
        linearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void getMediaDetails(ArrayList<MediaDetails> l) {
        mediaDetailseslist = l;
        passDataToAdapter();
    }

    private void passDataToAdapter() {
        webView.setVisibility(View.GONE);
        commentCountDisplay = Integer.parseInt(mSharedPreferences.getString(AppData.SettingKey, "5"));
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerviewAdapter = new RecyclerviewAdapter(mediaDetailseslist, this, mUsername, commentCountDisplay);
        mRecyclerView.setAdapter(mRecyclerviewAdapter);
        linearLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:

                break;
            case R.id.setting:
                View v = LayoutInflater.from(this).inflate(R.layout.inputdialog, null);
                mAlertBuilder.setView(v);
                mAlertBuilder.setIcon(R.drawable.ic_settings_black_24dp);
                final EditText editText = (EditText) v.findViewById(R.id.editText2);
                commentCountDisplay = Integer.parseInt(mSharedPreferences.getString(AppData.SettingKey, "5"));
                if (commentCountDisplay != 0) {
                    editText.setText(commentCountDisplay + "");
                }
                mAlertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String input = editText.getText().toString();
                        SharedPreferences.Editor editor = mSharedPreferences.edit();

                        if (input.trim().length() != 0) {
                            editor.putString(AppData.SettingKey, input);
                            editor.commit();

                            mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            mRecyclerviewAdapter = new RecyclerviewAdapter(mediaDetailseslist, MainActivity.this, mUsername, Integer.parseInt(editText.getText().toString()));
                            mRecyclerView.setAdapter(mRecyclerviewAdapter);
                        } else {
                            Snackbar.make(mCoordinatorLayout, "Please enter valid number", Snackbar.LENGTH_LONG).show();
                        }


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = mAlertBuilder.create();
                alertDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        reloadConnection();
    }
}













