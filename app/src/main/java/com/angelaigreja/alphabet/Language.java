package com.angelaigreja.alphabet;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by angelaigreja on 01/01/15.
 */
@AllArgsConstructor
@Data
public class Language implements Parcelable {
    public static final HashMap<String, Integer> FLAGS = new HashMap<>();
    public static final HashMap<String, Integer> TITLES = new HashMap<>();

    static {
        TITLES.put("enUS", R.string.us);
        FLAGS.put("enUS", R.drawable.us);

        TITLES.put("enGB", R.string.uk);
        FLAGS.put("enGB", R.drawable.uk);

        TITLES.put("frFR", R.string.fr);
        FLAGS.put("frFR", R.drawable.france);

        TITLES.put("deDE", R.string.de);
        FLAGS.put("deDE", R.drawable.germany);

        TITLES.put("itIT", R.string.it);
        FLAGS.put("itIT", R.drawable.italy);

        TITLES.put("ptPT", R.string.pt);
        FLAGS.put("ptPT", R.drawable.portugal);

        TITLES.put("esES", R.string.es);
        FLAGS.put("esES", R.drawable.spain);
    }

    public static final Parcelable.Creator<Language> CREATOR
            = new Parcelable.Creator<Language>() {
        public Language createFromParcel(Parcel in) {
            return new Language(in);
        }

        public Language[] newArray(int size) {
            return new Language[size];
        }
    };
    private int title;
    private int flag;
    private String[] alphabet;
    private Locale locale;

    private Language(Parcel in) {
        title = in.readInt();
        flag = in.readInt();
        alphabet = new String[in.readInt()];
        in.readStringArray(alphabet);
        locale = (Locale) in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(title);
        dest.writeInt(flag);
        dest.writeInt(alphabet.length);
        dest.writeStringArray(alphabet);
        dest.writeSerializable(locale);
    }
}
