package com.hsbc.gltc.globalkalendar.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Henyue-GZ on 2014/7/8.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "kalendar.7z";
    private static final int DB_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("drop table if exists SYS_PROPERTIES");
        db.execSQL("create table SYS_PROPERTIES(key TEXT PRIMARY KEY, value TEXT)");
        db.execSQL("insert into SYS_PROPERTIES values('WEIBO_APP_KEY', '1496930975')");
        db.execSQL("insert into SYS_PROPERTIES values('WEIBO_APP_SECRET', '567471f5eac4d3e9bc41cb2a2b221bc6')");
        db.execSQL("insert into SYS_PROPERTIES values('WEIBO_REDIRECT_URL', 'WEIBO_REDIRECT_URL')");
        db.execSQL("insert into SYS_PROPERTIES values('WEIBO_SCOPE', 'email,direct_messages_read,direct_messages_write,friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write')");
        db.execSQL("insert into SYS_PROPERTIES values('WEIBO_OAUTH2_URL', 'https://api.weibo.com/oauth2/authorize')");
        db.execSQL("insert into SYS_PROPERTIES values('WEIBO_UPDATE_URL', 'https://api.weibo.com/2/statuses/update.json')");
        db.execSQL("insert into SYS_PROPERTIES values('WEIXIN_APP_KEY', 'wxebe6bb0d16a86b99')");
    }

    public String getProperties(String key) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select value from SYS_PROPERTIES where key = ?", new String[]{key});
        if (cursor.moveToFirst()) {
            String value = cursor.getString(cursor.getColumnIndex("value"));
            return value;
        }
        return null;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
