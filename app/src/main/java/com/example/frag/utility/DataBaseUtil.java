package com.example.frag.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.frag.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataBaseUtil {
    private Context context;
    public static String dbName = "citydb.db";
    private static String DATABASE_PATH;

    public DataBaseUtil(Context context){
        this.context = context;
        String packageName = context.getPackageName();
        DATABASE_PATH = "/data/data/" + packageName + "/databases/";
    }

    public boolean isDbExists() {
        SQLiteDatabase db = null;
        try {
            String databaseFilename = DATABASE_PATH + dbName;
            db = SQLiteDatabase.openDatabase(databaseFilename, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (db != null) {
            db.close();
            return true;
        }
        return false;
    }

    public void copyDatabase() throws IOException {
        String databaseFilename = DATABASE_PATH + dbName;
        File dir = new File(DATABASE_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        FileOutputStream os = new FileOutputStream(databaseFilename);
        InputStream is = context.getResources().openRawResource(R.raw.citydb);
        byte[] buffer = new byte[8192];
        int count = 0;
        while((count = is.read(buffer)) > 0) {
            os.write(buffer, 0, count);
            os.flush();
        }
        is.close();
        os.close();
    }
}
