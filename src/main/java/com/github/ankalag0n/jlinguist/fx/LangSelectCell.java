package com.github.ankalag0n.jlinguist.fx;

import com.github.ankalag0n.jlinguist.file.FileInfo;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.Locale;

/**
 * Cell for list view with language selection.
 */
public class LangSelectCell extends ListCell<FileInfo>
{
    private final LangSelectEvent selectEvent;
    private final String defaultLanguageText;

    /**
     * @param selectEvent Event that will be fired after selection of language.
     * @param defaultLanguageText Text to be displayed for default language.
     */
    public LangSelectCell(LangSelectEvent selectEvent, String defaultLanguageText)
    {
        this.selectEvent = selectEvent;
        this.defaultLanguageText = defaultLanguageText;
    }

    @Override
    protected void updateItem(FileInfo item, boolean empty)
    {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            ImageView flag;

            Locale locale = item.getLocale();
            if (locale != null) {
                String text = locale.getDisplayLanguage();
                if (locale.getDisplayCountry().length() > 0) {
                    text = text + " (" + locale.getDisplayCountry() + ")";
                }

                setText(text);

                URL flagUrl = null;
                if (locale.getCountry().length() > 0) {
                    flagUrl = getClass().getClassLoader().getResource("images/flags/" + locale.getLanguage() + "_" + locale.getCountry() + ".png");
                }

                if (flagUrl == null) {
                    flagUrl = getClass().getClassLoader().getResource("images/flags/" + locale.getLanguage() + ".png");
                    if (flagUrl == null) {
                        flagUrl = getClass().getClassLoader().getResource("images/flags/unknown.png");
                    }
                }

                flag = new ImageView(flagUrl.toString());
            } else {
                setText(defaultLanguageText);
                flag = new ImageView(getClass().getClassLoader().getResource("images/flags/default.png").toString());
            }

            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> selectEvent.languageSelectionChanged(item, newValue));

            HBox hBox = new HBox(checkBox, flag);

            setGraphic(hBox);
        }
    }
}
