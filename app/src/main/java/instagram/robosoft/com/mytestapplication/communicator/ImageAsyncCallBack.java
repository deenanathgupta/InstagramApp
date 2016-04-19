package instagram.robosoft.com.mytestapplication.communicator;

import android.graphics.Bitmap;

/**
 * Created by deena on 11/2/16.
 */
public interface ImageAsyncCallBack {
    void processFinish(Bitmap bitmap, String Url);
}
