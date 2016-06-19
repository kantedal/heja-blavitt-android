package applications.kantedal.hejablvitt.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
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
import applications.kantedal.hejablvitt.fragments.DiscussionEntryFragment;
import applications.kantedal.hejablvitt.fragments.NewsItemFragment;
import applications.kantedal.hejablvitt.models.Comment;
import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.models.DiscussionEntry;
import applications.kantedal.hejablvitt.models.LeagueRound;
import applications.kantedal.hejablvitt.models.News;

/**
 * Created by filles-dator on 2015-06-20.
 */
public class MyCommentsAdapter extends RecyclerView.Adapter<MyCommentsAdapter.ViewHolder>
{
    private BlavittApplication mApp;

    private List<CommentItem> commentsDataSet;
    private Context mContext;
    private Fragment mParentFragment;

    private List<LikedComment> likedComment;
    private String newsId;

    public MyCommentsAdapter(Fragment parentFragment, String newsId) {
        this.mApp = (BlavittApplication) parentFragment.getActivity().getApplication();
        this.mParentFragment = parentFragment;
        this.newsId = newsId;

        commentsDataSet = new ArrayList<>();
        likedComment = new ArrayList<LikedComment>();

        JSONArray likedCommentsTemp = ParseUser.getCurrentUser().getJSONArray("likedComment");
        for (int j = 0; j < likedCommentsTemp.length(); j++) {
            try {
                JSONArray jsonArr = (JSONArray) likedCommentsTemp.get(j);
                likedComment.add(new LikedComment(jsonArr.get(0).toString(), (Integer) jsonArr.get(1)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.view_my_comment_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        final CommentItem commentItem = commentsDataSet.get(i);
        final ViewHolder commentViewHolder = viewHolder;

        commentViewHolder.commentTextView.setText(commentItem.comment.getComment());

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        dateFormatter.setTimeZone(TimeZone.getTimeZone("CEST"));

        Date d = commentItem.comment.getCreatedDate();
        Long time = d.getTime();
        time +=(2*60*60*1000);
        Date commentDate = new Date(time);

        commentViewHolder.dateTextView.setText(dateFormatter.format(commentDate));
        commentViewHolder.usernameTextView.setText(commentItem.comment.getUsername());

        commentViewHolder.likesTextView.setText(Integer.toString(commentItem.comment.getLikes()));

        if (i % 2 == 0) {
            commentViewHolder.commentContainer.setBackgroundColor(0x22FFFFFF);
        } else
            commentViewHolder.commentContainer.setBackgroundColor(Color.TRANSPARENT);

        commentViewHolder.upVoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLikes(commentViewHolder, 1, commentItem.comment);
            }
        });

        commentViewHolder.downVoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLikes(commentViewHolder, -1, commentItem.comment);
            }
        });

        if (commentItem.comment.isLiked() == 1) {
            commentViewHolder.downVoteBtn.setAlpha(0.3f);
            commentViewHolder.upVoteBtn.setAlpha(1.0f);
        } else if (commentItem.comment.isLiked() == -1){
            commentViewHolder.upVoteBtn.setAlpha(0.3f);
            commentViewHolder.downVoteBtn.setAlpha(1.0f);
        }else{
            commentViewHolder.downVoteBtn.setAlpha(1.0f);
            commentViewHolder.upVoteBtn.setAlpha(1.0f);
        }

        if(commentItem.news != null){
            commentViewHolder.headerTextView.setText("Svar till nyhet: " + commentItem.news.getTitle());
            commentViewHolder.commentContainer.setTransitionName("transitionNews" + i);
            commentViewHolder.commentContainer.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mParentFragment.setSharedElementReturnTransition(TransitionInflater.from(mParentFragment.getActivity()).inflateTransition(R.transition.change_image_transform));
                        mParentFragment.setExitTransition(TransitionInflater.from(mParentFragment.getActivity()).inflateTransition(android.R.transition.fade));

                        NewsItemFragment newsItemFragment = NewsItemFragment.newInstance(commentItem.news.getObjectId(), i);
                        newsItemFragment.setSharedElementEnterTransition(TransitionInflater.from(mParentFragment.getActivity()).inflateTransition(R.transition.change_image_transform));
                        newsItemFragment.setEnterTransition(TransitionInflater.from(mParentFragment.getActivity()).inflateTransition(android.R.transition.fade));

                        FragmentManager fm = mParentFragment.getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_slide_out_bottom);
                        ft.replace(R.id.content_frame, newsItemFragment);
                        ft.addToBackStack(null);
                        ft.addSharedElement(commentViewHolder.commentContainer, commentViewHolder.commentContainer.getTransitionName());
                        ft.commit();
                    }

                    commentItem.news.increment("views");
                    commentItem.news.saveInBackground();
                }
            });
        }else if(commentItem.discussionEntry != null){
            commentViewHolder.discussionEntry = commentItem.discussionEntry;

            commentViewHolder.headerTextView.setText("Svar till diskussionsinlägg: " + commentItem.discussionEntry.getText());

            if (android.os.Build.VERSION.SDK_INT >= 21)
                commentViewHolder.commentContainer.setTransitionName("transitionDiscussion" + i);

            commentViewHolder.commentContainer.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mParentFragment.setSharedElementReturnTransition(TransitionInflater.from(mParentFragment.getActivity()).inflateTransition(R.transition.change_image_transform));
                        mParentFragment.setExitTransition(TransitionInflater.from(mParentFragment.getActivity()).inflateTransition(android.R.transition.fade));

                        DiscussionEntryFragment discussionEntryFragment = DiscussionEntryFragment.newInstance(commentItem.discussionEntry.getObjectId(), i);
                        discussionEntryFragment.setSharedElementEnterTransition(TransitionInflater.from(mParentFragment.getActivity()).inflateTransition(R.transition.change_image_transform));
                        discussionEntryFragment.setEnterTransition(TransitionInflater.from(mParentFragment.getActivity()).inflateTransition(android.R.transition.fade));

                        FragmentManager fm = mParentFragment.getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_slide_out_bottom);
                        ft.replace(R.id.content_frame, discussionEntryFragment);
                        ft.addToBackStack(null);

                        ft.addSharedElement(commentViewHolder.commentContainer, commentViewHolder.commentContainer.getTransitionName());
                        ft.commit();
                    }

                    commentItem.discussionEntry.increment("views");
                    commentItem.discussionEntry.saveInBackground();
                }
            });
        }
    }

    @Override
    public int getItemCount(){
        return commentsDataSet.size();
    }

    public void generateCommentData(List<Comment> commentData){
        commentsDataSet.clear();
        for(int i=0; i<commentData.size(); i++){
            final CommentItem newCommentItem = new CommentItem();
            final int count = i;
            newCommentItem.comment = commentData.get(i);

            if(newCommentItem.comment.getNewsId() != null){
                Log.d("Log", newCommentItem.comment.getNewsId() + "  " + (mApp.getNewsManager().getNewsById(newCommentItem.comment.getNewsId()) == null));
                newCommentItem.news = mApp.getNewsManager().getNewsById(newCommentItem.comment.getNewsId());
                if(newCommentItem.news == null){
                    ParseQuery<News> newsQuery = ParseQuery.getQuery("News");
                    newsQuery.whereEqualTo("objectId", newCommentItem.comment.getNewsId());
                    newsQuery.getFirstInBackground(new GetCallback<News>() {
                        @Override
                        public void done(News news, ParseException e) {
                            newCommentItem.news = news;
                            mApp.getNewsManager().getNewsDataSet().add(newCommentItem.news);
                            notifyItemChanged(count);
                        }
                    });
                }
            }else{
                newCommentItem.discussionEntry = mApp.getDiscussionManager().getDiscussionById(newCommentItem.comment.getDiscussionId());
                if(newCommentItem.discussionEntry == null){
                    ParseQuery<DiscussionEntry> discussionQuery = ParseQuery.getQuery("DiscussionEntry");
                    discussionQuery.whereEqualTo("objectId", newCommentItem.comment.getDiscussionId());
                    discussionQuery.getFirstInBackground(new GetCallback<DiscussionEntry>() {
                        @Override
                        public void done(DiscussionEntry discussionEntry, ParseException e) {
                            newCommentItem.discussionEntry = discussionEntry;
                            notifyItemChanged(count);
                        }
                    });
                }
            }

            commentsDataSet.add(newCommentItem);
        }
        notifyDataSetChanged();
    }

    private class CommentItem {
        public Comment comment;
        public News news;
        public DiscussionEntry discussionEntry;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        protected News newsItem;
        protected DiscussionEntry discussionEntry;

        protected TextView commentTextView;
        protected TextView dateTextView;
        protected TextView usernameTextView;
        protected TextView headerTextView;

        protected LinearLayout commentContainer;

        protected TextView upVoteBtn;
        protected TextView likesTextView;
        protected TextView downVoteBtn;

        public ViewHolder(View commentView) {
            super(commentView);

            commentContainer = (LinearLayout) commentView.findViewById(R.id.commentContainer);

            commentTextView = (TextView) commentView.findViewById(R.id.commentTextView);
            dateTextView = (TextView) commentView.findViewById(R.id.dateText);
            usernameTextView = (TextView) commentView.findViewById(R.id.username);
            headerTextView = (TextView) commentView.findViewById(R.id.my_comment_header);

            upVoteBtn = (TextView) commentView.findViewById(R.id.upVoteButton);
            likesTextView = (TextView) commentView.findViewById(R.id.likesText);
            downVoteBtn = (TextView) commentView.findViewById(R.id.downVoteButton);

            Typeface font = Typeface.createFromAsset(commentView.getContext().getAssets(), "icon_font_material.ttf");
            upVoteBtn.setTypeface(font);
            downVoteBtn.setTypeface(font);
        }
    }

    private class LikedComment{
        protected String objectId;
        protected int like;

        public LikedComment(String objectId, int like){
            this.objectId = objectId;
            this.like = like;
        }
    }

    private JSONArray toJSONArray(){
        JSONArray newLikedComment = new JSONArray();
        for(LikedComment tempLikedComment : likedComment){
            JSONArray json = new JSONArray();
            json.put(tempLikedComment.objectId);
            json.put(tempLikedComment.like);
            newLikedComment.put(json);
        }
        return newLikedComment;
    }

    private void updateLikes(ViewHolder viewHolder, int like, Comment comment){
        if(like==1 && comment.isLiked()==-1 || like==-1 && comment.isLiked()==1 || comment.isLiked()==0) {

            if(comment.isLiked() == 0) {
                comment.addLike(like);
                comment.setLiked(like);
                likedComment.add(new LikedComment(comment.getObjectId(), like));
            }else{
                comment.addLike(like * 2);
                comment.setLiked(like);
                for(LikedComment likedCommentTemp : likedComment){
                    if(likedCommentTemp.objectId.equals(comment.getObjectId())) {
                        likedCommentTemp.like = like;
                        break;
                    }
                }
            }

            viewHolder.likesTextView.setText(Integer.toString(comment.getLikes()));
            comment.saveInBackground();

            ParseUser.getCurrentUser().put("likedComment", toJSONArray());
            ParseUser.getCurrentUser().saveInBackground();

            if (like == 1){
                viewHolder.downVoteBtn.setAlpha(0.3f);
                viewHolder.upVoteBtn.setAlpha(1.0f);
            }else {
                viewHolder.upVoteBtn.setAlpha(0.3f);
                viewHolder.downVoteBtn.setAlpha(1.0f);
            }

        }
    }
}


