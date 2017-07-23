package com.example.a.headlinereader;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONObject;

public class AddArticleSource extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article_source);
        setTitle("Add Article Source");

        //create the back button
        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //set up back button's function
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }*/

    public void clickReaction(View v){
        String usernameId = ((EditText) findViewById(R.id.etUsernameId))
                .getText().toString();
        final String credit = ((EditText) findViewById(R.id.etCredibility)).getText().toString();

        new GraphRequest(
                MainActivity.accessToken,
                "/" + usernameId + "?fields=id,username,name,category,about,website",
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

                            //insert retrieved info into database
                            ContentValues values = new ContentValues();
                            values.put(DbContract.ArticleSource.FACEBOOK_ID, facebookId);
                            values.put(DbContract.ArticleSource.USERNAME, username);
                            values.put(DbContract.ArticleSource.NAME, name);
                            values.put(DbContract.ArticleSource.CATEGORY, category);
                            values.put(DbContract.ArticleSource.ABOUT, about);
                            values.put(DbContract.ArticleSource.WEBSITE, website);
                            values.put(DbContract.ArticleSource.CREDIBILITY, credit);
                            long newRowId = MainActivity.db.insert(DbContract.ArticleSource.TABLE_NAME,
                                    null, values);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

        startActivity(new Intent(this, MainActivity.class));
    }
}
