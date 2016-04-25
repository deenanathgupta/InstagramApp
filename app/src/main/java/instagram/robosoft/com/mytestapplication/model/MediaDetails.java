package instagram.robosoft.com.mytestapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by deena on 26/2/16.
 */
public class MediaDetails implements Parcelable {
    private String mediaUrl;
    private String postDescription;
    private String mediaId;
    private String totalLike;
    private String totlaNoOfComment;
    private String userProfilePic;
    private String userName;
    private String createdTime;
    private ArrayList<CommentDetails> commentDetailsArrayList;
    private String dateDiff;

    public MediaDetails(Parcel in) {
        mediaUrl = in.readString();
        postDescription = in.readString();
        mediaId = in.readString();
        totalLike = in.readString();
        totlaNoOfComment = in.readString();
        userProfilePic = in.readString();
        userName = in.readString();
        createdTime = in.readString();
        dateDiff = in.readString();
    }

    public MediaDetails() {
    }

    public static final Creator<MediaDetails> CREATOR = new Creator<MediaDetails>() {
        @Override
        public MediaDetails createFromParcel(Parcel in) {
            return new MediaDetails(in);
        }

        @Override
        public MediaDetails[] newArray(int size) {
            return new MediaDetails[size];
        }
    };

    public String getDateDiff() {
        return dateDiff;
    }

    public void setDateDiff(String dateDiff) {
        this.dateDiff = dateDiff;
    }

    public ArrayList<CommentDetails> getCommentDetailsArrayList() {
        return commentDetailsArrayList;
    }

    public void setCommentDetailsArrayList(ArrayList<CommentDetails> commentDetailsArrayList) {
        this.commentDetailsArrayList = commentDetailsArrayList;
    }

    public String getCraetedTime() {
        return createdTime;
    }

    public void setCraetedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTotlaNoOfComment() {
        return totlaNoOfComment;
    }

    public void setTotlaNoOfComment(String totlaNoOfComment) {
        this.totlaNoOfComment = totlaNoOfComment;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(String totalLike) {
        this.totalLike = totalLike;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mediaUrl);
        dest.writeString(postDescription);
        dest.writeString(mediaId);
        dest.writeString(totalLike);
        dest.writeString(totlaNoOfComment);
        dest.writeString(userProfilePic);
        dest.writeString(userName);
        dest.writeString(createdTime);
        dest.writeString(dateDiff);
    }
}
