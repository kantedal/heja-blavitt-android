package applications.kantedal.hejablvitt.widget;

import java.util.List;
import java.util.Random;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.activities.MainActivity;
import applications.kantedal.hejablvitt.manager.NewsManager;
import applications.kantedal.hejablvitt.models.News;

public class NewsWidgetProvider extends AppWidgetProvider {

    public static String UPDATE_LIST = "UPDATE_LIST";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; ++i) {
            RemoteViews remoteViews = updateWidgetListView(context,
                    appWidgetIds[i]);
            appWidgetManager.updateAppWidget(appWidgetIds[i],
                    remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        updateWidget(context);
    }

    private void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, NewsWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listViewWidget);
    }


    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {
        Intent svcIntent = new Intent(context, NewsWidgetService.class);

        RemoteViews widget = new RemoteViews(context.getPackageName(),R.layout.widget_layout);
        widget.setRemoteAdapter(appWidgetId, R.id.listViewWidget, svcIntent);
        //widget.setEmptyView(R.id.listViewWidget, R.id.empty_view);

        Intent clickIntent = new Intent(context, MainActivity.class);
        PendingIntent clickPI = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        widget.setPendingIntentTemplate(R.id.listViewWidget, clickPI);

        //Intent clickIntent = new Intent(context, NewsWidgetProvider.class);
        //clickIntent.setAction(UPDATE_LIST);
        //PendingIntent pendingIntentRefresh = PendingIntent.getBroadcast(context,0, clickIntent, 0);
        //widget.setOnClickPendingIntent(R.id.update_list, pendingIntentRefresh);

        return widget;
    }
}
