package com.angelaigreja.alphabet;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener, AlphabetFragment.FragmentListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mLanguages;

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tts = new TextToSpeech(this, this);

        mTitle = mDrawerTitle = getTitle();
        mLanguages = getResources().getStringArray(R.array.languages);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        /* mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);*/
        // set up the drawer's list view with items and click listener


        // create the grid item mapping
        int[] flags = {R.drawable.uk, R.drawable.us, R.drawable.germany, R.drawable.spain};

        String[] from = {"flag", "title"};
        int[] to = new int[]{R.id.flag, R.id.title};

        // prepare the list of all records
        List<HashMap<String, Object>> fillMaps = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < mLanguages.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(from[0], flags[i]);
            map.put(from[1], mLanguages[i]);
            fillMaps.add(map);
        }

        // fill in the grid_item layout
        SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.navdrawer_item, from, to);

        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //ImageView image  = (ImageView) findViewById(R.id.flag);
        //image.setImageResource(R.drawable.ic_drawer);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up'  caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLetterClick(String letter) {
        tts.speak(letter, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        getFragmentManager().beginTransaction().replace(R.id.content_frame, AlphabetFragment.newInstance(position)).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mLanguages[position]);
        switch (position) {
            case 0:
                tts.setLanguage(Locale.UK);
                break;
            case 1:
                tts.setLanguage(Locale.US);
                break;
            case 2:
                tts.setLanguage(Locale.FRENCH);
                break;
            case 3:
                tts.setLanguage(Locale.GERMAN);
                break;
            case 4:
                tts.setLanguage(Locale.ITALIAN);
                break;
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Log.i("TTS", "Initialization finished");
        } else {
            Log.e("TTS", "Initialization failed");
        }
    }

    @Override
    public void onPause() {
        tts.stop();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}
