package applications.kantedal.hejablvitt.dialogs;

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;

import applications.kantedal.hejablvitt.R;
import applications.kantedal.hejablvitt.activities.MainActivity;
import applications.kantedal.hejablvitt.fragments.TabFragment;

/**
 * Created by filles-dator on 2015-07-03.
 */
public class ListPickerDialog extends DialogFragment {

    private String[] sortOptions;
    private int listIndex = MainActivity.SCREEN_NEWS;

    public void setSortList(int listIndex){
        this.listIndex = listIndex;
    }

    private SortListCallback mCallback;
    public void setCallback(SortListCallback callback){
        mCallback = callback;
    }

    public interface SortListCallback {
        void sortList(int sortType);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.Theme_AppCompat_Light));

        switch (listIndex){
            case MainActivity.SCREEN_NEWS:
                sortOptions = getActivity().getResources().getStringArray(R.array.sort_news);
                break;
            case MainActivity.SCREEN_DISCUSSION:
                sortOptions = getActivity().getResources().getStringArray(R.array.sort_discussion);
                break;
        }

        builder.setTitle("Sortera på").setItems(sortOptions,
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mCallback.sortList(which);
                }
            }
        );
        return builder.create();
    }
}
