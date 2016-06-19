package applications.kantedal.hejablvitt.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;

import applications.kantedal.hejablvitt.fragments.DiscussionFragment;
import applications.kantedal.hejablvitt.fragments.NewsFragment;
import applications.kantedal.hejablvitt.fragments.TeamFragment;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[];
    int NumbOfTabs;

    private NewsFragment newsFragment;
    private DiscussionFragment discussionFragment;
    private TeamFragment teamFragment;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

        newsFragment = NewsFragment.newInstance();
        discussionFragment = DiscussionFragment.newInstance();
        teamFragment = new TeamFragment();
    }

    public NewsFragment getNewsTab(){
        return newsFragment;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        if(position == 0) // if the position is 0 we are returning the First tab
        {
            return newsFragment;
        }
        else if(position == 1)
        {
            return teamFragment;
        }
        else
        {
            return discussionFragment;
        }
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return 3;
    }
}