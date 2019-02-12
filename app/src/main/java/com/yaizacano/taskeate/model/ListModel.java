package com.yaizacano.taskeate.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Notes")
//és una struct
public class ListModel implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;
    public ArrayList<NoteElement> content = new ArrayList<>();

    public ListModel() {

    }

    public ListModel(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.content = new Gson().fromJson(in.readString(), new TypeToken<ArrayList<NoteElement>>() {
        }.getType());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(new Gson().toJson(content, new TypeToken<ArrayList<NoteElement>>() {
        }.getType()));
    }


    public static final Creator<ListModel> CREATOR = new Creator<ListModel>() {
        @Override
        public ListModel createFromParcel(Parcel in) {
            return new ListModel(in);
        }

        @Override
        public ListModel[] newArray(int size) {
            return new ListModel[size];
        }
    };
}
