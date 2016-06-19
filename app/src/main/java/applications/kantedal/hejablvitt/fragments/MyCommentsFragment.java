package applications.kantedal.hejablvitt.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.adapters.CommentsAdapter;
import applications.kantedal.hejablvitt.adapters.MyCommentsAdapter;
import applications.kantedal.hejablvitt.models.Comment;

/**
 * Created by filles-dator on 2015-06-09.
 */
public class MyCommentsFragment extends Fragment {

    private RecyclerView commentsRecycler;
    private MyCommentsAdapter commentsAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String objectId;
    private TextView noCommentsText;

    protected List<Comment> commentsDataSet;
    public MyCommentsFragment(){}

    public static MyCommentsFragment newInstance(){
        MyCommentsFragment fragment = new MyCommentsFragment();
        return fragment;
    }

    private void refreshComments(){

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        ParseQuery<Comment> query = ParseQuery.getQuery("Comments");
        query.orderByDescending("createdAt");

        query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> comments, ParseException e) {
                if (e == null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    if(comments.size() != 0) {
                        noCommentsText.setVisibility(View.GONE);
                        commentsDataSet.clear();
                        for (int i = 0; i < comments.size(); i++) {
                            commentsDataSet.add(comments.get(i));
                        }
                        commentsAdapter.generateCommentData(commentsDataSet);
                        //commentsAdapter.dataChanged();
                    }else {
                        noCommentsText.setVisibility(View.VISIBLE);
                    }
                } else {

                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commentsDataSet = new ArrayList<>();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_my_comments, container, false);

        commentsRecycler = (RecyclerView) rootView.findViewById(R.id.commentsRecyclerView);
        commentsRecycler.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        commentsRecycler.setLayoutManager(layoutManager);
        commentsAdapter = new MyCommentsAdapter(this, objectId);
        commentsRecycler.setAdapter(commentsAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.comments_swipe_layout);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshComments();
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        refreshComments();

        noCommentsText = (TextView) rootView.findViewById(R.id.noCommentsText);
        noCommentsText.setVisibility(View.GONE);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshComments();
            }
        });

        return rootView;
    }


}
