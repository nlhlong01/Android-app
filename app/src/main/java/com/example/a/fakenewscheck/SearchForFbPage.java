package com.example.a.fakenewscheck;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchForFbPage extends ListActivity {
    public List<ItemObjects> list;
    public MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_fb_page);
        onSearchRequested();
        list = new ArrayList<>();
        handleIntent(getIntent());
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parent.getItemAtPosition(position);
                ItemObjects clickedItem = (ItemObjects) getListView().getAdapter().
                        getItem(position);
                String fbId = clickedItem.getId();
                String fbName = clickedItem.getName();
                Intent i = new Intent(SearchForFbPage.this, AddArticleSource.class);
                i.putExtra(Intent.EXTRA_TEXT, fbId);
                i.putExtra(Intent.EXTRA_UID, fbName);
                startActivity(i);
            }
        });

        // Get the intent, verify the action and get the query

        adapter = new MyAdapter(SearchForFbPage.this, list);
        setListAdapter(adapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            new GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/search?q=" + query + "&type=page&limit=20&&fields=picture,about,name," +
                            "is_verified",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            JSONObject jsonResponse = response.getJSONObject();
                            new SetListAdapter().execute(jsonResponse);
                        }
                    }).executeAsync();
        }
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater lInflater;
        private List<ItemObjects> listStorage;

        public MyAdapter(Context context, List<ItemObjects> customizedListView) {
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
                convertView = getLayoutInflater().inflate(R.layout.fb_page_list, container, false);

                listViewHolder.image = (ImageView) convertView.findViewById(R.id.ivFoundPageLogo);
                listViewHolder.name = (TextView) convertView.findViewById(R.id.tvFoundPageName);
                listViewHolder.about = (TextView) convertView.findViewById(R.id.tvFoundArticleHeadline);

                convertView.setTag(listViewHolder);
            }
            else {
                listViewHolder = (ViewHolder) convertView.getTag();
            }
            listViewHolder.name.setText(listStorage.get(position).getName());
            listViewHolder.about.setText(listStorage.get(position).getAbout());
            listViewHolder.image.setImageBitmap(listStorage.get(position).getImage());
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView image;
        TextView name;
        TextView about;
    }

    public class ItemObjects {
        private String name, about, id;
        private Bitmap image;

        public ItemObjects(Bitmap image, String name, String about, String id) {
            this.image = image;
            this.about = about;
            this.name = name;
            this.id = id;
        }

        public String getAbout() {
            return about;
        }

        public Bitmap getImage() {
            return image;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }


    }

    public class SetListAdapter extends AsyncTask<JSONObject, ItemObjects, Void>{

        @Override
        protected Void doInBackground(JSONObject... params) {
            JSONObject jsonResponse = params[0];
            try {
                JSONArray data = jsonResponse.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject page = data.getJSONObject(i);
                    String name = page.getString("name");
                    String about = page.getString("about");
                    String id = page.getString("id");
                    String picUrl = page.getJSONObject("picture").
                            getJSONObject("data").getString("url");

                    URL url = new URL(picUrl);
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    ItemObjects itemObjects = new ItemObjects(bmp, name, about, id);
                    publishProgress(itemObjects);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(SearchForFbPage.ItemObjects... values) {
            super.onProgressUpdate(values);
            list.add(values[0]);
            adapter.notifyDataSetChanged();
        }
    }
}