package instagram.robosoft.com.mytestapplication.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import instagram.robosoft.com.mytestapplication.communicator.CommentDetailsCallBack;
import instagram.robosoft.com.mytestapplication.constant.AppData;
import instagram.robosoft.com.mytestapplication.model.CommentDetails;
import instagram.robosoft.com.mytestapplication.model.MediaDetails;
import instagram.robosoft.com.mytestapplication.utils.Util;

/**
 * Created by deena on 26/2/16.
 */
public class GetComment extends AsyncTask<Void, Void, Map> {
    private List<MediaDetails> mMediaDetailses;
    private HttpURLConnection httpURLConnection = null;
    private Map<String,List<CommentDetails>> listMap=null;
    private Map<String,MediaDetails> mediaDetailsMap=null;
    //private List<CommentDetails> commentDetailsList;
    private Context mContext = null;
    private CommentDetailsCallBack mCommentDetailsCallBack;

    public GetComment(Map<String,MediaDetails> mMediaDetailses, Context mContext) {
        this.mediaDetailsMap = mMediaDetailses;
        //commentDetailsList = new ArrayList<>();
        listMap=new HashMap<>();
        this.mContext = mContext;
        mCommentDetailsCallBack = (CommentDetailsCallBack) mContext;
    }

    @Override
    protected Map doInBackground(Void... params) {
        URL url = null;String mediaId=null;
        try {
            Iterator iterator = mediaDetailsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                mediaId = (String) entry.getKey();
                List<CommentDetails> commentDetailsList1=new ArrayList<>();
                String recentComment = AppData.APIURL + "/media/" +mediaId + "/comments?access_token=" + AppData.accesstokn;
                url = new URL(recentComment);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                String respose = String.valueOf(Util.covertInputStreamToString(httpURLConnection.getInputStream()));
                //Log.i("CommentList", respose);
                JSONObject jsonObject = (JSONObject) new JSONTokener(respose).nextValue();
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    CommentDetails commentDetail = new CommentDetails();
                    JSONObject object = data.getJSONObject(i);
                    String comment = object.getString("text");
                    String commentFromUser = object.getJSONObject("from").getString("username");
                    commentDetail.setComment(comment);
                    commentDetail.setCommentedBy(commentFromUser);
                    Log.i("Comment:", comment);
                    Log.i("CommentFrom", commentFromUser);
                    //commentDetailsList.add(commentDetail);
                    commentDetailsList1.add(commentDetail);
                }
                listMap.put(mediaId,commentDetailsList1);
                Log.i("CommentFrom", "----------------------------------");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }

        return listMap;
    }

    @Override
    protected void onPostExecute(Map listMap) {
        mCommentDetailsCallBack.commentDetails(listMap);
    }
}
