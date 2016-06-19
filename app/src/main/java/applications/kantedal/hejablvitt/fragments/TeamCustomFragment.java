package applications.kantedal.hejablvitt.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import applications.kantedal.hejablvitt.BlavittApplication;
import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.adapters.LeagueAdapter;

/**
 * Created by filles-dator on 2015-08-19.
 */
public class TeamCustomFragment extends Fragment{

    protected BlavittApplication mApp;

    protected RecyclerView mTeamCustomRecycler;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    private TeamFragment.OnContainerTouchListener mTouchCallback;
    protected boolean mIsUpdating = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (BlavittApplication) getActivity().getApplication();
    }

    protected void setupFragment(View rootView){
        mTeamCustomRecycler = (RecyclerView) rootView.findViewById(R.id.fragment_custom_team_recycler);
        mTeamCustomRecycler.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mTeamCustomRecycler.setLayoutManager(layoutManager);

        mTeamCustomRecycler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("Log", "ON TOUCH");
                return false;
            }
        });

        RecyclerView.ItemAnimator animator = mTeamCustomRecycler.getItemAnimator();
        animator.setAddDuration(2000);
        animator.setRemoveDuration(1000);
        mTeamCustomRecycler.setItemAnimator(animator);

    }
}
