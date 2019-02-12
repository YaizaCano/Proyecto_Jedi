package com.yaizacano.taskeate;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yaizacano.taskeate.bbdd.Database;
import com.yaizacano.taskeate.bbdd.NotesDao;
import com.yaizacano.taskeate.model.ListModel;

import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MyAdapter.OnItemClickListener {

    private RecyclerView mRecyclerView;

    private Database mDatabase;

    private ArrayList<ListModel> notes = new ArrayList<>();
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = Database.getInstance(this);

        mRecyclerView = findViewById(R.id.my_recycler_view);

        Button mButton = findViewById(R.id.add_task);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNoteActivity(null);
            }
        });

        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        myAdapter = new MyAdapter(this, notes, this);
        mRecyclerView.setAdapter(myAdapter);

        registerForContextMenu(mRecyclerView);

        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                openAuthorDialog();
                break;
            case R.id.logout:
                SharedPreferences sp = getSharedPreferences(LoginActivity.KEY_SETTINGS, MODE_PRIVATE);
                sp.edit().remove(LoginActivity.KEY_USERNAME).apply();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAuthorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Yaiza Cano Duarte");
        builder.setMessage("yaiza.cano@est.fib.upc.edu");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void refresh() {
        TareaDatabase tarea = new TareaDatabase(mDatabase.notesDao(), new DatabaseResult() {
            @Override
            public void result(ArrayList<ListModel> result) {
                //refrescar los datos
                notes = result;
                myAdapter.setList(notes);
                myAdapter.notifyDataSetChanged();
            }
        });
        tarea.execute();
    }

    private void startNoteActivity(ListModel lm) {
        Intent i = new Intent(this, NoteActivity.class);

        if (lm == null) {
            lm = new ListModel();
            i.putExtra("NoteTitle", true);
        }
        i.putExtra("Note", lm);
        startActivityForResult(i, 2113);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2113) {
            refresh();
        }
    }

    @Override
    public void onItemClick(View v, int pos) {
        startNoteActivity(notes.get(pos));
    }


    private static class TareaDatabase extends AsyncTask<Void, Void, ArrayList<ListModel>> {

        private NotesDao notesDao;
        private DatabaseResult databaseResult;

        TareaDatabase(NotesDao notesDao, DatabaseResult databaseResult) {
            this.notesDao = notesDao;
            this.databaseResult = databaseResult;
        }

        @Override
        protected void onPostExecute(ArrayList<ListModel> listModels) {
            super.onPostExecute(listModels);
            databaseResult.result(listModels);
        }

        @Override
        protected ArrayList<ListModel> doInBackground(Void... voids) {
            return (ArrayList<ListModel>) notesDao.getNotes();
        }
    }

    private interface DatabaseResult {
        void result(ArrayList<ListModel> result);
    }


}
