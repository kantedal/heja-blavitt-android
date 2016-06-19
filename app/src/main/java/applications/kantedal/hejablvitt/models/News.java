package applications.kantedal.hejablvitt.models;

        import android.util.Log;

        import com.parse.ParseClassName;
        import com.parse.ParseObject;
        import com.parse.ParseQuery;

        import java.util.Date;

/**
 * Created by filles-dator on 2015-06-20.
 */

@ParseClassName("News")
public class News extends ParseObject {

    private int liked = 0;

    public String getTitle(){
        return getString("title");
    }

    public String getContentSnippet(){
        return getString("contentSnippet").trim().replace("&quot;", "\"");
    }

    public String getUrl(){
        return getString("link");
    }

    public Date getPublishedDate(){
        return getDate("publishedDate");
    }

    public int getLikes() {
        return (get("likes") == null ? getInt("likes") : 0);
    }

    public int getViews() {
        try{
            return getInt("views");
        }catch(NullPointerException e){
            return 0;
        }
    }

    public String getSource() { return getString("source"); }

    public void addView(){
        put("views", getViews()+1);
    }

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

    public static ParseQuery<News> getQuery(){
        return ParseQuery.getQuery(News.class);
    }
}
