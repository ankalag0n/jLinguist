package com.github.ankalag0n.jlinguist.filter;

import com.github.ankalag0n.jlinguist.model.Phrase;

import java.util.function.Predicate;

/**
 * Filter to filter out phrases that dose not match given text in selected language.
 */
public class TranslationFilter implements Predicate<Phrase>
{
    /**
     * Language key used in filtering.
     */
    private final String languageKey;

    /**
     * Text to search for.
     */
    private final String searchedText;

    /**
     * Default constructor.
     *
     * @param languageKey Language key used in filtering.
     * @param searchedText Text to search for.
     */
    public TranslationFilter(String languageKey, String searchedText)
    {
        this.languageKey  = languageKey;
        this.searchedText = searchedText;
    }

    @Override
    public boolean test(Phrase phrase)
    {
        String translation = phrase.getTranslation(languageKey);

        if (translation == null) {
            return false;
        }

        return translation.contains(searchedText);
    }
}
