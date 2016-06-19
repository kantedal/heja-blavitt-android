package applications.kantedal.hejablvitt;

import android.app.Application;
import android.content.SharedPreferences;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

import java.util.ArrayList;
import java.util.List;

import applications.kantedal.hejablvitt.manager.DiscussionManager;
import applications.kantedal.hejablvitt.manager.LeagueManager;
import applications.kantedal.hejablvitt.manager.NewsManager;
import applications.kantedal.hejablvitt.manager.SessionManager;
import applications.kantedal.hejablvitt.manager.StatisticsManager;
import applications.kantedal.hejablvitt.models.Comment;
import applications.kantedal.hejablvitt.models.DiscussionComment;
import applications.kantedal.hejablvitt.models.DiscussionEntry;
import applications.kantedal.hejablvitt.models.LeagueRound;
import applications.kantedal.hejablvitt.models.News;
import applications.kantedal.hejablvitt.models.NewsSource;

/**
 * Created by filles-dator on 2015-07-16.
 */
public class BlavittApplication extends Application {

    private SessionManager sessionManager;
    private NewsManager newsManager;
    private DiscussionManager discussionManager;
    private LeagueManager leagueManager;
    private StatisticsManager statisticsManager;

    public static final String PREFS = "BlavittPrefs";

    Permission[] fbPermissions = new Permission[] {
            Permission.USER_PHOTOS,
            Permission.EMAIL,
            Permission.PUBLISH_ACTION
    };

    SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
            .setAppId("558745864257581")
            .setNamespace("Heja Blåvitt")
            .setPermissions(fbPermissions)
            .build();

    public void onCreate (){
        super.onCreate();
        setup();
    }

    private void setup(){
        ParseObject.registerSubclass(News.class);
        ParseObject.registerSubclass(DiscussionEntry.class);
        ParseObject.registerSubclass(DiscussionComment.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(LeagueRound.class);
        ParseObject.registerSubclass(NewsSource.class);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "Xuoo4SPP7w9o7riBffzbXPvshEmKDVYLbXv9J0Yl", "QwuyFgdMDuTAWzHTvmhfTfbYYzYoDTodYtdxaSXv");

        this.sessionManager = new SessionManager();
        this.newsManager = new NewsManager(this);
        this.discussionManager = new DiscussionManager();
        this.leagueManager = new LeagueManager();
        this.statisticsManager = new StatisticsManager();

        SimpleFacebook.setConfiguration(configuration);
    }

    public SessionManager getSessionManager(){ return sessionManager; }
    public NewsManager getNewsManager(){ return newsManager; }
    public DiscussionManager getDiscussionManager(){ return discussionManager; }
    public LeagueManager getLeagueManager(){ return leagueManager; }
    public StatisticsManager getStatisticsManager(){ return statisticsManager; }


}
