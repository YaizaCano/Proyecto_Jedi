package com.yaizacano.taskeate.bbdd;

import com.yaizacano.taskeate.model.ListModel;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface NotesDao {

    @Query("SELECT * FROM Notes")
    List<ListModel> getNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ListModel lm);

    @Update
    void update(ListModel lm);

    @Delete
    void delete(ListModel lm);

    @Delete
    void delete(List<ListModel> lm);

}
