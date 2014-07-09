package com.hsbc.gltc.globalkalendar.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Henyue-GZ on 2014/7/8.
 */
public class DBHelper {
    private static final String DB_NAME = "kalendar.xml";
    private static SQLiteDatabase readableDB;
    private static SQLiteDatabase writableDB;

    private static SQLiteDatabase getReadableDB(Context context) throws IOException {
        if (readableDB == null) {
            readableDB = getDB(context, SQLiteDatabase.OPEN_READONLY);
        }
        return readableDB;
    }

    private static SQLiteDatabase getWritableDB(Context context) throws IOException {
        if (writableDB == null || !writableDB.isOpen()) {
            writableDB = getDB(context, SQLiteDatabase.OPEN_READWRITE);
        }
        return writableDB;
    }

    private static SQLiteDatabase getDB(Context context, int flag) throws IOException {
        String filesDir = context.getFilesDir().getAbsolutePath();
        String dbFullPath = filesDir + "/" + DB_NAME;
        initDBToDevice(context, dbFullPath);
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFullPath, null, flag);
        return db;
    }

    /**
     * Copy the database from assets to device storage while the database not exists.
     * @param context
     * @param dbFullPath
     * @throws IOException
     */
    private static void initDBToDevice(Context context, String dbFullPath) throws IOException {
        File f = new File(dbFullPath);
        if (!f.exists()) {
            InputStream is = context.getAssets().open(DB_NAME);
            OutputStream os = new FileOutputStream(dbFullPath);

            // Write file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            // close streams
            os.flush();
            os.close();
            is.close();
        }
    }

    public static String getSysProperties(Context context, SysConstants key) {
        try {
            Cursor cursor = getReadableDB(context).rawQuery("select value from SYS_PROPERTIES where key = ?", new String[]{key.name()});
            if (cursor.moveToFirst()) {
                String value = cursor.getString(cursor.getColumnIndex("value"));
                cursor.close();
                return value;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
