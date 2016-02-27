package instagram.robosoft.com.mytestapplication;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import instagram.robosoft.com.mytestapplication.asynctask.GetAccessToken;
import instagram.robosoft.com.mytestapplication.constant.AppData;

/**
 * Created by deena on 24/2/16.
 */
public class WebViewClient extends android.webkit.WebViewClient {
    private Context mContext;

    public WebViewClient(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith(AppData.CALLBACKURL)) {
            String part[] = url.split("=");
            new GetAccessToken(part[1].trim(), mContext).execute(AppData.tokenURLString);
            return true;
        }
        return false;
    }
}
