package instagram.robosoft.com.mytestapplication.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by deena on 25/2/16.
 */
public class Util {
    public static StringBuffer covertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        // Log.i("Test", "covertInputStreamToString()");
        StringBuffer data = new StringBuffer();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                data.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
        return data;
    }

    public static boolean isNwConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
        if (nwInfo != null && nwInfo.isAvailable()) {
            Log.i("Deena", "Inside Utiltity");
            return true;
        }
        return false;
    }

}
