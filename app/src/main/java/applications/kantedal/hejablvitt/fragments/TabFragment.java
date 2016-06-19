package applications.kantedal.hejablvitt.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import applications.kantedal.hejablvitt.BlavittApplication;
import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.activities.MainActivity;
import applications.kantedal.hejablvitt.adapters.ViewPagerAdapter;
import applications.kantedal.hejablvitt.dialogs.ListPickerDialog;
import applications.kantedal.hejablvitt.view.SlidingTabLayout;
import applications.kantedal.hejablvitt.view.SlidingTabStrip;

/**
 * Created by filles-dator on 2015-07-29.
 */
public class TabFragment extends Fragment {
    protected BlavittApplication mApp;

    private Toolbar mToolbar;
    private ViewPager mPager;
    private ViewPagerAdapter adapter;
    private SlidingTabLayout mTabs;
    private CharSequence Titles[]={"Nyheter","Allsvenskan","Diskussion"};

    private int Numboftabs = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (BlavittApplication) getActivity().getApplication();
        adapter = new ViewPagerAdapter(getChildFragmentManager(), Titles, Numboftabs);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setup(view);
    }

    public int getCurrentItem(){
        return mPager.getCurrentItem();
    }

    private void setup(View rootView){

        // Assigning ViewPage View and setting the adapter
        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(2);
        mPager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        mTabs = (SlidingTabLayout) rootView.findViewById(R.id.tabs);
        //mTabs.setCustomTabView(R.layout.tab_layout, R.id.tabTitleView);
        mTabs.setDistributeEvenly(true);

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                switch (position) {
                    case 0:
                        int clr1 = Color.parseColor("#ffffff");
                        clr1 = SlidingTabStrip.setColorAlpha(clr1, (byte) 50);
                        return clr1;
                    case 1:
                        int clr2 = Color.parseColor("#ffffff");
                        clr2 = SlidingTabStrip.setColorAlpha(clr2, (byte) 50);
                        return clr2;
                    case 2:
                        int clr3 = Color.parseColor("#ffffff");
                        clr3 = SlidingTabStrip.setColorAlpha(clr3, (byte) 50);
                        return clr3;
                }
                return Color.parseColor("#bdbdbd");
            }
        });

//        TextView sortButton = (TextView) rootView.findViewById(R.id.sortButton);
//        final ListPickerDialog listPickerDialog = new ListPickerDialog();
//        listPickerDialog.setCallback(this);
//        sortButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                switch(mPager.getCurrentItem()) {
//                    case SCREEN_NEWS:
//                        listPickerDialog.setSortList(SCREEN_NEWS);
//                        listPickerDialog.show(getActivity().getSupportFragmentManager(), "sort_news");
//                        break;
//                    case SCREEN_DISCUSSION:
//                        listPickerDialog.setSortList(SCREEN_DISCUSSION);
//                        listPickerDialog.show(getActivity().getSupportFragmentManager(), "sort_discussion");
//                        break;
//                }
//            }
//        });

        // Setting the ViewPager For the SlidingTabsLayout
        mTabs.setViewPager(mPager);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("Log", "ON RESUME");
    }

}
