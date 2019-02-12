package com.yaizacano.taskeate.bbdd;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yaizacano.taskeate.model.NoteElement;

import java.util.ArrayList;
import java.util.List;

import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

public class Converter {

    @TypeConverter
    public static String toString(ArrayList<NoteElement> list) {
        return new Gson().toJson(list, new TypeToken<ArrayList<NoteElement>>() {
        }.getType());
    }

    @TypeConverter
    public static ArrayList<NoteElement> toList(String s) {
        return new Gson().fromJson(s, new TypeToken<ArrayList<NoteElement>>() {
        }.getType());
    }
}
