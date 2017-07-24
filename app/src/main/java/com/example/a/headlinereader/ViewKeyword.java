package com.example.a.headlinereader;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ViewKeyword extends AppCompatActivity {
    TableLayout tableLayout;
    List<CheckBox> checkBoxList;
    int rowCount;
    String tableName;
    CheckBox cbSelectAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_keyword);
        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        checkBoxList = new ArrayList<>();
//        cbSelectAll = (CheckBox) findViewById(R.id.cbSelectAll);

        //find out which type of data is going to be shown
        /*Bundle bundle = getIntent().getExtras();
        dataType = bundle.getString("type");
        setTitle(dataType);

        String sortOrder;
        if (dataType.equals("Keyword")) {
            tableName = DbContract.Keyword.TABLE_NAME;
            sortOrder = DbContract.Keyword._ID + " ASC";
        }
        else if (dataType.equals("Category")) {
            tableName = DbContract.Category.TABLE_NAME;
            sortOrder = DbContract.Category._ID + " ASC";
        }
        else  {
            tableName = DbContract.ArticleSource.TABLE_NAME;
            sortOrder = DbContract.ArticleSource._ID + " ASC";
        }*/
        tableName = getIntent().getStringExtra("table");
        setTitle(tableName);
        String sortOrder = "_id ASC";
        new showTables().execute(tableName, sortOrder);
        rowCount = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_show, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.menu_delete) {
            deleteData();
            Bundle bundle = new Bundle();
            bundle.putString("table", tableName);
            Intent intent = new Intent(this, ViewKeyword.class);
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("view", 1);
            startActivity(intent);
            return true;
        }
        else return super.onOptionsItemSelected(item);
    }

    private class showTables extends AsyncTask <String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Cursor cursor = MainActivity.db.query(params[0],
                    null, null, null, null, null, params[1]);

            String colNames[] = cursor.getColumnNames();
            createFirstRow(colNames);

            int columnCount = cursor.getColumnCount();
            String cols[] = new String[columnCount];
            if (cursor.moveToFirst()) {
                do {
                    for (int i = 0; i <= cols.length - 1; i++) {
                        cols[i] = cursor.getString(i);
                    }
                    createNewRow(cols);
                } while (cursor.moveToNext());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            cbSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    for (CheckBox checkBox : checkBoxList) {
                        checkBox.setChecked(true);
                    }
                }
            });
        }
    }

    private void createFirstRow(String... colNames) {
        TableRow tableRow = new TableRow(this);
        cbSelectAll = new CheckBox(this);
        cbSelectAll.setPadding(0, 0, 20, 0);
        tableRow.addView(cbSelectAll);
        for (int i = 0; i <= colNames.length - 1; i++) {
            TextView textView = new TextView(this);
            textView.setPadding(0, 0, 20, 0);
            textView.setText(colNames[i]);
            tableRow.addView(textView);
        }
        tableLayout.addView(tableRow, 0);
    }

    private void createNewRow(String... cols) {
        TableRow tableRow = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(lp);

        CheckBox checkBox = new CheckBox(this);
        checkBox.setTag(cols[0]);
        checkBoxList.add(checkBox);
        tableRow.addView(checkBox);
        for (int i = 0; i <= cols.length - 1; i++) {
            TextView textView = new TextView(this);
            textView.setText(cols[i]);
            tableRow.addView(textView);
        }
        tableLayout.addView(tableRow, 1);
        rowCount++;
    }

    public void deleteData() {
        String ids[] = new String[rowCount];
        int i = 0;
        for (CheckBox checkBox : checkBoxList) {
            if (checkBox.isChecked()) {
                ids[i] = (String) checkBox.getTag();
                i++;
            }
        }
        String args = TextUtils.join(", ", ids);
        MainActivity.db.execSQL(String.format("DELETE FROM " + tableName
                + " WHERE _id IN (%s);", args));
    }
}