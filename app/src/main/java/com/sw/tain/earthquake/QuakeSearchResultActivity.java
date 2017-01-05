package com.sw.tain.earthquake;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.sw.tain.earthquake.DB.QuakeContentProvider;
import com.sw.tain.earthquake.DB.QuakeDBModel;


public class QuakeSearchResultActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String QUERY_EXTRA_KEY = "query_extra_key";
    private SimpleCursorAdapter mAdapter;
    private ListView mListSearchResult;
    private QuakeSearchResultFragment mResultFragemnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quake_search_result);

        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_expandable_list_item_1, null,
                new String[]{QuakeDBModel.QuakeTable.COL.SUMMARY},
                new int[]{android.R.id.text1}, 0);

//        mListSearchResult = (ListView)findViewById(R.id.list_view_quake_search_result);
//        mListSearchResult.setAdapter(mAdapter);

        FragmentManager fm = getSupportFragmentManager();
        mResultFragemnt = (QuakeSearchResultFragment)fm.findFragmentById(R.id.fragment_quake_search_result_list);
        mResultFragemnt.setListAdapter(mAdapter);

        getSupportLoaderManager().initLoader(0, null, this);


        parseIntent(getIntent());


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntent(getIntent());
    }

    private void parseIntent(Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SEARCH)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            performSearch(query);
        }
    }

    private void performSearch(String query) {
        Bundle args = new Bundle();
        args.putString(QUERY_EXTRA_KEY, query);
        getSupportLoaderManager().restartLoader(0, args, this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = new String[]{ QuakeDBModel.QuakeTable.COL.KEY_ID,
                QuakeDBModel.QuakeTable.COL.SUMMARY};
        String sortOrder = QuakeDBModel.QuakeTable.COL.SUMMARY + " COLLATE LOCALIZED ASC";
        String where = null;

        if(args!=null){
            String query = args.getString(QUERY_EXTRA_KEY);
            where = QuakeDBModel.QuakeTable.COL.SUMMARY + " like \"%" + query + "%\"";
        }
        CursorLoader loader = new CursorLoader(this, QuakeContentProvider.CONTENT_URI,
                projection, where, null, sortOrder);

        return loader;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}
