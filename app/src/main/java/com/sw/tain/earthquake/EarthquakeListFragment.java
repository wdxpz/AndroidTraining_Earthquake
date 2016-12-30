package com.sw.tain.earthquake;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;

import com.sw.tain.earthquake.DB.QuakeContentProvider;
import com.sw.tain.earthquake.DB.QuakeDBModel;
import com.sw.tain.earthquake.Model.Quake;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.sw.tain.earthquake.DB.QuakeDBModel.*;

/**
 * Created by home on 2016/12/28.
 */

public class EarthquakeListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ArrayList<Quake> mQuakeArrayList;
    private ArrayAdapter<Quake> mArrayAdapter;
    private SimpleCursorAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

//        mQuakeArrayList = new ArrayList<>();
//        mArrayAdapter = new ArrayAdapter<Quake>(getContext(), android.R.layout.simple_list_item_1, mQuakeArrayList);

        mAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_expandable_list_item_1, null,
                new String[]{QuakeTable.COL.SUMMARY},
                new int[]{android.R.id.text1}, 0);
        setListAdapter(mAdapter);

        getActivity().getSupportLoaderManager().initLoader(0, null, this);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                refreshEarthquakes();
            }
        });
        t.start();
    }


    private Handler handler = new Handler();

    public void refreshEarthquakes(){
        String quakeFeed = getResources().getString(R.string.quake_feed);

        URL url;
        HttpURLConnection httpConn;
        try {
            url = new URL(quakeFeed);
            httpConn = (HttpURLConnection)url.openConnection();

            if(httpConn.getResponseCode()!= HttpURLConnection.HTTP_OK) return;

            InputStream inStream = httpConn.getInputStream();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Document doc = docBuilder.parse(inStream);

            NodeList nodeList = doc.getElementsByTagName("event");
            if(nodeList!=null && nodeList.getLength()>0){
                for(int i= 0; i<nodeList.getLength(); i++){
                    Element node = (Element)nodeList.item(i);

                    Element description = (Element)node.getElementsByTagName("description").item(0);
                    Element title = (Element)description.getElementsByTagName("text").item(0);
                    String sDetails = title.getFirstChild().getNodeValue();

                    Element origin = (Element)node.getElementsByTagName("origin").item(0);
                    Element longitude = (Element)((Element)origin.getElementsByTagName("longitude").item(0)).getElementsByTagName("value").item(0);
                    Element latitude = (Element)((Element)origin.getElementsByTagName("latitude").item(0)).getElementsByTagName("value").item(0);
                    Element when = (Element)((Element)node.getElementsByTagName("time").item(0)).getElementsByTagName("value").item(0);
                    String dt = when.getFirstChild().getNodeValue();
                    double dLongitude = Double.parseDouble(longitude.getFirstChild().getNodeValue());
                    double dlatitude = Double.parseDouble(latitude.getFirstChild().getNodeValue());

                    Element magnitude = (Element)node.getElementsByTagName("magnitude").item(0);
                    Element mag = (Element)magnitude.getElementsByTagName("mag").item(0);
                    Element magvalue = (Element)mag.getElementsByTagName("value").item(0);
                    double dMagnitude = Double.parseDouble(magvalue.getFirstChild().getNodeValue());
                    Element link = (Element)node.getElementsByTagName("preferredOriginID").item(0);
                    String sLink = link.getFirstChild().getNodeValue();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
                    Date qdate = new GregorianCalendar(0, 0, 0).getTime();
                    try {
                        qdate = sdf.parse(dt);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Location location = new Location("dummyGPS");
                    location.setLongitude(dLongitude);
                    location.setLatitude(dlatitude);

                    final Quake quake = new Quake(qdate, sDetails, sLink, location, dMagnitude);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            addNewQuake(quake);
                        }
                    });



                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } finally{
        }

    }

    private void addNewQuake(Quake quake) {
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cursor = cr.query(QuakeContentProvider.CONTENT_URI,
                null,
                QuakeTable.COL.DATE + "=" + quake.getDate().getTime(),
                null, null);
        if(cursor.getCount()==0){
            ContentValues values = new ContentValues();
            values.put(QuakeTable.COL.DATE, quake.getDate().getTime());
            values.put(QuakeTable.COL.DETAILS, quake.getDetails());
            values.put(QuakeTable.COL.SUMMARY, quake.toString());
            values.put(QuakeTable.COL.LATITUDE, quake.getLocation().getLatitude());
            values.put(QuakeTable.COL.LONGITUDE, quake.getLocation().getLongitude());
            values.put(QuakeTable.COL.LINK, quake.getLink());
            values.put(QuakeTable.COL.MAGNITUDE, quake.getMagnitude());

            cr.insert(QuakeContentProvider.CONTENT_URI, values);

            getActivity().getSupportLoaderManager().restartLoader(0, null, this);
        }

        cursor.close();
//        mQuakeArrayList.add(quake);
//        mArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projections = new String[]{QuakeTable.COL.KEY_ID, QuakeTable.COL.SUMMARY};

        CursorLoader loader = new CursorLoader(getActivity(), QuakeContentProvider.CONTENT_URI, projections, null, null, null);

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
