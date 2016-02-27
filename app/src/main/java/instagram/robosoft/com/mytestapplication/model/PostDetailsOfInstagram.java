package instagram.robosoft.com.mytestapplication.model;

import java.util.List;
import java.util.Map;

/**
 * Created by deena on 26/2/16.
 */
public class PostDetailsOfInstagram {
    private String mediaUrl;
    private String postDescription;
    public String totlaNoOfComment;
    public String totalLike;
    private String mediaId;
    private List<CommentDetails> commentDetailsList;

    public List<CommentDetails> getCommentDetailsList() {
        return commentDetailsList;
    }

    public void setCommentDetailsList(List<CommentDetails> commentDetailsList) {
        this.commentDetailsList = commentDetailsList;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getTotlaNoOfComment() {
        return totlaNoOfComment;
    }

    public void setTotlaNoOfComment(String totlaNoOfComment) {
        this.totlaNoOfComment = totlaNoOfComment;
    }

    public String getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(String totalLike) {
        this.totalLike = totalLike;
    }
}
