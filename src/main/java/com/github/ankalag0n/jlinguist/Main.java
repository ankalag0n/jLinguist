package com.github.ankalag0n.jlinguist;

import com.github.ankalag0n.jlinguist.controller.MainWindowController;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.stage.Stage;

/**
 * Main class of the application.
 */
public class Main extends Application
{
    /**
     * Static instance for this applications object.
     */
    private static Main me;

    public static void main(String[] args)
    {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        me = this;

        Injector injector = Guice.createInjector(new AppModule());

        MainWindowController mainWindowController = injector.getInstance(MainWindowController.class);

        mainWindowController.setMainWindow(primaryStage);
        primaryStage.setScene(mainWindowController.getScene());
        primaryStage.setTitle("jLinguist");
        primaryStage.setWidth(900.0);
        primaryStage.setHeight(600.0);
        primaryStage.show();
    }

    /**
     * Returns instance of the current applications class.
     *
     * @return Application.
     */
    public static Main getInstance()
    {
        return me;
    }
}
