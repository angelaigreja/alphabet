package com.angelaigreja.alphabet;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * {@link Fragment} that appears in the "content_frame and shows to alphabet for the selected language.
 * Use the {@link AlphabetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlphabetFragment extends Fragment {

    private static final String ARG_LANGUAGE = "language";
    private Language mLanguage;

    private int automaticIndex = 0;
    private MenuItem automaticSpeakMenuItem;

    private GridView gridView;
    private FragmentListener mListener;

    public AlphabetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param language Integer for the languages resources.
     * @return A new instance of fragment AlphabetFragment.
     */
    public static AlphabetFragment newInstance(Language language) {
        AlphabetFragment fragment = new AlphabetFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LANGUAGE, language);
        fragment.setArguments(args);
        return fragment;
    }

    public static String capitalizeFirstLetter(String original) {
        if (original.length() == 0)
            return original;
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mLanguage = getArguments().getParcelable(ARG_LANGUAGE);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        mListener.getTTS().setOnUtteranceProgressListener(null);
        if (automaticSpeakMenuItem != null) {
            automaticSpeakMenuItem.setIcon(android.R.drawable.ic_media_play);
        }
        automaticIndex = 0;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_play:
                automaticSpeakMenuItem = item;
                mListener.getTTS().stop();
                if (automaticIndex != 0) {
                    mListener.getTTS().setOnUtteranceProgressListener(null);
                    item.setIcon(android.R.drawable.ic_media_play);
                    automaticIndex = 0;
                } else {
                    item.setIcon(android.R.drawable.ic_media_pause);
                    mListener.onLetterClick(mLanguage.getAlphabet()[automaticIndex++]);
                    mListener.getTTS().setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            if (automaticIndex < mLanguage.getAlphabet().length) {
                                mListener.onLetterClick(mLanguage.getAlphabet()[automaticIndex++]);
                            } else {
                                mListener.getTTS().setOnUtteranceProgressListener(null);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        automaticSpeakMenuItem.setIcon(android.R.drawable.ic_media_play);
                                    }
                                });
                                automaticIndex = 0;
                            }
                        }

                        @Override
                        public void onError(final String utteranceId) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    automaticSpeakMenuItem.setIcon(android.R.drawable.ic_media_play);
                                    Toast.makeText(getActivity(), getString(R.string.error, utteranceId,
                                            capitalizeFirstLetter(getString(mLanguage.getTitle()).toLowerCase())), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_alphabet, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridview);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                R.layout.card, R.id.letter, mLanguage.getAlphabet());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String toSpeak = ((TextView) v.findViewById(R.id.letter)).getText().toString();
                if (mListener != null && automaticIndex == 0) {
                    mListener.onLetterClick(toSpeak);
                } else {
                    Toast.makeText(getActivity(), R.string.autoplay, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof FragmentListener) {
            mListener = (FragmentListener) activity;
        } else {
            throw new RuntimeException("Activity must implement FragmentListener");
        }
    }

    public interface FragmentListener {
        void onLetterClick(String letter);

        TextToSpeech getTTS();
    }

}
