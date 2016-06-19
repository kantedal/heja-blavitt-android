package applications.kantedal.hejablvitt.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import applications.kantedal.hejablvitt.R;

public class TeamExpandableListAdapter extends BaseExpandableListAdapter {

    public static final int SELECTION_LEAGUE = 0;
    public static final int SELECTION_SCORING_LEAGUE = 1;
    public static final int SELECTION_ASSIST_LEAGUE = 2;
    public static final int SELECTION_CROWD_LEAGUE = 3;
    public static final int SELECTION_TEAM = 4;
    public static final int SELECTION_GAMES = 5;

    private int mCurrentSelection = SELECTION_LEAGUE;

    private Context mContext;
    private String mTitle;
    private List<String> mListData;

    public TeamExpandableListAdapter(Context context) {
        this.mContext = context;
        this.mListData = new ArrayList<String>();

        mListData.add("Allvsvenskan");
        mListData.add("Skytteliga");
        mListData.add("Assistliga");
        mListData.add("Publikliga");
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return mListData.get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        LayoutInflater infalInflater = (LayoutInflater) this.mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.view_expandable_list_item, null);


        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.view_expandable_list_item_text);

        txtListChild.setText(childText);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mListData.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return "test";
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = mListData.get(mCurrentSelection);

        LayoutInflater infalInflater = (LayoutInflater) this.mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = infalInflater.inflate(R.layout.view_expandable_list_header, null);

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.view_expandable_list_item_text);

        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void selectItem(int childPosition) {
        mCurrentSelection = childPosition;
        this.notifyDataSetChanged();
    }
}
