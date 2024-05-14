package com.example.sessionexample;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String SHARED_PREF_NAME = "session";
    String SESSION_KEY = "session_user";

    String SESSION_USER_NAME = "session_user_name";

    public SessionManagement(Context context)
    {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);

        editor = sharedPreferences.edit();
    }

    public void saveSession(User user)
    {
        String name = user.getName();

        editor.putString(SESSION_USER_NAME,name);
        editor.commit();
    }

    public int getSession()
    {
        return sharedPreferences.getInt(SESSION_KEY,-1);
    }

    public String getSessionUserName()
    {
        return sharedPreferences.getString(SESSION_USER_NAME,"null");
    }

    public void removeSession()
    {
        editor.putInt(SESSION_KEY,-1).commit();
    }
}
