package applications.kantedal.hejablvitt.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import applications.kantedal.hejablvitt.BlavittApplication;
import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.models.NewsSource;
import applications.kantedal.hejablvitt.view.MaterialFont;

/**
 * Created by filles-dator on 2015-06-20.
 */
public class NewsSourcesAdapter extends RecyclerView.Adapter<NewsSourcesAdapter.ViewHolder> {

    BlavittApplication mApp;
    List<NewsSource> mNewsSources;

    Context mContext;
    SharedPreferences.Editor mSharedPrefsEditor;

    public NewsSourcesAdapter(BlavittApplication app, Context context) {
        mApp = app;
        mContext = context;
        mNewsSources = new ArrayList<>();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        mSharedPrefsEditor = sp.edit();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.view_news_source, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final NewsSource newsSource = mNewsSources.get(i);

        if ((i + 1) % 2 == 0) {
            viewHolder.background.setBackgroundColor(0x22FFFFFF);
        } else
            viewHolder.background.setBackgroundColor(Color.TRANSPARENT);

        if(!newsSource.isSelected())
            viewHolder.checker.setVisibility(View.GONE);

        viewHolder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newsSource.isSelected()){
                    mSharedPrefsEditor.putBoolean(newsSource.getName(), false);
                    mSharedPrefsEditor.commit();

                    newsSource.setSelected(false);
                    viewHolder.checker.setVisibility(View.GONE);
                }else{
                    mSharedPrefsEditor.putBoolean(newsSource.getName(), true);
                    mSharedPrefsEditor.commit();

                    newsSource.setSelected(true);
                    viewHolder.checker.setVisibility(View.VISIBLE);

                }

                mApp.getNewsManager().getNewsDataSet().clear();

            }
        });

        viewHolder.nameTextView.setText(newsSource.getName());
    }

    public void setNewsSources(List newsSources){
        mNewsSources = newsSources;
    }


    @Override
    public int getItemCount(){
        return mNewsSources.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        protected LinearLayout background;
        protected TextView nameTextView;
        protected MaterialFont checker;

        public ViewHolder(View newsSourceView) {
            super(newsSourceView);
            background = (LinearLayout) newsSourceView.findViewById(R.id.view_news_source_background);
            nameTextView = (TextView) newsSourceView.findViewById(R.id.view_news_source_name);
            checker = (MaterialFont) newsSourceView.findViewById(R.id.view_news_source_checker);
        }
    }


}


