package instagram.robosoft.com.mytestapplication;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;

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
    private String mUsername;
    private Map<String, MediaDetails> mediaDetailsMap;
    private WebView webView;
    private AlertDialog.Builder mAlertBuilder;
    private RecyclerviewAdapter mRecyclerviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAlertBuilder = new AlertDialog.Builder(this);
        mAlertBuilder.setTitle("Setting.");
        webView = (WebView) findViewById(R.id.webview);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        sharedPreferences = getSharedPreferences(AppData.SETTINGPREFRENCE, MODE_PRIVATE);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
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
}













