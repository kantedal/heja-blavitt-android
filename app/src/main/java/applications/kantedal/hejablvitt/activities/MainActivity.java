package applications.kantedal.hejablvitt.activities;


import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.listeners.OnPublishListener;

import java.util.List;

import applications.kantedal.hejablvitt.BlavittApplication;
import applications.kantedal.hejablvitt.adapters.DrawerListAdapter;
import applications.kantedal.hejablvitt.adapters.ViewPagerAdapter;
import applications.kantedal.hejablvitt.dialogs.ListPickerDialog;
import applications.kantedal.hejablvitt.dialogs.ShareRateDialog;
import applications.kantedal.hejablvitt.dialogs.UsernameDialog;
import applications.kantedal.hejablvitt.fragments.MyActivityFragment;
import applications.kantedal.hejablvitt.fragments.MyCommentsFragment;
import applications.kantedal.hejablvitt.fragments.MyDiscussionFragment;
import applications.kantedal.hejablvitt.fragments.NewsFragment;
import applications.kantedal.hejablvitt.fragments.NewsItemFragment;
import applications.kantedal.hejablvitt.fragments.NewsSourcesFragment;
import applications.kantedal.hejablvitt.fragments.TabFragment;
import applications.kantedal.hejablvitt.manager.NewsManager;
import applications.kantedal.hejablvitt.manager.SessionManager;
import applications.kantedal.hejablvitt.models.News;
import applications.kantedal.hejablvitt.view.CustomToolbar;
import applications.kantedal.hejablvitt.view.DividerItemDecoration;
import applications.kantedal.hejablvitt.view.SlidingTabLayout;
import applications.kantedal.hejablvitt.R;

