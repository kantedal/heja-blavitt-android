package applications.kantedal.hejablvitt.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import applications.kantedal.hejablvitt.BlavittApplication;
import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.fragments.CommentsFragment;
import applications.kantedal.hejablvitt.manager.DiscussionManager;
import applications.kantedal.hejablvitt.models.DiscussionEntry;
import applications.kantedal.hejablvitt.util.DateHandler;

/**
 * Created by filles-dator on 2015-07-06.
 */
public class DiscussionActivity extends ActionBarActivity {

    private DiscussionManager discussionManager;
    private DiscussionEntry discussionEntry;

    private String discussionText;
    private int likes;
    private String url;
    private String objectId;

    private TextView discussionTextView;
    private TextView contentSnippetTextView;
    private TextView likesTextView;
    private TextView usernameTextView;
    private TextView dateTextView;

    private TextView upVoteButton;
    private TextView downVoteButton;

    private Toolbar toolbar;
    private LinearLayout webViewButton;
    private LinearLayout commentsViewButton;
    private LinearLayout backButton;

    private void addLike(String objectId, int ike){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion);

        discussionManager = ((BlavittApplication) getApplication()).getDiscussionManager();

        Bundle extras = getIntent().getExtras();
        if (savedInstanceState == null) {
            if(extras == null) {
                discussionText = null;
                likes=0;
                objectId=null;
            } else {
                discussionText = extras.getString("text");
                likes = extras.getInt("likes");
                objectId = extras.getString("objectId");
            }
        }

        discussionEntry = discussionManager.findDiscussion(objectId);

        discussionTextView = (TextView) findViewById(R.id.discussionTextView);
        likesTextView = (TextView) findViewById(R.id.likesText);
        usernameTextView = (TextView) findViewById(R.id.username);
        dateTextView = (TextView) findViewById(R.id.dateText);

        upVoteButton =  (TextView) findViewById(R.id.upVoteButton);
        downVoteButton =  (TextView) findViewById(R.id.downVoteButton);

        updateLikesView();

        upVoteButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (discussionManager.addLike(1, discussionEntry)) {
                            updateLikesView();
                            discussionManager.notifyDiscussionItem(discussionManager.getIndex(discussionEntry));
                        }
                    }
                }
        );

        downVoteButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (discussionManager.addLike(-1, discussionEntry)) {
                            updateLikesView();
                            discussionManager.notifyDiscussionItem(discussionManager.getIndex(discussionEntry));
                        }
                    }
                }
        );

        discussionTextView.setText(discussionText);
        likesTextView.setText(Integer.toString(likes));

        usernameTextView.setText(discussionEntry.getUsername());
        dateTextView.setText(DateHandler.dateToString(discussionEntry.getCreatedDate()));

        webViewButton = (LinearLayout) findViewById(R.id.webViewBtn);
        commentsViewButton = (LinearLayout) findViewById(R.id.commentsBtn);

        toolbar = (Toolbar) findViewById(R.id.toolbar_news);
        toolbar.hideOverflowMenu();
        toolbar.setPadding(0, 60, 0, 0);
        setSupportActionBar(toolbar);

        backButton = (LinearLayout) toolbar.findViewById(R.id.back_button);
        backButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        onBackPressed();
                    }
                }
        );


        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment commentsFragment = CommentsFragment.newInstance(objectId, CommentsFragment.COMMENTS_DISCUSSION);

        toolbar.findViewById(R.id.webViewBtn).setVisibility(View.GONE);
        toolbar.findViewById(R.id.commentsBtn).setVisibility(View.GONE);

        ft.replace(R.id.containerNews, commentsFragment, "newsFragment");
        ft.commit();
    }

    private void updateLikesView(){
        likesTextView.setText(Integer.toString(discussionEntry.getLikes()));
        if (discussionEntry.isLiked() == 1) {
            downVoteButton.setAlpha(0.3f);
            upVoteButton.setAlpha(1.0f);
        } else if (discussionEntry.isLiked() == -1){
            upVoteButton.setAlpha(0.3f);
            downVoteButton.setAlpha(1.0f);
        }
    }


}
