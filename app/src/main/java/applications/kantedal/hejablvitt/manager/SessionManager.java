package applications.kantedal.hejablvitt.manager;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.parse.LogInCallback;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

import applications.kantedal.hejablvitt.BlavittApplication;
import applications.kantedal.hejablvitt.models.News;
import applications.kantedal.hejablvitt.models.User;

/**
 * Created by filles-dator on 2015-07-16.
 */
public class SessionManager {

    private List<SessionListener> sessionListener = new ArrayList<SessionListener>();

    public static final int SESSION_LOGIN_SUCCESS = 1;
    public static final int SESSION_LOGIN_FAILED = 2;
    public static final int SESSION_IS_UPDATING = 3;
    public static final int SESSION_CREATED_USER = 4;
    public static final int SESSION_USERNAME_EXISTS = 5;

    private boolean isLoggedIn = false;

    public void login(String username, String password){
        notifySessionListener(SESSION_LOGIN_FAILED);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser newUser, com.parse.ParseException e) {
                if (newUser != null) {
                    notifySessionListener(SESSION_LOGIN_SUCCESS);
                    isLoggedIn = true;
                } else {
                    notifySessionListener(SESSION_LOGIN_FAILED);
                    isLoggedIn = false;
                }
            }
        });
    }

    public boolean isLoggedIn(){
        return isLoggedIn;
    }

    public void signUp(String username, String password){
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.put("likedNews", new ArrayList<>());
        user.put("likedComment", new ArrayList<>());
        user.put("likedDiscussionEntries", new ArrayList<>());

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    notifySessionListener(SESSION_LOGIN_SUCCESS);
                    notifySessionListener(SESSION_CREATED_USER);
                } else {
                    if(e.getCode() == 202)
                        notifySessionListener(SESSION_USERNAME_EXISTS);
                }
            }
        });
    }

    private void notifySessionListener(int event) {
        for(SessionListener listener : sessionListener){
            if(listener != null){
                listener.OnSessionEvent(event);
            }
        }
    }

    public void subscribeSessionListener(SessionListener listener){
        if(listener != null){
            sessionListener.add(listener);
        }
    }

    public void unsubscribeSessionListener(SessionListener listener){
        if(listener != null){
            sessionListener.remove(listener);
        }
    }

    public interface SessionListener
    {
        void OnSessionEvent(int event);
    }
}
