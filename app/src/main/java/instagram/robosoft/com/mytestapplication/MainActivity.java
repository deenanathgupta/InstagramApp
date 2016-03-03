package instagram.robosoft.com.mytestapplication;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.RelativeLayout;

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
    private SharedPreferences sharedPreferences;
    private String mUsername;
    private Map<String, MediaDetails> mediaDetailsMap;
    private WebView webView;
    private AlertDialog.Builder mAlertBuilder;
    private RecyclerviewAdapter mRecyclerviewAdapter;
    private FloatingActionButton mFab;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.htab_toolbar);
        setSupportActionBar(toolbar);
        mAlertBuilder = new AlertDialog.Builder(this);
        mAlertBuilder.setTitle("Setting.");
        webView = (WebView) findViewById(R.id.webview);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        sharedPreferences = getSharedPreferences(AppData.SETTINGPREFRENCE, MODE_PRIVATE);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorlayout);
        if (Util.isNwConnected(this)) {
            giveUrlToWebView();
        } else {
            reloadConnection();
        }


    }

    private void reloadConnection() {
        Snackbar.make(coordinatorLayout, "Internet connection is not Available", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
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
    public void getData(String s) {
        getSupportActionBar().setTitle(s);
        this.mUsername = s;
        new MediaDetailsAsyncTask(this).execute(AppData.USER_INFORMATION, AppData.FOLLWERS);
    }

    @Override
    public void getMediaDetails(Map<String, MediaDetails> l) {
        mediaDetailsMap = l;
        webView.setVisibility(View.GONE);
        int commentCountDisplay = Integer.parseInt(sharedPreferences.getString(AppData.SettingKey, "5"));
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerviewAdapter = new RecyclerviewAdapter(mediaDetailsMap, this, mUsername, commentCountDisplay);
        mRecyclerView.setAdapter(mRecyclerviewAdapter);
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

            case R.id.setting:
                View v = LayoutInflater.from(this).inflate(R.layout.inputdialog, null);
                mAlertBuilder.setView(v);
                mAlertBuilder.setIcon(R.drawable.ic_settings_black_24dp);
                final EditText editText = (EditText) v.findViewById(R.id.editText2);
                mAlertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(AppData.SettingKey, editText.getText().toString());
                        editor.commit();
                        if (editText.getText().toString().trim().length() != 0) {
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            mRecyclerviewAdapter = new RecyclerviewAdapter(mediaDetailsMap, MainActivity.this, mUsername, Integer.parseInt(editText.getText().toString()));
                            mRecyclerView.setAdapter(mRecyclerviewAdapter);
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













