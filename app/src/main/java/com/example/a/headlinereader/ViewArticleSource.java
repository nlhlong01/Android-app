package com.example.a.headlinereader;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ViewArticleSource extends AppCompatActivity {
    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_article_source);
        setTitle("Article Source");
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        new showTables().execute();
    }

    public class showTables extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String sortOrder = DbContract.ArticleSource._ID + " ASC";
            Cursor cursor = MainActivity.db.query(DbContract.ArticleSource.TABLE_NAME,
                    null, null, null, null, null, sortOrder);
            int indexId = cursor.getColumnIndexOrThrow(DbContract.ArticleSource._ID);
            int indexFacebookId = cursor.getColumnIndexOrThrow(DbContract
                        .ArticleSource.FACEBOOK_ID);
            int indexUsername = cursor.getColumnIndexOrThrow(DbContract
                    .ArticleSource.USERNAME);
            int indexName = cursor.getColumnIndexOrThrow(DbContract
                    .ArticleSource.NAME);
            int indexCategory = cursor.getColumnIndexOrThrow(DbContract
                    .ArticleSource.CATEGORY);
            int indexAbout = cursor.getColumnIndexOrThrow(DbContract
                    .ArticleSource.ABOUT);
            int indexWebsite = cursor.getColumnIndexOrThrow(DbContract
                    .ArticleSource.WEBSITE);
            int indexCredibility = cursor.getColumnIndexOrThrow(DbContract
                    .ArticleSource.CREDIBILITY);
            if (cursor.moveToFirst()) {
                do {
                    String strId = cursor.getString(indexId);
                    String strFacebookId = cursor.getString(indexFacebookId);
                    String strUsername = cursor.getString(indexUsername);
                    String strName = cursor.getString(indexName);
                    String strCategory = cursor.getString(indexCategory);
                    String strAbout = cursor.getString(indexAbout);
                    String strWebsite = cursor.getString(indexWebsite);
                    String strCredibility = cursor.getString(indexCredibility);
                    createNewRow(strId, strFacebookId, strUsername, strName, strCategory, strAbout,
                            strWebsite, strCredibility);
                } while (cursor.moveToNext());
            }
            return null;
        }
    }

    public void createNewRow(String id, String facebookId, String username, String name,
                             String category, String about, String website, String credibility) {
        TableRow tableRow = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(lp);

        TextView tvID = new TextView(this);
        TextView tvFacebookId = new TextView(this);
        TextView tvUsername = new TextView(this);
        TextView tvName = new TextView(this);
        TextView tvCategory = new TextView(this);
        TextView tvAbout = new TextView(this);
        TextView tvWebsite = new TextView(this);
        TextView tvCredibility = new TextView(this);

        tvID.setText(id);
        tvFacebookId.setText(facebookId);
        tvUsername.setText(username);
        tvName.setText(name);
        tvCategory.setText(category);
        tvAbout.setText(about);
        tvWebsite.setText(website);
        tvCredibility.setText(credibility);

        tableRow.addView(tvID);
        tableRow.addView(tvFacebookId);
        tableRow.addView(tvUsername);
        tableRow.addView(tvName);
        tableRow.addView(tvCategory);
        tableRow.addView(tvAbout);
        tableRow.addView(tvWebsite);
        tableRow.addView(tvCredibility);
        tableLayout.addView(tableRow, 1);
    }
}