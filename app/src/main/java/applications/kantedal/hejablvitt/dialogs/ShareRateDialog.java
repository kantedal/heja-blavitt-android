package applications.kantedal.hejablvitt.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.listeners.OnPublishListener;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.ArrayList;

import applications.kantedal.hejablvitt.BlavittApplication;
import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.activities.MainActivity;
import applications.kantedal.hejablvitt.manager.SessionManager;

public class ShareRateDialog extends DialogFragment{

    private FrameLayout mShareButton;
    private FrameLayout mRateButton;
    private FrameLayout mNoButton;

    private SimpleFacebook mSimpleFacebook;


    private void showFacebookDialog(){
        OnPublishListener onPublishListener = new OnPublishListener() {
            @Override
            public void onComplete(String postId) {
                SharedPreferences sp = getActivity().getSharedPreferences(BlavittApplication.PREFS, getActivity().getApplicationContext().MODE_PRIVATE);
                SharedPreferences.Editor sharedEditor = sp.edit();
                sharedEditor.putBoolean("isRated", true);
                sharedEditor.commit();

                dismiss();
            }
        };

        Feed feed = new Feed.Builder()
                .setName("Heja Blåvitt")
                .setDescription("Nyheter, diskussioner och statistik om IFK Göteborg.")
                .setLink("https://play.google.com/store/apps/details?id=applications.kantedal.hejablvitt")
                .build();

        mSimpleFacebook = SimpleFacebook.getInstance(getActivity());
        mSimpleFacebook.publish(feed, true, onPublishListener);
    }

    private void showRate() {
        dismiss();

        SharedPreferences sp = getActivity().getSharedPreferences(BlavittApplication.PREFS, getActivity().getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sp.edit();
        sharedEditor.putBoolean("isRated", true);
        sharedEditor.commit();

        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            //Toast.makeText(getActivity().getBaseContext(), "Okänt fel.")
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.dialog_sharerate);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mShareButton = (FrameLayout) dialog.findViewById(R.id.dialog_sharerate_share);
        mRateButton = (FrameLayout) dialog.findViewById(R.id.dialog_sharerate_rate);
        mNoButton = (FrameLayout) dialog.findViewById(R.id.dialog_sharerate_no);

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showFacebookDialog();
            }
        });

        mRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRate();
            }
        });

        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return dialog;
    }
}