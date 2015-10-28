package fr.aqamad.tutoyoyo;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by Gregoire on 26/10/2015.
 */
public class YoyoTutsSuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "fr.aqamad.YoyoTutsSuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public YoyoTutsSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
