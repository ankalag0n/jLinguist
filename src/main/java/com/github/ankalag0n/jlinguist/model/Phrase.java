package com.github.ankalag0n.jlinguist.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Data model that represents phrase.
 */
public class Phrase
{
    /**
     * Key under which the default translation is set.
     */
    public static final String DEFAULT_TRANSLATION_KEY = "/DEFAULT/";

    /**
     * Key under which the phrase is saved.
     */
    private StringProperty key;

    /**
     * Map with phrase translations. Key of the map is the language code. Value is the translation of the phrase.
     * Default translation is saved under /DEFAULT/ key.
     */
    private Map<String, StringProperty> translations = new HashMap<>();

    /**
     * Map with comments for the translations. Key of the map is the language code. Value is the comment for the translation.
     * Default translation is saved under /DEFAULT/ key.
     */
    private Map<String, String> comments = new HashMap<>();

    public Phrase(String key)
    {
        this.key = new SimpleStringProperty(key);
    }

    /**
     * @return Returns key property.
     */
    public StringProperty keyProperty()
    {
        return key;
    }

    /**
     * @return Returns key value.
     */
    public String getKey()
    {
        return key.get();
    }

    /**
     * Sets the key value.
     *
     * @param key New value for property key.
     */
    public void setKey(String key)
    {
        this.key.set(key);
    }

    /**
     * Sets the translation for the given language.
     *
     * @param language Language code.
     * @param value Translation value.
     */
    public void setTranslation(String language, String value)
    {
        if (translations.containsKey(language)) {
            translations.get(language).set(value);
        } else {
            translations.put(language, new SimpleStringProperty(value));
        }
    }

    /**
     * Returns the translation for the given language.
     *
     * @param language Language code.
     * @return Translation.
     */
    public String getTranslation(String language)
    {
        if (translations.containsKey(language)) {
            return translations.get(language).get();
        } else {
            return null;
        }
    }

    /**
     * Returns the translation property for the given language.
     *
     * @param language Language code.
     * @return Translation property.
     */
    public StringProperty getTranslationProperty(String language)
    {
        if (translations.containsKey(language)) {
            return translations.get(language);
        } else {
            return null;
        }
    }

    /**
     * Sets comment for the phrase and given language.
     *
     * @param language Language code.
     * @param value Comment value.
     */
    public void setComment(String language, String value)
    {
        comments.put(language, value);
    }

    /**
     * Gets the comment for the language.
     *
     * @param language Language code.
     * @return Comment or null if comment is not set.
     */
    public String getComment(String language)
    {
        if (comments.containsKey(language)) {
            return comments.get(language);
        }

        return null;
    }

    /**
     * Removes the comment for the given language.
     *
     * @param language Language code.
     */
    public void removeComment(String language)
    {
        if (comments.containsKey(language)) {
            comments.remove(language);
        }
    }

    public String toString()
    {
        return "Phrase [ " + key.get() + " - " + translations + "]";
    }
}
