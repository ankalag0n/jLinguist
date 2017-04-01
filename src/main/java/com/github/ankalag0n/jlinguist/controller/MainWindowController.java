package com.github.ankalag0n.jlinguist.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Interface for main window controller.
 */
public interface MainWindowController
{
    /**
     * @return Scene that is managed by controller.
     */
    Scene getScene();

    /**
     * @param mainWindow Applications main window.
     */
    void setMainWindow(Stage mainWindow);
}
