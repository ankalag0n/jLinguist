package com.github.ankalag0n.jlinguist.file;

import java.io.File;
import java.util.List;

/**
 * Service for performing operations on file system.
 */
public interface FileSystemService
{
    /**
     * Returns list with all language files and their locale for the given file.
     *
     * @param file File that will be parsed in search for language files.
     * @return List of all language files in group.
     */
    List<FileInfo> getFileInfo(File file);

    /**
     * Returns base name of the file without it's extension and language suffix.
     *
     * @param file File.
     * @return Base name.
     */
    String getFileBaseName(File file);
}
