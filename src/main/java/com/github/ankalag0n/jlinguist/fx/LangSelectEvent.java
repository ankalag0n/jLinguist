package com.github.ankalag0n.jlinguist.fx;

import com.github.ankalag0n.jlinguist.file.FileInfo;

/**
 * Interface for events fired by language select checkboxes.
 */
public interface LangSelectEvent
{
    /**
     * Method fired after language file was selected or deselected.
     *
     * @param fileInfo File that was selected (or deselected).
     * @param selected TRUE if file was selected and FALSE if deselected.
     */
    void languageSelectionChanged(FileInfo fileInfo, boolean selected);
}
