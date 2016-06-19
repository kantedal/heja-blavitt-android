package applications.kantedal.hejablvitt.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.Toast;

import applications.kantedal.hejablvitt.BlavittApplication;
import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.adapters.LeagueAdapter;
import applications.kantedal.hejablvitt.adapters.NewsAdapter;
import applications.kantedal.hejablvitt.adapters.StatisticsAdapter;
import applications.kantedal.hejablvitt.adapters.TeamExpandableListAdapter;
import applications.kantedal.hejablvitt.manager.LeagueManager;
import applications.kantedal.hejablvitt.manager.NewsManager;
import applications.kantedal.hejablvitt.models.LeagueRound;

public class TeamFragment extends Fragment {

    private ExpandableListView mExpandableListView;
    private TeamExpandableListAdapter mListAdapter;

    private FragmentManager fm;
    private FragmentTransaction ft;

    private OnContainerTouchListener mTouchCallback;
    public interface OnContainerTouchListener {
        void OnTouch();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_team, container, false);

        if(savedInstanceState == null) {

            fm = getActivity().getSupportFragmentManager();

            mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.fragment_team_expandable_list);
            mListAdapter = new TeamExpandableListAdapter(getActivity());
            mExpandableListView.setAdapter(mListAdapter);
            mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    mListAdapter.selectItem(childPosition);

                    switch (childPosition) {
                        case TeamExpandableListAdapter.SELECTION_LEAGUE:
                            LeagueFragment leagueFragment = LeagueFragment.newInstance();
                            changeView(leagueFragment);
                            break;
                        case TeamExpandableListAdapter.SELECTION_CROWD_LEAGUE:
                            StatisticsFragment statisticsCrowdFragment = StatisticsFragment.newInstance(StatisticsAdapter.VIEW_TYPE_CROWD_LEAGUE);
                            changeView(statisticsCrowdFragment);
                            break;
                        case TeamExpandableListAdapter.SELECTION_SCORING_LEAGUE:
                            StatisticsFragment statisticsScoreFragment = StatisticsFragment.newInstance(StatisticsAdapter.VIEW_TYPE_SCORE_LEAGUE);
                            changeView(statisticsScoreFragment);
                            break;
                        case TeamExpandableListAdapter.SELECTION_ASSIST_LEAGUE:
                            StatisticsFragment statisticsAssistFragment = StatisticsFragment.newInstance(StatisticsAdapter.VIEW_TYPE_ASSIST_LEAGUE);
                            changeView(statisticsAssistFragment);
                            break;
                    }

                    mExpandableListView.collapseGroup(0);
                    return false;
                }
            });
        }

        mListAdapter.selectItem(TeamExpandableListAdapter.SELECTION_LEAGUE);
        LeagueFragment leagueFragment = LeagueFragment.newInstance();
        changeView(leagueFragment);


        return rootView;
    }

    private void changeView(Fragment fragment){
        ft = fm.beginTransaction();
        ft.replace(R.id.fragment_team_container, fragment, "leagueFragment");
        ft.commit();
    }

}
