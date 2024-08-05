package com.example.nasaimageoftheday;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
public class DBHelper extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "NASAImages";

    public final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "DatesList";
    public final static String COL_DATE = "date";
    public final static String COL_TITLE = "title";


    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUM);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                    + COL_DATE + " TEXT PRIMARY KEY, "
                    + COL_TITLE + " TEXT)"
                    );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
