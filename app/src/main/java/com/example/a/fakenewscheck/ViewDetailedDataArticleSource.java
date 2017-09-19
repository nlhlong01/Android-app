package com.example.a.fakenewscheck;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import java.net.URL;

public class ViewDetailedDataArticleSource extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cursor cursor;
        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_view_detailed_data_article_source);
        cursor = TabbedActivity.db.query(DbContract.ArticleSource.TABLE_NAME, null,
                "_id = " + getIntent().getStringExtra("entry id"), null, null, null, null);
        if (cursor.moveToFirst()) {
            String picUrl = cursor.getString(cursor.getColumnIndexOrThrow("picture"));
            new GetImage().execute(picUrl);
            TextView tvFbId = (TextView) findViewById(R.id.tvDetDataFbId);
            tvFbId.setText("Facebook ID: " + cursor.getString(cursor.getColumnIndexOrThrow("facebook_id")));
            TextView tvUsrname = (TextView) findViewById(R.id.tvDetDataUsrname);
            tvUsrname.setText("Username: " + cursor.getString(cursor.getColumnIndexOrThrow("username")));
            TextView tvName = (TextView) findViewById(R.id.tvDetDataName);
            tvName.setText("Name: " + cursor.getString(cursor.getColumnIndexOrThrow("name")));
            TextView tvCat = (TextView) findViewById(R.id.tvDetDataCat);
            tvCat.setText("Category: " + cursor.getString(cursor.getColumnIndexOrThrow("category")));
            TextView tvAbt = (TextView) findViewById(R.id.tvDetDataAbt);
            tvAbt.setText("About: " + cursor.getString(cursor.getColumnIndexOrThrow("about")));
            TextView tvWbst = (TextView) findViewById(R.id.tvDetDataWbst);
            tvWbst.setText("Website: " + cursor.getString(cursor.getColumnIndexOrThrow("website")));
            RatingBar rbCre = (RatingBar) findViewById(R.id.ratingBar);
            rbCre.setIsIndicator(true);
            rbCre.setRating(Integer.valueOf(
                    cursor.getString(cursor.getColumnIndexOrThrow("credibility"))));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, ViewDatabase.class);
            intent.putExtra("table", "ArticleSource");
            startActivity(intent);
            return true;
        }
        else return super.onOptionsItemSelected(item);
    }

    public class GetImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            String strUrl = params[0];
            URL url = null;
            Bitmap bmp = null;
            try {
                url = new URL(strUrl);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView ivPicture = (ImageView) findViewById(R.id.ivDetDataPicture);
            ivPicture.setImageBitmap(bitmap);
        }
    }
}
