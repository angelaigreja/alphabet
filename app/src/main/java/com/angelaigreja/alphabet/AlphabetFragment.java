package com.angelaigreja.alphabet;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

/**
 * {@link Fragment} that appears in the "content_frame and shows to alphabet for the selected language.
 * Use the {@link AlphabetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlphabetFragment extends Fragment {

    private static final String ARG_LANGUAGE = "language";

    private int mLanguage;

    private String[] letters = new String[]{
            "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };


    private String[] ENGLISH = new String[]{
            "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };

    private String[] GERMAN = new String[]{
            "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z",
            "Ä", "Ö", "Ü", "ß"
    };


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
    public static AlphabetFragment newInstance(int language) {
        AlphabetFragment fragment = new AlphabetFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LANGUAGE, language);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLanguage = getArguments().getInt(ARG_LANGUAGE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_alphabet, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridview);
        //UnicodeSet unicodeSet = locale.getExemplarSet(0,ES_STANDARD);
        //Set<String> lettersSet = locale.getUnicodeLocaleKeys();
        //String[] letters =  lettersSet.toArray(new String[ lettersSet.size()]);
        // Create adapter to set value for grid view
        /*if(language.equalsIgnoreCase("ENGLISH")){
            letters = ENGLISH;
        }
        if(language.equalsIgnoreCase("GERMAN")){
            letters = GERMAN;
        } */
        switch (mLanguage) {
            case 1:
                letters = ENGLISH;
            case 2:
                letters = GERMAN;
        }


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.card, R.id.letter, letters);


        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String toSpeak = ((TextView) v.findViewById(R.id.letter)).getText().toString();
                if (mListener != null) {
                    mListener.onLetterClick(toSpeak);
                }
                //tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
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
    }

}
