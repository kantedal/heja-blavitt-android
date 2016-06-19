package applications.kantedal.hejablvitt.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
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
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.adapters.CommentsAdapter;
import applications.kantedal.hejablvitt.models.Comment;

/**
 * Created by filles-dator on 2015-06-09.
 */
public class CommentsFragment extends Fragment {

    public static final int COMMENTS_NEWS = 0;
    public static final int COMMENTS_DISCUSSION = 1;

    private RecyclerView commentsRecycler;
    private CommentsAdapter commentsAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String objectId;

    private RippleView postCommentButton;
    private EditText commentTextField;
    private TextView noCommentsText;

    protected List<Comment> commentsDataSet;
    public CommentsFragment(){}

    public static CommentsFragment newInstance(String objectId, int viewType){
        CommentsFragment fragment = new CommentsFragment();

        Bundle args = new Bundle();
        args.putString("objectId", objectId);
        args.putInt("viewType", viewType);
        fragment.setArguments(args);

        return fragment;
    }

    private void refreshComments(){
        ParseQuery<Comment> query = ParseQuery.getQuery("Comments");
        query.orderByDescending("createdAt");

        Log.d("Log", getArguments().get("viewType") + " Viewtype");
        switch ((int) getArguments().get("viewType")) {
            case COMMENTS_DISCUSSION:
                query.whereEqualTo("discussionId", objectId);
                break;
            case COMMENTS_NEWS:
                query.whereEqualTo("newsId", objectId);
                break;
        }

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
                        commentsAdapter.dataChanged();
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

        Bundle args = getArguments();
        objectId = args.getString("objectId");

        commentsDataSet = new ArrayList<>();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_comments, container, false);

        commentsRecycler = (RecyclerView) rootView.findViewById(R.id.commentsRecyclerView);
        commentsRecycler.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        commentsRecycler.setLayoutManager(layoutManager);
        commentsAdapter = new CommentsAdapter(commentsDataSet, getActivity(), objectId);
        commentsRecycler.setAdapter(commentsAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.comments_swipe_layout);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        refreshComments();

        noCommentsText = (TextView) rootView.findViewById(R.id.noCommentsText);
        noCommentsText.setVisibility(View.GONE);

        postCommentButton = (RippleView) rootView.findViewById(R.id.postCommentButton);
        commentTextField = (EditText) rootView.findViewById(R.id.commentTextField);
        commentTextField.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    String commentText = commentTextField.getText().toString();
                    if (!commentText.equals("")) {
                        mSwipeRefreshLayout.setRefreshing(true);


                        Comment newComment = new Comment(commentText, objectId, (getArguments().getInt("viewType") == COMMENTS_NEWS ? true : false));

                        newComment.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                refreshComments();
                            }
                        });

                        commentTextField.setText("");
                    }
                    return true;
                }
                return false;
            }
        });

        postCommentButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                        View view = getActivity().getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        String commentText = commentTextField.getText().toString();
                        if (!commentText.equals("")) {
                            mSwipeRefreshLayout.setRefreshing(true);


                            Comment newComment = new Comment(commentText, objectId, (getArguments().getInt("viewType") == COMMENTS_NEWS ? true : false));

                            newComment.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    refreshComments();
                                }
                            });

                            commentTextField.setText("");
                        }

                    }
                }
        );

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshComments();
            }
        });

        return rootView;
    }


}
