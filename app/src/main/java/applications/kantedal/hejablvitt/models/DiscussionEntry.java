package applications.kantedal.hejablvitt.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.Date;

/**
 * Created by filles-dator on 2015-06-20.
 */

@ParseClassName("Discussion")
public class DiscussionEntry extends ParseObject{

    public DiscussionEntry(String discussionText){
        put("text", discussionText);
        put("likes", 0);
        put("comments", 0);
        put("views", 0);
        put("userId", ParseUser.getCurrentUser().getObjectId());
        put("username", ParseUser.getCurrentUser().getUsername());
    }

    public DiscussionEntry(){}

    private int liked = 0;

    public String getText(){
        return getString("text");
    }

    public int getLikes(){
        return getInt("likes");
    }

    public String getUsername() { return getString("username"); }

    public String getUserId() { return getString("userId"); }

    public JSONArray getComments(){
        return getJSONArray("comments");
    }

    public void addLike(int like){
        int likes = getInt("likes");
        put("likes", likes+like);
    }

    public int isLiked(){
        return liked;
    }

    public void setLiked(int like){
        liked = like;
    }

    public Date getCreatedDate(){
        return getCreatedAt();
    }

    public static ParseQuery<DiscussionEntry> getQuery(){
        return ParseQuery.getQuery(DiscussionEntry.class);
    }
}
