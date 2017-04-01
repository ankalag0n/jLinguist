package com.github.ankalag0n.jlinguist.controller;

import com.github.ankalag0n.jlinguist.Main;
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for about window.
 */
public class AboutWindowControllerImpl implements javafx.fxml.Initializable, AboutWindowController
{
    /**
     * Scene managed by the controller.
     */
    private Scene scene;

    /**
     * Message bundle.
     */
    private ResourceBundle messageBundle;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //
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

    @Override
    public void showWindow()
    {
        Stage window = new Stage();
        window.setScene(scene);
        window.setWidth(400.0);
        window.setHeight(400.0);
        window.setResizable(false);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(messageBundle.getString("about.windowTitle"));

        window.show();
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

    public void openGuiceWebPage()
    {
        Main.getInstance().getHostServices().showDocument("https://github.com/google/guice");
    }

    public void openCommonsWebPage()
    {
        Main.getInstance().getHostServices().showDocument("https://commons.apache.org/proper/commons-configuration/");
    }

    public void openFatCowWebPage()
    {
        Main.getInstance().getHostServices().showDocument("http://www.fatcow.com/free-icons");
    }
}
