package applications.kantedal.hejablvitt.adapters;

import android.content.Context;
import android.graphics.Color;
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
import applications.kantedal.hejablvitt.view.MaterialFont;

/**
 * Created by filles-dator on 2015-08-05.
 */
public class DrawerListAdapter extends RecyclerView.Adapter<DrawerListAdapter.ViewHolder> {

    private List<DrawerListItem> mDrawerListItems;
    private Context mContext;
    private int mSelectedPosition = 0;

    private OnItemClickListener mCallback;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public DrawerListAdapter(Context context){
        mContext = context;

        mDrawerListItems = new ArrayList<>();
        mDrawerListItems.add(new DrawerListItem("Hem", mContext.getResources().getString(R.string.icon_home)));
        mDrawerListItems.add(new DrawerListItem("Mina Diskussionsinlägg", mContext.getResources().getString(R.string.icon_entry)));
        mDrawerListItems.add(new DrawerListItem("Mina kommentarer", mContext.getResources().getString(R.string.icon_comments)));
        mDrawerListItems.add(new DrawerListItem("Min aktivitet", mContext.getResources().getString(R.string.icon_heart)));
        mDrawerListItems.add(new DrawerListItem("Anpassa nyhetskällor", mContext.getResources().getString(R.string.icon_gear)));
    }

    public void setOnItemClickListener(OnItemClickListener callback){
        mCallback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.drawer_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        final ViewHolder vh = (ViewHolder) viewHolder;

        vh.titleTextView.setText(mDrawerListItems.get(i).title);
        vh.iconTextView.setText(mDrawerListItems.get(i).icon);

        vh.backgroundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallback != null)
                    mCallback.onItemClick(i);
                mSelectedPosition = i;
            }
        });

        if(mSelectedPosition == i)
            vh.backgroundLayout.setSelected(true);
        else
            vh.backgroundLayout.setSelected(false);
    }

    @Override
    public int getItemCount(){
        return mDrawerListItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        protected LinearLayout backgroundLayout;
        protected MaterialFont iconTextView;
        protected TextView titleTextView;

        public ViewHolder(View listItemView) {
            super(listItemView);

            backgroundLayout = (LinearLayout) listItemView.findViewById(R.id.drawer_list_item_layout);
            iconTextView = (MaterialFont) listItemView.findViewById(R.id.drawer_list_item_icon);
            titleTextView = (TextView) listItemView.findViewById(R.id.drawer_list_item_title);
        }
    }

    private class DrawerListItem {
        public  DrawerListItem(String title, String icon){
            this.title = title;
            this.icon = icon;
        }
        public String title;
        public String icon;
    }
}
