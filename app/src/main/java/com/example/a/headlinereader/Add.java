package com.example.a.headlinereader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Add extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
    }

    /*public void clickReaction(View v) {
        if (v.getId() == R.id.buttonKeyword) {
            startActivity(new Intent(this, AddKeyword.class));
        }
        if (v.getId() == R.id.buttonCategory) {
            startActivity(new Intent(this, AddCategory.class));
        }
        if (v.getId() == R.id.buttonArticleSource) {
            startActivity(new Intent(this, AddArticleSource.class));
        }
    }*/
}
