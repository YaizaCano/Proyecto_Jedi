package com.yaizacano.taskeate;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yaizacano.taskeate.drag.OnStartDragListener;
import com.yaizacano.taskeate.model.NoteElement;

import java.util.ArrayList;
import java.util.Collections;

import androidx.recyclerview.widget.RecyclerView;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemsViewHolder> implements ItemTouchHelperAdapter {

    private ArrayList<NoteElement> mElements;
    private final ShowKeyboard mShowKeyboard;
    private final OnStartDragListener mDragStartListener;

    public ItemsAdapter(ArrayList<NoteElement> list, ShowKeyboard sk, OnStartDragListener startDragListener) {
        mElements = list;
        mShowKeyboard = sk;
        mDragStartListener = startDragListener;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mElements, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mElements, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public ArrayList<NoteElement> getList() {
        return mElements;
    }

    public static class ItemsViewHolder extends RecyclerView.ViewHolder {

        public EditText mEditText;
        public CheckBox mCheckbox;
        public ImageView drag;
        public ImageButton delete;

        public ItemsViewHolder(View v) {
            super(v);
            mEditText = v.findViewById(R.id.list_edittext);
            mCheckbox = v.findViewById(R.id.checkbox);
            drag = v.findViewById(R.id.draggable_image);
            delete = v.findViewById(R.id.delete);
        }
    }


    @Override
    public ItemsAdapter.ItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_checkbox, parent, false);
        return new ItemsAdapter.ItemsViewHolder(v);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ItemsAdapter.ItemsViewHolder holder, int position) {

        holder.mEditText.setText(mElements.get(holder.getAdapterPosition()).text);
        holder.mCheckbox.setChecked(mElements.get(holder.getAdapterPosition()).checked);
        holder.mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mElements.get(holder.getAdapterPosition()).checked = isChecked;
            }
        });

        holder.drag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });

        holder.mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mElements.get(holder.getAdapterPosition()).text = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(holder.getAdapterPosition());
            }
        });

        if (position == mElements.size() - 1 && holder.mEditText.requestFocus()) {
            mShowKeyboard.showKeyboard(holder.mEditText);
        }
    }


    @Override
    public int getItemCount() {
        return mElements.size();
    }

    public void setList(ArrayList<NoteElement> list) {
        mElements = list;
    }

    public void addItem() {
        mElements.add(new NoteElement());
        notifyItemInserted(mElements.size() - 1);
    }

    private void removeItem(int position) {
        if (position >= 0) {
            mElements.remove(position);
            notifyItemRemoved(position);
        }
    }


}