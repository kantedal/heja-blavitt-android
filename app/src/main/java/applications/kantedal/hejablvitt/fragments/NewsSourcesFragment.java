package applications.kantedal.hejablvitt.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import applications.kantedal.hejablvitt.BlavittApplication;
import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.adapters.NewsSourcesAdapter;
import applications.kantedal.hejablvitt.adapters.StatisticsAdapter;
import applications.kantedal.hejablvitt.manager.NewsManager;
import applications.kantedal.hejablvitt.models.NewsSource;

/**
 * Created by filles-dator on 2015-08-20.
 */
public class NewsSourcesFragment extends Fragment implements NewsManager.NewsListener {

    private BlavittApplication mApp;

    private RecyclerView mNewsSourcesRecycler;
    private NewsSourcesAdapter mNewsSourcesAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private List<NewsSource> mNewsSources;

    public static NewsSourcesFragment newInstance(){
        NewsSourcesFragment newsSourcesFragment = new NewsSourcesFragment();

        return newsSourcesFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mApp = (BlavittApplication) getActivity().getApplication();
        mNewsSources = mApp.getNewsManager().getNewsSources();
        mApp.getNewsManager().subscribeNewsListener(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news_sources, container, false);

        mNewsSourcesRecycler = (RecyclerView) rootView.findViewById(R.id.fragment_news_sources_recycler);
        mNewsSourcesRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        mNewsSourcesAdapter = new NewsSourcesAdapter(mApp, getActivity());
        mNewsSourcesRecycler.setAdapter(mNewsSourcesAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_news_sources_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mApp.getNewsManager().queryNewsSources(false);
            }
        });

        if(mNewsSources.size() == 0)
            mApp.getNewsManager().queryNewsSources(false);
        else{
            mNewsSourcesAdapter.setNewsSources(mNewsSources);
            mNewsSourcesAdapter.notifyDataSetChanged();
        }

        return rootView;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mApp.getNewsManager().unsubscribeNewsListener(this);
    }

    public void OnNewsEvent(int event){
        switch (event){
            case NewsManager.NEWS_IS_UPDATING:
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
                break;
            case NewsManager.NEWS_SOURCES_UPDATED:
                mSwipeRefreshLayout.setRefreshing(false);
                mNewsSources = mApp.getNewsManager().getNewsSources();
                mNewsSourcesAdapter.setNewsSources(mNewsSources);
                mNewsSourcesAdapter.notifyDataSetChanged();
                break;
            case NewsManager.NEWS_QUERY_FAILED:
                mSwipeRefreshLayout.setRefreshing(false);
                break;
        }
    }

    public void OnNewsItemEvent(int event, int index){}
}
