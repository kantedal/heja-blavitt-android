package applications.kantedal.hejablvitt.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.andexert.library.RippleView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import applications.kantedal.hejablvitt.BlavittApplication;
import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.activities.DiscussionActivity;
import applications.kantedal.hejablvitt.adapters.DiscussionAdapter;
import applications.kantedal.hejablvitt.adapters.NewsAdapter;
import applications.kantedal.hejablvitt.dialogs.ListPickerDialog;
import applications.kantedal.hejablvitt.manager.DiscussionManager;
import applications.kantedal.hejablvitt.manager.NewsManager;
import applications.kantedal.hejablvitt.models.DiscussionEntry;

/**
 * Created by filles-dator on 2015-06-09.
 */
public class DiscussionFragment extends Fragment implements DiscussionManager.DiscussionListener{

    protected BlavittApplication mApp;
    protected DiscussionManager discussionManager;

    private RecyclerView discussionRecycler;
    private DiscussionAdapter discussionAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearLayout discussionListLayout;
    private LinearLayout addDiscussionEntryField;
    private CardView addDiscussionEntryButton;
    private EditText discussionTextField;
    private RippleView closeDiscussionButton;
    private RippleView postDiscussionEntryButton;

    private boolean mShowDiscussionField = false;

    protected List<DiscussionEntry> discussionDataSet;

    public DiscussionFragment(){
        discussionDataSet = new ArrayList<DiscussionEntry>();
    }

    public static DiscussionFragment newInstance(){
        DiscussionFragment fragment = new DiscussionFragment();

        return fragment;
    }

    public void OnDiscussionItemEvent(int index, int event){
        switch (event){
            case DiscussionManager.DISCUSSION_ITEM_UPDATED:
                mSwipeRefreshLayout.setRefreshing(false);
                discussionAdapter.notifyItemChanged(index);
                break;
            case DiscussionManager.DISCUSSION_IS_UPDATING:
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
                break;
        }
    }

    public void OnDiscussionEvent(int event){
        switch (event){
            case DiscussionManager.DISCUSSION_ADDED:
                mSwipeRefreshLayout.setRefreshing(false);
                break;
            case DiscussionManager.DISCUSSION_UPDATED:
                mSwipeRefreshLayout.setRefreshing(false);
                discussionDataSet.clear();
                discussionAdapter.notifyDataSetChanged();
                break;
            case DiscussionManager.DISCUSSION_QUERY_FAILED:
                break;
            case DiscussionManager.DISCUSSION_IS_UPDATING:
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApp = (BlavittApplication) getActivity().getApplication();
        discussionManager = mApp.getDiscussionManager();

        discussionDataSet = new ArrayList<DiscussionEntry>();
    }

    private void setShowCommentField(boolean isVisible, int duration){
        mShowDiscussionField = isVisible;
        addDiscussionEntryButton.setClickable(!isVisible);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) discussionListLayout.getLayoutParams();
        params.bottomMargin = (isVisible ? 150 : 0);
        discussionListLayout.setLayoutParams(params);

        final int fieldStart = (isVisible ? 160 : 0);
        final int fieldEnd = (isVisible ? 0 : 160);

        final int buttonStart = (isVisible ? 0 : 210);
        final int buttonEnd = (isVisible ? 210 : 0);

        TranslateAnimation fieldAnim = new TranslateAnimation( 0, 0, fieldStart, fieldEnd );
        fieldAnim.setDuration(duration);
        fieldAnim.setFillAfter(true);

        TranslateAnimation buttonAnim = new TranslateAnimation( 0, 0, buttonStart, buttonEnd );
        buttonAnim.setDuration(duration);
        buttonAnim.setFillAfter(true);

        buttonAnim.setAnimationListener(new TranslateAnimation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation arg0) {
                if(mShowDiscussionField){
                    discussionTextField.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    imm.showSoftInput(discussionTextField, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        addDiscussionEntryField.startAnimation(fieldAnim);
        addDiscussionEntryButton.startAnimation(buttonAnim);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_discussion, container, false);

        discussionListLayout = (LinearLayout) rootView.findViewById(R.id.discussionListLayout);
        addDiscussionEntryButton = (CardView) rootView.findViewById(R.id.addDiscussionEntryButton);
        addDiscussionEntryField = (LinearLayout) rootView.findViewById(R.id.addDiscussionEntryField);
        closeDiscussionButton = (RippleView) rootView.findViewById(R.id.close_discussion_field);
        discussionTextField = (EditText) rootView.findViewById(R.id.discussionTextField);
        postDiscussionEntryButton = (RippleView) rootView.findViewById(R.id.postDiscussionEntryButton);

        setShowCommentField(false, 0);

        addDiscussionEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setShowCommentField(true, 300);
            }
        });

        closeDiscussionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(discussionTextField.getWindowToken(), 0);

                discussionTextField.setText("");
                setShowCommentField(false, 300);
            }
        });

        discussionTextField.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if(discussionTextField.getText().toString() != "") {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(discussionTextField.getWindowToken(), 0);

                        discussionManager.addDiscussionEntry(discussionTextField.getText().toString());

                        discussionTextField.setText("");
                        setShowCommentField(false, 300);
                    }
                    return true;
                }
                return false;
            }
        });

        postDiscussionEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(discussionTextField.getText().toString() != "") {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(discussionTextField.getWindowToken(), 0);

                    discussionManager.addDiscussionEntry(discussionTextField.getText().toString());

                    discussionTextField.setText("");
                    setShowCommentField(false, 300);
                }
            }
        });

        discussionRecycler = (RecyclerView) rootView.findViewById(R.id.feedRecyclerView);
        discussionRecycler.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        discussionRecycler.setLayoutManager(layoutManager);
        discussionAdapter = new DiscussionAdapter(mApp.getDiscussionManager().getDiscussionDataSet(), this, false);
        discussionRecycler.setAdapter(discussionAdapter);

        RecyclerView.ItemAnimator animator = discussionRecycler.getItemAnimator();
        animator.setAddDuration(1000);
        animator.setRemoveDuration(1000);
        discussionRecycler.setItemAnimator(animator);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mApp.getDiscussionManager().queryDiscussion(NewsManager.SORT_PUBLISHED_DATE, false);
            }
        });

        mApp.getDiscussionManager().subscribeDiscussionListener(this);

        return rootView;
    }


}
