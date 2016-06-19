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
import applications.kantedal.hejablvitt.models.LeagueRound;

/**
 * Created by filles-dator on 2015-07-16.
 */
public class LeagueManager {

    private List<LeagueListener> sessionListener = new ArrayList<LeagueListener>();

    public final static int LEAGUE_UPDATED = 1;
    public final static int LEAGUE_QUERY_FAILED = 2;
    public final static int LEAGUE_IS_UPDATING = 3;

    private List<LeagueRound> leagueDataSet;


    public LeagueManager(){
        leagueDataSet = new ArrayList<>();
    }

    public List getLeagueDataSet(){
        return leagueDataSet;
    }

    public LeagueRound getLatestRound() {
        int latest_round = 0;
        for(int i=0; i<leagueDataSet.size(); i++){
            if(leagueDataSet.get(i).getTableItems().size() != 0) {
                if(leagueDataSet.get(i).getRound() > leagueDataSet.get(latest_round).getRound())
                    latest_round = i;
            }
        }
        return leagueDataSet.get(latest_round);
    }

    public void queryLeague(){
        notifyLeagueListener(LEAGUE_IS_UPDATING);

        ParseQuery<LeagueRound> query = ParseQuery.getQuery("League");
        query.findInBackground(new FindCallback<LeagueRound>() {
            @Override
            public void done(List<LeagueRound> leagueRounds, ParseException e) {
                if (e == null) {
                    leagueDataSet.clear();

                    for (int i = 0; i < leagueRounds.size(); i++) {
                        leagueRounds.get(i).setTeams();
                        leagueDataSet.add(leagueRounds.get(i));
                    }

                    notifyLeagueListener(LEAGUE_UPDATED);
                } else {
                    notifyLeagueListener(LEAGUE_QUERY_FAILED);
                }
            }
        });
    }

    public List<LeagueRound> getDiscussionDataSet(){ return leagueDataSet; }

    private void notifyLeagueListener(int event) {
        for(LeagueListener listener : sessionListener){
            if(listener != null){
                listener.OnLeagueEvent(event);
            }
        }
    }

    public void subscribeLeagueListener(LeagueListener listener){
        if(listener != null){
            sessionListener.add(listener);
        }
    }

    public void unsubscribeLeagueListener(LeagueListener listener){
        if(listener != null){
            sessionListener.remove(listener);
        }
    }

    public interface LeagueListener
    {
        public void OnLeagueEvent(int event);
    }
}
