package instagram.robosoft.com.mytestapplication.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import instagram.robosoft.com.mytestapplication.MainActivity;
import instagram.robosoft.com.mytestapplication.communicator.CommentDetailsCallBack;
import instagram.robosoft.com.mytestapplication.constant.AppData;
import instagram.robosoft.com.mytestapplication.model.CommentDetails;
import instagram.robosoft.com.mytestapplication.model.MediaDetails;
import instagram.robosoft.com.mytestapplication.utils.Util;

/**
 * Created by deena on 29/2/16.
 */
public class RequestForCommentAsyncTask extends  AsyncTask<String, CommentDetails, ArrayList<ArrayList<CommentDetails>>> {
    private SharedPreferences mSharedPreferences;
    private ArrayList<MediaDetails> mediaDetailses;
    int mCommentCountDisplay;
    private ArrayList<ArrayList<CommentDetails>> commentArrayList;
    private CommentDetailsCallBack commentDetailsCallBack;
    private Util util;
    private ProgressDialog progressBar;
    private Context mContext;


    public RequestForCommentAsyncTask(ArrayList<MediaDetails> mediaDetailses, Context mContext) {
        this.mediaDetailses = mediaDetailses;
        this.mContext = mContext;
        mSharedPreferences = mContext.getSharedPreferences(AppData.SETTINGPREFRENCE, mContext.MODE_PRIVATE);
        mCommentCountDisplay = Integer.parseInt(mSharedPreferences.getString(AppData.SettingKey, AppData.defaultNoOfComment));
        commentArrayList = new ArrayList<>();
        commentDetailsCallBack = (CommentDetailsCallBack) mContext;
        util = new Util();
        progressBar = new ProgressDialog(mContext);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar = ProgressDialog.show(mContext, "", "Loading...");

    }

    @Override
    protected ArrayList<ArrayList<CommentDetails>> doInBackground(String... params) {
        for (MediaDetails mediaDetails : mediaDetailses) {
            ArrayList<CommentDetails> arrayList = util.getCommentDetails(mediaDetails.getMediaId(), mCommentCountDisplay);
            commentArrayList.add(arrayList);
        }
        if (isCancelled())
            progressBar.dismiss();
        return commentArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<ArrayList<CommentDetails>> arrayLists) {
        super.onPostExecute(arrayLists);
        commentDetailsCallBack.commentDetails(arrayLists);
        progressBar.dismiss();
    }

}
