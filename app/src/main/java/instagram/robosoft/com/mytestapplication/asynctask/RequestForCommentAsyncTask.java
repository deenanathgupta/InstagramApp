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
    HttpURLConnection mHttpUrlConnection;
    private Context mContext;
    private ViewGroup mChildViewGroup;
    private int mCommentCount;
    private int mCount = 0;
    private SharedPreferences mSharedPreferences;

    public RequestForCommentAsyncTask(ViewGroup childViewGroup, Context mContext, int mCommentCount) {
        this.mContext = mContext;
        this.mChildViewGroup = childViewGroup;
        this.mCommentCount = mCommentCount;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            CommentDetails commentDetails;
            String recentComment = AppData.APIURL + "/media/" + params[0] + "/comments?access_token=" + AppData.accesstokn;
            URL url = new URL(recentComment);
            mHttpUrlConnection = (HttpURLConnection) url.openConnection();
            String respose = String.valueOf(Util.covertInputStreamToString(mHttpUrlConnection.getInputStream()));
            JSONObject jsonObject = (JSONObject) new JSONTokener(respose).nextValue();
            JSONArray data = jsonObject.getJSONArray("data");
            if (mCommentCount > data.length())
                mCount = data.length();
            else if (mCommentCount < data.length())
                mCount = mCommentCount;
            int temp = data.length() - mCount;
            for (int i = 0; i < data.length(); i++) {
                if (i >= temp) {
                    commentDetails = new CommentDetails();
                    JSONObject object = data.getJSONObject(i);
                    String comment = object.getString("text");
                    String commentFromUser = object.getJSONObject("from").getString("username");
                    commentDetails.setCommentedBy(commentFromUser);
                    commentDetails.setComment(comment);
                    publishProgress(commentDetails);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            mHttpUrlConnection.disconnect();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(CommentDetails... values) {
        super.onProgressUpdate(values);
        TextView textViewComment = new TextView(mContext);
        textViewComment.append(values[0].getCommentedBy() + ":-" + values[0].getComment());
        mChildViewGroup.addView(textViewComment,0);

    }
}
