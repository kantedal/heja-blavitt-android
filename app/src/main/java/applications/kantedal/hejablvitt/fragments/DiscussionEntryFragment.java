package applications.kantedal.hejablvitt.fragments;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import applications.kantedal.hejablvitt.BlavittApplication;
import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.manager.DiscussionManager;
import applications.kantedal.hejablvitt.models.DiscussionEntry;
import applications.kantedal.hejablvitt.util.DateHandler;

/**
 * Created by filles-dator on 2015-07-06.
 */
public class DiscussionEntryFragment extends Fragment {

    private DiscussionManager mDiscussionManager;
    private DiscussionEntry mDiscussionEntry;

    private String objectId;
    private int mFragmentPosition;

    private TextView discussionTextView;
    private TextView contentSnippetTextView;
    private TextView likesTextView;
    private TextView usernameTextView;
    private TextView dateTextView;

    private TextView upVoteButton;
    private TextView downVoteButton;

    private LinearLayout discussionContainer;

    public static DiscussionEntryFragment newInstance(String objectId, int fragmentPosition) {
        DiscussionEntryFragment myFragment = new DiscussionEntryFragment();

        Bundle args = new Bundle();
        args.putString("objectId", objectId);
        args.putInt("fragmentPosition", fragmentPosition);
        myFragment.setArguments(args);

        return myFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_discussion_entry, container, false);

        objectId = getArguments().getString("objectId");
        mFragmentPosition = getArguments().getInt("fragmentPosition");
        mDiscussionEntry = mDiscussionManager.getDiscussionById(objectId);

        discussionContainer = (LinearLayout) rootView.findViewById(R.id.discussionContainer);

        if (android.os.Build.VERSION.SDK_INT >= 21)
            discussionContainer.setTransitionName("transitionDiscussion"+mFragmentPosition);

        discussionTextView = (TextView) rootView.findViewById(R.id.discussionTextView);
        likesTextView = (TextView) rootView.findViewById(R.id.likesText);
        usernameTextView = (TextView) rootView.findViewById(R.id.usernameTextView);
        dateTextView = (TextView) rootView.findViewById(R.id.dateText);

        upVoteButton =  (TextView) rootView.findViewById(R.id.upVoteButton);
        downVoteButton =  (TextView) rootView.findViewById(R.id.downVoteButton);

        updateLikesView();

        upVoteButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (mDiscussionManager.addLike(1, mDiscussionEntry)) {
                            updateLikesView();
                            mDiscussionManager.notifyDiscussionItem(mDiscussionManager.getIndex(mDiscussionEntry));
                        }
                    }
                }
        );

        downVoteButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (mDiscussionManager.addLike(-1, mDiscussionEntry)) {
                            updateLikesView();
                            mDiscussionManager.notifyDiscussionItem(mDiscussionManager.getIndex(mDiscussionEntry));
                        }
                    }
                }
        );

        discussionTextView.setText(mDiscussionEntry.getText());
        likesTextView.setText(Integer.toString(mDiscussionEntry.getLikes()));

        usernameTextView.setText(mDiscussionEntry.getUsername());
        dateTextView.setText(DateHandler.dateToString(mDiscussionEntry.getCreatedDate()));

        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment commentsFragment = CommentsFragment.newInstance(objectId, CommentsFragment.COMMENTS_DISCUSSION);

        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.containerComments, commentsFragment, "discussionFragment");
        ft.commit();

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDiscussionManager = ((BlavittApplication) getActivity().getApplication()).getDiscussionManager();
    }

    private void updateLikesView(){
        likesTextView.setText(Integer.toString(mDiscussionEntry.getLikes()));
        if (mDiscussionEntry.isLiked() == 1) {
            downVoteButton.setAlpha(0.3f);
            upVoteButton.setAlpha(1.0f);
        } else if (mDiscussionEntry.isLiked() == -1){
            upVoteButton.setAlpha(0.3f);
            downVoteButton.setAlpha(1.0f);
        }
    }



}
