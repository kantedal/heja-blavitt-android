package applications.kantedal.hejablvitt.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by filles-dator on 2015-06-20.
 */

@ParseClassName("League")
public class LeagueRound extends ParseObject{

    private ArrayList<TableItem> tableItems;

    public LeagueRound(){
        tableItems = new ArrayList<>();
    }

    public int getRound(){
        return getInt("round");
    }

    public ArrayList<TableItem> getTableItems(){
        return tableItems;
    }

    public void setTeams(){
        JSONArray tempTeams = getJSONArray("table");
        tableItems.clear();
        if(tempTeams != null) {
            for (int j = 0; j < tempTeams.length(); j++) {
                try {
                    JSONArray jsonArr = (JSONArray) tempTeams.get(j);

                    TableItem tempTeam = new TableItem();
                    tempTeam.teamName = jsonArr.get(0).toString();
                    tempTeam.gamesPlayed = (Integer) jsonArr.get(1);
                    tempTeam.wins = (Integer) jsonArr.get(2);
                    tempTeam.draws = (Integer) jsonArr.get(3);
                    tempTeam.losses = (Integer) jsonArr.get(4);
                    tempTeam.goals = (Integer) jsonArr.get(5);
                    tempTeam.against = (Integer) jsonArr.get(6);
                    tempTeam.goalDiff = (Integer) jsonArr.get(7);
                    tempTeam.points = (Integer) jsonArr.get(8);

                    tableItems.add(tempTeam);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class TableItem {
        public String teamName;
        public int gamesPlayed;
        public int wins;
        public int draws;
        public int losses;
        public int goals;
        public int against;
        public int goalDiff;
        public int points;
    }

    public static ParseQuery<DiscussionEntry> getQuery(){
        return ParseQuery.getQuery(DiscussionEntry.class);
    }
}
