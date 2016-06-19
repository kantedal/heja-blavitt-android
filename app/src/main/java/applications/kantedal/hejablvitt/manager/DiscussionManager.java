package applications.kantedal.hejablvitt.manager;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import applications.kantedal.hejablvitt.models.Comment;
import applications.kantedal.hejablvitt.models.DiscussionEntry;

/**
 * Created by filles-dator on 2015-07-16.
 */
public class DiscussionManager {

    private List<DiscussionListener> sessionListener = new ArrayList<DiscussionListener>();

    public final static int DISCUSSION_ADDED = 1;
    public final static int DISCUSSION_UPDATED = 2;
    public final static int DISCUSSION_QUERY_FAILED = 3;
    public final static int DISCUSSION_IS_UPDATING = 4;
    public final static int DISCUSSION_ITEM_UPDATED = 5;

    public final static int SORT_PUBLISHED_DATE = 0;
    public final static int SORT_VIEWS = 1;
    public final static int SORT_LIKES_DESC = 2;
    public final static int SORT_LIKES_ASC = 3;

    private List<DiscussionEntry> DiscussionDataSet;
    private List<DiscussionEntry> MyDiscussionDataSet;

    public DiscussionManager(){
        DiscussionDataSet = new ArrayList<>();
        MyDiscussionDataSet = new ArrayList<>();
    }

    public void addDiscussionEntry(String discussionText){
        notifyDiscussionListener(DISCUSSION_IS_UPDATING);
        DiscussionEntry newComment = new DiscussionEntry(discussionText);
        newComment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    queryDiscussion(SORT_PUBLISHED_DATE, false);
                }else{
                    notifyDiscussionItem(DISCUSSION_QUERY_FAILED);
                }
            }
        });
    }

    public DiscussionEntry findDiscussion(String objectId){
        for(int i=0; i<DiscussionDataSet.size(); i++){
            if(DiscussionDataSet.get(i).getObjectId().equals(objectId))
                return DiscussionDataSet.get(i);
        }
        return null;
    }

    public void notifyDiscussionItem(int index){
        notifyDiscussionItemListener(index, DISCUSSION_ITEM_UPDATED);
    }

    public int getIndex(DiscussionEntry Discussion){
        for(int i=0; i<DiscussionDataSet.size(); i++)
            if(DiscussionDataSet.get(i).getObjectId().equals(Discussion.getObjectId()))
                return i;
        return -1;
    }

    public DiscussionEntry getDiscussionById(String objectId){
        if(objectId != null) {
            for (DiscussionEntry discussion : DiscussionDataSet) {
                if (objectId.equals(discussion.getObjectId()))
                    return discussion;
            }
        }
        return null;
    }

    public boolean addLike(int like, DiscussionEntry discussionEntry){
        int index = 0;
        for(int i=0; i<DiscussionDataSet.size(); i++)
            if(DiscussionDataSet.get(i).getObjectId().equals(discussionEntry.getObjectId()))
                index = i;

        if(discussionEntry.isLiked()==0) {
            int likesToAdd = like;
            if(discussionEntry.isLiked() == 1)
                likesToAdd = -2;
            else if(discussionEntry.isLiked() == -1)
                likesToAdd = 2;

            discussionEntry.addLike(likesToAdd);
            discussionEntry.setLiked(like);
            discussionEntry.saveInBackground();

            ArrayList<ArrayList> userLikedDiscussionList = (ArrayList) ParseUser.getCurrentUser().get("likedDiscussionEntries");
            ArrayList userLikedDiscussion = new ArrayList();
            userLikedDiscussion.add(discussionEntry.getObjectId());
            userLikedDiscussion.add(like);
            userLikedDiscussionList.add(userLikedDiscussion);
            ParseUser.getCurrentUser().put("likedDiscussionEntries", userLikedDiscussionList);
            ParseUser.getCurrentUser().saveInBackground();

            return true;
        }
        return false;
    }

    public void setLikedDiscussionEntry() {
        ArrayList<ArrayList> likedDiscussion = (ArrayList) ParseUser.getCurrentUser().get("likedDiscussionEntries");

        for (DiscussionEntry DiscussionTemp : DiscussionDataSet) {
            for (ArrayList liked : likedDiscussion) {
                if (DiscussionTemp.getObjectId().equals(liked.get(0))) {
                    DiscussionTemp.setLiked((Integer) liked.get(1));
                }
            }
        }
    }

    public void queryDiscussion(int sortOption, final boolean myDiscussion){
        notifyDiscussionListener(DISCUSSION_IS_UPDATING);

        if(!myDiscussion)
            DiscussionDataSet.clear();
        else
            MyDiscussionDataSet.clear();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -7);

        ParseQuery<DiscussionEntry> query = ParseQuery.getQuery("Discussion");

        switch (sortOption) {
            case SORT_PUBLISHED_DATE: query.orderByDescending("createdAt");
                break;
            case SORT_VIEWS: query.orderByDescending("views");
                break;
            case SORT_LIKES_DESC: query.orderByDescending("likes");
                break;
            case SORT_LIKES_ASC: query.orderByAscending("likes");
                break;
        }

        if(myDiscussion)
            query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());

        query.findInBackground(new FindCallback<DiscussionEntry>() {
            @Override
            public void done(List<DiscussionEntry> discussionEntries, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < discussionEntries.size(); i++) {
                        if(!myDiscussion)
                            DiscussionDataSet.add(discussionEntries.get(i));
                        else
                            MyDiscussionDataSet.add(discussionEntries.get(i));
                    }

                    setLikedDiscussionEntry();

                    notifyDiscussionListener(DISCUSSION_UPDATED);
                } else {
                    notifyDiscussionListener(DISCUSSION_QUERY_FAILED);
                }
            }
        });
    }

    public List<DiscussionEntry> getDiscussionDataSet(){ return DiscussionDataSet; }

    public List<DiscussionEntry> getMyDiscussionDataSet() { return MyDiscussionDataSet; }

    private void notifyDiscussionListener(int event) {
        for(DiscussionListener listener : sessionListener){
            if(listener != null){
                listener.OnDiscussionEvent(event);
            }
        }
    }

    private void notifyDiscussionItemListener(int index, int event) {
        for(DiscussionListener listener : sessionListener){
            if(listener != null){
                listener.OnDiscussionItemEvent(index, event);
            }
        }
    }


    public void subscribeDiscussionListener(DiscussionListener listener){
        if(listener != null){
            sessionListener.add(listener);
        }
    }

    public void unsubscribeDiscussionListener(DiscussionListener listener){
        if(listener != null){
            sessionListener.remove(listener);
        }
    }

    public interface DiscussionListener
    {
        public void OnDiscussionEvent(int event);
        public void OnDiscussionItemEvent(int index, int event);
    }
}
