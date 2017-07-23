package com.example.a.headlinereader;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class AddCategory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        setTitle("Add Category");

        //create the back button
        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //set up back button's function
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void clickReaction (View v){
        EditText categoryField = (EditText) findViewById(R.id.etCategory);
        String category = categoryField.getText().toString();

        ContentValues values = new ContentValues();
        values.put(DbContract.Category.CATEGORY, category);
        long newRowId = MainActivity.db.insert(DbContract.Category.TABLE_NAME
                , null, values);

        startActivity(new Intent(this, MainActivity.class));
    }
}
