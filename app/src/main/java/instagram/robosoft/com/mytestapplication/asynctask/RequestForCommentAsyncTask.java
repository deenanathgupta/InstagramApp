package instagram.robosoft.com.mytestapplication.asynctask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import instagram.robosoft.com.mytestapplication.MainActivity;
import instagram.robosoft.com.mytestapplication.constant.AppData;
import instagram.robosoft.com.mytestapplication.model.CommentDetails;
import instagram.robosoft.com.mytestapplication.utils.Util;

/**
 * Created by deena on 29/2/16.
 */
public class RequestForCommentAsyncTask extends AsyncTask<String, CommentDetails, Void> {
    HttpURLConnection httpURLConnection;
    private Context mContext;
    private ViewGroup childViewGroup;
    private int commentCount;
    private int count = 0;
    private SharedPreferences mSharedPreferences;

    public RequestForCommentAsyncTask(ViewGroup childViewGroup, Context mContext, int commentCount) {
        //Log.i("CommentAsyncTask", "DC");
        this.mContext = mContext;
        this.childViewGroup = childViewGroup;
        this.commentCount = commentCount;
        mSharedPreferences = mContext.getSharedPreferences(AppData.SETTINGPREFRENCE, Context.MODE_PRIVATE);

    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            //Log.i("CommentAsyncTask", "doInBackground()");
            CommentDetails commentDetails;
            String recentComment = AppData.APIURL + "/media/" + params[0] + "/comments?access_token=" + AppData.accesstokn;
            URL url = new URL(recentComment);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            String respose = String.valueOf(Util.covertInputStreamToString(httpURLConnection.getInputStream()));
            //Log.i("CommentList", respose);
            JSONObject jsonObject = (JSONObject) new JSONTokener(respose).nextValue();
            JSONArray data = jsonObject.getJSONArray("data");
            if (commentCount > data.length())
                count = data.length();
            else if (commentCount < data.length())
                count = commentCount;
            for (int i = 0; i < count; i++) {
                commentDetails = new CommentDetails();
                JSONObject object = data.getJSONObject(i);
                String comment = object.getString("text");
                String commentFromUser = object.getJSONObject("from").getString("username");
                commentDetails.setCommentedBy(commentFromUser);
                commentDetails.setComment(comment);
                publishProgress(commentDetails);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(CommentDetails... values) {
        super.onProgressUpdate(values);
        TextView textViewComment = new TextView(mContext);
        textViewComment.append(values[0].getCommentedBy() + ":-" + values[0].getComment());
        childViewGroup.addView(textViewComment);

    }
}
