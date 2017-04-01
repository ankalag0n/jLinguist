package com.github.ankalag0n.jlinguist.controller;

import com.google.inject.Inject;
import com.google.inject.Provider;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Provider responsible for creation of multiline editors controller.
 */
public class MultilineEditorWindowControllerProvider implements Provider<MultilineEditorWindowController>
{
    /**
     * Message bundle.
     */
    private final ResourceBundle messageBundle;

    @Inject
    public MultilineEditorWindowControllerProvider(ResourceBundle messageBundle)
    {
        this.messageBundle = messageBundle;
    }

    @Override
    public MultilineEditorWindowController get()
    {
        Parent                   content;
        MultilineEditorWindowControllerImpl controller;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MultilineEditorWindow.fxml"), messageBundle);

        try {
            content    = fxmlLoader.load();
            controller = fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        controller.setScene(new Scene(content));
        controller.setMessageBundle(messageBundle);

        return controller;
    }
}
