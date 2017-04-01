package com.github.ankalag0n.jlinguist.controller;

import com.github.ankalag0n.jlinguist.file.FileInfo;
import com.github.ankalag0n.jlinguist.file.FileSystemService;
import com.github.ankalag0n.jlinguist.filter.TranslationFilter;
import com.github.ankalag0n.jlinguist.fx.LangSelectCell;
import com.github.ankalag0n.jlinguist.filter.PhraseKeyFilter;
import com.github.ankalag0n.jlinguist.fx.PhraseValueFactory;
import com.github.ankalag0n.jlinguist.model.Phrase;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of editors panel controller.
 */
public class EditorsPanelControllerImpl implements javafx.fxml.Initializable, EditorsPanelController
{
    /**
     * Top level pane.
     */
    @FXML
    private SplitPane mainPane;

    /**
     * Tab inside which the panel is placed.
     */
    private Tab tab = null;

    /**
     * List of available languages.
     */
    @FXML
    private ListView<FileInfo> languageSelect;

    /**
     * Table view with the list of phrases.
     */
    @FXML
    private TableView<Phrase> phraseTableView;

    /**
     * ChoiceBox with the selection of charsets.
     */
    @FXML
    private ChoiceBox<String> charsetSelect;

    /**
     * First column with the phrase key.
     */
    @FXML
    private TableColumn phraseKeyColumn;

    /**
     * Search field for the phrase key column.
     */
    @FXML
    private TextField phraseKeySearchField;

    /**
     * Map with phrases. Key of the map is the key of the phrase.
     */
    private Map<String, Phrase> phraseMap = new HashMap<>();

    /**
     * TRUE if any of the phrases where modified and not yet saved to the disk.
     */
    private boolean isModified = false;

    /**
     * List of language files for edited file.
     */
    private List<FileInfo> langFiles;

    /**
     * Message bundle.
     */
    private ResourceBundle messageBundle;

    /**
     * Provider responsible for creation of multiline editor.
     */
    private MultilineEditorWindowControllerProvider multilineEditorWindowControllerProvider;

    /**
     * Provider responsible for creation of new language form window.
     */
    private AddTranslationWindowControllerProvider addTranslationWindowControllerProvider;

    /**
     * TRUE if the event with change of encoding should be ignored.
     */
    private boolean suppressEncodingChangeEvent = false;

    /**
     * Map with the list of loaded languages. Key is the language code. Value is true if the language is currently visible.
     */
    private Map<String, Boolean> loadedLanguages = new HashMap<>();

    /**
     * Map with fields used to filter phrases.
     */
    private Map<String, TextField> filterFields = new HashMap<>();

