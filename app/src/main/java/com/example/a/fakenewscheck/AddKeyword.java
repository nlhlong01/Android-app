package com.example.a.fakenewscheck;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class AddKeyword extends AppCompatActivity {
    int categoryId;
    Spinner spnCategory;
    private Intent mainActIntent;
    public Context context;
    public List<String> spinnerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_keyword);
        context = this;
        setTitle("Add Keyword");
        mainActIntent = new Intent(this, TabbedActivity.class);
        mainActIntent.putExtra("view", 0);

        //create a spinner for category
        String sortOrder = DbContract.Category._ID + " ASC";
        Cursor spinnerCursor = TabbedActivity.db.query(
                DbContract.Category.TABLE_NAME,
                new String[]{DbContract.Category.CATEGORY},
                null, null, null, null, sortOrder);
        int index = spinnerCursor.getColumnIndexOrThrow(DbContract.Category.CATEGORY);
        spinnerList = new ArrayList<>();
        spnCategory = (Spinner) findViewById(R.id.spnCategory);
        if (spinnerCursor.moveToFirst()) {
            do {
                String spinnerContent = spinnerCursor.getString(index);
                spinnerList.add(spinnerContent);
            } while (spinnerCursor.moveToNext());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, spinnerList);
        /*ArrayAdapter<String> lvAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                spinnerList);*/
        spnCategory.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == android.R.id.home) {
            startActivity(mainActIntent);
            return true;
        }
        else return super.onOptionsItemSelected(item);
    }

    public void clickReaction(View v){
        EditText keywordField = (EditText) findViewById(R.id.buttonKeyword);
        String keyword = keywordField.getText().toString();

        final AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context,
                    android.R.style.Theme_Material_Dialog);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        if (keyword.equals("")) {
            builder.setTitle("Input error")
                    .setMessage("Please enter a keyword")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else if (spinnerList.isEmpty()) {
            builder.setTitle("Input error")
                    .setMessage("There must be at least a category")
                    .setNeutralButton("Add a new category", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(context, AddCategory.class));
                        }
                    })
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else {
            //get category FACEBOOK_ID from selected category name
            String selectedCategory = spnCategory.getSelectedItem().toString();
            String whereClause = DbContract.Category.CATEGORY + " = '" + selectedCategory + "'";
            Cursor storingCursor = null;
            storingCursor = TabbedActivity.db.query(
                    DbContract.Category.TABLE_NAME,
                    new String[]{DbContract.Category._ID, DbContract.Category.CATEGORY},
                    whereClause, null, null, null, null);
            int idIndex = storingCursor.getColumnIndexOrThrow(DbContract.ArticleSource._ID);
            categoryId = 0;
            if (storingCursor.moveToFirst()) {
                categoryId = storingCursor.getInt(idIndex);
            }

            //add retrieved information to table of keyword
            ContentValues values = new ContentValues();
            values.put(DbContract.Keyword.KEYWORD, keyword);
            values.put(DbContract.Keyword.CATEGORY_ID, categoryId);
            long newRowId = TabbedActivity.db.insert(DbContract.Keyword.TABLE_NAME, null, values);
            startActivity(mainActIntent);
        }
    }
}