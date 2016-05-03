package instagram.robosoft.com.mytestapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import instagram.robosoft.com.mytestapplication.MainActivity;
import instagram.robosoft.com.mytestapplication.R;
import instagram.robosoft.com.mytestapplication.asynctask.PostCommentAsyncTask;
import instagram.robosoft.com.mytestapplication.asynctask.RequestForCommentAsyncTask;
import instagram.robosoft.com.mytestapplication.communicator.ImageAsyncCallBack;
import instagram.robosoft.com.mytestapplication.constant.AppData;
import instagram.robosoft.com.mytestapplication.model.CommentDetails;
import instagram.robosoft.com.mytestapplication.model.MediaDetails;
import instagram.robosoft.com.mytestapplication.utils.ImageDownloader;
import instagram.robosoft.com.mytestapplication.utils.Util;

/**
 * Created by deena on 24/2/16.
 */
public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder> implements ImageAsyncCallBack {
    private LruCache<String, Bitmap> mLruCache;
    private Context mContext;
    private String mUserName = null;
    private int mCommentCount;
    private View mView;
    private ViewHolder mViewHolder;
    HttpURLConnection httpURLConnection;
    private ArrayList<MediaDetails> mediaDetailseslist;

    public RecyclerviewAdapter(ArrayList<MediaDetails> mMediaDetailsMap, Context mContext, String Username, int commentCount) {
        this.mediaDetailseslist = mMediaDetailsMap;
        this.mUserName = Username;
        this.mContext = mContext;
        this.mCommentCount = commentCount;

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<>(cacheSize);
    }


    @Override
    public RecyclerviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerviewAdapter.ViewHolder viewHolder;
        view = LayoutInflater.from(mContext).inflate(R.layout.singlerow, parent, false);
        viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mViewHolder = holder;
        final MediaDetails mediaDetails = mediaDetailseslist.get(position);
        holder.txtTotalComment.setText(mediaDetails.getTotlaNoOfComment());
        holder.txtTotalLike.setText(mediaDetails.getTotalLike());
        holder.txtDescrption.setText(mediaDetails.getPostDescription());

        Picasso.with(mContext).load(mediaDetails.getUserProfilePic()).into(holder.profilePic);
        holder.txtUserName.setText(mediaDetails.getUserName());
        //holder.txtPostDate.setText(mediaDetails.getCraetedTime().substring(0, mediaDetails.getCraetedTime().indexOf(",")));
        holder.txtPostDate.setText(mediaDetails.getDateDiff() + "");

        

        holder.likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.likeImageView.setImageResource(R.drawable.likefilled);
                String url = "  https://api.instagram.com/v1/media/" + mediaDetails.getMediaId() + "/likes";
                new PostCommentAsyncTask("", false).execute(url);
                int count = Integer.parseInt(mediaDetails.getTotalLike()) + 1;
                holder.txtTotalLike.setText(String.valueOf(count));
            }
        });

        commentOnFeed(holder, mediaDetails, position);
        setImageOfFeed(holder, mediaDetails);


        holder.viewGroup.removeAllViews();
        //new RequestForCommentAsyncTask(holder.viewGroup, mContext, mCommentCount).execute(mediaDetails.getMediaId());
        ArrayList<CommentDetails> arrayListComment = mediaDetailseslist.get(position).getCommentDetailsArrayList();
        for (CommentDetails commentDetails : arrayListComment) {
            TextView textViewComment = new TextView(mContext);
            textViewComment.append(mUserName + ":-" + commentDetails.getComment());
            holder.viewGroup.addView(textViewComment);
        }

    }

    private void setImageOfFeed(ViewHolder holder, MediaDetails mediaDetails) {
        Bitmap bMapScaled;
        Bitmap bitmap = mLruCache.get(mediaDetails.getMediaUrl());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        if (mLruCache.get(mediaDetails.getMediaId()) != null) {
            int currentOrientation = mContext.getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                bMapScaled = Bitmap.createScaledBitmap(bitmap, screenWidth, screenHeight, true);
            } else {
                bMapScaled = Bitmap.createScaledBitmap(bitmap, screenWidth, 400, true);
            }
            holder.postImage.setImageBitmap(bMapScaled);
        } else {
            new ImageDownloader(holder.postImage, this, mContext).execute(mediaDetails.getMediaUrl());
        }
    }


    private void commentOnFeed(final ViewHolder holder, final MediaDetails mediaDetails, final int position) {
        holder.sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comt = holder.editText.getText().toString();
                if (comt.trim().length() != 0 && Util.isNwConnected(mContext)) {
                    TextView textViewComment = new TextView(mContext);
                    textViewComment.append(mUserName + ":-" + comt);
                    holder.viewGroup.addView(textViewComment, 0);
                    new PostCommentAsyncTask(comt, true).execute(AppData.APIURL + "/media/" + mediaDetailseslist.get(position).getMediaId() + "/comments");
                    holder.editText.setText("");
                    int count = Integer.parseInt(mediaDetails.getTotlaNoOfComment()) + 1;
                    holder.txtTotalComment.setText(count + "");
                    ArrayList<CommentDetails> tempArrayList = new ArrayList<>();
                    CommentDetails commentDetails = new CommentDetails();
                    commentDetails.setCommentedBy(mediaDetails.getUserName());
                    commentDetails.setComment(comt);
                    mediaDetailseslist.get(position).getCommentDetailsArrayList().add(commentDetails);
                    mediaDetails.setTotlaNoOfComment(count + "");
                    notifyItemChanged(position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mediaDetailseslist.size();
    }

    @Override
    public void processFinish(Bitmap bitmap, String Url) {
        if (bitmap != null) {
            mLruCache.put(Url, bitmap);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView postImage;
        private TextView txtDescrption;
        private ViewGroup viewGroup;
        private EditText editText;
        private ImageView sendImage;
        private TextView txtTotalComment, txtTotalLike;
        private ImageView profilePic;
        private TextView txtPostDate;
        private TextView txtUserName;
        private TextView txtLike;
        private ImageView likeImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            postImage = (ImageView) itemView.findViewById(R.id.imgpost);
            txtDescrption = (TextView) itemView.findViewById(R.id.txtdescription);
            viewGroup = (ViewGroup) itemView.findViewById(R.id.container);
            editText = (EditText) itemView.findViewById(R.id.editText);
            sendImage = (ImageView) itemView.findViewById(R.id.imageView);
            txtTotalLike = (TextView) itemView.findViewById(R.id.txttotalnooflike);
            txtTotalComment = (TextView) itemView.findViewById(R.id.txttotalnoofcomment);
            profilePic = (ImageView) itemView.findViewById(R.id.userimage);
            txtUserName = (TextView) itemView.findViewById(R.id.txtusername);
            txtPostDate = (TextView) itemView.findViewById(R.id.postdate);
            likeImageView= (ImageView) itemView.findViewById(R.id.likeIcon);
            //txtLike = (TextView) itemView.findViewById(R.id.like);
        }
    }


}
