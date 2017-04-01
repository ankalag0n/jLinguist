package com.github.ankalag0n.jlinguist.controller;

import com.github.ankalag0n.jlinguist.model.Language;
import javafx.util.Callback;

/**
 * Controller for window with new translation form.
 */
public interface AddTranslationWindowController
{
    /**
     * Shows the multiline editors window.
     */
    void showWindow();

    /**
     * Sets the callback to be executed after the language was chosen.
     *
     * @param languageSelectedCallback Callback.
     */
    void setLanguageSelectedCallback(Callback<Language, Void> languageSelectedCallback);
}
