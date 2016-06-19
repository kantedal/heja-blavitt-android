package applications.kantedal.hejablvitt.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import applications.kantedal.hejablvitt.models.Comment;
import applications.kantedal.hejablvitt.R;

/**
 * Created by filles-dator on 2015-06-20.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>
{
    private List<Comment> commentsDataSet;
    private Context mContext;
    private Activity parentActivity;

    private List<LikedComment> likedComment;
    private String newsId;

    public CommentsAdapter(List<Comment> commentsDataSet, Activity parentActivity, String newsId) {
        this.commentsDataSet = commentsDataSet;
        this.parentActivity = parentActivity;
        this.newsId = newsId;

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

    public void dataChanged() {
        for(Comment commentTemp : commentsDataSet){
            for(LikedComment likedCommentTemp : likedComment) {
                if (commentTemp.getObjectId().equals(likedCommentTemp.objectId)) {
                    commentTemp.setLiked(likedCommentTemp.like);
                    break;
                }
            }
        }
        super.notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.view_comment_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final Comment comment = commentsDataSet.get(i);
        final ViewHolder commentViewHolder = viewHolder;

        commentViewHolder.commentTextView.setText(comment.getComment());

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        dateFormatter.setTimeZone(TimeZone.getTimeZone("CEST"));

        Date d = comment.getCreatedDate();
        Long time = d.getTime();
        time +=(2*60*60*1000);
        Date commentDate = new Date(time);

        commentViewHolder.dateTextView.setText(dateFormatter.format(commentDate));
        commentViewHolder.usernameTextView.setText(comment.getUsername());

        commentViewHolder.likesTextView.setText(Integer.toString(comment.getLikes()));

        if (i % 2 == 0) {
            commentViewHolder.commentContainer.setBackgroundColor(0x22FFFFFF);
        } else
            commentViewHolder.commentContainer.setBackgroundColor(Color.TRANSPARENT);

        commentViewHolder.upVoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLikes(commentViewHolder, 1, comment);
            }
        });

        commentViewHolder.downVoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLikes(commentViewHolder, -1, comment);
            }
        });

        if (comment.isLiked() == 1) {
            commentViewHolder.downVoteBtn.setAlpha(0.3f);
            commentViewHolder.upVoteBtn.setAlpha(1.0f);
        } else if (comment.isLiked() == -1){
            commentViewHolder.upVoteBtn.setAlpha(0.3f);
            commentViewHolder.downVoteBtn.setAlpha(1.0f);
        }else{
            commentViewHolder.downVoteBtn.setAlpha(1.0f);
            commentViewHolder.upVoteBtn.setAlpha(1.0f);
        }

    }

    @Override
    public int getItemCount(){
        return commentsDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        protected TextView commentTextView;
        protected TextView dateTextView;
        protected TextView usernameTextView;

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


