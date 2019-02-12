package com.yaizacano.taskeate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yaizacano.taskeate.bbdd.Database;
import com.yaizacano.taskeate.bbdd.NotesDao;
import com.yaizacano.taskeate.model.ListModel;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private final OnItemClickListener mListener;
    private ArrayList<ListModel> mDataset;
    private Context mContext;
    private ActionMode mActionMode = null;

    private boolean mMultiSelect = false;
    private ArrayList<ListModel> mSelectedItems = new ArrayList<>();

    private static final int MAX_ITEMS_PREVIEW = 4;


    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mRoot, mExtra;
        public TextView mNoteTitle;

        public MyViewHolder(LinearLayout v) {
            super(v);
            mRoot = v;
            mNoteTitle = v.findViewById(R.id.notetitle);
            mExtra = v.findViewById(R.id.extra);

        }
    }

    public MyAdapter(Context context, ArrayList<ListModel> myDataset, OnItemClickListener listener) {
        mDataset = myDataset;
        mListener = listener;
        mContext = context;
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final ListModel lm = mDataset.get(position);

        holder.mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMultiSelect) {
                    selectItem(lm, holder);
                } else {
                    mListener.onItemClick(view, position);
                }
            }
        });

        holder.mRoot.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mActionMode == null) {
                    mActionMode = ((AppCompatActivity) mContext).startSupportActionMode(callback);
                }
                selectItem(lm, holder);
                return true;
            }
        });

        holder.mNoteTitle.setText(lm.title);
        holder.itemView.setBackgroundResource(R.drawable.note_item_bg_ripple);
        holder.mExtra.removeAllViews();

        int datasetSize = lm.content.size();
        if (datasetSize > 0) {
            for (int i = 0; i < Math.min(datasetSize, MAX_ITEMS_PREVIEW); i++) {

//                https://stackoverflow.com/questions/14963571/programmatically-styling-androids-checkbox
                CheckBox checkBox = new CheckBox(new ContextThemeWrapper(mContext, R.style.PreviewCheckbox));
                checkBox.setTextColor(mContext.getResources().getColorStateList(R.color.checkbox_preview_color));
                checkBox.setText(lm.content.get(i).text);
                checkBox.setChecked(lm.content.get(i).checked);
                checkBox.setFocusable(false);
                checkBox.setClickable(false);
                holder.mExtra.addView(checkBox);

            }

            if (datasetSize > MAX_ITEMS_PREVIEW) {
                LayoutInflater.from(mContext)
                        .inflate(R.layout.checkbox_preview_more, holder.mExtra, true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setList(ArrayList<ListModel> list) {
        mDataset = list;
    }

    private void selectItem(ListModel lm, MyViewHolder holder) {
        if (mMultiSelect) {
            if (mSelectedItems.contains(lm)) {
                mSelectedItems.remove(lm);
                holder.itemView.setBackgroundResource(R.drawable.note_item_bg_ripple);
                if (mSelectedItems.size() == 0 && mActionMode != null) {
                    mActionMode.finish();
                }
            } else {
                mSelectedItems.add(lm);
                holder.itemView.setBackgroundResource(R.drawable.note_item_bg_ripple_selected);
            }
        }
    }

    private ActionMode.Callback callback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mMultiSelect = true;
            MenuItem menuItem = menu.add(Menu.NONE, 0, Menu.NONE, "DELETE");
            menuItem.setIcon(R.drawable.ic_delete_black_24dp);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            NotesDao notesDao = Database.getInstance(mContext).notesDao();
            notesDao.delete(mSelectedItems);
            mDataset.removeAll(mSelectedItems);
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mSelectedItems.clear();
            notifyDataSetChanged();
            mMultiSelect = false;
            mActionMode = null;
        }
    };
}