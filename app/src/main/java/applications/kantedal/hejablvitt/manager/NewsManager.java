package applications.kantedal.hejablvitt.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import applications.kantedal.hejablvitt.models.News;
import applications.kantedal.hejablvitt.models.NewsSource;

/**
 * Created by filles-dator on 2015-07-16.
 */
public class NewsManager {

    private List<NewsListener> sessionListener = new ArrayList<NewsListener>();

    public final static int NEWS_ADDED = 1;
    public final static int NEWS_UPDATED = 2;
    public final static int NEWS_QUERY_FAILED = 3;
    public final static int NEWS_IS_UPDATING = 4;
    public final static int NEWS_ITEM_UPDATED = 5;
    public final static int NEWS_SOURCES_UPDATED = 6;

    public final static int SORT_PUBLISHED_DATE = 0;
    public final static int SORT_VIEWS = 1;
    public final static int SORT_LIKES_DESC = 2;
    public final static int SORT_LIKES_ASC = 3;

    private boolean mIsUpdating = false;

    private List<News> mNewsDataSet;
    private List<NewsSource> mNewsSources;

    private Context mContext;

    public NewsManager(Context context){
        mNewsDataSet = new ArrayList<>();
        mNewsSources = new ArrayList<>();
        mContext = context;
    }

    public News findNews(String objectId){
        for(int i=0; i< mNewsDataSet.size(); i++){
            if(mNewsDataSet.get(i).getObjectId().equals(objectId))
                return mNewsDataSet.get(i);
        }
        return null;
    }

    public void notifyNewsItem(int index){
        notifyNewsItemListener(index, NEWS_ITEM_UPDATED);
    }

    public int getIndex(News news){
        for(int i=0; i< mNewsDataSet.size(); i++)
            if(mNewsDataSet.get(i).getObjectId().equals(news.getObjectId()))
                return i;
        return -1;
    }

    public boolean addLike(int like, News news){
        int index = 0;
        for(int i=0; i< mNewsDataSet.size(); i++)
            if(mNewsDataSet.get(i).getObjectId().equals(news.getObjectId()))
                index = i;

        if(news.isLiked()==0) {
            int likesToAdd = like;
            if(news.isLiked() == 1)
                likesToAdd = -2;
            else if(news.isLiked() == -1)
                likesToAdd = 2;

            news.addLike(likesToAdd);
            news.setLiked(like);
            news.saveInBackground();

            ArrayList<ArrayList> userLikedNewsList = (ArrayList) ParseUser.getCurrentUser().get("likedNews");
            ArrayList userLikedNews = new ArrayList();
            userLikedNews.add(news.getObjectId());
            userLikedNews.add(like);
            userLikedNewsList.add(userLikedNews);
            ParseUser.getCurrentUser().put("likedNews", userLikedNewsList);
            ParseUser.getCurrentUser().saveInBackground();

            return true;
        }
        return false;
    }

    public void setLikedNews() {
        ArrayList<ArrayList> likedNews = (ArrayList) ParseUser.getCurrentUser().get("likedNews");

        for (News newsTemp : mNewsDataSet) {
            for (ArrayList liked : likedNews) {
                if (newsTemp.getObjectId().equals(liked.get(0))) {
                    newsTemp.setLiked((Integer) liked.get(1));
                }
            }
        }
    }

    public boolean isUpdating(){
        return mIsUpdating;
    }

    //Variable for new that the current user has voted
    public boolean mQueryVotedNews = false;

    public void queryNews(int sortOption){
        notifyNewsListener(NEWS_IS_UPDATING);
        mIsUpdating = true;

        if(mNewsSources.size() == 0){
            queryNewsSources(true);
            return;
        }

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -7);

        ParseQuery<News> query = ParseQuery.getQuery("News");
        query.whereGreaterThan("publishedDate", c.getTime());

        switch (sortOption) {
            case SORT_PUBLISHED_DATE: query.orderByDescending("publishedDate");
                break;
            case SORT_VIEWS: query.orderByDescending("views");
                break;
            case SORT_LIKES_DESC: query.orderByDescending("likes");
                break;
            case SORT_LIKES_ASC: query.orderByAscending("likes");
                break;
        }

        if(mQueryVotedNews){
            ArrayList<String> votedNews = new ArrayList<>();
            ArrayList<ArrayList> likedNews = (ArrayList) ParseUser.getCurrentUser().get("likedNews");
            for (ArrayList voted : likedNews)
                votedNews.add((String) voted.get(0));

            query.whereContainedIn("objectId", votedNews);
        }else{
            ArrayList<String> newsSources = new ArrayList<>();
            for(NewsSource newsSource : mNewsSources) {
                if(!newsSource.isSelected())
                    newsSources.add(newsSource.getName());
            }

            query.whereNotContainedIn("source", newsSources);
        }

        if(ParseUser.getCurrentUser() != null) {
            query.findInBackground(new FindCallback<News>() {
                @Override
                public void done(List<News> news, ParseException e) {
                    if (e == null) {
                        mIsUpdating = false;
                        mNewsDataSet.clear();
                        for (int i = 0; i < news.size(); i++)
                            mNewsDataSet.add(news.get(i));

                        setLikedNews();

                        notifyNewsListener(NEWS_UPDATED);
                    } else {
                        notifyNewsListener(NEWS_QUERY_FAILED);
                    }
                }
            });
        }
    }

    public void queryNewsSources(final boolean shouldQueryNews){
        notifyNewsListener(NEWS_IS_UPDATING);
        final ParseQuery<NewsSource> query = ParseQuery.getQuery("NewsSources");
        query.findInBackground(new FindCallback<NewsSource>() {
            @Override
            public void done(List<NewsSource> newsSources, ParseException e) {
                if (e == null) {
                    mIsUpdating = false;
                    mNewsSources = newsSources;
                    notifyNewsListener(NEWS_SOURCES_UPDATED);
                    if(shouldQueryNews)
                        queryNews(SORT_PUBLISHED_DATE);

                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                    for(NewsSource newsSource : mNewsSources)
                        newsSource.setSelected(sharedPrefs.getBoolean(newsSource.getName(), true));

                } else {
                    notifyNewsItem(NEWS_QUERY_FAILED);
                }
            }
        });
    }

    public void clearNewsDataSet(){ mNewsDataSet.clear(); }

    public List<News> getNewsDataSet(){ return mNewsDataSet; }
    public List<NewsSource> getNewsSources(){ return mNewsSources; }

    public News getNewsById(String objectId){
        for(News news : mNewsDataSet) {
            if (objectId.equals(news.getObjectId()))
                return news;
        }

        return null;
    }

    private void notifyNewsListener(int event) {
        for(NewsListener listener : sessionListener){
            if(listener != null){
                listener.OnNewsEvent(event);
            }
        }
    }

    private void notifyNewsItemListener(int index, int event) {
        for(NewsListener listener : sessionListener){
            if(listener != null){
                listener.OnNewsItemEvent(index, event);
            }
        }
    }


    public void subscribeNewsListener(NewsListener listener){
        if(listener != null){
            sessionListener.add(listener);
        }
    }

    public void unsubscribeNewsListener(NewsListener listener){
        if(listener != null){
            sessionListener.remove(listener);
        }
    }


    public interface NewsListener
    {
        public void OnNewsEvent(int event);
        public void OnNewsItemEvent(int index, int event);
    }
}
