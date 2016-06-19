package applications.kantedal.hejablvitt.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.activities.MainActivity;

/**
 * Created by filles-dator on 2015-07-10.
 */
public class CustomToolbar extends Toolbar {

    public static final int BUTTON_COMMENTS = R.id.commentsButton;
    public static final int BUTTON_NEWS = R.id.newsButton;
    public static final int BUTTON_SORT = R.id.sortButton;

    private int mButtonSelection;

    Context mContext;
    FrameLayout mActionButton;

    private AlphaAnimation alphaAnimShow;
    private AlphaAnimation alphaAnimHide;

    private AlphaAnimation alphaToggleHide;
    private AlphaAnimation alphaToggleShow;

    public FrameLayout getActionButton() {
        return mActionButton;
    }

    public interface ToolbarListener {
        public int onToolbarOptionSelected();
    }

    public CustomToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, R.layout.tool_bar);
        mContext = context;

        alphaAnimShow = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimShow.setFillAfter(true);
        alphaAnimShow.setDuration(500);

        alphaAnimHide = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimHide.setFillAfter(true);
        alphaAnimHide.setDuration(500);

        alphaToggleShow = new AlphaAnimation(0.0f, 1.0f);
        alphaToggleShow.setFillAfter(true);
        alphaToggleShow.setDuration(0);

        alphaToggleHide = new AlphaAnimation(1.0f, 0.0f);
        alphaToggleHide.setFillAfter(true);
        alphaToggleHide.setDuration(0);
    }

    public void setupButtons(){
        mActionButton = (FrameLayout) this.findViewById(R.id.action_button);
        mActionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mContext).sortList(0);
            }
        });

        toggleVisibility(BUTTON_SORT, true);
        toggleVisibility(BUTTON_COMMENTS, false);
        toggleVisibility(BUTTON_NEWS, false);

        mButtonSelection = R.id.sortButton;
    }

    public void showButton(int buttonType) {

        Log.d("Log", "BUTTON_COMMENTS " + BUTTON_COMMENTS + "    BUTTON_SORT: " + BUTTON_SORT + "  BUTTON NEWS: " + BUTTON_NEWS + "   CURRENT " + mButtonSelection);

        if(buttonType != mButtonSelection) {
            animateShowButton(buttonType);
            animateHideButton(mButtonSelection);
            mButtonSelection = buttonType;
        }
    }

    private void animateShowButton(int buttonId){
        MaterialFont button = (MaterialFont) this.findViewById(buttonId);
        button.startAnimation(alphaAnimShow);
    }

    private void animateHideButton(int buttonId) {
        if(buttonId == BUTTON_NEWS)
            Log.d("LOg", "NUTTON NEWS");

        MaterialFont button = (MaterialFont) this.findViewById(buttonId);
        button.startAnimation(alphaAnimHide);
    }

    private void toggleVisibility(int buttonId, boolean isVisible){
        if(isVisible)
            this.findViewById(buttonId).startAnimation(alphaToggleShow);
        else
            this.findViewById(buttonId).startAnimation(alphaToggleHide);
    }

}
