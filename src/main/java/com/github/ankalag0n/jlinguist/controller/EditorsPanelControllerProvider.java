package com.github.ankalag0n.jlinguist.controller;

import com.github.ankalag0n.jlinguist.file.FileSystemService;
import com.google.inject.Provider;
import javafx.fxml.FXMLLoader;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Provider for editors panel.
 */
public class EditorsPanelControllerProvider implements Provider<EditorsPanelController>
{
    /**
     * Service for accessing files.
     */
    private final FileSystemService fileSystemService;

    /**
     * Message bundle.
     */
    private final ResourceBundle messageBundle;

    /**
     * Provider responsible for creation of multiline editor.
     */
    private final MultilineEditorWindowControllerProvider multilineEditorWindowControllerProvider;

    /**
     * Provider responsible for creation of new language form window.
     */
    private final AddTranslationWindowControllerProvider addTranslationWindowControllerProvider;

    @Inject
    public EditorsPanelControllerProvider(
        FileSystemService fileSystemService,
        ResourceBundle messageBundle,
        MultilineEditorWindowControllerProvider multilineEditorWindowControllerProvider,
        AddTranslationWindowControllerProvider addTranslationWindowControllerProvider
    )
    {
        this.fileSystemService = fileSystemService;
        this.messageBundle = messageBundle;
        this.multilineEditorWindowControllerProvider = multilineEditorWindowControllerProvider;
        this.addTranslationWindowControllerProvider = addTranslationWindowControllerProvider;
    }

    @Override
    public EditorsPanelController get()
    {
        EditorsPanelControllerImpl controller;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/EditorsPanel.fxml"), messageBundle);

        try {
            fxmlLoader.load();
            controller = fxmlLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        controller.setFileSystemService(fileSystemService);
        controller.setMessageBundle(messageBundle);
        controller.setMultilineEditorWindowControllerProvider(multilineEditorWindowControllerProvider);
        controller.setAddTranslationWindowControllerProvider(addTranslationWindowControllerProvider);

        return controller;
    }
}
