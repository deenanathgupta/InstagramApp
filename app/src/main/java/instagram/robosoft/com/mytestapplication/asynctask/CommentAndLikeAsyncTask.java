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
public class CommentAndLikeAsyncTask extends AsyncTask<String, Void, Void> {
    private HttpURLConnection mHttpURLConnection = null;
    private String mCommentText;
    private int flag = 0;

    public CommentAndLikeAsyncTask(String mCommentText, int mFlag) {
        this.mCommentText = mCommentText;
        this.flag = mFlag;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            URL url = new URL(params[0]);
            mHttpURLConnection = (HttpURLConnection) url.openConnection();
            Log.i("URL", "CommentUrl:" + url);
            if (flag != 3) {
                mHttpURLConnection.setRequestMethod("POST");
                mHttpURLConnection.setDoOutput(true);
                mHttpURLConnection.connect();

                OutputStream os = mHttpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                if (flag == 1)
                    bufferedWriter.write("&access_token=" + AppData.accesstokn + "&text=" + mCommentText);
                else if (flag == 2)
                    bufferedWriter.write("&access_token=" + AppData.accesstokn + "&likes");

                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();
            } else {
                mHttpURLConnection.setRequestMethod("DELETE");
                mHttpURLConnection.setDoOutput(true);
                mHttpURLConnection.connect();
                OutputStream os = mHttpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bufferedWriter.write("&access_token=" + AppData.accesstokn);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();
            }
            int responseCode = mHttpURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
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
