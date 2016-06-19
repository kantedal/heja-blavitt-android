package applications.kantedal.hejablvitt.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.ArrayList;

import applications.kantedal.hejablvitt.BlavittApplication;
import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.activities.MainActivity;
import applications.kantedal.hejablvitt.manager.SessionManager;

public class UsernameDialog extends DialogFragment implements SessionManager.SessionListener {
    private SessionManager sessionManager;

    private ProgressDialog progress;
    private Button mButton;
    private EditText mEditText;
    private TextView mErrorMessage;
    private TextView mErrorStar;

    private String mPassword;
    private String mUsername;


    private void createUser(){
        mErrorMessage.setVisibility(View.GONE);

        mPassword = new SessionIdentifierGenerator().nextSessionId();
        mUsername = mEditText.getText().toString();

        if(mUsername.length() > 3 && mUsername.length() < 15){
            progress.show();
            sessionManager.signUp(mUsername,mPassword);
        }else{
            mErrorMessage.setText("* Ditt användarnamn måste vara över 3 och mindre än 15 tecken");
            mErrorMessage.setVisibility(View.VISIBLE);
        }



    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sessionManager = ((BlavittApplication) getActivity().getApplication()).getSessionManager();
        sessionManager.subscribeSessionListener(this);

        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.dialog_login);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        progress = new ProgressDialog(getActivity());
        progress.setMessage("Skapar användare");

        mErrorMessage = (TextView) dialog.findViewById(R.id.error_message);
        mErrorMessage.setVisibility(View.GONE);

        mButton = (Button) dialog.findViewById(R.id.button1);
        mEditText = (EditText) dialog.findViewById(R.id.editText1);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createUser();

//                mListener.onUsernameSubmitListener(mEditText.getText().toString());
//                dismiss();
            }
        });
        return dialog;
    }

    @Override
    public void OnSessionEvent(int event) {
        switch (event) {
            case SessionManager.SESSION_CREATED_USER:
                progress.dismiss();
                SharedPreferences prefs = getActivity().getSharedPreferences(BlavittApplication.PREFS, getActivity().getApplicationContext().MODE_PRIVATE);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("username", mUsername);
                editor.putString("password", mPassword);
                editor.commit();

                dismiss();
                break;
            case SessionManager.SESSION_USERNAME_EXISTS:
                progress.dismiss();
                mErrorMessage.setVisibility(View.VISIBLE);
                mErrorMessage.setText("* Användarnamnet existerar redan, välj ett annat.");
                break;
        }
    }

    public final class SessionIdentifierGenerator {
        private SecureRandom random = new SecureRandom();

        public String nextSessionId() {
            return new BigInteger(130, random).toString(32);
        }
    }
}