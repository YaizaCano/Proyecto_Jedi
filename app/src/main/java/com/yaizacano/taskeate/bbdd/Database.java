package com.yaizacano.taskeate.bbdd;

import android.content.Context;

import com.yaizacano.taskeate.model.ListModel;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@androidx.room.Database(entities = {ListModel.class}, version = 1, exportSchema = false)
@TypeConverters({Converter.class})
public abstract class Database extends RoomDatabase {

    private static Database INSTANCE;

    public static Database getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), Database.class, "yiza-bbdd")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    public abstract NotesDao notesDao();
}
