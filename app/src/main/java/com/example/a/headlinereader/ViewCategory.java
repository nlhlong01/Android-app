package com.example.a.headlinereader;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ViewCategory extends AppCompatActivity {
    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_category);
        setTitle("Category");
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        new showTables().execute();
    }

    public class showTables extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String sortOrder = DbContract.Category._ID + " ASC";
            Cursor cursor = MainActivity.db.query(DbContract.Category.TABLE_NAME,
                    null, null, null, null, null, sortOrder);
            int indexId = cursor.getColumnIndexOrThrow(DbContract.Category._ID);
            int indexCat = cursor.getColumnIndexOrThrow(DbContract.Category.CATEGORY);
            if (cursor.moveToFirst()) {
                do {
                    String strId = cursor.getString(indexId);
                    String strCat = cursor.getString(indexCat);
                    createNewRow(strId, strCat);
                } while (cursor.moveToNext());
            }
            return null;
        }
    }

    public void createNewRow(String _id, String category) {
        TableRow tableRow = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(lp);
        TextView tvID = new TextView(this); tvID.setText(_id);
        TextView tvCategory = new TextView(this);  tvCategory.setText(category);
        tableRow.addView(tvID);
        tableRow.addView(tvCategory);
        tableRow.setClickable(true);
        /*tableRow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                v.setBackgroundColor(Color.GRAY);
            }
        });*/

        tableLayout.addView(tableRow, 1);
    }
}
