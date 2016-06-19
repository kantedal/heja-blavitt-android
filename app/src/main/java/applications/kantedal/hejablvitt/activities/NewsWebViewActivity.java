package applications.kantedal.hejablvitt.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import applications.kantedal.hejablvitt.fragments.NewsWebViewFragment;
import applications.kantedal.hejablvitt.R;

/**
 * Created by filles-dator on 2015-06-21.
 */
public class NewsWebViewActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

        if(savedInstanceState == null){
            setContentView(R.layout.activity_webview);

            Typeface font = Typeface.createFromAsset(getAssets(), "icon_font_material.ttf");
            final Button exitButton = (Button)findViewById(R.id.exitButton);
            exitButton.setTypeface(font);

            TextView titleText = (TextView) findViewById(R.id.titleTextView);
            titleText.setText((String) getIntent().getExtras().get("title"));

            exitButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    exitButton.setTextColor(0xff000000);
                    finish();
                    overridePendingTransition(R.anim.staystill, R.anim.push_out_bottom);
                }
            });

            NewsWebViewFragment webViewFragment = new NewsWebViewFragment();
            webViewFragment.setArguments(getIntent().getExtras());


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // overridePendingTransition(R.anim.staystill, R.anim.push_out_bottom);
    }

}
