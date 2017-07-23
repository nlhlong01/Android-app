package com.example.a.headlinereader;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {
    public CallbackManager callbackManager;
    public Spinner spinnerKeyword;
    public Spinner spinnerSource;
    public static TextView tvResult;
    public ListView listView;
    public ArrayList arrayList;
    public ArrayAdapter lvAdapter;
    public ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        spinnerKeyword = (Spinner) findViewById(R.id.spinnerKeyword);
        spinnerSource = (Spinner) findViewById(R.id.spinnerSource);

        /*FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);*/

        tvResult = (TextView) findViewById(R.id.tvResult);
        listView = (ListView) findViewById(R.id.lvResult);
        arrayList = new ArrayList();
        lvAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(lvAdapter);
        pb = (ProgressBar) findViewById(R.id.pbSearchProgress);
        setSpinnerResource();
    }

    public void setSpinnerResource () {
        String sortOrder = DbContract.Category._ID + " ASC";
        int index;
        List<String> spinnerList;
        String strContent;
        String[] spinnerArray;
        ArrayAdapter<String> adapter;

        Cursor cursor = MainActivity.db.query(
                DbContract.Keyword.TABLE_NAME,
                new String[]{DbContract.Keyword.KEYWORD}, null, null, null, null, sortOrder);
        index = cursor.getColumnIndexOrThrow(DbContract.Keyword.KEYWORD);
        spinnerList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                strContent = cursor.getString(index);
                spinnerList.add(strContent);
            } while (cursor.moveToNext());
        }

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, spinnerList);
        spinnerKeyword.setAdapter(adapter);

        //set resources of article source spinner
        Cursor sourceCursor = null;
        try {
            sourceCursor = MainActivity.db.query(
                    DbContract.ArticleSource.TABLE_NAME,
                    new String[]{DbContract.ArticleSource.NAME}, null, null, null, null, sortOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        index = sourceCursor.getColumnIndexOrThrow(DbContract.ArticleSource.NAME);
        spinnerList = new ArrayList<>();

        if (sourceCursor.moveToFirst()) {
            do {
                strContent = sourceCursor.getString(index);
                spinnerList.add(strContent);
            } while (sourceCursor.moveToNext());
        }

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, spinnerList);
        spinnerSource.setAdapter(adapter);
    }

    public void clickReaction(View v) {
        pb.setVisibility(View.VISIBLE);
        arrayList.clear();
        lvAdapter.notifyDataSetChanged();

        String[] projection = {
                DbContract.ArticleSource.NAME,
                DbContract.ArticleSource.USERNAME
        };
        String selectedSource = spinnerSource.getSelectedItem().toString();
        String selectedKeyword = spinnerKeyword.getSelectedItem().toString();
        String whereClause = DbContract.ArticleSource.NAME + " = '" + selectedSource + "'";
        Cursor snidcursor = MainActivity.db.query(
                DbContract.ArticleSource.TABLE_NAME, projection, whereClause,
                null, null, null, null);
        int index = snidcursor.getColumnIndexOrThrow(DbContract.ArticleSource.USERNAME);
        String facebookId = null;
        if (snidcursor.moveToFirst()) {
            try {
                facebookId = snidcursor.getString(index);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getAllPostsContent(facebookId, selectedKeyword);
    }

    public void getAllPostsContent(String facebookId, final String keyword) {
        new GraphRequest(
                MainActivity.accessToken,
                "/" + facebookId + "/posts?fields=message,link",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        try {
                            JSONObject jsonResponse = response.getJSONObject();
                            JSONArray data = jsonResponse.getJSONArray("data");
                            new getContentFromUrl().execute(data, keyword);
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject post = data.getJSONObject(i);
                                String content = null;
                                if (post.has("message") && !post.has("link")) {
                                    content = post.getString("message");
                                    if (content.contains(keyword)) {
                                        arrayList.add(content);
                                        lvAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        pb.setVisibility(View.GONE);
                    }
                }
        ).executeAsync();
    }

    public class getContentFromUrl extends AsyncTask<Object, String, Void> {

        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Object... object) {
            JSONArray data = (JSONArray) object[0];
            String keyword = (String) object[1];
            String html = null;
            String content = null;
            try {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject post = data.getJSONObject(i);
                    if (post.has("link")) {
                        String url = post.getString("link");
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpGet httpGet = new HttpGet(url);
                        HttpResponse httpResponse = httpClient.execute(httpGet);
                        HttpEntity responseEntity = httpResponse.getEntity();
                        InputStream inputStream = responseEntity.getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                inputStream, "iso-8859-1"), 8);
                        StringBuilder stringBuilder = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null)
                            stringBuilder.append(line + "\n");
                        html = stringBuilder.toString();
                        inputStream.close();
                    }
                    Document doc = Jsoup.parse(html);
                    String title = doc.head().getElementsByAttributeValue("property", "og:title")
                            .first().attr("content");
                    String description = doc.head().getElementsByAttributeValue("property", "og:description")
                            .first().attr("content");
                    content = title + "\n" + description;
                    if (content.contains(keyword)) {
                        publishProgress(content);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            String content = progress[0];
            arrayList.add(content);
            lvAdapter.notifyDataSetChanged();
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            pb.setVisibility(View.GONE);
        }
    }
}
