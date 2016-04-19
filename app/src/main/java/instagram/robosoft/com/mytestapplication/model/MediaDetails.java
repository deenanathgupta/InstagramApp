package instagram.robosoft.com.mytestapplication.model;

/**
 * Created by deena on 26/2/16.
 */
public class MediaDetails {
    private String mediaUrl;
    private String postDescription;
    private String mediaId;
    private String totalLike;
    private String totlaNoOfComment;
    private String userProfilePic;
    private String userName;

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
}
