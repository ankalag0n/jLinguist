package com.github.ankalag0n.jlinguist.controller;

import com.google.inject.Provider;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Provider responsible for creation of applications main window (returns MainWindowController instance).
 */
public class MainWindowControllerProvider implements Provider<MainWindowController>
{
    /**
     * Provider responsible for creation of editors panel.
     */
    private final EditorsPanelControllerProvider editorsPanelControllerProvider;

    /**
     * Provider for about window.
     */
    private final AboutWindowControllerProvider aboutWindowControllerProvider;

    /**
     * Message bundle.
     */
    private final ResourceBundle messageBundle;

    @Inject
    public MainWindowControllerProvider(EditorsPanelControllerProvider editorsPanelControllerProvider, AboutWindowControllerProvider aboutWindowControllerProvider, ResourceBundle messageBundle)
    {
        this.editorsPanelControllerProvider = editorsPanelControllerProvider;
        this.aboutWindowControllerProvider = aboutWindowControllerProvider;
        this.messageBundle = messageBundle;
    }

    @Override
    public MainWindowController get()
    {
        Parent                   content;
        MainWindowControllerImpl controller;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"), messageBundle);

        try {
            content    = fxmlLoader.load();
            controller = fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        controller.setScene(new Scene(content));
        controller.setMessageBundle(messageBundle);
        controller.setEditorsPanelControllerProvider(editorsPanelControllerProvider);
        controller.setAboutWindowControllerProvider(aboutWindowControllerProvider);

        return controller;
    }
}
