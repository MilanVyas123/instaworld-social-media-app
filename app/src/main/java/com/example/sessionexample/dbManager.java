package com.example.sessionexample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Patterns;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class dbManager extends SQLiteOpenHelper {
    private static String dbname="Yalaan.db";

    public dbManager(@Nullable Context context) {
        super(context, dbname, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String qry = "Create Table users(ID INTEGER PRIMARY KEY AUTOINCREMENT,EMAIL TEXT NOT NULL,USERNAME TEXT NOT NULL UNIQUE,PASSWORD TEXT)";
        db.execSQL(qry);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public String addUser(String t1,String t2,String t3)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        String user = checkUser(t2);

        if( user != null)
        {
            return "Username Already Exists";
        }
        else if(t1.isEmpty() || t2.isEmpty() || t3.isEmpty() || t1.trim().isEmpty() || t1.trim().isEmpty() || t1.trim().isEmpty())
        {
            return "Email and Username and Password must be filled";
        }
        else if(!(Patterns.EMAIL_ADDRESS.matcher(t1).matches()))
        {
            return "Invalid Email";
        }
        else
        {
            cv.put("EMAIL",t1);
            cv.put("USERNAME",t2);
            cv.put("PASSWORD",t3);

            long res = db.insert("users",null,cv);

            if(res==-1)
                return "Registration failed";
            else
                return "Registration successfull";
        }
    }

    public User selectUser(String t1,String t2)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection[] = {t1,t2};

        String query = "Select * from users WHERE USERNAME=? AND PASSWORD=?";

        Cursor cursor =db.rawQuery(query,selection);

        if(cursor.moveToNext())
        {
            User user = new User();
            //user.setId(cursor.getInt(0));
            user.setName(t1);

            return user;
        }
        else
            return null;
    }

    private String checkUser(String t1)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection[] = {t1};

        String query = "Select * from users WHERE USERNAME=?";

        Cursor cursor =db.rawQuery(query,selection);

        if(cursor.moveToNext())
            return "Username Already Exists";
        else
            return null;
    }
}
