package applications.kantedal.hejablvitt.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by filles-dator on 2015-06-20.
 */

@ParseClassName("Comments")
public class DiscussionComment extends ParseObject implements Serializable{

    private int liked = 0;

    public DiscussionComment(){}

    public DiscussionComment(String commentText, String newsId){
        put("comment", commentText);
        put("likes", 0);
        put("publishedDate", new Date());
        put("newsId", newsId);
        put("userId", ParseUser.getCurrentUser().getObjectId());
    }

    public String getDiscussionComment(){
        return getString("comment");
    }

    public int getLikes(){
        return getInt("likes");
    }

    public String getUserId(){
        return getString("userId");
    }

    public Date getPublishedDate(){
        return getDate("publishedDate");
    }

    public Date getCreatedDate() { return getCreatedAt(); }

    public int isLiked(){
        return liked;
    }

    public void setLiked(int like){
        liked = like;
    }

    public void addLike(int like){
        int likes = getInt("likes");
        put("likes", likes+like);
    }


    public static ParseQuery<Comment> getQuery(){
        return ParseQuery.getQuery(Comment.class);
    }
}
