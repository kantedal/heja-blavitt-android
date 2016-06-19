package applications.kantedal.hejablvitt.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import applications.kantedal.hejablvitt.BlavittApplication;
import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.activities.DiscussionActivity;
import applications.kantedal.hejablvitt.adapters.DiscussionAdapter;
import applications.kantedal.hejablvitt.adapters.NewsAdapter;
import applications.kantedal.hejablvitt.dialogs.ListPickerDialog;
import applications.kantedal.hejablvitt.manager.DiscussionManager;
import applications.kantedal.hejablvitt.manager.NewsManager;
import applications.kantedal.hejablvitt.models.DiscussionEntry;

/**
 * Created by filles-dator on 2015-06-09.
 */
public class MyDiscussionFragment extends Fragment implements DiscussionManager.DiscussionListener{

    protected BlavittApplication mApp;
    protected DiscussionManager discussionManager;

    private RecyclerView discussionRecycler;
    private TextView noEntriesTextView;
    private DiscussionAdapter discussionAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    protected List<DiscussionEntry> discussionDataSet;

    public MyDiscussionFragment(){
        discussionDataSet = new ArrayList<DiscussionEntry>();
    }

    public static MyDiscussionFragment newInstance(){
        MyDiscussionFragment fragment = new MyDiscussionFragment();

        return fragment;
    }

    public void OnDiscussionItemEvent(int index, int event){
        switch (event){
            case DiscussionManager.DISCUSSION_ITEM_UPDATED:
                mSwipeRefreshLayout.setRefreshing(false);
                discussionAdapter.notifyItemChanged(index);
                break;
            case DiscussionManager.DISCUSSION_IS_UPDATING:
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
                break;
        }
    }

    public void OnDiscussionEvent(int event){
        switch (event){
            case DiscussionManager.DISCUSSION_ADDED:
                mSwipeRefreshLayout.setRefreshing(false);
                break;
            case DiscussionManager.DISCUSSION_UPDATED:
                mSwipeRefreshLayout.setRefreshing(false);
                //discussionDataSet.clear();
                if(mApp.getDiscussionManager().getMyDiscussionDataSet().size() != 0){
                    noEntriesTextView.setVisibility(View.GONE);
                    discussionAdapter.notifyDataSetChanged();
                }else
                    noEntriesTextView.setVisibility(View.VISIBLE);

                break;
            case DiscussionManager.DISCUSSION_QUERY_FAILED:
                break;
            case DiscussionManager.DISCUSSION_IS_UPDATING:
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApp = (BlavittApplication) getActivity().getApplication();
        discussionManager = mApp.getDiscussionManager();

        discussionDataSet = new ArrayList<DiscussionEntry>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_my_discussion, container, false);

        discussionRecycler = (RecyclerView) rootView.findViewById(R.id.feedRecyclerView);
        discussionRecycler.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        discussionRecycler.setLayoutManager(layoutManager);
        discussionAdapter = new DiscussionAdapter(mApp.getDiscussionManager().getMyDiscussionDataSet(), this, true);
        discussionRecycler.setAdapter(discussionAdapter);

        noEntriesTextView = (TextView) rootView.findViewById(R.id.noCommentsText);

        RecyclerView.ItemAnimator animator = discussionRecycler.getItemAnimator();
        animator.setAddDuration(1000);
        animator.setRemoveDuration(1000);
        discussionRecycler.setItemAnimator(animator);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mApp.getDiscussionManager().queryDiscussion(NewsManager.SORT_PUBLISHED_DATE, true);
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mApp.getDiscussionManager().queryDiscussion(NewsManager.SORT_PUBLISHED_DATE, true);
            }
        });

        mApp.getDiscussionManager().subscribeDiscussionListener(this);

        return rootView;
    }


}
