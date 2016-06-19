package applications.kantedal.hejablvitt.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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

import applications.kantedal.hejablvitt.BlavittApplication;
import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.activities.MainActivity;
import applications.kantedal.hejablvitt.fragments.TeamCustomFragment;
import applications.kantedal.hejablvitt.manager.StatisticsManager;
import applications.kantedal.hejablvitt.models.LeagueRound;

/**
 * Created by filles-dator on 2015-06-20.
 */
public class StatisticsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private BlavittApplication mApp;
    private MainActivity mParent;

    public static final int VIEW_TYPE_SCORE_LEAGUE = 0;
    public static final int VIEW_TYPE_ASSIST_LEAGUE = 1;
    public static final int VIEW_TYPE_CROWD_LEAGUE = 2;

    private int mViewType;

    public StatisticsAdapter(MainActivity parent, int viewType) {
        mApp = (BlavittApplication) parent.getApplication();
        mParent = parent;
        mViewType = viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (mViewType) {
            case VIEW_TYPE_SCORE_LEAGUE:
                View scoreView = inflater.inflate(R.layout.view_scoring_list_item, viewGroup, false);
                return new ScoringViewHolder(scoreView);
            case VIEW_TYPE_ASSIST_LEAGUE:
                View assistView = inflater.inflate(R.layout.view_assist_list_item, viewGroup, false);
                return new AssistViewHolder(assistView);
            case VIEW_TYPE_CROWD_LEAGUE:
                View crowdView = inflater.inflate(R.layout.view_crowd_list_item, viewGroup, false);
                return new CrowdViewHolder(crowdView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {
        switch (mViewType) {
            case VIEW_TYPE_SCORE_LEAGUE:
                ScoringViewHolder svh = (ScoringViewHolder) viewHolder;
                if(i == 0) {
                    svh.background.setBackgroundColor(Color.TRANSPARENT);
                    svh.positionTextView.setText("");
                    svh.nameTextView.setText("Namn");
                    svh.teamTextView.setText("Lag");
                    svh.goalsTextView.setText("Mål");
                }else{
                    if ((i + 1) % 2 == 0) {
                        svh.background.setBackgroundColor(0x22FFFFFF);
                    } else
                        svh.background.setBackgroundColor(Color.TRANSPARENT);

                    StatisticsManager.ScoreItem scoreItem = (StatisticsManager.ScoreItem) mApp.getStatisticsManager().getScoringLeague().get(i-1);

                    svh.positionTextView.setText(Integer.toString(i));
                    svh.nameTextView.setText(scoreItem.name);
                    svh.teamTextView.setText(scoreItem.team);
                    svh.goalsTextView.setText(Integer.toString(scoreItem.goals));

                    if (scoreItem.team.equals("GBG"))
                        svh.background.setBackgroundColor(0x33000000);
                }
                break;
            case VIEW_TYPE_ASSIST_LEAGUE:
                AssistViewHolder avh = (AssistViewHolder) viewHolder;
                if(i == 0) {
                    avh.background.setBackgroundColor(Color.TRANSPARENT);
                    avh.positionTextView.setText("");
                    avh.nameTextView.setText("Namn");
                    avh.nameTextView.setTypeface(null, Typeface.NORMAL);
                    avh.teamTextView.setText("Lag");
                    avh.teamTextView.setTypeface(null, Typeface.NORMAL);
                    avh.assistTextView.setText("Ass");
                    avh.assistTextView.setTypeface(null, Typeface.NORMAL);
                }else{
                    if ((i + 1) % 2 == 0) {
                        avh.background.setBackgroundColor(0x22FFFFFF);
                    } else
                        avh.background.setBackgroundColor(Color.TRANSPARENT);

                    StatisticsManager.AssistItem assistItem = (StatisticsManager.AssistItem) mApp.getStatisticsManager().getAssistLeague().get(i-1);

                    avh.positionTextView.setText(Integer.toString(i));
                    avh.nameTextView.setText(assistItem.name);
                    avh.teamTextView.setText(assistItem.team);
                    avh.assistTextView.setText(Integer.toString(assistItem.assists));

                    if (assistItem.team.equals("GBG"))
                        avh.background.setBackgroundColor(0x33000000);

                }
                break;
            case VIEW_TYPE_CROWD_LEAGUE:
                CrowdViewHolder cvh = (CrowdViewHolder) viewHolder;
                if(i == 0) {
                    cvh.background.setBackgroundColor(Color.TRANSPARENT);
                    cvh.positionCard.setVisibility(View.GONE);
                    cvh.positionTextView.setText("");
                    cvh.teamTextView.setText("Lag");
                    cvh.teamTextView.setTypeface(null, Typeface.NORMAL);
                    cvh.crowdTextView.setText("Publiksiffra");
                    cvh.crowdTextView.setTypeface(null, Typeface.NORMAL);
                }else{
                    if ((i + 1) % 2 == 0) {
                        cvh.background.setBackgroundColor(0x22FFFFFF);
                    } else
                        cvh.background.setBackgroundColor(Color.TRANSPARENT);

                    StatisticsManager.CrowdItem crowdItem = (StatisticsManager.CrowdItem) mApp.getStatisticsManager().getCrowdLeague().get(i-1);

                    cvh.positionCard.setVisibility(View.GONE);
                    cvh.positionTextView.setText(Integer.toString(i));
                    cvh.teamTextView.setText(crowdItem.team);
                    cvh.crowdTextView.setText(Integer.toString(crowdItem.crowdCount));

                    if (crowdItem.team.equals("GBG"))
                        cvh.background.setBackgroundColor(0x33000000);

                }
               break;
        }
    }


    @Override
    public int getItemCount(){
        switch (mViewType) {
            case VIEW_TYPE_SCORE_LEAGUE:
                return mApp.getStatisticsManager().getScoringLeague().size()+1;
            case VIEW_TYPE_ASSIST_LEAGUE:
                return mApp.getStatisticsManager().getAssistLeague().size()+1;
            case VIEW_TYPE_CROWD_LEAGUE:
                return mApp.getStatisticsManager().getCrowdLeague().size()+1;
        }
        return 0;
    }

    public static class ScoringViewHolder extends RecyclerView.ViewHolder
    {
        protected LinearLayout background;
        protected TextView positionTextView;
        protected TextView nameTextView;
        protected TextView teamTextView;
        protected TextView goalsTextView;

        public ScoringViewHolder(View tableItemView) {
            super(tableItemView);
            background = (LinearLayout) tableItemView.findViewById(R.id.view_scoring_list_item_background);
            positionTextView = (TextView) tableItemView.findViewById(R.id.view_scoring_list_item_position);
            nameTextView = (TextView) tableItemView.findViewById(R.id.view_scoring_list_item_name);
            teamTextView = (TextView) tableItemView.findViewById(R.id.view_scoring_list_item_team);
            goalsTextView = (TextView) tableItemView.findViewById(R.id.view_scoring_list_item_goals);
        }
    }

    public static class AssistViewHolder extends RecyclerView.ViewHolder
    {
        protected LinearLayout background;
        protected TextView positionTextView;
        protected TextView nameTextView;
        protected TextView teamTextView;
        protected TextView assistTextView;

        public AssistViewHolder(View tableItemView) {
            super(tableItemView);
            background = (LinearLayout) tableItemView.findViewById(R.id.view_assist_list_item_background);
            positionTextView = (TextView) tableItemView.findViewById(R.id.view_assist_list_item_position);
            nameTextView = (TextView) tableItemView.findViewById(R.id.view_assist_list_item_name);
            teamTextView = (TextView) tableItemView.findViewById(R.id.view_assist_list_item_team);
            assistTextView = (TextView) tableItemView.findViewById(R.id.view_assist_list_item_goals);
        }
    }

    public static class CrowdViewHolder extends RecyclerView.ViewHolder
    {
        protected LinearLayout background;
        protected CardView positionCard;
        protected TextView positionCardPositionText;
        protected TextView positionTextView;
        protected TextView teamTextView;
        protected TextView crowdTextView;

        public CrowdViewHolder(View tableItemView) {
            super(tableItemView);
            background = (LinearLayout) tableItemView.findViewById(R.id.view_crowd_list_item_background);
            positionCard = (CardView) tableItemView.findViewById(R.id.view_crowd_list_item_card);
            positionCardPositionText = (TextView) tableItemView.findViewById(R.id.view_crowd_list_item_position_card);
            positionTextView = (TextView) tableItemView.findViewById(R.id.view_crowd_list_item_position);
            teamTextView = (TextView) tableItemView.findViewById(R.id.view_crowd_list_item_team);
            crowdTextView = (TextView) tableItemView.findViewById(R.id.view_crowd_list_item_crowd);
        }
    }

}


