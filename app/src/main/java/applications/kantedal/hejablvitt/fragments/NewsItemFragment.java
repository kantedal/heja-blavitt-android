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

import com.parse.CountCallback;
import com.parse.ParseQuery;

import applications.kantedal.hejablvitt.BlavittApplication;
import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.activities.MainActivity;
import applications.kantedal.hejablvitt.manager.NewsManager;
import applications.kantedal.hejablvitt.models.Comment;
import applications.kantedal.hejablvitt.models.News;
import applications.kantedal.hejablvitt.util.DateHandler;
import applications.kantedal.hejablvitt.view.CustomToolbar;

/**
 * Created by filles-dator on 2015-07-06.
 */
public class NewsItemFragment extends Fragment {

    private NewsManager newsManager;
    private News news;

    private final int WEB_VIEW = 0;
    private final int COMMENTS_VIEW = 1;

    private String title;
    private String contentSnippet;
    private int likes;
    private int isLiked;
    private String url;
    private String objectId;

    private TextView titleTextView;
    private TextView contentSnippetTextView;
    private TextView likesTextView;

    private TextView upVoteButton;
    private TextView downVoteButton;

    private TextView sourceTextView;
    private TextView dateTextView;

    private LinearLayout newsContainer;

    private CustomToolbar mToolbar;

    private int mFragmentPosition;
    private int mCurrentView = WEB_VIEW;

    public static NewsItemFragment newInstance(String objectId, int fragmentPosition) {
        NewsItemFragment myFragment = new NewsItemFragment();

        Bundle args = new Bundle();
        args.putString("objectId", objectId);
        args.putInt("fragmentPosition", fragmentPosition);
        myFragment.setArguments(args);

        return myFragment;
    }

    public void setNews(News news){
        this.news = news;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_news_item, container, false);

        objectId = getArguments().getString("objectId");
        mFragmentPosition = getArguments().getInt("fragmentPosition");
        if(news == null)
            news = newsManager.findNews(objectId);

        newsContainer = (LinearLayout) rootView.findViewById(R.id.newsContainer);

        if (android.os.Build.VERSION.SDK_INT >= 21)
            newsContainer.setTransitionName("transitionNews"+mFragmentPosition);

        titleTextView = (TextView) rootView.findViewById(R.id.titleTextView);
        contentSnippetTextView = (TextView) rootView.findViewById(R.id.contentSnippetTextView);
        likesTextView = (TextView) rootView.findViewById(R.id.likesText);

        upVoteButton =  (TextView) rootView.findViewById(R.id.upVoteButton);
        downVoteButton =  (TextView) rootView.findViewById(R.id.downVoteButton);
        sourceTextView = (TextView) rootView.findViewById(R.id.sourceText);
        dateTextView = (TextView) rootView.findViewById(R.id.dateText);

        updateLikesView();

        upVoteButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (newsManager.addLike(1, news)) {
                            updateLikesView();
                            newsManager.notifyNewsItem(newsManager.getIndex(news));
                        }
                    }
                }
        );

        downVoteButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (newsManager.addLike(-1, news)) {
                            updateLikesView();
                            newsManager.notifyNewsItem(newsManager.getIndex(news));
                        }
                    }
                }
        );


        titleTextView.setText(news.getTitle());
        contentSnippetTextView.setText(news.getContentSnippet());

        sourceTextView.setText(news.getSource());
        dateTextView.setText(DateHandler.dateToString(news.getPublishedDate()));

        mToolbar = ((MainActivity) getActivity()).getToolbar();
        mToolbar.showButton(CustomToolbar.BUTTON_COMMENTS);
        mToolbar.getActionButton().setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if(mCurrentView == WEB_VIEW) {
                            mToolbar.showButton(CustomToolbar.BUTTON_NEWS);
                            mCurrentView = COMMENTS_VIEW;
                            switchView(mCurrentView);
                        }else{
                            mToolbar.showButton(CustomToolbar.BUTTON_COMMENTS);
                            mCurrentView = WEB_VIEW;
                            switchView(mCurrentView);
                        }
                    }
                }
        );


        ParseQuery<Comment> query = ParseQuery.getQuery("Comments");
        query.whereEqualTo("newsId", objectId);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, com.parse.ParseException e) {
           //     ((TextView)toolbar.findViewById(R.id.commentsCount)).setText( Integer.toString(i) );
            }
        });


        switchView(WEB_VIEW);

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsManager = ((BlavittApplication) getActivity().getApplication()).getNewsManager();
    }

    private void updateLikesView(){
        Log.d("Log", news.getTitle());
        if(news.getLikes() != 0)
            likesTextView.setText(Integer.toString(news.getLikes()));
        else
            likesTextView.setText(Integer.toString(0));

        if (news.isLiked() == 1) {
            downVoteButton.setAlpha(0.3f);
            upVoteButton.setAlpha(1.0f);
        } else if (news.isLiked() == -1){
            upVoteButton.setAlpha(0.3f);
            downVoteButton.setAlpha(1.0f);
        }
    }

    private void switchView(int viewType)
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if(viewType == WEB_VIEW){
            WebViewFragment webViewFragment = NewsWebViewFragment.newInstance(news.getUrl());


            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            ft.replace(R.id.containerNews, webViewFragment, "webViewFragment");
            ft.commit();
        }
        else if(viewType == COMMENTS_VIEW){
            Fragment commentsFragment = CommentsFragment.newInstance(objectId, CommentsFragment.COMMENTS_NEWS);


            ft.replace(R.id.containerNews, commentsFragment, "newsFragment");
            ft.commit();
        }
    }


}
