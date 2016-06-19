package applications.kantedal.hejablvitt.manager;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import applications.kantedal.hejablvitt.models.Comment;
import applications.kantedal.hejablvitt.models.DiscussionEntry;
import applications.kantedal.hejablvitt.models.LeagueRound;

/**
 * Created by filles-dator on 2015-07-16.
 */
public class StatisticsManager {

    private List<StatisticsListener> sessionListener = new ArrayList<StatisticsListener>();

    public final static int STATISTICS_UPDATED = 1;
    public final static int STATISTICS_QUERY_FAILED = 2;
    public final static int STATISTICS_IS_UPDATING = 3;


    private List<CrowdItem> mCrowdLeague;
    private List<AssistItem> mAssistLeague;
    private List<ScoreItem> mScoringLeague;

    public StatisticsManager(){
        mCrowdLeague = new ArrayList<>();
        mAssistLeague = new ArrayList<>();
        mScoringLeague = new ArrayList<>();
    }

    public List getCrowdLeague(){
        return mCrowdLeague;
    }

    public List getAssistLeague(){
        return mAssistLeague;
    }

    public List getScoringLeague(){
        return mScoringLeague;
    }


    public void queryStatistics(){
        notifyLeagueListener(STATISTICS_IS_UPDATING);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Statistics");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> statistics, ParseException e) {
                if (e == null) {
                    mCrowdLeague.clear();
                    mScoringLeague.clear();
                    mAssistLeague.clear();

                    for(ParseObject statisticsObject : statistics){
                        final String type = (String) statisticsObject.get("type");
                        switch (type) {
                            case "crowdLeague":
                                ArrayList<ArrayList> crowdArray = (ArrayList) statisticsObject.get("data");
                                for(ArrayList crowdObject : crowdArray){
                                    CrowdItem crowdItem = new CrowdItem();
                                    crowdItem.crowdCount = (int) crowdObject.get(2);
                                    crowdItem.team = (String) crowdObject.get(0);

                                    mCrowdLeague.add(crowdItem);
                                }
                                break;
                            case "assistLeague":
                                ArrayList<ArrayList> assistArray = (ArrayList) statisticsObject.get("data");
                                for(ArrayList assistObject : assistArray){
                                    AssistItem assistItem = new AssistItem();
                                    assistItem.name = (String) assistObject.get(1);
                                    assistItem.team = (String) assistObject.get(2);
                                    assistItem.assists = (int) assistObject.get(3);

                                    mAssistLeague.add(assistItem);
                                }
                                break;
                            case "scoringLeague":
                                ArrayList<ArrayList> scoreArray = (ArrayList) statisticsObject.get("data");
                                for(ArrayList scoreObject : scoreArray){
                                    ScoreItem scoreItem = new ScoreItem();
                                    scoreItem.name = (String) scoreObject.get(1);
                                    scoreItem.team = (String) scoreObject.get(2);
                                    scoreItem.goals = (int) scoreObject.get(3);

                                    mScoringLeague.add(scoreItem);
                                }
                                break;
                        }
                    }

                    notifyLeagueListener(STATISTICS_UPDATED);
                } else {
                    notifyLeagueListener(STATISTICS_QUERY_FAILED);
                }
            }
        });
    }

    private void notifyLeagueListener(int event) {
        for(StatisticsListener listener : sessionListener){
            if(listener != null){
                listener.OnStatisticsEvent(event);
            }
        }
    }

    public void subscribeStatisticsListener(StatisticsListener listener){
        if(listener != null){
            sessionListener.add(listener);
        }
    }

    public void unsubscribeStatisticsListener(StatisticsListener listener){
        if(listener != null){
            sessionListener.remove(listener);
        }
    }

    public interface StatisticsListener
    {
        void OnStatisticsEvent(int event);
    }

    public class CrowdItem {
        public String team;
        public int crowdCount;
    }

    public class AssistItem {
        public String name;
        public String team;
        public int assists;
    }

    public class ScoreItem {
        public String name;
        public String team;
        public int goals;
    }
}
