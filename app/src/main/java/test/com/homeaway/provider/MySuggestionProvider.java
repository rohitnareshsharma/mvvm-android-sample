package test.com.homeaway.provider;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Provider for creating recent search suggestion interface.
 * for further detail check this
 * @link https://developer.android.com/guide/topics/search/adding-recent-query-suggestions.html
 */
public class MySuggestionProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "test.com.homeaway.provider.MySuggestionProvider";

    public final static int MODE = DATABASE_MODE_QUERIES;

    public MySuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}