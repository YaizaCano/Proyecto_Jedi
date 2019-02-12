package com.yaizacano.taskeate;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yaizacano.taskeate.bbdd.Database;
import com.yaizacano.taskeate.drag.EditItemTouchHelperCallback;
import com.yaizacano.taskeate.drag.OnStartDragListener;
import com.yaizacano.taskeate.model.ListModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NoteActivity extends AppCompatActivity implements View.OnClickListener, OnItemChanged, ShowKeyboard {

    private ListModel lm;

    private TextView mAddItem;
    private RecyclerView mItems;

    private ItemsAdapter mItemsAdapter;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("Note")) {
            lm = getIntent().getExtras().getParcelable("Note");
        }

        if (lm == null) {
            //Ha sucedido algun error y no tengo la nota que hay que modificar
            finish();
        }

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAddItem = findViewById(R.id.add_item);
        mItems = findViewById(R.id.items_recycler);

        mAddItem.setOnClickListener(this);

        setTitle(lm.title);

        mItemsAdapter = new ItemsAdapter(lm.content, this, new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                mItemTouchHelper.startDrag(viewHolder);
            }
        });

        mItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        ItemTouchHelper.Callback callback = new EditItemTouchHelperCallback(mItemsAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mItems);

        mItems.setAdapter(mItemsAdapter);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("NoteTitle")) {
            editTitle();
        }
    }


    private void editTitle() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit title");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_edit_title, null);

        final EditText titleInput = dialogView.findViewById(R.id.title_input);
        titleInput.setText(lm.title);

        builder.setView(dialogView);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                lm.title = titleInput.getText().toString();
                setTitle(lm.title);
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        showKeyboard(titleInput);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_item:
                mItemsAdapter.addItem();
                break;
        }
    }

    @Override
    public void onCheckboxChange(boolean checked, int position) {
        lm.content.get(position).checked = checked;
        mItemsAdapter.setList(lm.content);
        //No need to notify
    }

    @Override
    public void showKeyboard(final EditText v) {
        //https://stackoverflow.com/questions/5520085/android-show-softkeyboard-with-showsoftinput-is-not-working
        v.post(new Runnable() {
            @Override
            public void run() {
                if (v.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    boolean isShowing = imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                    if (!isShowing) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                lm.content = mItemsAdapter.getList();
                lm.id = (int) Database.getInstance(this).notesDao().insert(lm);
                Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.edit_title:
                editTitle();
                break;
            case android.R.id.home:
                supportFinishAfterTransition();
        }
        return super.onOptionsItemSelected(item);
    }
}
