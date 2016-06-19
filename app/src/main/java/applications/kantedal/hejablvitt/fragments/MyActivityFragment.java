package applications.kantedal.hejablvitt.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import applications.kantedal.hejablvitt.BlavittApplication;
import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.adapters.NewsAdapter;
import applications.kantedal.hejablvitt.manager.NewsManager;
import applications.kantedal.hejablvitt.models.News;


public class MyActivityFragment extends Fragment
        implements NewsManager.NewsListener {

    protected BlavittApplication mApp;

    private RecyclerView newsRecycler;
    private NewsAdapter newsAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mNoActivityTextView;

    private int sortOption = 0;

    private List<News> newsDataSet;

    public MyActivityFragment(){
        newsDataSet = new ArrayList<News>();
    }

    public static MyActivityFragment newInstance(){
        return new MyActivityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApp = (BlavittApplication) getActivity().getApplication();
        mApp.getNewsManager().subscribeNewsListener(this);

        newsDataSet = new ArrayList<>();
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
                if(mApp.getNewsManager().getNewsDataSet().size() == 0){
                    mNoActivityTextView.setVisibility(View.VISIBLE);
                }else {
                    newsAdapter.notifyDataSetChanged();
                    mNoActivityTextView.setVisibility(View.GONE);
                }

                break;
            case NewsManager.NEWS_QUERY_FAILED:
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

            mNoActivityTextView = (TextView) rootView.findViewById(R.id.noCommentsText);

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

            mApp.getNewsManager().clearNewsDataSet();
            mApp.getNewsManager().mQueryVotedNews = true;
            mApp.getNewsManager().queryNews(NewsManager.SORT_PUBLISHED_DATE);
        }

        return rootView;
    }

    @Override
    public void onDestroy(){
        mApp.getNewsManager().clearNewsDataSet();
        mApp.getNewsManager().unsubscribeNewsListener(this);
        super.onDestroy();
    }

}
