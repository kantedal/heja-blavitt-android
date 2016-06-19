package applications.kantedal.hejablvitt.adapters;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.models.LeagueRound;

/**
 * Created by filles-dator on 2015-06-20.
 */
public class LeagueAdapter extends RecyclerView.Adapter<LeagueAdapter.ViewHolder>
{
    private LeagueRound mLeagueRound;

    public LeagueAdapter(LeagueRound leagueRound) {
        this.mLeagueRound = leagueRound;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.view_league_team, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final LeagueRound.TableItem tableItem = mLeagueRound.getTableItems().get(i);

        if ((i + 1) % 2 == 0) {
            viewHolder.background.setBackgroundColor(0x22FFFFFF);
        } else
            viewHolder.background.setBackgroundColor(Color.TRANSPARENT);


        if((i < 3 || i > 13) && android.os.Build.VERSION.SDK_INT >= 21){
            viewHolder.positionTextView.setVisibility(View.GONE);
            viewHolder.positionCardView.setVisibility(View.VISIBLE);

            switch (i+1) {
                case 1:
                    viewHolder.positionCardView.setCardBackgroundColor(0x9944aa44);
                    break;
                case 2:
                    viewHolder.positionCardView.setCardBackgroundColor(0x9977aa77);
                    break;
                case 3:
                    viewHolder.positionCardView.setCardBackgroundColor(0x9977aa77);
                    break;
                case 14:
                    viewHolder.positionCardView.setCardBackgroundColor(0x99aa7777);
                    break;
                case 15:
                    viewHolder.positionCardView.setCardBackgroundColor(0x99aa4444);
                    break;
                case 16:
                    viewHolder.positionCardView.setCardBackgroundColor(0x99aa4444);
                    break;
            }

            viewHolder.positionCardTextView.setText(Integer.toString(i+1));
        }else{
            viewHolder.positionCardView.setVisibility(View.GONE);
            viewHolder.positionTextView.setVisibility(View.VISIBLE);

            viewHolder.positionTextView.setText(Integer.toString(i+1));
        }

        viewHolder.teamNameTextView.setText(tableItem.teamName);
        viewHolder.gamesPlayedTextView.setText(Integer.toString(tableItem.gamesPlayed));
        viewHolder.goalDiffTextView.setText(tableItem.goals +"-"+tableItem.against);
        viewHolder.pointsTextView.setText(Integer.toString(tableItem.points));
    }

    public void setLeagueRound(LeagueRound leagueRound){
        mLeagueRound = leagueRound;
    }

    @Override
    public int getItemCount(){
        if(mLeagueRound != null)
            return mLeagueRound.getTableItems().size();
        else
            return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        protected LinearLayout background;

        protected CardView positionCardView;
        protected TextView positionCardTextView;
        protected TextView positionTextView;

        protected TextView teamNameTextView;
        protected TextView gamesPlayedTextView;
        protected TextView goalDiffTextView;
        protected TextView pointsTextView;

        public ViewHolder(View tableItemView) {
            super(tableItemView);
            background = (LinearLayout) tableItemView.findViewById(R.id.view_league_team_background);

            positionCardView = (CardView) tableItemView.findViewById(R.id.view_league_team_position_background);
            positionCardTextView = (TextView) tableItemView.findViewById(R.id.view_league_team_position_card);
            positionTextView = (TextView) tableItemView.findViewById(R.id.view_league_team_position);

            teamNameTextView = (TextView) tableItemView.findViewById(R.id.view_league_team_name);
            gamesPlayedTextView = (TextView) tableItemView.findViewById(R.id.view_league_team_games_played);
            goalDiffTextView = (TextView) tableItemView.findViewById(R.id.view_league_team_goal_diff);
            pointsTextView = (TextView) tableItemView.findViewById(R.id.view_league_team_points);
        }
    }

}