    /**
     * File system service.
     */
    private FileSystemService fileSystemService;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        phraseTableView.getSelectionModel().setCellSelectionEnabled(true);
        phraseTableView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && event.isAltDown()) {
                openMultilineEditor();
            } else if (event.isShortcutDown()) {
                if (event.getCode() == KeyCode.C) {
                    copyToClipboard();
                } else if (event.getCode() == KeyCode.V) {
                    pasteFromClipboard();
                }
            }
        });

        languageSelect.setFocusTraversable(false);
        languageSelect.setCellFactory(param -> new LangSelectCell((fileInfo, selected) -> {
            if (selected) {
                loadLanguage(fileInfo);
            } else {
                unloadLanguage(fileInfo);
            }
        }, messageBundle.getString("editorsPanel.defaultLanguage")));

        charsetSelect.setItems(FXCollections.observableArrayList(Charset.availableCharsets().keySet()));
        charsetSelect.setValue("UTF-8");
        charsetSelect.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!suppressEncodingChangeEvent) {
                if (isModified) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle(messageBundle.getString("editorsPanel.encodingChange.warningTitle"));
                    alert.setHeaderText(messageBundle.getString("editorsPanel.encodingChange.warningHeader"));
                    alert.setContentText(messageBundle.getString("editorsPanel.encodingChange.warningInfo"));

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        reloadAllLoadedLanguages();
                    } else {
                        suppressEncodingChangeEvent = true;
                        charsetSelect.setValue(oldValue);
                    }
                } else {
                    reloadAllLoadedLanguages();
                }
            } else {
                suppressEncodingChangeEvent = false;
            }
        });

        phraseTableView.setRowFactory(tableView -> {
            final TableRow<Phrase>  row               = new TableRow<>();
            final ContextMenu       contextMenu       = new ContextMenu();
            final MenuItem          copy              = new MenuItem(messageBundle.getString("copy"), new ImageView(new Image("/images/icons/copy.png")));
            final MenuItem          paste             = new MenuItem(messageBundle.getString("paste"), new ImageView(new Image("/images/icons/paste.png")));
            final MenuItem          multilineEditor   = new MenuItem(messageBundle.getString("multilineEditor"), new ImageView(new Image("/images/icons/multiline.png")));
            final SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

            copy.setOnAction(event -> copyToClipboard());
            paste.setOnAction(event -> pasteFromClipboard());
            multilineEditor.setOnAction(event -> openMultilineEditor());

            contextMenu.getItems().addAll(copy, paste, separatorMenuItem, multilineEditor);
            row.contextMenuProperty().bind(
                Bindings.when(row.emptyProperty())
                    .then((ContextMenu)null)
                    .otherwise(contextMenu)
            );
            return row;
        });
    }

    /**
     * Sets the file system service.
     *
     * @param fileSystemService Service for accessing files.
     */
    void setFileSystemService(FileSystemService fileSystemService)
    {
        this.fileSystemService = fileSystemService;
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
     * Sets the file for editing.
     *
     * @param file File.
     */
    @Override
    public void setFile(File file)
    {
        langFiles = fileSystemService.getFileInfo(file);

        languageSelect.setItems(FXCollections.observableArrayList(langFiles));
    }

    /**
     * Reloads all loaded translations from the disk.
     */
    private void reloadAllLoadedLanguages()
    {
        langFiles.forEach(this::reloadLanguage);
    }

    /**
     * Reloads all translations for the given language from disk.
     *
     * @param fileInfo Language file to reload.
     */
    private void reloadLanguage(FileInfo fileInfo)
    {
        String localeKey = fileInfo.getLocale() == null ? Phrase.DEFAULT_TRANSLATION_KEY : fileInfo.getLocale().toString();

        if (phraseTableView.getColumns()
            .stream()
            .anyMatch(phraseTableColumn -> phraseTableColumn.getId().equals(localeKey))) {

            readPhrases(fileInfo);
        }
    }

    /**
     * Loads translations for the selected language.
     *
     * @param fileInfo Language data to load.
     */
    private void loadLanguage(FileInfo fileInfo)
    {
        String localeKey = fileInfo.getLocale() == null ? Phrase.DEFAULT_TRANSLATION_KEY : fileInfo.getLocale().toString();

        Optional<TableColumn<Phrase, ?>> existingColumn = phraseTableView.getColumns()
            .stream()
            .filter(phraseTableColumn -> phraseTableColumn.getId().equals(localeKey))
            .findAny();

        if (existingColumn.isPresent()) {
            // selected language is already loaded - only need to display column
            existingColumn.get().setVisible(true);
            loadedLanguages.put(localeKey, Boolean.TRUE);
            return;
        }

        readPhrases(fileInfo);

        String columnTitle;
        if (fileInfo.getLocale() == null) {
            columnTitle = messageBundle.getString("editorsPanel.defaultLanguage");
        } else {
            columnTitle = fileInfo.getLocale().getDisplayLanguage();
            if (fileInfo.getLocale().getDisplayCountry().length() > 0) {
                columnTitle = columnTitle + " (" + fileInfo.getLocale().getDisplayCountry() + ")";
            }
        }

        TableColumn<Phrase, String> column = new TableColumn<>();
        column.setCellValueFactory(new PhraseValueFactory(localeKey));
        column.setId(localeKey);
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            isModified = true;

            String language = ((PhraseValueFactory) event.getTableColumn().getCellValueFactory()).getProperty();
            event.getRowValue().setTranslation(language, event.getNewValue());

            phraseTableView.requestFocus();
        });

        createColumnFilter(column, columnTitle, localeKey);

        phraseTableView.getColumns().add(column);
        phraseTableView.setItems(FXCollections.observableArrayList(phraseMap.values()));

        loadedLanguages.put(localeKey, Boolean.TRUE);
    }

    /**
     * Creates filter that is located inside columns header.
     *
     * @param column Column inside which filter text field will be located.
     * @param columnTitle Column title.
     * @param langKey Language key.
     */
    private void createColumnFilter(TableColumn<Phrase, String> column, String columnTitle, String langKey)
    {
        VBox headerGraphic = new VBox();

        TextField headerTextField = new TextField();
        headerTextField.setMinWidth(10.0);
        headerTextField.getStyleClass().add("filter-text-field");
        headerGraphic.setAlignment(Pos.CENTER);
        headerGraphic.setSpacing(3.0);
        headerGraphic.setPadding(new Insets(2.0, 0.0, 2.0, 0.0));
        headerGraphic.getChildren().addAll(new Label(columnTitle), headerTextField);

        headerTextField.setOnAction(event -> doFilter());

        column.setGraphic(headerGraphic);
        filterFields.put(langKey, headerTextField);
    }

    /**
     * Filters the phrase list using filters inside filters map.
     */
    public void doFilter()
    {
        Stream<Phrase> phraseStream = phraseMap.values().stream();
        for (String languageKey : filterFields.keySet()) {
            if (loadedLanguages.get(languageKey)) {
                String filterValue = filterFields.get(languageKey).getText().trim();
                if (filterValue.length() > 0) {
                    phraseStream = phraseStream.filter(new TranslationFilter(languageKey, filterValue));
                }
            }
        }

        if (phraseKeyColumn.isVisible()) {
            String keySearch = phraseKeySearchField.getText().trim();
            if (keySearch.length() > 0) {
                phraseStream = phraseStream.filter(new PhraseKeyFilter(keySearch));
            }
        }

        phraseTableView.setItems(FXCollections.observableArrayList(phraseStream.collect(Collectors.toList())));
    }

    /**
     * Reads phrases for the given language file.
     *
     * @param fileInfo Language file to read.
     */
    private void readPhrases(FileInfo fileInfo)
    {
        PropertiesConfiguration config = new PropertiesConfiguration();

        config.setEncoding(charsetSelect.getValue());
        try {
            config.load(fileInfo.getFile());
        } catch (ConfigurationException e) {
            showExceptionAlert(
                messageBundle.getString("error.generalErrorTitle"),
                messageBundle.getString("error.unableToLoadFilesContent"),
                messageBundle.getString("error.readPropertiesFileError"),
                e
            );
        }

        config.getKeys().forEachRemaining(s -> {
            Phrase phrase;

            if (phraseMap.containsKey(s)) {
                phrase = phraseMap.get(s);
            } else {
                phrase = new Phrase(s);
                phraseMap.put(s, phrase);
            }

            if (fileInfo.getLocale() == null) {
                phrase.setTranslation(Phrase.DEFAULT_TRANSLATION_KEY, config.getString(s));
                String comment = config.getLayout().getComment(s);
                if (comment != null) {
                    phrase.setComment(Phrase.DEFAULT_TRANSLATION_KEY, comment.substring(1).trim());
                }
            } else {
                String language = fileInfo.getLocale().toString();
                phrase.setTranslation(language, config.getString(s));

                String comment = config.getLayout().getComment(s);
                if (comment != null) {
                    phrase.setComment(language, comment.substring(1).trim());
                }
            }
        });
    }

    /**
     * Unloads selected language (hides it's column).
     *
     * @param fileInfo Language do hide.
     */
    private void unloadLanguage(FileInfo fileInfo)
    {
        String localeKey = fileInfo.getLocale() == null ? Phrase.DEFAULT_TRANSLATION_KEY : fileInfo.getLocale().toString();

        phraseTableView.getColumns()
            .stream()
            .filter(phraseTableColumn -> phraseTableColumn.getId().equals(localeKey))
            .findFirst()
            .ifPresent(phraseTableColumn -> phraseTableColumn.setVisible(false));

        loadedLanguages.put(localeKey, Boolean.FALSE);
    }

    /**
     * Opens new window with multiline editor.
     */
    private void openMultilineEditor()
    {
        ObservableList<TablePosition> selectedCells = phraseTableView.getSelectionModel().getSelectedCells();
        if (selectedCells.size() == 1) {
            int col = calculateTrueColumnIndex(selectedCells.get(0).getColumn());
            int row = selectedCells.get(0).getRow();

            TableColumn column = phraseTableView.getColumns().get(col);

            if (column.getCellValueFactory() instanceof PhraseValueFactory) {
                String       lang          = ((PhraseValueFactory) column.getCellValueFactory()).getProperty();
                Phrase       phrase        = phraseTableView.getItems().get(row);
                List<String> languagesList = new LinkedList<>();

                // gathering other languages for multiline editor
                phraseTableView.getColumns().stream().filter(TableColumnBase::isVisible).forEach(c -> {
                    if (((TableColumn) c).getCellValueFactory() instanceof PhraseValueFactory) {
                        String language = ((PhraseValueFactory) ((TableColumn) c).getCellValueFactory()).getProperty();
                        if (!language.equals(lang)) {
                            languagesList.add(language);
                        }
                    }
                });

                languagesList.add(0, lang);

                MultilineEditorWindowController multilineEditorWindowController = multilineEditorWindowControllerProvider.get();

                multilineEditorWindowController.setPhrase(phrase);
                multilineEditorWindowController.setLanguages(languagesList);
                multilineEditorWindowController.setEditedLanguage(lang);
                multilineEditorWindowController.setOnSaveCallback(param -> {
                    isModified = true;
                    return null;
                });
                multilineEditorWindowController.showWindow();
            }
        }
    }

    /**
     * Copies content of selected cell into clipboard.
     */
    private void copyToClipboard()
    {
        ObservableList<TablePosition> selectedCells = phraseTableView.getSelectionModel().getSelectedCells();
        if (selectedCells.size() == 1) {
            int col = calculateTrueColumnIndex(selectedCells.get(0).getColumn());
            int row = selectedCells.get(0).getRow();

            TableColumn column = phraseTableView.getColumns().get(col);
            Phrase      phrase = phraseTableView.getItems().get(row);
            StringSelection stringSelection;

            if (column.getCellValueFactory() instanceof PhraseValueFactory) {
                String lang        = ((PhraseValueFactory) column.getCellValueFactory()).getProperty();
                String translation = phrase.getTranslation(lang);

                stringSelection = new StringSelection(translation != null ? translation : "");
            } else {
                stringSelection = new StringSelection(phrase.getKey());
            }

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        }
    }

    /**
     * Pastes data from clipboard into selected cell.
     */
    private void pasteFromClipboard()
    {
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        if (!systemClipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
            return;
        }

        String textToPaste;
        try {
            textToPaste = (String)systemClipboard.getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            showExceptionAlert(
                messageBundle.getString("error.generalErrorTitle"),
                messageBundle.getString("error.unableToAccessClipboard"),
                messageBundle.getString("error.pasteFromClipboardError"),
                e
            );
            return;
        }

        ObservableList<TablePosition> selectedCells = phraseTableView.getSelectionModel().getSelectedCells();
        if (selectedCells.size() == 1) {
            int col = calculateTrueColumnIndex(selectedCells.get(0).getColumn());
            int row = selectedCells.get(0).getRow();

            TableColumn column = phraseTableView.getColumns().get(col);
            Phrase      phrase = phraseTableView.getItems().get(row);

            if (column.getCellValueFactory() instanceof PhraseValueFactory) {
                String  lang        = ((PhraseValueFactory) column.getCellValueFactory()).getProperty();
                boolean needRefresh = phrase.getTranslation(lang) == null;

                phrase.setTranslation(lang, textToPaste);

                if (needRefresh) {
                    phraseTableView.refresh();
                }
            }
        }
    }

    /**
     * If columns are hidden the index of the selected one will be lower by the number of columns hidden before the selected.
     * This method adds appropriate number to the index so it will represent the real index of the selected column.
     *
     * @param selectedIndex Selected column index.
     * @return The true index of the column on the list.
     */
    private int calculateTrueColumnIndex(int selectedIndex)
    {
        int count = 0;

        while (count <= selectedIndex) {
            if (!phraseTableView.getColumns().get(count).isVisible()) {
                selectedIndex++;
            }

            count++;
        }

        return selectedIndex;
    }

    /**
     * Sets the provider for multiline editor.
     *
     * @param multilineEditorWindowControllerProvider Multiline editor provider.
     */
    void setMultilineEditorWindowControllerProvider(MultilineEditorWindowControllerProvider multilineEditorWindowControllerProvider)
    {
        this.multilineEditorWindowControllerProvider = multilineEditorWindowControllerProvider;
    }

    /**
     * Creates JavaFX tab for the panel.
     *
     * @return Tab.
     */
    @Override
    public Tab getTab()
    {
        if (tab == null) {
            tab = new Tab();
            tab.setText(langFiles.get(0).getFile().getName());
            tab.setContent(mainPane);
            tab.setClosable(true);
        }

        return tab;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isModified()
    {
        return isModified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveData()
    {
        langFiles.forEach(this::saveFile);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPhraseKeyVisibility(boolean visible)
    {
        phraseKeyColumn.setVisible(visible);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showAddNewLanguageWindow()
    {
        AddTranslationWindowController controller = addTranslationWindowControllerProvider.get();
        controller.setLanguageSelectedCallback(lang -> {
            File baseFile  = langFiles.get(0).getFile();
            File directory = baseFile.getParentFile();
            String newFile = fileSystemService.getFileBaseName(baseFile) + "_" + lang.getCode() + ".properties";
            File langFile  = new File(directory, newFile);

            if (!langFile.exists()) {
                try {
                    langFile.createNewFile();
                } catch (IOException e) {
                    showExceptionAlert(
                        messageBundle.getString("error.generalErrorTitle"),
                        messageBundle.getString("error.unableToCreateFile"),
                        messageBundle.getString("error.createPropertiesFileError"),
                        e
                    );
                }

                FileInfo fileInfo = new FileInfo(langFile, new Locale(lang.getCode()));
                langFiles.add(fileInfo);

                languageSelect.getItems().add(fileInfo);
            }

            return null;
        });

        controller.showWindow();
    }

    /**
     * Sets new translation window provider.
     *
     * @param addTranslationWindowControllerProvider Provider.
     */
    void setAddTranslationWindowControllerProvider(AddTranslationWindowControllerProvider addTranslationWindowControllerProvider)
    {
        this.addTranslationWindowControllerProvider = addTranslationWindowControllerProvider;
    }

    /**
     * Reads phrases for the given language file.
     *
     * @param fileInfo Language file to read.
     */
    private void saveFile(FileInfo fileInfo)
    {
        String lang = fileInfo.getLocale() == null ? Phrase.DEFAULT_TRANSLATION_KEY : fileInfo.getLocale().toString();

        if (!loadedLanguages.containsKey(lang)) {
            // language was never loaded into memory so it was not modified
            return;
        }

        PropertiesConfiguration config = new PropertiesConfiguration();

        config.setEncoding(charsetSelect.getValue());
        try {
            config.load(fileInfo.getFile());
        } catch (ConfigurationException e) {
            showExceptionAlert(
                messageBundle.getString("error.generalErrorTitle"),
                messageBundle.getString("error.unableToLoadFilesContent"),
                messageBundle.getString("error.readPropertiesFileError"),
                e
            );
        }

        for (Phrase phrase : phraseMap.values()) {
            String translation = phrase.getTranslation(lang);

            if (translation != null) {
                config.setProperty(phrase.getKey(), translation);

                String comment = phrase.getComment(lang);
                if (comment != null) {
                    config.getLayout().setComment(phrase.getKey(), "# " + comment);
                } else {
                    config.getLayout().setComment(phrase.getKey(), null);
                }
            } else {
                config.clearProperty(phrase.getKey());
            }
        }

        try {
            config.save(fileInfo.getFile());

            isModified = false;
        } catch (ConfigurationException e) {
            showExceptionAlert(
                messageBundle.getString("error.generalErrorTitle"),
                messageBundle.getString("error.unableToWriteFilesContent"),
                messageBundle.getString("error.writePropertiesFileError"),
                e
            );
        }
    }

    /**
     * Opens alert window with exception details.
     *
     * @param windowTitle Window title.
     * @param errorHeader Window header.
     * @param errorContent Message content.
     * @param ex Exception to be displayed.
     */
    private void showExceptionAlert(String windowTitle, String errorHeader, String errorContent, Exception ex)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(windowTitle);
        alert.setHeaderText(errorHeader);
        alert.setContentText(errorContent);

        if (ex != null) {
            StringWriter sw = new StringWriter();
            PrintWriter  pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            Label label = new Label(messageBundle.getString("error.exceptionDetails"));

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            alert.getDialogPane().setExpandableContent(expContent);
        }

        alert.show();
    }
}
