package instagram.robosoft.com.mytestapplication.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import instagram.robosoft.com.mytestapplication.constant.AppData;

/**
 * Created by deena on 29/2/16.
 */
public class PostCommentAsyncTask extends AsyncTask<String, Void, Void> {
    HttpURLConnection mHttpURLConnection = null;
    private String mCommentText;

    public PostCommentAsyncTask(String mCommentText) {
        this.mCommentText = mCommentText;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            Log.i("URL", "CommentUrl:" + url);
            mHttpURLConnection = (HttpURLConnection) url.openConnection();
            mHttpURLConnection.setRequestMethod("POST");
            mHttpURLConnection.setDoOutput(true);
            mHttpURLConnection.connect();

            OutputStream os = mHttpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bufferedWriter.write("&access_token=" + AppData.accesstokn + "&text=" + mCommentText);
            bufferedWriter.flush();
            bufferedWriter.close();
            os.close();
            int responseCode = mHttpURLConnection.getResponseCode();
            if (responseCode == mHttpURLConnection.HTTP_OK) {
                Log.i("Success", "CommentPosted" + responseCode);
            } else {
                Log.i("Failed", "CommentFailed" + responseCode);
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (mHttpURLConnection != null)
                mHttpURLConnection.disconnect();
        }
        return null;
    }
}
