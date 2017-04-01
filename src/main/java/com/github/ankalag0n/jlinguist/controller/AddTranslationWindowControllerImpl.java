package com.github.ankalag0n.jlinguist.controller;

import com.github.ankalag0n.jlinguist.model.Language;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.*;

/**
 * New translation controller implementation.
 */
public class AddTranslationWindowControllerImpl implements javafx.fxml.Initializable, AddTranslationWindowController
{
    /**
     * Scene managed by the controller.
     */
    private Scene scene;

    /**
     * Message bundle.
     */
    private ResourceBundle messageBundle;

    /**
     * Window managed by controller.
     */
    private Stage window = null;

    /**
     * Choice box with languages select.
     */
    @FXML
    private ChoiceBox<Language> languageChoiceBox;

    /**
     * Callback to be executed after language selection is completed correctly.
     */
    private Callback<Language, Void> languageSelectedCallback;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        Set<String>    addedLanguages = new HashSet<>();
        List<Language> languages      = new LinkedList<>();

        for (Locale locale : Locale.getAvailableLocales()) {
            if (locale.getDisplayLanguage().length() > 0) {
                Language lang = new Language(locale);

                if (!addedLanguages.contains(lang.getCode())) {
                    addedLanguages.add(lang.getCode());

                    languages.add(lang);
                }
            }
        }

        languages.sort(Language::compareTo);

        languageChoiceBox.setItems(FXCollections.observableArrayList(languages));
    }

    @Override
    public void showWindow()
    {
        if (window == null) {
            window = new Stage();
            window.setScene(scene);
            window.setWidth(400.0);
            window.setMinWidth(400.0);
            window.setHeight(120.0);
            window.setMinHeight(120.0);
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle(messageBundle.getString("addTranslationWindow.title"));
        }

        window.show();
    }

    /**
     * Closes window without triggering callback.
     */
    public void closeWindow()
    {
        window.close();
    }

    /**
     * Checks if form is filled correctly and executes callback.
     */
    public void addLanguage()
    {
        Language language = languageChoiceBox.getSelectionModel().getSelectedItem();

        if (language != null) {
            if (languageSelectedCallback != null) {
                languageSelectedCallback.call(language);
            }

            window.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(messageBundle.getString("addTranslationWindow.chooseLanguageDialog.title"));
            alert.setHeaderText(messageBundle.getString("addTranslationWindow.chooseLanguageDialog.description"));
            alert.setContentText(messageBundle.getString("addTranslationWindow.chooseLanguageDialog.details"));

            alert.show();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLanguageSelectedCallback(Callback<Language, Void> languageSelectedCallback)
    {
        this.languageSelectedCallback = languageSelectedCallback;
    }

    /**
     * Sets the scene.
     *
     * @param scene Scene that will be manged by controller.
     */
    public void setScene(Scene scene)
    {
        this.scene = scene;
    }

    /**
     * Sets the message bundle.
     *
     * @param messageBundle Message bundle.
     */
    void setMessageBundle(ResourceBundle messageBundle)
    {
        this.messageBundle = messageBundle;
    }
}
