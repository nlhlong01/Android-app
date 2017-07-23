package com.example.a.headlinereader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ViewTables extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tables);
    }

    /*public void clickReaction(View v) {
        if (v.getId() == R.id.btnKeyword) {
            startActivity(new Intent(this, ViewKeyword.class));
        }
        if (v.getId() == R.id.tvCategory) {
            startActivity(new Intent(this, ViewCategory.class));
        }
        if (v.getId() == R.id.tvArticleSource) {
            startActivity(new Intent(this, ViewArticleSource.class));
        }
    }*/
}
