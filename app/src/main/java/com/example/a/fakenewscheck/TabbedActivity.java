package com.example.a.fakenewscheck;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;

import java.util.ArrayList;

public class TabbedActivity extends AppCompatActivity {
    static final int NUM_ITEMS = 2;
    TabAdapter mAdapter;
    ViewPager mPager;
    TabLayout tabLayout;

    public static AccessToken accessToken;
    public CallbackManager callbackManager;
    public static SQLiteDatabase db;

    static Cursor kwCursor, artSrcCursor;

    static MultiSpinner spinnerKeyword;
    static MultiSpinner spinnerSource;

    static Context context;
    public int initCateNum;
    private int initKwNum;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        //if database has been created, show the tables immediately
        if (db == null) { new InitDb().execute(); }
        else { initPager(); }
        accessToken = AccessToken.getCurrentAccessToken();

        //set up floating action button (add button)
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(context,
                            android.R.style.Theme_Material_Dialog);
                } else {
                    builder = new AlertDialog.Builder(context);
                }
                builder.setTitle("Choose type of data")
                        .setItems(new String[] {"Keyword", "Category", "Article Source"},
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selectedType = ((AlertDialog)dialog).getListView()
                                        .getItemAtPosition(which).toString();
                                if (selectedType.equals("Keyword")) {
                                    startActivity(new Intent(context, AddKeyword.class));
                                }
                                else if (selectedType.equals("Category")) {
                                    startActivity(new Intent(context, AddCategory.class));
                                }
                                else startActivity(new Intent(context, AddArticleSource.class));
                            }
                        })
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.menu_logout) {
            LoginManager.getInstance().logOut();
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        else return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private class InitDb extends AsyncTask<Object, Object, SQLiteDatabase> {

        @Override
        protected SQLiteDatabase doInBackground(Object... params) {
            //creates database
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

            /*//insert initial categories
            String[] initCategories = {"general", "fire", "storm", "flood", "blackout",
                    "terrorist attack"};
            for (int i = 0; i < initCategories.length; i++) {
                ContentValues values = new ContentValues();
                values.put(DbContract.Category.CATEGORY, initCategories[i]);
                long newRowId;
                newRowId = db.insert(DbContract.Category.TABLE_NAME,null,values);
            }

            //insert initial keyword
            String[][] initKeywords = {
                    {"1", "Sperrung"}, {"1", "Einsatz"}, {"1", "Gefahr"}, {"1", "Notfall"},
                    {"1", "Evakuierung"}, {"1", "Rettung"}, {"1", "gefährlich"},
                    {"1", "Feuerwehr"}, {"1", "Polizei"}, {"1", "Gefährdung"}, {"1", "verletzt"},

                    {"2", "Feuer"}, {"2", "Brand"}, {"2", "brennt"},

                    {"3", "Sturm"}, {"3", "Unwettere"}, {"3", "Stürmen"}, {"3", "Gewitter"},

                    {"4", "geflutet"}, {"4", "Überflutung"}, {"4", "Hochwasser"},

                    {"5", "Stromausfall"},

                    {"6", "Terror"}, {"6", "Anschlag"}, {"6", "Bombe"}
            };
            for (int i = 0; i < initKeywords.length; i++) {
                ContentValues values = new ContentValues();
                values.put(DbContract.Keyword.CATEGORY_ID, initKeywords[i][0]);
                values.put(DbContract.Keyword.KEYWORD, initKeywords[i][1]);
                long newRowId;
                newRowId = db.insert(DbContract.Keyword.TABLE_NAME,null,values);
            }*/

            initPager();
        }
    }

    public class TabAdapter extends FragmentPagerAdapter {
        public TabAdapter(FragmentManager fm) {
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
        public static ArrayListFragment newInstance(int num) {
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
                //arrayList = new ArrayList();
                /*lvAdapter = new ArrayAdapter(context,
                        android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(lvAdapter);
                pb = (ProgressBar) v.findViewById(R.id.pbSearchProgress);*/
                spinnerKeyword = (MultiSpinner) v.findViewById(R.id.spinnerKeyword);
                spinnerSource = (MultiSpinner) v.findViewById(R.id.spinnerSource);

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

                /*ArrayAdapter adapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_item, spinnerList);
                spinnerKeyword.setAdapter(adapter);*/
                spinnerKeyword.setItems(spinnerList, new spinnerListener());

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

                spinnerSource.setItems(spinnerList, new spinnerListener());
                /*adapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_item, spinnerList);
                spinnerSource.setAdapter(adapter);*/

            }
            else v = inflater.inflate(R.layout.fragment_pager_database, container, false);
            /*else if (mNum == 1) {
                v = inflater.inflate(R.layout.fragment_pager_database, container, false);
            }
            else { v = inflater.inflate(R.layout.fragment_pager_add, container, false); }*/

            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }
    }

    public static class spinnerListener implements MultiSpinner.MultiSpinnerListener {

        @Override
        public void onItemsSelected(boolean[] selected) {

        }
    }

    private void initPager() {
        //initiate tabs
        mAdapter = new TabAdapter(getSupportFragmentManager());
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mPager));
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //Bundle bundle = getIntent().getExtras();
        int currentView = getIntent().getIntExtra("view", 0);
        mPager.setCurrentItem(currentView);
        ProgressBar pbPager = (ProgressBar) findViewById(R.id.pbPager);
        pbPager.setVisibility(View.GONE);
    }

    /*public void clickAdd(View v) {
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
    }*/

    public void clickData(View v) {
        //for view options
        Intent intent = new Intent(this, ViewDatabase.class);
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

        //warning dialog: proper search condition
        if (spinnerKeyword.getSelectedItem().toString().equals("") ||
                spinnerSource.getSelectedItem().toString().equals("")) {
            final AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context,
                        android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(context);
            }
            builder.setTitle("Search condition")
                    .setMessage("Please choose at least 1 keyword and 1 article source!")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        /*if (!isOnline() || accessToken.isExpired()) {
            final AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context,
                        android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(context);
            }
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert);
            if (!isOnline()) {
                builder.setTitle("No internet connection")
                        .setMessage("Please connect to the internet")
                        .show();
            }
            else {
                builder.setTitle("Access token is expired")
                        .setMessage("Please log in to facebook again")
                        .setPositiveButton("Log in", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(context, MainActivity.class));
                            }
                        })
                        .show();
            }
        }*/
        else {
            Intent intent = new Intent(this, SearchResults.class);
            boolean[] selectedKeywordPos = spinnerKeyword.getSelected();
            boolean[] selectedSourcePos = spinnerSource.getSelected();
            ArrayList<String> sourceList = new ArrayList<>();
            for (int i = 0; i < selectedSourcePos.length; i++) {
                if (selectedSourcePos[i]) {
                    sourceList.add(spinnerSource.getItemAtPosition(i));
                }
            }
            ArrayList<String> keywordList = new ArrayList<>();
            for (int i = 0; i < selectedKeywordPos.length; i++) {
                if (selectedKeywordPos[i]) {
                    keywordList.add(spinnerKeyword.getItemAtPosition(i));
                }
            }
            intent.putStringArrayListExtra("sources", sourceList);
            intent.putStringArrayListExtra("keywords", keywordList);
            /*String selectedKeyword = spinnerKeyword.getSelectedItem().toString();
            String selectedSource = spinnerSource.getSelectedItem().toString();
            intent.putExtra("source", selectedSource);
            intent.putExtra("keyword", selectedKeyword);*/
            startActivity(new Intent(intent));
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    /*public void clickLogout (View v) {
        LoginManager.getInstance().logOut();
        startActivity(new Intent(this, MainActivity.class));
    }*/

    /*public static class StateVO {
        private String title;
        private boolean selected;

        public StateVO(String title) {
            setTitle(title);
            setSelected(false);
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    public static class SpinnerAdapter extends ArrayAdapter<StateVO> {
        private Context mContext;
        private ArrayList<StateVO> listState;
        private SpinnerAdapter myAdapter;
        private boolean isFromView = false;
        private int resource;

        public SpinnerAdapter(Context context, int resource, List<StateVO> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.listState = (ArrayList<StateVO>) objects;
            this.myAdapter = this;
            this.resource = resource;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(final int position, View convertView,
                                  ViewGroup parent) {

            final ViewHolder holder;
            if (convertView == null) {
                LayoutInflater layoutInflator = LayoutInflater.from(mContext);
                convertView = layoutInflator.inflate(R.layout.spinner_checkbox_item, null);
                holder = new ViewHolder();
                holder.mTextView = (TextView) convertView
                        .findViewById(R.id.text);
                holder.mCheckBox = (CheckBox) convertView
                        .findViewById(R.id.checkbox);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.mTextView.setText(listState.get(position).getTitle());

            // To check whether check event fired from getview() or user input
            isFromView = true;
            holder.mCheckBox.setChecked(listState.get(position).isSelected());
            isFromView = false;

            if ((position == 0)) {
                holder.mCheckBox.setVisibility(View.INVISIBLE);
            } else {
                holder.mCheckBox.setVisibility(View.VISIBLE);
            }
            holder.mCheckBox.setTag(listState.get(position).getTitle());
            holder.mCheckBox.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                }
            });
            return convertView;
        }

        private class ViewHolder {
            private TextView mTextView;
            private CheckBox mCheckBox;
        }
    }*/
}