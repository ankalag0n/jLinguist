package com.github.ankalag0n.jlinguist.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller that manages applications main window.
 */
public class MainWindowControllerImpl implements javafx.fxml.Initializable, MainWindowController
{
    /**
     * Scene managed by controller.
     */
    private Scene scene;

    /**
     * Applications main window.
     */
    private Stage mainWindow;

    /**
     * Pane with editor panels.
     */
    @FXML
    private TabPane tabPane;

    /**
     * Button that toggles phrase key column visibility.
     */
    @FXML
    private ToggleButton togglePhraseKeyButton;

    /**
     * Provider responsible for creation of editors panel.
     */
    private EditorsPanelControllerProvider editorsPanelControllerProvider;

    /**
     * List of editor pane controllers.
     */
    private List<EditorsPanelController> editorsPanelControllers = new LinkedList<>();

    /**
     * Message bundle.
     */
    private ResourceBundle messageBundle;

    /**
     * Provider for About Window.
     */
    private AboutWindowControllerProvider aboutWindowControllerProvider;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Scene getScene()
    {
        return scene;
    }

    /**
     * Sets the scene.
     *
     * @param scene Scene that will be manged by controller.
     */
    void setScene(Scene scene)
    {
        this.scene = scene;

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN), this::openFileAction);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN), this::saveCurrentTab);
    }

    /**
     * sets the editors panel provider.
     *
     * @param editorsPanelControllerProvider Editors panel provider.
     */
    void setEditorsPanelControllerProvider(EditorsPanelControllerProvider editorsPanelControllerProvider)
    {
        this.editorsPanelControllerProvider = editorsPanelControllerProvider;
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
     * Sets main window.
     *
     * @param mainWindow Applications main window.
     */
    public void setMainWindow(Stage mainWindow)
    {
        this.mainWindow = mainWindow;

        mainWindow.setOnCloseRequest(event -> {
            if (editorsPanelControllers.stream().anyMatch(EditorsPanelController::isModified)) {
                int confirmResult = showConfirmSaveDialog();

                if (confirmResult == 1) {
                    saveAllTabs();
                } else if (confirmResult == 0) {
                    event.consume();
                }
            }
        });
    }

    /**
     * Shows file chooser dialog.
     */
    public void openFileAction()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(messageBundle.getString("mainWindow.openFile"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(messageBundle.getString("mainWindow.propertiesFileFilter"), "*.properties"));

        File file = fileChooser.showOpenDialog(mainWindow);

        if (file != null) {
            EditorsPanelController editorsPanelController = editorsPanelControllerProvider.get();
            editorsPanelController.setFile(file);
            editorsPanelControllers.add(editorsPanelController);

            Tab tab = editorsPanelController.getTab();
            tab.setOnCloseRequest(event -> {
                if (editorsPanelController.isModified()) {
                    int confirmResult = showConfirmSaveDialog();

                    if (confirmResult == 1) {
                        editorsPanelController.saveData();
                    } else if (confirmResult == 0) {
                        event.consume();
                    }
                }
            });

            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
        }
    }

    /**
     * Saves data of the currently selected tab.
     */
    public void saveCurrentTab()
    {
        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            Optional<EditorsPanelController> currentTabController = editorsPanelControllers.stream()
                .filter(editorsPanelController -> editorsPanelController.getTab() == currentTab && editorsPanelController.isModified())
                .findAny();

            currentTabController.ifPresent(EditorsPanelController::saveData);
        }
    }

    /**
     * Saves data for all of the tabs.
     */
    public void saveAllTabs()
    {
        editorsPanelControllers.stream()
            .filter(EditorsPanelController::isModified)
            .forEach(EditorsPanelController::saveData);
    }

    /**
     * Adds new language to the current tab.
     */
    public void addNewLanguage()
    {
        Tab currentTab = tabPane.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            Optional<EditorsPanelController> currentTabController = editorsPanelControllers.stream()
                .filter(editorsPanelController -> editorsPanelController.getTab() == currentTab)
                .findAny();

            currentTabController.ifPresent(EditorsPanelController::showAddNewLanguageWindow);
        }
    }

    /**
     * Changes visibility of the phrase key column.
     */
    public void togglePhraseKeyColumnVisibility()
    {
        editorsPanelControllers.forEach(controller -> controller.setPhraseKeyVisibility(togglePhraseKeyButton.isSelected()));
    }

    /**
     * Shows confirmation dialog with the question about unsaved data.
     *
     * @return 1 if user wishes to save data, -1 if user wants to discard data and 0 if user clicked cancel
     */
    private int showConfirmSaveDialog()
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(messageBundle.getString("mainWindow.confirmCloseDialog.warningTitle"));
        alert.setHeaderText(messageBundle.getString("mainWindow.confirmCloseDialog.warningHeader"));
        alert.setContentText(messageBundle.getString("mainWindow.confirmCloseDialog.warningInfo"));

        ButtonType saveButton    = new ButtonType(messageBundle.getString("mainWindow.confirmCloseDialog.button.saveChanges"));
        ButtonType discardButton = new ButtonType(messageBundle.getString("mainWindow.confirmCloseDialog.button.discardChanges"));
        ButtonType cancelButton  = new ButtonType(messageBundle.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(saveButton, discardButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent()) {
            return 0;
        }

        if (result.get() == saveButton) {
            return 1;
        } else if (result.get() != discardButton) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * Opens the About Window.
     */
    public void showAboutWindow()
    {
        aboutWindowControllerProvider.get().showWindow();
    }

    /**
     * Sets the provider for About Window.
     *
     * @param aboutWindowControllerProvider Provider.
     */
    void setAboutWindowControllerProvider(AboutWindowControllerProvider aboutWindowControllerProvider)
    {
        this.aboutWindowControllerProvider = aboutWindowControllerProvider;
    }
}
