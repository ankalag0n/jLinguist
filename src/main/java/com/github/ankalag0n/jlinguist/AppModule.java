package com.github.ankalag0n.jlinguist;

import com.github.ankalag0n.jlinguist.controller.*;
import com.github.ankalag0n.jlinguist.file.FileSystemService;
import com.github.ankalag0n.jlinguist.file.FileSystemServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.util.ResourceBundle;

/**
 * Module class for Guice configuration.
 */
public class AppModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(MainWindowController.class).toProvider(MainWindowControllerProvider.class);
        bind(EditorsPanelController.class).toProvider(EditorsPanelControllerProvider.class);
        bind(MultilineEditorWindowController.class).toProvider(MultilineEditorWindowControllerProvider.class);
        bind(AboutWindowController.class).toProvider(AboutWindowControllerProvider.class);
        bind(FileSystemService.class).to(FileSystemServiceImpl.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    ResourceBundle messageBundle()
    {
        return ResourceBundle.getBundle("locale/MessageBundle");
    }
}
