package com.sw.tain.earthquake.DB;

/**
 * Created by home on 2016/12/30.
 */

public class QuakeDBModel {
    public static final class QuakeTable{
        public static final String NAME = "QuakeTable";

        public static final class COL{
            public static final String KEY_ID = "_id";
            public static final String DATE = "date";
            public static final String DETAILS = "details";
            public static final String SUMMARY = "summary";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
            public static final String MAGNITUDE = "magnitude";
            public static final String LINK = "link";

        }
    }
}
