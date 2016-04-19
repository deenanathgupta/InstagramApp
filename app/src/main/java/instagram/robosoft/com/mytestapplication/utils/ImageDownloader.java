package instagram.robosoft.com.mytestapplication.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import instagram.robosoft.com.mytestapplication.communicator.ImageAsyncCallBack;

/**
 * Created by deena on 29/2/16.
 */
public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
    private Bitmap mImageBitMap = null;
    private InputStream mInputStream = null;
    private ImageView mImageView;
    private String Url;
    private ImageAsyncCallBack mImageAsyncCallBack;


    public ImageDownloader(ImageView imageView,ImageAsyncCallBack context) {
        this.mImageView = imageView;
        mImageAsyncCallBack= (ImageAsyncCallBack) context;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        //Log.i("Test", "ImageDownloader Inside doInBackground");
        Url = params[0];
        try {
            mInputStream = (InputStream) new URL(Url).getContent();
            mImageBitMap = BitmapFactory.decodeStream(mInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (mInputStream != null) {
                try {
                    mInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return mImageBitMap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        mImageView.setImageBitmap(bitmap);
        mImageAsyncCallBack.processFinish(bitmap, Url);
    }
}

