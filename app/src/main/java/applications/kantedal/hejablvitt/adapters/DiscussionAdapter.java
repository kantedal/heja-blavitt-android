package applications.kantedal.hejablvitt.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import applications.kantedal.hejablvitt.BlavittApplication;
import applications.kantedal.hejablvitt.activities.DiscussionActivity;
import applications.kantedal.hejablvitt.fragments.DiscussionEntryFragment;
import applications.kantedal.hejablvitt.fragments.DiscussionFragment;
import applications.kantedal.hejablvitt.fragments.NewsItemFragment;
import applications.kantedal.hejablvitt.manager.DiscussionManager;
import applications.kantedal.hejablvitt.models.DiscussionEntry;
import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.models.News;

/**
 * Created by filles-dator on 2015-06-20.
 */
public class DiscussionAdapter extends RecyclerView.Adapter<DiscussionAdapter.ViewHolder>
{
    private DiscussionManager discussionManager;

    private List<DiscussionEntry> discussionDataSet;
    private Context mContext;
    private Fragment parentFragment;

    private List<LikedDiscussionEntry> likedDiscussionEntries;
    private boolean mShowMyComments = false;

    public DiscussionAdapter(List<DiscussionEntry> discussionDataSet, Fragment parentFragment, boolean showMyComments) {
        this.discussionDataSet = discussionDataSet;
        this.parentFragment = parentFragment;
        this.mShowMyComments = showMyComments;


        discussionManager = ((BlavittApplication) parentFragment.getActivity().getApplication()).getDiscussionManager();

        likedDiscussionEntries = new ArrayList<LikedDiscussionEntry>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.discussion_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        final DiscussionEntry discussionEntry = discussionDataSet.get(i);
        final ViewHolder discussionEntryViewHolder = viewHolder;

        if (android.os.Build.VERSION.SDK_INT >= 21)
            discussionEntryViewHolder.discussionContainer.setTransitionName("transitionDiscussion"+i);

        discussionEntryViewHolder.discussionEntryTextView.setText(discussionEntry.getText());

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        dateFormatter.setTimeZone(TimeZone.getTimeZone("CEST"));

        Date d = discussionEntry.getCreatedDate();
        Long time = d.getTime();
        time +=(2*60*60*1000);
        Date discussionEntryDate = new Date(time);

        discussionEntryViewHolder.dateTextView.setText(dateFormatter.format(discussionEntryDate));
        discussionEntryViewHolder.usernameTextView.setText(discussionEntry.getUsername());
        discussionEntryViewHolder.likesTextView.setText(Integer.toString(discussionEntry.getLikes()));

        if (i % 2 == 0) {
            discussionEntryViewHolder.discussionContainer.setBackgroundColor(0x22FFFFFF);
        } else
            discussionEntryViewHolder.discussionContainer.setBackgroundColor(Color.TRANSPARENT);

        discussionEntryViewHolder.upVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (discussionManager.addLike(1, discussionEntry))
                    updateLikesView(discussionEntry, discussionEntryViewHolder);
            }
        });

        discussionEntryViewHolder.downVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (discussionManager.addLike(-1, discussionEntry))
                    updateLikesView(discussionEntry, discussionEntryViewHolder);
            }
        });

        discussionEntryViewHolder.clickDiscussionContainer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    parentFragment.setSharedElementReturnTransition(TransitionInflater.from(parentFragment.getActivity()).inflateTransition(R.transition.change_image_transform));
                    parentFragment.setExitTransition(TransitionInflater.from(parentFragment.getActivity()).inflateTransition(android.R.transition.fade));

                    DiscussionEntryFragment discussionEntryFragment = DiscussionEntryFragment.newInstance(discussionEntry.getObjectId(), i);
                    discussionEntryFragment.setSharedElementEnterTransition(TransitionInflater.from(parentFragment.getActivity()).inflateTransition(R.transition.change_image_transform));
                    discussionEntryFragment.setEnterTransition(TransitionInflater.from(parentFragment.getActivity()).inflateTransition(android.R.transition.fade));

                    FragmentManager fm = parentFragment.getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_slide_out_bottom);
                    ft.replace(R.id.content_frame, discussionEntryFragment);
                    ft.addToBackStack(null);
                    ft.addSharedElement(discussionEntryViewHolder.discussionContainer, discussionEntryViewHolder.discussionContainer.getTransitionName());
                    ft.commit();
                }else{
                    DiscussionEntryFragment discussionEntryFragment = DiscussionEntryFragment.newInstance(discussionEntry.getObjectId(), i);

                    FragmentManager fm = parentFragment.getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_slide_out_bottom);
                    ft.replace(R.id.content_frame, discussionEntryFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }

                discussionEntry.increment("views");
                discussionEntry.saveInBackground();
            }
        });

        if (discussionEntry.isLiked() == 1) {
            discussionEntryViewHolder.downVoteButton.setAlpha(0.3f);
            discussionEntryViewHolder.upVoteButton.setAlpha(1.0f);
        } else if (discussionEntry.isLiked() == -1){
            discussionEntryViewHolder.upVoteButton.setAlpha(0.3f);
            discussionEntryViewHolder.downVoteButton.setAlpha(1.0f);
        }else{
            discussionEntryViewHolder.downVoteButton.setAlpha(1.0f);
            discussionEntryViewHolder.upVoteButton.setAlpha(1.0f);
        }

    }

    private void updateLikesView(DiscussionEntry discussionEntry, ViewHolder newsViewHolder){
        newsViewHolder.likesTextView.setText(Integer.toString(discussionEntry.getLikes()));
        if (discussionEntry.isLiked() == 1) {
            newsViewHolder.downVoteButton.setAlpha(0.3f);
            newsViewHolder.upVoteButton.setAlpha(1.0f);
        } else if (discussionEntry.isLiked() == -1){
            newsViewHolder.upVoteButton.setAlpha(0.3f);
            newsViewHolder.downVoteButton.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount(){
        return discussionDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        protected TextView discussionEntryTextView;
        protected TextView dateTextView;
        protected TextView usernameTextView;

        protected LinearLayout discussionContainer;
        protected FrameLayout clickDiscussionContainer;

        protected TextView upVoteButton;
        protected TextView likesTextView;
        protected TextView downVoteButton;

        public ViewHolder(View discussionEntryView) {
            super(discussionEntryView);

            clickDiscussionContainer = (FrameLayout) discussionEntryView.findViewById(R.id.clickDiscussionContainer);
            discussionContainer = (LinearLayout) discussionEntryView.findViewById(R.id.discussionContainer);

            discussionEntryTextView = (TextView) discussionEntryView.findViewById(R.id.discussionTextView);
            dateTextView = (TextView) discussionEntryView.findViewById(R.id.dateText);
            usernameTextView = (TextView) discussionEntryView.findViewById(R.id.username);

            upVoteButton = (TextView) discussionEntryView.findViewById(R.id.upVoteButton);
            likesTextView = (TextView) discussionEntryView.findViewById(R.id.likesText);
            downVoteButton = (TextView) discussionEntryView.findViewById(R.id.downVoteButton);
        }
    }

    private class LikedDiscussionEntry{
        protected String objectId;
        protected int like;

        public LikedDiscussionEntry(String objectId, int like){
            this.objectId = objectId;
            this.like = like;
        }
    }
}


