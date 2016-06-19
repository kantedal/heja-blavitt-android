package applications.kantedal.hejablvitt.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.Date;

/**
 * Created by filles-dator on 2015-06-20.
 */

@ParseClassName("Comments")
public class Comment extends ParseObject{

    private int liked = 0;

    public Comment(){}

    public Comment(String commentText, String parentId, boolean isNews){
        put("comment", commentText);
        put("likes", 0);
        put("publishedDate", new Date());

        if(isNews)
            put("newsId", parentId);
        else
            put("discussionId", parentId);

        put("userId", ParseUser.getCurrentUser().getObjectId());
        put("username", ParseUser.getCurrentUser().getUsername());
    }

    public String getComment(){
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

    public String getUsername() { return getString("username"); }

    public String getNewsId() { return getString("newsId"); }

    public String getDiscussionId() {
        return getString("discussionId"); }

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
