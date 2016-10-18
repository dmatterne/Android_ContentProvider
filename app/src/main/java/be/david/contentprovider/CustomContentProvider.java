package be.david.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by David on 18/10/2016.
 */

public class CustomContentProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.androidatc.provider";
    static final String URL = "content://" + PROVIDER_NAME + "/nicknames";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String ID = "id";
    static final String NAME = "name";
    static final String NICK_NAME = "nickname";

    static final int NICKNAME = 1;
    static final int NICKNAME_ID = 2;

    private DBHelper dbHelper;

    private static HashMap<String, String> nickNameMap;
    static final UriMatcher uriMatcher;

    static {

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "nicknames", NICKNAME);
        uriMatcher.addURI(PROVIDER_NAME, "nicknames/#", NICKNAME_ID);

    }

    private SQLiteDatabase sqLiteDatabas;

    static final String DATABASE_NAME = "NicknamesDirectory";
    static final String TABLE_NAME = "Nicknames";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_NAME
            + "(id integer PRIMARY KEY AUTOINCREMENT, "
            + "name TEXT NOT NULL,"
            + "nickname TEXT NOT NULL);";

    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);


        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(DBHelper.class.getName(), "Upgrading database from version " + oldVersion
                    + " to " + newVersion + ", Old Data will be destroyed");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context c = getContext();
        dbHelper = new DBHelper(c);
        sqLiteDatabas = dbHelper.getWritableDatabase();

        if (sqLiteDatabas == null) {

            return false;

        } else {

            return true;

        }

    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);

        switch (uriMatcher.match(uri)) {

            case NICKNAME:
                queryBuilder.setProjectionMap(nickNameMap);
                break;
            case NICKNAME_ID:
                queryBuilder.appendWhere(ID + "=" + uri.getLastPathSegment());
                break;
            default:
                    throw new IllegalArgumentException("Unknown URI "+ uri);

        }

        if (sortOrder == null || sortOrder == "") {

            sortOrder = NAME;

        }

        Cursor cursor = queryBuilder.query(sqLiteDatabas,projection,selection,selectionArgs,null, null,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        switch (uriMatcher.match(uri)) {

            case NICKNAME:
                return "vnd.android.cursor.dir/vnd.example.nicknames";

            case NICKNAME_ID:
                return "vnd.android.cursor.item/vnd.example.nicknames";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);



        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long row = sqLiteDatabas.insert(TABLE_NAME,"",values);
        if (row > 0) {

            Uri newUri = ContentUris.withAppendedId(CONTENT_URI,row);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;

        } throw new SQLException("Fail to add a new record into " + uri);


    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int count = 0;
        switch (uriMatcher.match(uri)) {

            case NICKNAME:
                count = sqLiteDatabas.delete(TABLE_NAME,selection,selectionArgs);

                break;
            case NICKNAME_ID:
                String id = uri.getLastPathSegment();
                count = sqLiteDatabas.delete(TABLE_NAME,ID +  " = " + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "" ),selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);

        }

        getContext().getContentResolver().notifyChange(uri,null);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int count = 0 ;

        switch(uriMatcher.match(uri)) {

            case NICKNAME:
                count = sqLiteDatabas.update(TABLE_NAME,values,selection,selectionArgs);
                break;
            case NICKNAME_ID:
                String id = uri.getLastPathSegment();
                count = sqLiteDatabas.update(TABLE_NAME,values,ID + " = " + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }
}
