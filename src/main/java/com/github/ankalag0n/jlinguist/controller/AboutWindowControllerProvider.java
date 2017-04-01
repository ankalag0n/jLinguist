package com.github.ankalag0n.jlinguist.controller;

import com.google.inject.Inject;
import com.google.inject.Provider;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Provider responsible for creating about window.
 */
public class AboutWindowControllerProvider implements Provider<AboutWindowController>
{
    /**
     * Message bundle.
     */
    private final ResourceBundle messageBundle;

    @Inject
    public AboutWindowControllerProvider(ResourceBundle messageBundle)
    {
        this.messageBundle = messageBundle;
    }

    @Override
    public AboutWindowController get()
    {
        Parent                    content;
        AboutWindowControllerImpl controller;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/AboutWindow.fxml"), messageBundle);

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
