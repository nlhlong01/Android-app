package com.example.a.fakenewscheck;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONObject;

import java.util.ArrayList;

public class AddArticleSource extends AppCompatActivity {
    private Intent mainActIntent;
    public EditText etFullName;
    public String usernameId;
    public Spinner spnCredibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article_source);
        setTitle("Add Article Source");
        usernameId = null;
        usernameId = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        String fullName = getIntent().getStringExtra(Intent.EXTRA_UID);
        etFullName = (EditText) findViewById(R.id.etFullName);
        spnCredibility = (Spinner) findViewById(R.id.spnCredibility);
        ArrayAdapter spnCreAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,
                new String[] {"1", "2", "3", "4", "5"});
        spnCredibility.setAdapter(spnCreAdapter);
        if (usernameId != null) {
            etFullName.setText(fullName);
        }

        mainActIntent = new Intent(this, TabbedActivity.class);
        mainActIntent.putExtra("view", 0);


        etFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddArticleSource.this, SearchForFbPage.class));
            }
        });
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
        final String credit = spnCredibility.getSelectedItem().toString();

        new GraphRequest(
                TabbedActivity.accessToken,
                "/" + usernameId + "?fields=id,username,name,category,about,website,picture",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            //get article source info from graph api
                            JSONObject jsonResponse = response.getJSONObject();
                            String facebookId = jsonResponse.getString("id");
                            String username = jsonResponse.getString("username");
                            String name = jsonResponse.getString("name");
                            String category = jsonResponse.getString("category");
                            String about = jsonResponse.getString("about");
                            String website = jsonResponse.getString("website");
                            String picture = jsonResponse.getJSONObject("picture").
                                    getJSONObject("data").getString("url");

                            //insert retrieved info into database
                            ContentValues values = new ContentValues();
                            values.put(DbContract.ArticleSource.FACEBOOK_ID, facebookId);
                            values.put(DbContract.ArticleSource.USERNAME, username);
                            values.put(DbContract.ArticleSource.NAME, name);
                            values.put(DbContract.ArticleSource.CATEGORY, category);
                            values.put(DbContract.ArticleSource.ABOUT, about);
                            values.put(DbContract.ArticleSource.WEBSITE, website);
                            values.put(DbContract.ArticleSource.PICTURE, picture);
                            values.put(DbContract.ArticleSource.CREDIBILITY, credit);
                            long newRowId = TabbedActivity.db.insert(
                                    DbContract.ArticleSource.TABLE_NAME, null, values);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
        startActivity(mainActIntent);
    }
}
