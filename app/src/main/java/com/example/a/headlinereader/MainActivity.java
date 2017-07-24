package com.example.a.headlinereader;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.database.sqlite.*;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.AccessToken;
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

public class MainActivity extends FragmentActivity {
    static final int NUM_ITEMS = 3;
    MyAdapter mAdapter;
    ViewPager mPager;
    TabLayout tabLayout;

    public static AccessToken accessToken;
    public CallbackManager callbackManager;
    public static SQLiteDatabase db;

    static Cursor kwCursor, artSrcCursor;

    static ProgressBar pb;
    static ArrayList arrayList;
    static ArrayAdapter lvAdapter;
    static Spinner spinnerKeyword, spinnerSource;
    static TextView tvResult;
    static ListView listView;

    static Context context;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        if (db == null) {
            new InitDb().execute();
        }

        //intiates tabs
        mAdapter = new MyAdapter(getSupportFragmentManager());
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mPager));
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //Bundle bundle = getIntent().getExtras();
        int currentView = getIntent().getIntExtra("view", 0);
        mPager.setCurrentItem(currentView);

        //create new access token based on app access token
        accessToken = new AccessToken("388029638213907|UVePs5EUXeidxjlOQNLQjITIiQk",
                "388029638213907", "1289452034444279", null, null, null, null, null);


//        new GetPageContent().execute();

        /*LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });*/



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    private class InitDb extends AsyncTask<Object, Object, SQLiteDatabase> {

        @Override
        protected SQLiteDatabase doInBackground(Object... params) {
            try {
                DbHelper dbHelperHelper = new DbHelper(getBaseContext());
                SQLiteDatabase sqLiteDatabase = dbHelperHelper.getReadableDatabase();
                return sqLiteDatabase;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(SQLiteDatabase sqLiteDatabase) {
            super.onPostExecute(sqLiteDatabase);
            db = sqLiteDatabase;
        }
    }

    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            return ArrayListFragment.newInstance(position);
        }
    }

    public static class ArrayListFragment extends Fragment {
        int mNum;

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
        static ArrayListFragment newInstance(int num) {
            ArrayListFragment f = new ArrayListFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);
            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v;
            if (mNum == 0) {
                v = inflater.inflate(R.layout.fragment_pager_search, container, false);
                tvResult = (TextView) v.findViewById(R.id.tvResult);
                listView = (ListView) v.findViewById(R.id.lvResult);
                arrayList = new ArrayList();
                lvAdapter = new ArrayAdapter(context,
                        android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(lvAdapter);
                pb = (ProgressBar) v.findViewById(R.id.pbSearchProgress);
                spinnerKeyword = (Spinner) v.findViewById(R.id.spinnerKeyword);
                spinnerSource = (Spinner) v.findViewById(R.id.spinnerSource);

                //get data from the table of keyword
                String keywordSortOrder = DbContract.Category._ID + " ASC";
                kwCursor = db.query(
                        DbContract.Keyword.TABLE_NAME,
                        null, null, null, null, null, keywordSortOrder);

                int indexKeyword = kwCursor.getColumnIndexOrThrow(DbContract.Keyword.KEYWORD);
                ArrayList spinnerList = new ArrayList<>();

                if (kwCursor.moveToFirst()) {
                    do {
                        String strContent = kwCursor.getString(indexKeyword);
                        spinnerList.add(strContent);
                    } while (kwCursor.moveToNext());
                }

                ArrayAdapter adapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_item, spinnerList);
                spinnerKeyword.setAdapter(adapter);

                //get data from the table of article source
                String artSrcSortOrder = DbContract.ArticleSource._ID + " ASC";
                artSrcCursor = db.query(
                        DbContract.ArticleSource.TABLE_NAME,
                        new String[]{DbContract.ArticleSource.NAME,
                                DbContract.ArticleSource.USERNAME,
                                DbContract.ArticleSource.FACEBOOK_ID},
                        null, null, null, null, artSrcSortOrder);

                //set resource for article source spinner
                int indexName = artSrcCursor.getColumnIndexOrThrow(DbContract.ArticleSource.NAME);
                spinnerList = new ArrayList<>();

                if (artSrcCursor.moveToFirst()) {
                    do {
                        String strContent = artSrcCursor.getString(indexName);
                        spinnerList.add(strContent);
                    } while (artSrcCursor.moveToNext());
                }

                adapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_item, spinnerList);
                spinnerSource.setAdapter(adapter);
            }
            else if (mNum == 1) {
                v = inflater.inflate(R.layout.fragment_pager_database, container, false);
            }
            else { v = inflater.inflate(R.layout.fragment_pager_add, container, false); }


//            TextView tv = (TextView) v.findViewById(R.id.text);
//            tv.setText("Fragment #" + mNum);
            /*if (mNum == 0) {
                View v = inflater.inflate(R.layout.fragment_pager_add, container, false);
            }
            else {View v = inflater.inflate(R.layout.fragment_pager_view, container, false)};*/
            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }

        /*@Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("FragmentList", "Item clicked: " + id);
        }*/
    }

    public void clickAdd(View v) {
        //for add options
        Intent intent;
        if (v.getId() == R.id.btnAddKeyword) {
            intent = new Intent(this, AddKeyword.class);
        }
        else if (v.getId() == R.id.btnAddCategory) {
            intent = new Intent(this, AddCategory.class);
        }
        else {
            intent = new Intent(this, AddArticleSource.class);
        }
        startActivity(intent);
    }

    public void clickData(View v) {
        //for view options
        Intent intent = new Intent(this, ViewKeyword.class);
        if (v.getId() == R.id.btnViewKeyword) {
            intent.putExtra("table", DbContract.Keyword.TABLE_NAME);
        }
        if (v.getId() == R.id.btnViewCategory) {
            intent.putExtra("table", DbContract.Category.TABLE_NAME);
        }
        if (v.getId() == R.id.btnViewArcSrc) {
            intent.putExtra("table", DbContract.ArticleSource.TABLE_NAME);
        }
        startActivity(intent);
    }

    public void clickSearch(View v) {
        //for search button
        if (v.getId() == R.id.buttonSearch) {
            //prepare selected search conditions
            pb = (ProgressBar) findViewById(R.id.pbSearchProgress);
            pb.setVisibility(View.VISIBLE);
            arrayList.clear();
            lvAdapter.notifyDataSetChanged();

            String[] projection = {
                    DbContract.ArticleSource.NAME,
                    DbContract.ArticleSource.USERNAME
            };
            String selectedSource = spinnerSource.getSelectedItem().toString();
            final String selectedKeyword = spinnerKeyword.getSelectedItem().toString();
            String whereClause = DbContract.ArticleSource.NAME + " = '" + selectedSource + "'";
            Cursor snidcursor = db.query(
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

            //send a graph request to get all post content
            new GraphRequest(
                    MainActivity.accessToken,
                    "/" + facebookId + "/posts?fields=message,link",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {

                            try {
                                //search if the any post contains the keyword
                                JSONObject jsonResponse = response.getJSONObject();
                                JSONArray data = jsonResponse.getJSONArray("data");
                                new getContentFromUrl().execute(data, selectedKeyword);
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject post = data.getJSONObject(i);
                                    String content;
                                    if (post.has("message") && !post.has("link")) {
                                        content = post.getString("message");
                                        if (content.contains(selectedKeyword)) {
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
            String content;
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
                        String line;
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

/*
            Bundle params = new Bundle();
            params.putString("message", result);
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/feed",
                    params,
                    HttpMethod.POST,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {

                        }
                    }
            ).executeAsync();
*//**//**/

}



