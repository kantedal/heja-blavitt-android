package applications.kantedal.hejablvitt.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.activities.MainActivity;
import applications.kantedal.hejablvitt.adapters.LeagueAdapter;
import applications.kantedal.hejablvitt.adapters.StatisticsAdapter;
import applications.kantedal.hejablvitt.manager.LeagueManager;
import applications.kantedal.hejablvitt.manager.StatisticsManager;
import applications.kantedal.hejablvitt.models.LeagueRound;

/**
 * Created by filles-dator on 2015-08-20.
 */
public class StatisticsFragment extends TeamCustomFragment implements StatisticsManager.StatisticsListener {

    private StatisticsAdapter mStatisticsAdapter;
    private boolean mIsUpdating = false;
    private int mViewType;

    private TeamFragment.OnContainerTouchListener mTouchCallback;

    public static StatisticsFragment newInstance(int viewType){
        StatisticsFragment statisticsFragment = new StatisticsFragment();

        Bundle args = new Bundle();
        args.putInt("viewType", viewType);
        statisticsFragment.setArguments(args);

        return statisticsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp.getStatisticsManager().subscribeStatisticsListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_custom_team, container, false);
        setupFragment(rootView);

        mViewType = getArguments().getInt("viewType");

        mStatisticsAdapter = new StatisticsAdapter((MainActivity) getActivity(), mViewType);
        mTeamCustomRecycler.setAdapter(mStatisticsAdapter);
        mStatisticsAdapter.notifyDataSetChanged();

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_team_swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mApp.getStatisticsManager().queryStatistics();
            }
        });


        if(!mIsUpdating) {
            switch (mViewType) {
                case StatisticsAdapter.VIEW_TYPE_CROWD_LEAGUE:
                    if (mApp.getStatisticsManager().getCrowdLeague().size() == 0 && !mIsUpdating)
                        mApp.getStatisticsManager().queryStatistics();
                    break;
                case StatisticsAdapter.VIEW_TYPE_ASSIST_LEAGUE:
                    if (mApp.getStatisticsManager().getAssistLeague().size() == 0 && !mIsUpdating)
                        mApp.getStatisticsManager().queryStatistics();
                    break;
                case StatisticsAdapter.VIEW_TYPE_SCORE_LEAGUE:
                    if (mApp.getStatisticsManager().getScoringLeague().size() == 0 && !mIsUpdating)
                        mApp.getStatisticsManager().queryStatistics();
                    break;
            }
        }


        return rootView;
    }

    public void OnStatisticsEvent(int event){
        switch (event){
            case StatisticsManager.STATISTICS_UPDATED:
                mIsUpdating = false;
                mSwipeRefreshLayout.setRefreshing(false);
                mStatisticsAdapter.notifyDataSetChanged();
                break;
            case StatisticsManager.STATISTICS_QUERY_FAILED:
                mIsUpdating = false;
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
                break;
            case StatisticsManager.STATISTICS_IS_UPDATING:
                mIsUpdating = true;
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
                break;
        }
    }

}
