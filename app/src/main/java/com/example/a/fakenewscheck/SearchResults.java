package com.example.a.fakenewscheck;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RunnableFuture;

public class SearchResults extends ListActivity {
    public ProgressBar pb;
    public List arrayList;
    public ResultListAdapter adapter;
    //public ArrayList<AsyncTask> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        //set listview's adapter
        arrayList = new ArrayList();
        //adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        adapter = new ResultListAdapter(this, arrayList);
        setListAdapter(adapter);

        //prepare selected search conditions
        pb = (ProgressBar) findViewById(R.id.pbSearch);
        //pb.setVisibility(View.VISIBLE);
        arrayList.clear();
        adapter.notifyDataSetChanged();


        //apply filtering details
        String[] projection = {
                DbContract.ArticleSource.NAME,
                DbContract.ArticleSource.USERNAME,
                DbContract.ArticleSource.PICTURE
        };
        ArrayList<String> selectedSources = getIntent().getStringArrayListExtra("sources");
        final ArrayList<String> selectedKeywords = getIntent().getStringArrayListExtra("keywords");

        for (String selectedSource : selectedSources) {
            String whereClause = DbContract.ArticleSource.NAME + " = '" + selectedSource + "'";
            Cursor snidcursor = null;
            try {
                snidcursor = TabbedActivity.db.query(
                        DbContract.ArticleSource.TABLE_NAME, projection, whereClause,
                        null, null, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int index = snidcursor.getColumnIndexOrThrow(DbContract.ArticleSource.USERNAME);
            int nameIndex = snidcursor.getColumnIndexOrThrow(DbContract.ArticleSource.NAME);
            String facebookId = null;
            final String name;
            final String picUrl;
            if (snidcursor.moveToFirst()) {
                try {
                    facebookId = snidcursor.getString(index);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                name = snidcursor.getString(nameIndex);
                picUrl = snidcursor.getString(
                        snidcursor.getColumnIndexOrThrow(DbContract.ArticleSource.PICTURE));
            }
            else {
                name = null;
                picUrl = null;
            }

            //send a graph request to get all post content
            final String selectedKeyword = selectedKeywords.get(0);
            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/" + facebookId + "/posts?fields=message,link,permalink_url",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            try {
                                //search if the any post contains the keyword
                                JSONObject jsonResponse = response.getJSONObject();
                                JSONArray data = jsonResponse.getJSONArray("data");

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject post = data.getJSONObject(i);
                                    /*new GetContentFromUrl().execute(post,
                                            selectedKeywords, picUrl, name);*/
                                    AsyncTask getContent;
                                    if (post.has("link")) {
                                        if (post.getString("link")
                                                .contains("https://facebook.com")) {
                                            if (post.has("message")) {
                                                //read from message
                                                new GetContentFromFbPost()
                                                        .execute(post, selectedKeywords, picUrl,
                                                                name);
                                            }
                                        }
                                        else {
                                            //read from url
                                            new GetContentFromUrl()
                                                    .execute(post,
                                                            selectedKeywords, picUrl, name);
                                        }

                                    }
                                    else if (post.has("message")) {
                                        //read from message
                                        new GetContentFromFbPost().execute(post,
                                                selectedKeywords, picUrl, name);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
            ).executeAsync();
        }
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = ((ItemObjects) (getListView().getItemAtPosition(position))).getUrl();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
    }

    /*@Override
    protected void onDestroy() {
        for (AsyncTask task : taskList) {
            if (task !=null) {
                task.cancel(true);
            }
        }
        super.onDestroy();
    }*/

    public class ResultListAdapter extends BaseAdapter {
        private LayoutInflater lInflater;
        private List<ItemObjects> listStorage;

        public ResultListAdapter(Context context, List<ItemObjects> customizedListView) {
            lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listStorage = customizedListView;
        }

        @Override
        public int getCount() {
            return listStorage.size();
        }

        @Override
        public Object getItem(int position) {
            return listStorage.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            ViewHolder listViewHolder;
            if (convertView == null) {
                listViewHolder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.found_article_list, container,
                        false);

                listViewHolder.image = (ImageView) convertView.findViewById(R.id.ivFoundPageLogo);
                listViewHolder.name = (TextView) convertView.findViewById(R.id.tvFoundPageName);
                listViewHolder.headline = (TextView) convertView.
                        findViewById(R.id.tvFoundArticleHeadline);
                listViewHolder.content = (TextView) convertView.
                        findViewById(R.id.tvFoundArticleContent);

                convertView.setTag(listViewHolder);
            }
            else {
                listViewHolder = (ViewHolder) convertView.getTag();
            }
            listViewHolder.name.setText(listStorage.get(position).getName());
            ArrayList<String> keywords = listStorage.get(position).getKeywords();

            String headline = listStorage.get(position).getHeadline();
            String content = listStorage.get(position).getContent();
            for (String keyword : keywords) {
                headline = headline.replaceAll(keyword, "<font color='red'>" + keyword + "</font>");
                content = content.replaceAll(keyword, "<font color='red'>" + keyword + "</font>");
            }
            listViewHolder.headline.setText(Html.fromHtml(headline));
            listViewHolder.content.setText(Html.fromHtml(content));

            listViewHolder.image.setImageBitmap(listStorage.get(position).getImage());
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView image;
        TextView name;
        TextView headline;
        TextView content;
    }

    public class ItemObjects {
        private String name, headline, content, url;
        private Bitmap image;
        private ArrayList<String> keywords;

        public ItemObjects(Bitmap image, String name, String headline, String content, String url,
                           ArrayList<String> keywords) {
            this.image = image;
            this.headline = headline;
            this.name = name;
            this.content = content;
            this.url = url;
            this.keywords = keywords;
        }

        public String getHeadline() {
            return headline;
        }

        public Bitmap getImage() {
            return image;
        }

        public String getName() {
            return name;
        }

        public String getContent() {
            return content;
        }

        public String getUrl() {
            return url;
        }

        public ArrayList<String> getKeywords() {
            return keywords;
        }
    }

    public class GetContentFromFbPost extends AsyncTask<Object, ItemObjects, Void> {

        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Object... object) {
            JSONObject post = (JSONObject) object[0];
            ArrayList<String> keywords = (ArrayList) object[1];
            String picUrl = (String) object[2];
            String name = (String) object[3];

            try {
                URL url = new URL(picUrl);
                Bitmap bmp = BitmapFactory.decodeStream(
                        url.openConnection().getInputStream());

                String message = post.getString("message");
                for (String keyword : keywords) {
                    if (message.contains(keyword)) {
                        String permalink_url = post.getString("permalink_url");
                        ItemObjects item = new ItemObjects(
                                bmp, name, "", message, permalink_url, keywords);
                        publishProgress(item);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(ItemObjects... values) {
            arrayList.add(values[0]);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void result) {
            pb.setVisibility(View.GONE);
        }

    }

    public class GetContentFromUrl extends AsyncTask<Object, ItemObjects, Void> {

        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Object... object) {
            JSONObject post = (JSONObject) object[0];
            ArrayList<String> keywords = (ArrayList<String>) object[1];
            String picUrl = (String) object[2];
            String name = (String) object[3];
            String html;
            String link;

            ItemObjects item = null;
            try {
                URL url = new URL(picUrl);
                Bitmap bmp = BitmapFactory.decodeStream(
                        url.openConnection().getInputStream());

                link = post.getString("link");
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(link);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity responseEntity = httpResponse.getEntity();
                InputStream inputStream = responseEntity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        inputStream, "UTF-8"), 8);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    stringBuilder.append(line + "\n");
                html = stringBuilder.toString();
                inputStream.close();

                Document doc = Jsoup.parse(html);
                String title = doc.head().getElementsByAttributeValue("property", "og:title")
                        .first().attr("content");
                String description = doc.head().getElementsByAttributeValue("property",
                        "og:description").first().attr("content");
                for (String keyword : keywords) {
                    if (title.contains(keyword) || description.contains(keyword)) {
                        item = new ItemObjects(bmp, name, title, description, link,
                                keywords);
                        //arrayList.add(item);
                        publishProgress(item);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(ItemObjects... values) {
            arrayList.add(values[0]);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void result) { pb.setVisibility(View.GONE); }
    }
}