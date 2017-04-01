package com.github.ankalag0n.jlinguist.controller;

import com.github.ankalag0n.jlinguist.model.Phrase;
import javafx.util.Callback;

import java.util.List;

/**
 * Controller that manages multiline editor window.
 */
public interface MultilineEditorWindowController
{
    /**
     * Shows the multiline editors window.
     */
    void showWindow();

    /**
     * Sets the list of all languages that will be edited.
     *
     * @param languages Languages list.
     */
    void setLanguages(List<String> languages);

    /**
     * Sets the edited phrase.
     *
     * @param phrase Phrase.
     */
    void setPhrase(Phrase phrase);

    /**
     * Sets the language that was selected for edit.
     *
     * @param editedLanguage Edited language.
     */
    void setEditedLanguage(String editedLanguage);

    /**
     * Sets the callback function that will be executed after the value is saved.
     *
     * @param callback Callback function.
     */
    void setOnSaveCallback(Callback<MultilineEditorWindowController, Void> callback);
}
