package applications.kantedal.hejablvitt.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.models.News;
import applications.kantedal.hejablvitt.util.DateHandler;


public class NewsListProvider implements RemoteViewsService.RemoteViewsFactory {
    private List<News> mNewsDataSet = new ArrayList<>();
    private boolean validQuery = true;

    //private ArrayList<NewsListItem> listItemList = new ArrayList();
    private Context context = null;
    private int appWidgetId;

    public NewsListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

    }

    private void queryNews(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, -7);

        ParseQuery<News> query = ParseQuery.getQuery("News");
        query.whereGreaterThan("publishedDate", c.getTime());
        query.setLimit(10);
        query.findInBackground(new FindCallback<News>() {
            @Override
            public void done(List<News> news, ParseException e) {
                if (e == null) {
                    validQuery = true;
                    mNewsDataSet.clear();
                    for (int i = 0; i < news.size(); i++)
                        mNewsDataSet.add(news.get(i));

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, NewsWidgetProvider.class));
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listViewWidget);

                } else {
                    validQuery = false;
                }
            }
        });
    }

    @Override
    public void onCreate() {
        queryNews();
    }

    @Override
    public void onDataSetChanged() {
        //queryNews();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if(validQuery == true && mNewsDataSet.size() != 10) {
            return 0;
        }else if(validQuery == true && mNewsDataSet.size() == 10){
            return mNewsDataSet.size();
        }else{
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public RemoteViews getViewAt(int position) {
        Log.d("LOG", "POPULATE LIST VIEW " + position);

        RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.widget_list_row);

        if(mNewsDataSet.size() > position){
            News listItem = mNewsDataSet.get(position);
            remoteView.setTextViewText(R.id.titleTextView, listItem.getTitle());
            remoteView.setTextViewText(R.id.contentSnippetTextView, listItem.getContentSnippet());
            remoteView.setTextViewText(R.id.sourceText, listItem.getSource());
            remoteView.setTextViewText(R.id.dateText, DateHandler.dateToString(listItem.getPublishedDate()));

            Intent fillInIntent = new Intent();
            fillInIntent.putExtra("widget_clicked_id", listItem.getObjectId());
            remoteView.setOnClickFillInIntent(R.id.newsClickContainer, fillInIntent);
        }else{
            remoteView = getLoadingView();
        }

        //remoteView.setTextViewText(R.id.contentSnippetTextView, listItem.contentSnippet);

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(context.getPackageName(), R.layout.widget_loading_view);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
}