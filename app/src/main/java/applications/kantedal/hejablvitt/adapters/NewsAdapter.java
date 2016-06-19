package applications.kantedal.hejablvitt.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import applications.kantedal.hejablvitt.BlavittApplication;
import applications.kantedal.hejablvitt.activities.MainActivity;
import applications.kantedal.hejablvitt.fragments.CommentsFragment;
import applications.kantedal.hejablvitt.fragments.NewsFragment;
import applications.kantedal.hejablvitt.fragments.NewsItemFragment;
import applications.kantedal.hejablvitt.manager.NewsManager;
import applications.kantedal.hejablvitt.models.News;
import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.util.DateHandler;

/**
 * Created by filles-dator on 2015-06-20.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>
{
    private NewsManager newsManager;
    private List<News> newsDataSet;

    private Context mContext;
    private Fragment parentFragment;



    public NewsAdapter(List<News> newsDataSet, Fragment parentFragment) {
        this.newsDataSet = newsDataSet;
        this.parentFragment = parentFragment;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        newsManager = ((BlavittApplication) parentFragment.getActivity().getApplication()).getNewsManager();

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.news_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {

        final News news = newsDataSet.get(i);
        final ViewHolder newsViewHolder = viewHolder;

        if (android.os.Build.VERSION.SDK_INT >= 21)
            newsViewHolder.newsContainer.setTransitionName("transitionNews"+i);

        newsViewHolder.titleTextView.setText(news.getTitle());
        newsViewHolder.contentSnippetTextView.setText(news.getContentSnippet());

        newsViewHolder.dateTextView.setText(DateHandler.dateToString(news.getPublishedDate()));
        newsViewHolder.sourceTextView.setText(news.getSource());

        newsViewHolder.likesTextView.setText(Integer.toString(news.getLikes()));

        if ((i + 1) % 2 == 0) {
            newsViewHolder.newsContainer.setBackgroundColor(0x22FFFFFF);
        } else
            newsViewHolder.newsContainer.setBackgroundColor(Color.TRANSPARENT);

        newsViewHolder.clickNewsContainer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    parentFragment.setSharedElementReturnTransition(TransitionInflater.from(parentFragment.getActivity()).inflateTransition(R.transition.change_image_transform));
                    parentFragment.setExitTransition(TransitionInflater.from(parentFragment.getActivity()).inflateTransition(android.R.transition.fade));

                    NewsItemFragment newsItemFragment = NewsItemFragment.newInstance(news.getObjectId(), i);
                    newsItemFragment.setSharedElementEnterTransition(TransitionInflater.from(parentFragment.getActivity()).inflateTransition(R.transition.change_image_transform));
                    newsItemFragment.setEnterTransition(TransitionInflater.from(parentFragment.getActivity()).inflateTransition(android.R.transition.fade));

                    FragmentManager fm = parentFragment.getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_slide_out_bottom);
                    ft.replace(R.id.content_frame, newsItemFragment);
                    ft.addToBackStack(null);
                    ft.addSharedElement(newsViewHolder.newsContainer, newsViewHolder.newsContainer.getTransitionName());
                    ft.commit();
                } else {
                    NewsItemFragment newsItemFragment = NewsItemFragment.newInstance(news.getObjectId(), i);

                    FragmentManager fm = parentFragment.getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_slide_out_bottom);
                    ft.replace(R.id.content_frame, newsItemFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }

                news.increment("views");
                news.saveInBackground();
            }
        });
        
        newsViewHolder.upVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newsManager.addLike(1, news))
                    updateLikesView(news, newsViewHolder);
            }
        });

        newsViewHolder.downVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newsManager.addLike(-1, news))
                    updateLikesView(news, newsViewHolder);
            }
        });

        if (news.isLiked() == 1) {
            newsViewHolder.downVoteButton.setAlpha(0.3f);
            newsViewHolder.upVoteButton.setAlpha(1.0f);
        } else if (news.isLiked() == -1){
            newsViewHolder.upVoteButton.setAlpha(0.3f);
            newsViewHolder.downVoteButton.setAlpha(1.0f);
        }else{
            newsViewHolder.downVoteButton.setAlpha(1.0f);
            newsViewHolder.upVoteButton.setAlpha(1.0f);
        }

    }

    private void updateLikesView(News news, ViewHolder newsViewHolder){
        newsViewHolder.likesTextView.setText(Integer.toString(news.getLikes()));
        if (news.isLiked() == 1) {
            newsViewHolder.downVoteButton.setAlpha(0.3f);
            newsViewHolder.upVoteButton.setAlpha(1.0f);
        } else if (news.isLiked() == -1){
            newsViewHolder.upVoteButton.setAlpha(0.3f);
            newsViewHolder.downVoteButton.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount(){
        return newsDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        protected TextView contentSnippetTextView;
        protected TextView titleTextView;
        protected TextView dateTextView;
        protected TextView sourceTextView;

        protected TextView commentsIcon;
        protected TextView commentsCountText;

        protected LinearLayout newsContainer;
        protected LinearLayout clickNewsContainer;
        protected LinearLayout commentsButton;

        protected TextView upVoteButton;
        protected TextView likesTextView;
        protected TextView downVoteButton;

        public ViewHolder(View newsView) {
            super(newsView);

            titleTextView = (TextView) newsView.findViewById(R.id.titleTextView);
            contentSnippetTextView = (TextView) newsView.findViewById(R.id.contentSnippetTextView);
            dateTextView = (TextView) newsView.findViewById(R.id.dateText);
            sourceTextView = (TextView) newsView.findViewById(R.id.sourceText);

            upVoteButton = (TextView) newsView.findViewById(R.id.upVoteButton);
            likesTextView = (TextView) newsView.findViewById(R.id.likesText);
            downVoteButton = (TextView) newsView.findViewById(R.id.downVoteButton);

            newsContainer = (LinearLayout) newsView.findViewById(R.id.newsContainer);
            clickNewsContainer = (LinearLayout) newsView.findViewById(R.id.newsClickContainer);
        }
    }


}


