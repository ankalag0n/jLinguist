package com.github.ankalag0n.jlinguist.filter;

import com.github.ankalag0n.jlinguist.model.Phrase;

import java.util.function.Predicate;

/**
 * Filter to filter out phrases that key dose not match given text.
 */
public class PhraseKeyFilter implements Predicate<Phrase>
{
    /**
     * Text to search for.
     */
    private final String searchKey;

    /**
     * @param searchKey Text to search for.
     */
    public PhraseKeyFilter(String searchKey)
    {
        this.searchKey = searchKey;
    }

    @Override
    public boolean test(Phrase phrase)
    {
        return phrase.getKey().contains(searchKey);
    }
}
