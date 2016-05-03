package instagram.robosoft.com.mytestapplication.asynctask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import instagram.robosoft.com.mytestapplication.WebViewClient;
import instagram.robosoft.com.mytestapplication.communicator.CallBack;
import instagram.robosoft.com.mytestapplication.constant.AppData;
import instagram.robosoft.com.mytestapplication.utils.Util;

/**
 * Created by deena on 24/2/16.
 */
public class GetAccessToken extends AsyncTask<String, Void, String[]> {
    private String mAccessTokenString;
    private String token = null;
    private String username = null;
    private String mUserDetails[] = new String[3];
    private String mUserprofilePic = null;
    private CallBack mCallBack;
    private SharedPreferences mSharedpreferences;

    public GetAccessToken(String token, Context context) {
        this.token = token.trim();
        mCallBack = (CallBack) context;
        mSharedpreferences = context.getSharedPreferences(AppData.MYPREFERENCE, context.MODE_PRIVATE);

    }

    @Override
    protected String[] doInBackground(String... params) {
        SharedPreferences.Editor editor = mSharedpreferences.edit();
        try {
            URL url = new URL(params[0]);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
            outputStreamWriter.write("client_id=" + AppData.CLIENT_ID +
                    "&client_secret=" + AppData.CLIENT_SECRET +
                    "&grant_type=authorization_code" +
                    "&redirect_uri=" + AppData.CALLBACKURL +
                    "&code=" + token);
            outputStreamWriter.flush();

            String response = String.valueOf(Util.covertInputStreamToString(httpURLConnection.getInputStream()));
            Log.i("test", "Access Token:" + response);
            JSONObject jsonObject = (JSONObject) new JSONTokener(response).nextValue();
            mAccessTokenString = jsonObject.getString("access_token");
            editor.putString(AppData.Name, mAccessTokenString);
            String id = jsonObject.getJSONObject("user").getString("id");
            username = jsonObject.getJSONObject("user").getString("username");
            mUserprofilePic = jsonObject.getJSONObject("user").getString("profile_picture");
            mUserDetails[0] = username;
            mUserDetails[1] = mUserprofilePic;
            mUserDetails[2] = id;

            editor.putString(AppData.accesstoken, mAccessTokenString);
            editor.apply();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {

        }
        return mUserDetails;
    }

    @Override
    protected void onPostExecute(String data[]) {
        mCallBack.getData(data);
    }

}