public class MainActivity extends FragmentActivity
        implements SessionManager.SessionListener, ListPickerDialog.SortListCallback, DrawerListAdapter.OnItemClickListener {

    public static final int SCREEN_NEWS = 0;
    public static final int SCREEN_DISCUSSION = 1;
    public static final int SCREEN_TEAM = 2;

    protected BlavittApplication mApp;

    private TabFragment mTabFragment;

    private CustomToolbar mToolbar;

    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private DrawerListAdapter mDrawerListAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private SimpleFacebook mSimpleFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer_layout);


        if (savedInstanceState == null) {
            mApp = (BlavittApplication) getApplication();
            mApp.getSessionManager().subscribeSessionListener(this);


            if(!mApp.getSessionManager().isLoggedIn()) {
                SharedPreferences prefs = this.getSharedPreferences(BlavittApplication.PREFS, getApplicationContext().MODE_PRIVATE);
                String storedUsername = prefs.getString("username", null);
                String storedPassword = prefs.getString("password", null);

                if (storedUsername != null && storedPassword != null) {
                    mApp.getSessionManager().login(storedUsername, storedPassword);
                } else {
                    UsernameDialog usernameDialog = new UsernameDialog();
                    usernameDialog.show(getSupportFragmentManager(), "usernameDialog");
                }

                int logInCount = prefs.getInt("logInCount", 0)+1;
                boolean isRated = prefs.getBoolean("isRated", false);

                SharedPreferences.Editor sharedEditor = prefs.edit();
                sharedEditor.putInt("logInCount", logInCount);
                sharedEditor.commit();

                if(logInCount%10 == 0 && !isRated){
                    ShareRateDialog shareRateDialog = new ShareRateDialog();
                    shareRateDialog.show(getSupportFragmentManager(), "usernameDialog");
                }
            }

            mToolbar = (CustomToolbar) findViewById(R.id.tool_bar);
            mToolbar.hideOverflowMenu();
            if (android.os.Build.VERSION.SDK_INT >= 19)
                mToolbar.setPadding(0, 60, 0, 0);
            mToolbar.setupButtons();
            mToolbar.showButton(CustomToolbar.BUTTON_SORT);

            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerToggle = new ActionBarDrawerToggle(
                    this, mDrawerLayout, mToolbar,
                    R.string.abc_action_bar_home_description, R.string.abc_action_bar_home_description
            );
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDrawerLayout.isDrawerOpen(Gravity.LEFT))
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                    else if (mDrawerLayout.getDrawerLockMode(Gravity.LEFT) == DrawerLayout.LOCK_MODE_UNLOCKED)
                        mDrawerLayout.openDrawer(Gravity.LEFT);
                    else
                        onBackPressed();
                }
            });

            mDrawerList = (RecyclerView) findViewById(R.id.navigation_drawer_layout_list);
            //mDrawerList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
            mDrawerList.setLayoutManager(new LinearLayoutManager(this));

            mDrawerListAdapter = new DrawerListAdapter(this);
            mDrawerListAdapter.setOnItemClickListener(this);
            mDrawerList.setAdapter(mDrawerListAdapter);

            onItemClick(0);
        }
    }

    private void openClickedNews(String newsId){
        Log.d("Clicked news", "OPEN CLICKED NEWS " + newsId);

        ParseQuery<News> query = News.getQuery();
        query.whereEqualTo("objectId", newsId);
        query.getFirstInBackground(new GetCallback<News>() {
            public void done(News news, ParseException e) {
                if (news == null) {
                    Log.d("Log", "error");
                } else {
                    NewsItemFragment newsItemFragment = NewsItemFragment.newInstance(news.getObjectId(), 0);
                    newsItemFragment.setNews(news);

                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.setCustomAnimations(R.anim.abc_grow_fade_in_from_bottom, R.anim.abc_slide_out_bottom);
                    ft.replace(R.id.content_frame, newsItemFragment);
                    ft.addToBackStack(null);
                    ft.commit();

                    news.increment("views");
                    news.saveInBackground();
                }
            }
        });
    }

    public void sortList(int sortOption){
        switch(mTabFragment.getCurrentItem()) {
            case SCREEN_NEWS:
                mApp.getNewsManager().queryNews(sortOption);
                break;
            case SCREEN_DISCUSSION:
                mApp.getDiscussionManager().queryDiscussion(sortOption, false);
                break;
        }
    }

    public void OnSessionEvent(int event){
        switch (event){
            case SessionManager.SESSION_LOGIN_SUCCESS:
                mApp.getNewsManager().queryNews(NewsManager.SORT_PUBLISHED_DATE);
                mApp.getDiscussionManager().queryDiscussion(NewsManager.SORT_PUBLISHED_DATE, false);
                mApp.getLeagueManager().queryLeague();
                break;
            case SessionManager.SESSION_LOGIN_FAILED:
                Log.d("Log", "Logged out");
                break;
            case SessionManager.SESSION_IS_UPDATING:
                break;
        }
    }

    public CustomToolbar getToolbar(){
        return mToolbar;
    }


    private void lockDrawer(){
        if(mDrawerLayout.getDrawerLockMode(Gravity.LEFT) == DrawerLayout.LOCK_MODE_UNLOCKED) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float slideOffset = (Float) valueAnimator.getAnimatedValue();
                    mDrawerToggle.onDrawerSlide(mDrawerLayout, slideOffset);
                }
            });
            anim.setInterpolator(new DecelerateInterpolator());

            anim.setDuration(500);
            anim.start();
        }
    }

    private void unlockDrawer(){
        if(mDrawerLayout.getDrawerLockMode(Gravity.LEFT) == DrawerLayout.LOCK_MODE_LOCKED_CLOSED) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            ValueAnimator anim = ValueAnimator.ofFloat(1, 0);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float slideOffset = (Float) valueAnimator.getAnimatedValue();
                    mDrawerToggle.onDrawerSlide(mDrawerLayout, slideOffset);
                }
            });
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(800);
            anim.start();
        }
    }


    public static final int VIEW_HOME = 0;
    public static final int VIEW_DISCUSSION_ENTRY = 1;
    public static final int VIEW_COMMENTS = 2;
    public static final int VIEW_ACTIVITY = 3;
    public static final int VIEW_SET_NEWS_SOURCES = 4;

    public void onItemClick(int position) {
        Fragment newFragment = new Fragment();
        switch (position) {
            case VIEW_HOME:
               // if(mTabFragment == null){
                    mTabFragment = new TabFragment();
                    mTabFragment.setRetainInstance(true);
                //}
                newFragment = mTabFragment;
                break;
            case VIEW_DISCUSSION_ENTRY:
                newFragment = MyDiscussionFragment.newInstance();
                break;
            case VIEW_COMMENTS:
                newFragment = MyCommentsFragment.newInstance();
                break;
            case VIEW_ACTIVITY:
                newFragment = MyActivityFragment.newInstance();
                break;
            case VIEW_SET_NEWS_SOURCES:
                newFragment = NewsSourcesFragment.newInstance();
                break;
        }
        mDrawerListAdapter.notifyDataSetChanged();
        // Insert the fragment by replacing any existing fragment
        if(newFragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, newFragment)
                    .commit();
        }

        mDrawerLayout.closeDrawer(Gravity.LEFT);

    }

    @Override
    public void onAttachFragment(Fragment fragment){
        if(getSupportFragmentManager().getBackStackEntryCount() == 1)
            lockDrawer();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);

        if(mApp.getSessionManager().isLoggedIn() == true){
            Bundle extras = getIntent().getExtras();
            if(extras != null){
                openClickedNews(getIntent().getExtras().getString("widget_clicked_id", null));
            }
        }
        else
        {
            mApp.getSessionManager().subscribeSessionListener(new SessionManager.SessionListener() {
                @Override
                public void OnSessionEvent(int event) {
                    switch (event){
                        case SessionManager.SESSION_LOGIN_SUCCESS:
                            Bundle extras = getIntent().getExtras();
                            if (extras != null) {
                                openClickedNews(getIntent().getExtras().getString("widget_clicked_id", null));
                            }
                            break;
                    }
                }
            });
        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d("Log", "On destroy");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 1)
            unlockDrawer();

        mToolbar.showButton(CustomToolbar.BUTTON_SORT);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    
}
