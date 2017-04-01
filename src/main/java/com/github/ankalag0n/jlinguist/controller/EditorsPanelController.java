package com.github.ankalag0n.jlinguist.controller;

import javafx.scene.control.Tab;

import java.io.File;

/**
 * Controller for managing editors panel.
 */
public interface EditorsPanelController
{
    /**
     * Sets the file for editing.
     *
     * @param file File.
     */
    void setFile(File file);

    /**
     * Creates JavaFX tab for the panel.
     *
     * @return Tab.
     */
    Tab getTab();

    /**
     * @return TRUE if at lest one phrase was modified.
     */
    boolean isModified();

    /**
     * Saves all of the translations into files.
     */
    void saveData();

    /**
     * Sets visibility of the phrase key column.
     *
     * @param visible TRUE to show the column, FALSE to hide.
     */
    void setPhraseKeyVisibility(boolean visible);

    /**
     * Shows window with form for adding new language.
     */
    void showAddNewLanguageWindow();
}
