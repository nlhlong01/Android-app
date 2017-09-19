package com.example.a.fakenewscheck;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ViewDatabase extends AppCompatActivity {
    TableLayout tableLayout;
    List<CheckBox> checkBoxList;
    int rowCount;
    String tableName;
    CheckBox cbSelectAll;
    public boolean clickable;
    private int undeletableValNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_database);
        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tableLayout = (TableLayout) findViewById(R.id.tableLayout);
        checkBoxList = new ArrayList<>();
        tableName = getIntent().getStringExtra("table");
        setTitle(tableName);
        //String sortOrder = "_id ASC";
        Cursor cursor;
        if (tableName.equals(DbContract.Keyword.TABLE_NAME)) {
            cursor = TabbedActivity.db.rawQuery("SELECT k._id, keyword, category FROM Keyword k " +
                    "JOIN Category c ON k.category_id=c._id ORDER BY keyword ASC", null);
            clickable = false;
        }
        else if (tableName.equals(DbContract.Category.TABLE_NAME)) {
            cursor = TabbedActivity.db.query(tableName,
                    new String[] {DbContract.Category._ID, DbContract.Category.CATEGORY}
                    , null, null, null, null, DbContract.Category.CATEGORY + " ASC");
            clickable = true;
        }
        else {
            cursor = TabbedActivity.db.query(tableName,
                    new String[] {DbContract.ArticleSource._ID, DbContract.ArticleSource.NAME}
                    , null, null, null, null, DbContract.ArticleSource.NAME + " ASC");
            clickable = true;
        }

        new showTables().execute(cursor);
        rowCount = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view_database, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.menu_delete) {
            deleteData();
            Bundle bundle = new Bundle();
            bundle.putString("table", tableName);
            Intent intent = new Intent(this, ViewDatabase.class);
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, TabbedActivity.class);
            intent.putExtra("view", 1);
            startActivity(intent);
            return true;
        }
        else return super.onOptionsItemSelected(item);
    }

    private class showTables extends AsyncTask <Cursor, Void, Void> {
        TableRow trColumnNames;
        List<TableRow> trRows;

        @Override
        protected Void doInBackground(Cursor... cursors) {
            trRows = new ArrayList<>();
            Cursor cursor = cursors[0];
            String colNamesStr[] = cursor.getColumnNames();
            trColumnNames = createColumnNames(colNamesStr);

            int columnCount = cursor.getColumnCount();
            String cols[] = new String[columnCount];
            if (cursor.moveToFirst()) {
                do {
                    for (int i = 0; i <= cols.length - 1; i++) {
                        cols[i] = cursor.getString(i);
                    }
                    trRows.add(createOneRow(cols, clickable));
                } while (cursor.moveToNext());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tableLayout.addView(trColumnNames, 0);
            for (TableRow row : trRows) {
                tableLayout.addView(row);
            }
            cbSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (CheckBox checkBox : checkBoxList) {
                            checkBox.setChecked(true);
                        }
                    }
                    else {
                        for (CheckBox checkBox : checkBoxList) {
                            checkBox.setChecked(false);
                        }
                    }
                }
            });
        }
    }

    private TableRow createColumnNames(String... colNames) {
        TableRow tableRow = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 100);
        tableRow.setLayoutParams(lp);
        cbSelectAll = new CheckBox(this);
        cbSelectAll.setPadding(0, 0, 20, 0);
        tableRow.addView(cbSelectAll);
        for (int i = 1; i <= colNames.length - 1; i++) {
            TextView textView = new TextView(this);
            textView.setPadding(0, 0, 20, 0);
            textView.setText(colNames[i]);
            textView.setTextSize(30);
            textView.setTypeface(null, Typeface.BOLD);
            tableRow.addView(textView);
        }
        return tableRow;
    }

    private TableRow createOneRow(String[] cols, boolean clickable) {
        final TableRow tableRow = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                100);
        tableRow.setLayoutParams(lp);
        final CheckBox checkBox = new CheckBox(this);
        checkBox.setTag(cols[0]);
        checkBoxList.add(checkBox);
        tableRow.addView(checkBox);
        for (int i = 1; i <= cols.length - 1; i++) {
            TextView textView = new TextView(this);
            textView.setText(cols[i]);
            textView.setTextSize(28);
            textView.setPadding(0, 0, 20, 0);
            tableRow.addView(textView);
        }
        rowCount++;
        if (clickable) {
            tableRow.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent i = new Intent(ViewDatabase.this, ViewDetailedDataArticleSource.class);
                    i.putExtra("table name", tableName);
                    i.putExtra("entry id", (String) checkBox.getTag());
                    startActivity(i);
                    return false;
                }
            });
            /*tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ViewDatabase.this, ViewDetailedDataArticleSource.class));
                }
            });*/
        }
        return tableRow;
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
        TabbedActivity.db.execSQL(String.format("DELETE FROM " + tableName
                + " WHERE _id IN (%s);", args));
    }
}