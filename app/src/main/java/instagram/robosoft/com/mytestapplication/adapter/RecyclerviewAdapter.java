package instagram.robosoft.com.mytestapplication.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import instagram.robosoft.com.mytestapplication.MainActivity;
import instagram.robosoft.com.mytestapplication.R;
import instagram.robosoft.com.mytestapplication.asynctask.PostCommentAsyncTask;
import instagram.robosoft.com.mytestapplication.asynctask.RequestForCommentAsyncTask;
import instagram.robosoft.com.mytestapplication.communicator.ImageAsyncCallBack;
import instagram.robosoft.com.mytestapplication.constant.AppData;
import instagram.robosoft.com.mytestapplication.model.MediaDetails;
import instagram.robosoft.com.mytestapplication.utils.ImageDownloader;

/**
 * Created by deena on 24/2/16.
 */
public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder> implements ImageAsyncCallBack {
    private Map<String, MediaDetails> mediaDetailsMap;
    private LruCache<String, Bitmap> mLruCache;
    private Context mContext;
    private String mUserName = null;
    private Iterator mIterator;
    private List<String> mMediaIdList;
    private List<MediaDetails> mMediaDetailsList;
    private int commentCount;

    public RecyclerviewAdapter(Map<String, MediaDetails> mediaDetailsMap, Context mContext, String Username, int commentCount) {
        this.mediaDetailsMap = mediaDetailsMap;
        this.mUserName = Username;
        this.mContext = mContext;
        mMediaIdList = new ArrayList<>();
        this.commentCount = commentCount;
        
        mMediaDetailsList = new ArrayList<>();
        mIterator = mediaDetailsMap.entrySet().iterator();
        while (mIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) mIterator.next();
            mMediaIdList.add((String) entry.getKey());
            mMediaDetailsList.add((MediaDetails) entry.getValue());
        }
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<>(cacheSize);
    }


    @Override
    public RecyclerviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerviewAdapter.ViewHolder viewHolder = null;
        view = LayoutInflater.from(mContext).inflate(R.layout.singlerow, parent, false);
        viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtDescrption.setText(mMediaDetailsList.get(position).getPostDescription());
        Bitmap bitmap = mLruCache.get(mMediaDetailsList.get(position).getMediaUrl());
        if (bitmap != null) {
            holder.postImage.setImageBitmap(bitmap);
        } else {
            new ImageDownloader(holder.postImage, this).execute(mMediaDetailsList.get(position).getMediaUrl());
        }
        holder.viewGroup.removeAllViews();
        new RequestForCommentAsyncTask(holder.viewGroup, mContext, commentCount).execute(mMediaDetailsList.get(position).getMediaId());
    }

    @Override
    public int getItemCount() {
        return mediaDetailsMap.size();
    }

    @Override
    public void processFinish(Bitmap bitmap, String Url) {
        if (bitmap != null)
            mLruCache.put(Url, bitmap);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView postImage;
        private TextView txtDescrption;
        private ViewGroup viewGroup;
        private EditText editText;
        private ImageView sendImage;

        public ViewHolder(View itemView) {
            super(itemView);
            postImage = (ImageView) itemView.findViewById(R.id.imgpost);
            txtDescrption = (TextView) itemView.findViewById(R.id.txtdescription);
            viewGroup = (ViewGroup) itemView.findViewById(R.id.container);
            editText = (EditText) itemView.findViewById(R.id.editText);
            sendImage = (ImageView) itemView.findViewById(R.id.imageView);
            sendImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comt = editText.getText().toString();
                    if (comt.trim().length() != 0) {
                        TextView textViewComment = new TextView(mContext);
                        textViewComment.append(mUserName + ":-" + comt);
                        viewGroup.addView(textViewComment);
                        new PostCommentAsyncTask(comt).execute(AppData.APIURL + "/media/" + mMediaIdList.get(getAdapterPosition()) + "/comments");
                        editText.setText("");
                    }
                }
            });
        }
    }
}
