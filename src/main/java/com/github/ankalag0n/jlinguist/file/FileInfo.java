package com.github.ankalag0n.jlinguist.file;

import java.io.File;
import java.util.Locale;

/**
 * Holds information about file.
 */
public class FileInfo
{
    /**
     * The file.
     */
    private File file;

    /**
     * Language for the file.
     */
    private Locale locale;

    /**
     * Default constructor.
     */
    public FileInfo() {}

    /**
     * Constructor.
     *
     * @param file The file.
     * @param locale Language for the file.
     */
    public FileInfo(File file, Locale locale)
    {
        this.file = file;
        this.locale = locale;
    }

    /**
     * @return The file.
     */
    public File getFile()
    {
        return file;
    }

    /**
     * @param file The file.
     * @return this
     */
    public FileInfo setFile(File file)
    {
        this.file = file;
        return this;
    }

    /**
     * @return File locale
     */
    public Locale getLocale()
    {
        return locale;
    }

    /**
     * @param locale File locale
     * @return this
     */
    public FileInfo setLocale(Locale locale)
    {
        this.locale = locale;
        return this;
    }

    @Override
    public String toString()
    {
        return "FileInfo[file = " + file + ", locale = " + locale + "]";
    }
}
