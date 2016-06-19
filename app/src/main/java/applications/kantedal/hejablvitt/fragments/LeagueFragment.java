package applications.kantedal.hejablvitt.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.adapters.LeagueAdapter;
import applications.kantedal.hejablvitt.manager.LeagueManager;
import applications.kantedal.hejablvitt.models.LeagueRound;

public class LeagueFragment extends TeamCustomFragment implements LeagueManager.LeagueListener{

    private LeagueAdapter mLeagueAdapter;
    private LeagueRound mCurrentLeagueRound;

    private TeamFragment.OnContainerTouchListener mTouchCallback;

    public static LeagueFragment newInstance(){
        LeagueFragment leagueFragment = new LeagueFragment();
        return leagueFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp.getLeagueManager().subscribeLeagueListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_custom_team, container, false);
        setupFragment(rootView);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_team_swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mApp.getLeagueManager().queryLeague();
            }
        });

        if(mApp.getLeagueManager().getDiscussionDataSet().size() == 0 && !mIsUpdating){
            mApp.getLeagueManager().queryLeague();
        }else if(!mIsUpdating)
            mCurrentLeagueRound = mApp.getLeagueManager().getLatestRound();

        mLeagueAdapter = new LeagueAdapter(mCurrentLeagueRound);
        mTeamCustomRecycler.setAdapter(mLeagueAdapter);
        mLeagueAdapter.notifyDataSetChanged();

        return rootView;
    }

    public void OnLeagueEvent(int event){
        switch (event){
            case LeagueManager.LEAGUE_UPDATED:
                mIsUpdating = false;
                mSwipeRefreshLayout.setRefreshing(false);
                mCurrentLeagueRound = mApp.getLeagueManager().getLatestRound();
                mLeagueAdapter.setLeagueRound(mCurrentLeagueRound);
                mLeagueAdapter.notifyDataSetChanged();
                break;
            case LeagueManager.LEAGUE_QUERY_FAILED:
                mIsUpdating = false;
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
                break;
            case LeagueManager.LEAGUE_IS_UPDATING:
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
