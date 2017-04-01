package com.github.ankalag0n.jlinguist.fx;

import com.github.ankalag0n.jlinguist.model.Phrase;
import javafx.beans.NamedArg;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Factory responsible for creating values for table view phrase cells.
 */
public class PhraseValueFactory implements Callback <TableColumn.CellDataFeatures <Phrase, String>, ObservableValue <String>>
{
    /**
     * Name of the property (language code of the translation that will be displayed).
     */
    private final String property;

    /**
     * Constructor.
     *
     * @param property Name of the property (language code of the translation that will be displayed).
     */
    public PhraseValueFactory(@NamedArg("property") String property)
    {
        this.property = property;
    }

    /**
     * @return Name of the property (language code of the translation that will be displayed).
     */
    public String getProperty()
    {
        return property;
    }

    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<Phrase, String> param)
    {
        return param.getValue().getTranslationProperty(property);
    }
}
