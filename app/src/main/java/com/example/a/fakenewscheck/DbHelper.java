package com.example.a.fakenewscheck;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by amcprak on 29.03.17.
 */

public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "Database4.db";

    private static final String INTEGER = " INTEGER";
    private static final String TEXT = " TEXT";
    private static final String DATE_TIME = " DATETIME";
    private static final String COMMA = ",";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String FOREIGN_KEY = "FOREIGN KEY";
    private static final String REFERENCES = " REFERENCES ";

    private static final String SQL_CREATE_TABLE_CATEGORY =
            "CREATE TABLE " + DbContract.Category.TABLE_NAME + " (" +
                    DbContract.Category._ID + INTEGER + PRIMARY_KEY + COMMA +
                    DbContract.Category.CATEGORY + TEXT +
            " );";

    private static final String SQL_CREATE_TABLE_KEYWORD =
            "CREATE TABLE " + DbContract.Keyword.TABLE_NAME + " (" +
                    DbContract.Keyword._ID + INTEGER + PRIMARY_KEY + COMMA +
                    DbContract.Keyword.CATEGORY_ID + INTEGER + COMMA +
                    DbContract.Keyword.KEYWORD + TEXT + COMMA +
                    FOREIGN_KEY + "(" + DbContract.Keyword.CATEGORY_ID + ")" +
                    REFERENCES + DbContract.Category.TABLE_NAME +
                    "(" + DbContract.Category._ID + ")" +
            " );";

    private static final String SQL_CREATE_TABLE_KEYWORD_ARTICLE =
            "CREATE TABLE " + DbContract.Keyword_Article.TABLE_NAME + " (" +
                    DbContract.Keyword_Article._ID + INTEGER + PRIMARY_KEY + COMMA +
                    DbContract.Keyword_Article.KEYWORD_ID + INTEGER + COMMA +
                    DbContract.Keyword_Article.ARTICLE_ID + INTEGER + COMMA +
                    FOREIGN_KEY + "(" + DbContract.Keyword_Article.KEYWORD_ID + ")" +
                    REFERENCES + DbContract.Keyword.TABLE_NAME +
                    "(" + DbContract.Keyword._ID + ")" + COMMA +
                    FOREIGN_KEY + "(" + DbContract.Keyword_Article.ARTICLE_ID + ")" +
                    REFERENCES + DbContract.Article.TABLE_NAME +
                    "(" + DbContract.Article._ID + ")" +
            " );";

    private static final String SQL_CREATE_TABLE_CATEGORY_INCIDENT =
            "CREATE TABLE " + DbContract.Category_Incident.TABLE_NAME + " (" +
                    DbContract.Category_Incident._ID + INTEGER + PRIMARY_KEY + COMMA +
                    DbContract.Category_Incident.CATEGORY_ID + INTEGER + COMMA +
                    DbContract.Category_Incident.INCIDENT_ID + INTEGER + COMMA +
                    FOREIGN_KEY + "(" + DbContract.Category_Incident.CATEGORY_ID + ")" +
                    REFERENCES + DbContract.Category.TABLE_NAME +
                    "(" + DbContract.Category._ID + ")" + COMMA +
                    FOREIGN_KEY + "(" + DbContract.Category_Incident.INCIDENT_ID + ")" +
                    REFERENCES + DbContract.Incident.TABLE_NAME +
                    "(" + DbContract.Incident._ID + ")" +
            " );";

    private static final String SQL_CREATE_TABLE_INCIDENT =
            "CREATE TABLE " + DbContract.Incident.TABLE_NAME + " (" +
                    DbContract.Incident._ID + INTEGER + PRIMARY_KEY + COMMA +
                    DbContract.Incident.DATE_TIME + DATE_TIME + COMMA +
                    DbContract.Incident.DESCRIPTION + TEXT +
            " );";

    private static final String SQL_CREATE_TABLE_ARTICLE =
            "CREATE TABLE " + DbContract.Article.TABLE_NAME + " (" +
                    DbContract.Article._ID + INTEGER + PRIMARY_KEY + COMMA +
                    DbContract.Article.POST_ID_NUMBER + INTEGER + COMMA +
                    DbContract.Article.POST_ID_TEXT + TEXT + COMMA +
                    DbContract.Article.CREATE_TIME + TEXT + COMMA +
                    DbContract.Article.MESSAGE + TEXT + COMMA +
                    DbContract.Article.INCIDENT_ID + INTEGER + COMMA +
                    DbContract.Article.SOURCE_ID + INTEGER + COMMA +
                    DbContract.Article.ARTICLE_TYPE + TEXT + COMMA +
                    FOREIGN_KEY + "(" + DbContract.Article.INCIDENT_ID + ")" +
                    REFERENCES + DbContract.Incident.TABLE_NAME +
                    "(" + DbContract.Incident._ID + ")" + COMMA +
                    FOREIGN_KEY + "(" + DbContract.Article.SOURCE_ID + ")" +
                    REFERENCES + DbContract.ArticleSource.TABLE_NAME +
                    "(" + DbContract.ArticleSource._ID + ")" +
            " );";

    private static final String SQL_CREATE_TABLE_LOCATION =
            "CREATE TABLE " + DbContract.Location.TABLE_NAME + " (" +
                    DbContract.Location._ID + INTEGER + PRIMARY_KEY + COMMA +
                    DbContract.Location.LATITUDE + INTEGER + COMMA +
                    DbContract.Location.LONGITUDE + INTEGER + COMMA +
                    DbContract.Location.ZIP_CODE + INTEGER + COMMA +
                    DbContract.Location.CITY + TEXT +
            " );";

    private static final String SQL_CREATE_TABLE_ARTICLE_SOURCE =
            "CREATE TABLE " + DbContract.ArticleSource.TABLE_NAME + " (" +
                    DbContract.ArticleSource._ID + INTEGER + PRIMARY_KEY + COMMA +
                    DbContract.ArticleSource.FACEBOOK_ID + TEXT + COMMA +
                    DbContract.ArticleSource.USERNAME + TEXT + COMMA +
                    DbContract.ArticleSource.NAME + TEXT + COMMA +
                    DbContract.ArticleSource.CATEGORY + TEXT + COMMA +
                    DbContract.ArticleSource.ABOUT + TEXT + COMMA +
                    DbContract.ArticleSource.WEBSITE + TEXT + COMMA +
                    DbContract.ArticleSource.PICTURE + TEXT + COMMA +
                    DbContract.ArticleSource.CREDIBILITY + INTEGER +
            " );";

    private static final String SQL_CREATE_TABLE_KEYWORD_CATEGORY =
            "CREATE TABLE " + DbContract.Keyword_Category.TABLE_NAME + " (" +
                    DbContract.Keyword_Category._ID + INTEGER + PRIMARY_KEY + COMMA +
                    DbContract.Keyword_Category.KEYWORD_ID + INTEGER + COMMA +
                    DbContract.Keyword_Category.CATEGORY_ID + INTEGER + COMMA +
                    FOREIGN_KEY + "(" + DbContract.Keyword_Category.KEYWORD_ID + ")" +
                    REFERENCES + DbContract.Keyword.TABLE_NAME +
                    "(" + DbContract.Keyword._ID + ")" + COMMA +
                    FOREIGN_KEY + "(" + DbContract.Keyword_Category.CATEGORY_ID + ")" +
                    REFERENCES + DbContract.Category.TABLE_NAME +
                    "(" + DbContract.Category._ID + ")" +
                    " );";

    private static final String SQL_DELETE_ENTRIES_CATEGORY =
            "DROP TABLE IF EXISTS " + DbContract.Category.TABLE_NAME + ";";

    private static final String SQL_DELETE_ENTRIES_KEYWORD =
            "DROP TABLE IF EXISTS " + DbContract.Keyword.TABLE_NAME + ";";

    private static final String SQL_DELETE_ENTRIES_KEYWORD_ARTICLE =
            "DROP TABLE IF EXISTS " + DbContract.Keyword_Article.TABLE_NAME + ";";

    private static final String SQL_DELETE_ENTRIES_CATEGORY_INCIDENT =
            "DROP TABLE IF EXISTS " + DbContract.Category_Incident.TABLE_NAME + ";";

    private static final String SQL_DELETE_ENTRIES_INCIDENT =
            "DROP TABLE IF EXISTS " + DbContract.Incident.TABLE_NAME + ";";

    private static final String SQL_DELETE_ENTRIES_ARTICLE =
            "DROP TABLE IF EXISTS " + DbContract.Article.TABLE_NAME + ";";

    private static final String SQL_DELETE_ENTRIES_LOCATION =
            "DROP TABLE IF EXISTS " + DbContract.Location.TABLE_NAME + ";";

    private static final String SQL_DELETE_ENTRIES_ARTICLE_SOURCE =
            "DROP TABLE IF EXISTS " + DbContract.ArticleSource.TABLE_NAME + ";";

    private static final String SQL_DELETE_ENTRIES_TABLE_KEYWORD_CATEGORY =
            "DROP TABLE IF EXISTS " + DbContract.Keyword_Category.TABLE_NAME + ";";


    // If you change the database schema, you must increment the database version.

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_ARTICLE);
        db.execSQL(SQL_CREATE_TABLE_ARTICLE_SOURCE);
        db.execSQL(SQL_CREATE_TABLE_CATEGORY);
        db.execSQL(SQL_CREATE_TABLE_CATEGORY_INCIDENT);
        db.execSQL(SQL_CREATE_TABLE_INCIDENT);
        db.execSQL(SQL_CREATE_TABLE_KEYWORD);
        db.execSQL(SQL_CREATE_TABLE_KEYWORD_ARTICLE);
        db.execSQL(SQL_CREATE_TABLE_LOCATION);
        db.execSQL(SQL_CREATE_TABLE_KEYWORD_CATEGORY);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES_ARTICLE);
        db.execSQL(SQL_DELETE_ENTRIES_ARTICLE_SOURCE);
        db.execSQL(SQL_DELETE_ENTRIES_CATEGORY);
        db.execSQL(SQL_DELETE_ENTRIES_CATEGORY_INCIDENT);
        db.execSQL(SQL_DELETE_ENTRIES_INCIDENT);
        db.execSQL(SQL_DELETE_ENTRIES_KEYWORD);
        db.execSQL(SQL_DELETE_ENTRIES_KEYWORD_ARTICLE);
        db.execSQL(SQL_DELETE_ENTRIES_LOCATION);
        db.execSQL(SQL_DELETE_ENTRIES_TABLE_KEYWORD_CATEGORY);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}