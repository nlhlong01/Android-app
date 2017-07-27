package com.example.a.fakenewscheck;

import android.provider.BaseColumns;

/**
 * Created by amcprak on 29.03.17.
 */

public final class DbContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DbContract() {}

    /* Inner class that defines the table contents */
    public static class Category implements BaseColumns {
        public static final String TABLE_NAME = "Category";
        public static final String CATEGORY = "category";
    }

    public static class Keyword implements BaseColumns {
        public static final String TABLE_NAME = "Keyword";
        public static final String KEYWORD = "keyword";
        public static final String CATEGORY_ID = "category_ID";
    }

    public static class Keyword_Article implements BaseColumns {
        public static final String TABLE_NAME = "Keyword_Article";
        public static final String KEYWORD_ID = "keyword_ID";
        public static final String ARTICLE_ID = "article_ID";
    }

    public static class Category_Incident implements BaseColumns {
        public static final String TABLE_NAME = "Category_Incident";
        public static final String CATEGORY_ID = "category_ID";
        public static final String INCIDENT_ID = "incident_ID";
    }

    public static class Incident implements BaseColumns {
        public static final String TABLE_NAME = "Incident";
        public static final String DATE_TIME = "date_time";
        public static final String DESCRIPTION = "description";
    }

    public static class Article implements BaseColumns {
        public static final String TABLE_NAME = "Article";
        public static final String POST_ID_NUMBER = "postID_number";
        public static final String POST_ID_TEXT = "postID_text";
        public static final String CREATE_TIME = "create_time";
        public static final String MESSAGE = "message";
        public static final String INCIDENT_ID = "incident_ID";
        public static final String SOURCE_ID = "source_ID";
        public static final String ARTICLE_TYPE = "article_type";
    }

    public static class Location implements BaseColumns {
        public static final String TABLE_NAME = "Location";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String ZIP_CODE = "ZIP_code";
        public static final String CITY = "city";
    }

    public static class ArticleSource implements BaseColumns {
        public static final String TABLE_NAME = "ArticleSource";
        public static final String FACEBOOK_ID = "facebook_id";
        public static final String USERNAME = "username";
        public static final String NAME = "name";
        public static final String CATEGORY = "category";
        public static final String ABOUT = "about";
        public static final String WEBSITE = "website";
        public static final String CREDIBILITY = "credibility";
    }
}