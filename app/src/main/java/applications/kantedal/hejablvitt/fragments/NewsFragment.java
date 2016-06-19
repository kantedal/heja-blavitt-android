package applications.kantedal.hejablvitt.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import applications.kantedal.hejablvitt.BlavittApplication;
import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.activities.MainActivity;
import applications.kantedal.hejablvitt.adapters.NewsAdapter;
import applications.kantedal.hejablvitt.manager.NewsManager;
import applications.kantedal.hejablvitt.models.News;

/**
 * Created by filles-dator on 2015-06-09.
 */
public class NewsFragment extends Fragment
        implements NewsManager.NewsListener {

    protected BlavittApplication mApp;

    private RecyclerView newsRecycler;
    private NewsAdapter newsAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<News> newsDataSet;

    public NewsFragment(){
        newsDataSet = new ArrayList<News>();
    }

    public static NewsFragment newInstance(){
        return new NewsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApp = (BlavittApplication) getActivity().getApplication();

        newsDataSet = new ArrayList<>();

        mApp.getNewsManager().subscribeNewsListener(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mApp.getNewsManager().unsubscribeNewsListener(this);
    }

    public void OnNewsItemEvent(int index, int event){
        switch (event){
            case NewsManager.NEWS_ITEM_UPDATED:
                mSwipeRefreshLayout.setRefreshing(false);
                newsAdapter.notifyItemChanged(index);
                break;
            case NewsManager.NEWS_IS_UPDATING:
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
                break;
        }
    }

    public void OnNewsEvent(int event){
        switch (event){
            case NewsManager.NEWS_ADDED:
                mSwipeRefreshLayout.setRefreshing(false);
                break;
            case NewsManager.NEWS_UPDATED:
                mSwipeRefreshLayout.setRefreshing(false);
                newsAdapter.notifyDataSetChanged();
                break;
            case NewsManager.NEWS_QUERY_FAILED:
                break;
            case NewsManager.NEWS_IS_UPDATING:
                if(!mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(true);
                        }
                    });
                }
                break;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_news, container, false);

        if(savedInstanceState == null) {
            newsRecycler = (RecyclerView) rootView.findViewById(R.id.feedRecyclerView);
            newsRecycler.setHasFixedSize(true);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            newsRecycler.setLayoutManager(layoutManager);
            newsAdapter = new NewsAdapter(mApp.getNewsManager().getNewsDataSet(), this);
            newsRecycler.setAdapter(newsAdapter);

            RecyclerView.ItemAnimator animator = newsRecycler.getItemAnimator();
            animator.setAddDuration(2000);
            animator.setRemoveDuration(1000);
            newsRecycler.setItemAnimator(animator);

            mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.activity_main_swipe_refresh_layout);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mApp.getNewsManager().queryNews(NewsManager.SORT_PUBLISHED_DATE);
                }
            });

            if(mApp.getNewsManager().mQueryVotedNews){
                mApp.getNewsManager().mQueryVotedNews = false;
                mApp.getNewsManager().queryNews(NewsManager.SORT_PUBLISHED_DATE);
            }else if(mApp.getNewsManager().getNewsDataSet().size() == 0 && !mApp.getNewsManager().isUpdating())
                mApp.getNewsManager().queryNews(NewsManager.SORT_PUBLISHED_DATE);

        }

        return rootView;
    }


}
