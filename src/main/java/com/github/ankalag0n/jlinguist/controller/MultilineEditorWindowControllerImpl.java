package com.github.ankalag0n.jlinguist.controller;

import com.github.ankalag0n.jlinguist.model.Phrase;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.*;

/**
 * Implementation for multiline editors controller.
 */
public class MultilineEditorWindowControllerImpl implements javafx.fxml.Initializable, MultilineEditorWindowController
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
     * Main container for all form fields.
     */
    @FXML
    private VBox mainPanel;

    /**
     * Map with dynamically added field with translation values.
     */
    private Map<String, TextArea> translationFields = new HashMap<>();

    /**
     * Map with dynamically added field with comment values.
     */
    private Map<String, TextArea> commentFields = new HashMap<>();

    /**
     * Edited phrase.
     */
    private Phrase phrase;

    /**
     * Language that column was selected for edit.
     */
    private String editedLanguage;

    /**
     * Callback that will be executed after the value is saved.
     */
    private Callback<MultilineEditorWindowController, Void> onSaveCallback = null;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        mainPanel.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && event.isAltDown()) {
                saveValues();
            }
        });
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLanguages(List<String> languages)
    {
        String commentText = messageBundle.getString("comment");

        for (String language : languages) {
            TextArea phraseField = new TextArea();

            String phraseText = phrase.getTranslation(language);
            if (phraseText != null) {
                phraseField.setText(phraseText);
            }

            phraseField.setMaxHeight(Double.MAX_VALUE);
            phraseField.setMaxWidth(Double.MAX_VALUE);
            phraseField.setWrapText(true);
            HBox.setHgrow(phraseField, Priority.ALWAYS);
            HBox.setMargin(phraseField, new Insets(2.5));

            TextArea commentField = new TextArea();

            String comment = phrase.getComment(language);
            if (comment != null) {
                commentField.setText(comment);
            }

            commentField.setMinWidth(200.0);
            commentField.setMaxWidth(200.0);
            commentField.setPrefWidth(200.0);
            commentField.setPromptText(commentText);
            commentField.setWrapText(true);
            HBox.setMargin(commentField, new Insets(2.5));

            HBox langLayout = new HBox(phraseField, commentField);
            langLayout.setPrefWidth(200.0);
            langLayout.setPrefHeight(150.0);

            String langName;
            if (language != Phrase.DEFAULT_TRANSLATION_KEY) {
                Locale lang = new Locale(language);
                if (lang.getDisplayCountry() != null && lang.getDisplayCountry().length() > 0) {
                    langName = lang.getDisplayName() + " (" + lang.getDisplayCountry() + ")";
                } else {
                    langName = lang.getDisplayName();
                }
            } else {
                langName = messageBundle.getString("editorsPanel.defaultLanguage");
            }

            TitledPane langPane = new TitledPane(langName, langLayout);
            langPane.setCollapsible(false);
            langPane.setMaxWidth(Double.MAX_VALUE);
            langPane.setMaxHeight(Double.MAX_VALUE);
            VBox.setVgrow(langPane, Priority.ALWAYS);

            mainPanel.getChildren().add(langPane);

            translationFields.put(language, phraseField);
            commentFields.put(language, commentField);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPhrase(Phrase phrase)
    {
        this.phrase = phrase;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEditedLanguage(String editedLanguage)
    {
        this.editedLanguage = editedLanguage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnSaveCallback(Callback<MultilineEditorWindowController, Void> callback)
    {
        onSaveCallback = callback;
    }

    /**
     * {@inheritDoc}
     */
    public void showWindow()
    {
        if (window == null) {
            window = new Stage();
            window.setScene(scene);
            window.setTitle(phrase.getKey());
            window.setWidth(600.0);
            window.setMinWidth(600.0);
            if (translationFields.size() == 1) {
                window.setHeight(260.0);
                window.setMinHeight(260.0);
            } else {
                window.setHeight(225.0 * translationFields.size());
                window.setMinHeight(225.0 * translationFields.size());
            }
            window.initModality(Modality.APPLICATION_MODAL);
        }

        window.show();
        TextArea phraseField = translationFields.get(editedLanguage);
        phraseField.requestFocus();
        phraseField.selectAll();
    }

    /**
     * Saves edited value.
     */
    public void saveValues()
    {
        for (String language : translationFields.keySet()) {
            phrase.setTranslation(language, translationFields.get(language).getText().trim());

            String comment = commentFields.get(language).getText().trim();
            if (comment.length() > 0) {
                phrase.setComment(language, comment);
            } else {
                phrase.removeComment(language);
            }
        }

        if (onSaveCallback != null) {
            onSaveCallback.call(this);
        }

        window.close();
    }

    /**
     * Closes window and cancels edit.
     */
    public void closeWindow()
    {
        window.close();
    }
}
