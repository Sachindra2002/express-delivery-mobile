package com.example.express_delivery_mobile.Provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.HashMap;

public class AddPackageToLocalStorage extends ContentProvider {
    public AddPackageToLocalStorage() {
    }

    // Authority
    private static final String PROVIDER_NAME = "com.example.express_delivery_mobile.Provider";

    // Content URI
    private static final String URL = "content://" + PROVIDER_NAME + "/saved_packages";

    // Constants
    public static final Uri CONTENT_URI = Uri.parse(URL);
    public static final String MAIL_ID = "mailId";
    public static final String DRIVER_ID = "driverId";
    public static final String DRIVER_EMAIL = "driverEmail";
    public static final String CUSTOMER_NAME = "customerName";
    public static final String CUSTOMER_CONTACT = "customerContact";
    public static final String CUSTOMER_EMAIL = "customerEmail";
    public static final String TRANSPORTATION_STATUS = "transportationStatus";
    public static final String PACKAGE_STATUS = "packageStatus";
    public static final String PICKUP_ADDRESS = "pickupAddress";
    public static final String DROP_OFF_ADDRESS = "dropOffAddress";
    public static final String WEIGHT = "weight";
    public static final String PARCEL_TYPE = "parcelType";
    public static final String PAYMENT_METHOD = "paymentMethod";
    public static final String TOTAL_COST = "totalCost";

    private static final int uriCode = 1;
    private static final UriMatcher uriMatcher;
    private static HashMap<String, String> values;

    static {
        // to match the content URI
        // every time user access table under content provider
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // to access whole table
        uriMatcher.addURI(PROVIDER_NAME, "saved_packages", uriCode);

        // to access a particular row
        // of the table
        uriMatcher.addURI(PROVIDER_NAME, "saved_packages/*", uriCode);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        if (db != null) {
            return true;
        }
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME);
        switch (uriMatcher.match(uri)) {
            case uriCode:
                qb.setProjectionMap(values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (s1 == null || s1 == "") {
            s1 = MAIL_ID;
        }
        Cursor c = qb.query(db, strings, s, strings1, null,
                null, s1);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case uriCode:
                return "vnd.android.cursor.dir/saved_packages";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long rowID = db.insert(TABLE_NAME, "", contentValues);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLiteException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case uriCode:
                count = db.delete(TABLE_NAME, s, strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case uriCode:
                count = db.update(TABLE_NAME, contentValues, s, strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /*Handling database*/
    private SQLiteDatabase db;

    // name of the database
    static final String DATABASE_NAME = "ExpressDB";

    // table name of the database
    static final String TABLE_NAME = "Bookmarks";

    // database version
    static final int DATABASE_VERSION = 1;

    // sql query to create the table
    static final String CREATE_DB_TABLE = " CREATE TABLE " + TABLE_NAME
            + " (mailId INTEGER PRIMARY KEY, "
            + " driverId TEXT NOT NULL, "
            + " driverEmail TEXT NOT NULL, "
            + " customerName TEXT NOT NULL, "
            + " customerContact TEXT NOT NULL, "
            + " customerEmail INTEGER NOT NULL, "
            + " transportationStatus TEXT NOT NULL, "
            + " packageStatus TEXT NOT NULL, "
            + " dropOffAddress TEXT NOT NULL, "
            + " weight TEXT NOT NULL, "
            + " parcelType TEXT NOT NULL,"
            + " paymentMethod TEXT NOT NULL, "
            + " totalCost TEXT NOT NULL,"
            + " pickupAddress TEXT NOT NULL);";

    // creating a database
    private static class DatabaseHelper extends SQLiteOpenHelper {

        // defining a constructor
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // creating a table in the database
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // sql query to drop a table
            // having similar name
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
