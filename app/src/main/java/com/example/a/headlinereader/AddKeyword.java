package com.example.a.headlinereader;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NavUtils;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_keyword);
        setTitle("Add Keyword");

        //create a spinner for category
        String sortOrder = DbContract.Category._ID + " ASC";
        Cursor spinnerCursor = MainActivity.db.query(
                DbContract.Category.TABLE_NAME,
                new String[]{DbContract.Category.CATEGORY},
                null,
                null,
                null,
                null,
                sortOrder);
        int index = spinnerCursor.getColumnIndexOrThrow(DbContract.Category.CATEGORY);
        List<String> spinnerList = new ArrayList<>();
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

        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return getFragmentManager().popBackStackImmediate();
        /*switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }*/
    }

    public void clickReaction(View v){
        EditText keywordField = (EditText) findViewById(R.id.buttonKeyword);
        String keyword = keywordField.getText().toString();

        //get category FACEBOOK_ID from selected category name
        String selectedCategory = spnCategory.getSelectedItem().toString();
        String whereClause = DbContract.Category.CATEGORY + " = '" + selectedCategory + "'";
        Cursor storingCursor = null;
        storingCursor = MainActivity.db.query(
                DbContract.Category.TABLE_NAME,
                new String[]{DbContract.Category._ID, DbContract.Category.CATEGORY},
                whereClause,
                null,
                null,
                null,
                null);
        int idIndex = storingCursor.getColumnIndexOrThrow(DbContract.ArticleSource._ID);
        categoryId = 0;
        if (storingCursor.moveToFirst()) {
            categoryId = storingCursor.getInt(idIndex);
        }

        //add retrieved information to table of keyword
        ContentValues values = new ContentValues();
        values.put(DbContract.Keyword.KEYWORD, keyword);
        values.put(DbContract.Keyword.CATEGORY_ID, categoryId);
        long newRowId = MainActivity.db.insert(DbContract.Keyword.TABLE_NAME
                , null, values);
        startActivity(new Intent(this, MainActivity.class));
    }
}