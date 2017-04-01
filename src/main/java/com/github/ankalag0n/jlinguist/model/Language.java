package com.github.ankalag0n.jlinguist.model;

import java.util.Locale;

/**
 * Model for the Choice Box with languages select.
 */
public class Language implements Comparable<Language>
{
    /**
     * Two letter language code.
     */
    private final String code;

    /**
     * Language name to display on a list.
     */
    private final String name;

    public Language(Locale locale)
    {
        code = locale.getLanguage();
        name = locale.getDisplayLanguage();
    }

    public String getCode()
    {
        return code;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public int compareTo(Language o)
    {
        return name.compareTo(o.name);
    }
}
