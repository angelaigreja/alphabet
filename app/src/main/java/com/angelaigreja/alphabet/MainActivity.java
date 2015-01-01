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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener, AlphabetFragment.FragmentListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private List<Language> mLanguages;

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tts = new TextToSpeech(this, this);

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

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
    }


    private Map<String, String[]> getAlphabets() {

        try {
            final JSONObject jsonObject = (JSONObject) new JSONParser().parse(new InputStreamReader(getAssets().open("alphabets.json")));
            final Iterator<String> nameItr = jsonObject.keySet().iterator();
            final Map<String, String[]> alphabets = new HashMap<>();
            while (nameItr.hasNext()) {
                final String name = nameItr.next();
                alphabets.put(name, jsonObject.get(name).toString().split("(?!^)"));

            }
            return alphabets;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Language> getLanguages() {

        try {
            final JSONArray langArray = (JSONArray) new JSONParser().parse(new InputStreamReader(getAssets().open("languages.json")));
            final List<Language> languages = new ArrayList<>();
            final Map<String, String[]> alphabets = getAlphabets();
            for (int i = 0; i < langArray.size(); i++) {
                JSONObject langObj = (JSONObject) langArray.get(i);
                String country = langObj.get("country").toString();
                String language = langObj.get("language").toString();
                final Locale loc = new Locale(language, country);
                final String key = language + country;
                if (isLocaleTSSSuported(loc)) {
                    String alphabet = langObj.get("alphabet").toString();
                    Language lang = new Language(Language.TITLES.get(key), Language.FLAGS.get(key), alphabets.get(alphabet), loc);
                    languages.add(lang);
                }
            }
            return languages;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isLocaleTSSSuported(Locale locale) {
        try {
            int res = tts.isLanguageAvailable(locale);
            boolean hasVariant = (null != locale.getVariant() && locale.getVariant().length() > 0);
            boolean hasCountry = (null != locale.getCountry() && locale.getCountry().length() > 0);

            boolean isLocaleSupported =
                    !hasVariant && !hasCountry && res == TextToSpeech.LANG_AVAILABLE ||
                            !hasVariant && hasCountry && res == TextToSpeech.LANG_COUNTRY_AVAILABLE ||
                            res == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE;

            return isLocaleSupported;
        } catch (Exception ex) {
            Log.e("TTS", "Error checking if language is available for TTS (locale=" + locale + "): " + ex.getClass().getSimpleName() + "-" + ex.getMessage());
        }
        return false;

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
        getFragmentManager().beginTransaction().replace(R.id.content_frame, AlphabetFragment.newInstance(mLanguages.get(position))).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        final Language lang = mLanguages.get(position);
        setTitle(lang.getTitle());
        tts.setLanguage(lang.getLocale());
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

            mLanguages = getLanguages();
            // set a custom shadow that overlays the main content when the drawer opens
        /* mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);*/
            // set up the drawer's list view with items and click listener

            String[] from = {"flag", "title"};
            int[] to = new int[]{R.id.flag, R.id.title};

            // prepare the list of all records
            List<HashMap<String, Object>> fillMaps = new ArrayList<>();
            for (int i = 0; i < mLanguages.size(); i++) {
                HashMap<String, Object> map = new HashMap<>();
                map.put(from[0], mLanguages.get(i).getFlag());
                map.put(from[1], getString(mLanguages.get(i).getTitle()));
                fillMaps.add(map);
            }

            // fill in the grid_item layout
            SimpleAdapter adapter = new SimpleAdapter(this, fillMaps, R.layout.navdrawer_item, from, to);

            mDrawerList.setAdapter(adapter);

            if (mLanguages.size() > 0) {
                selectItem(0);
            }
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
