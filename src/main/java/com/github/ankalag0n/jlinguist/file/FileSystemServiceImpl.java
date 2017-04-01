package com.github.ankalag0n.jlinguist.file;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for performing operations on file system.
 */
public class FileSystemServiceImpl implements FileSystemService
{
    /**
     * Pattern used to search language parts in file names.
     */
    private final Pattern filePattern = Pattern.compile("^(.*)(_([a-z]{2})(_[A-Z]{2})?)\\.properties");

    /**
     * Returns list with all language files and their locale for the given file.
     *
     * @param file File that will be parsed in search for language files.
     * @return List of all language files in group.
     */
    @Override
    public List<FileInfo> getFileInfo(File file)
    {
        if (!file.getName().endsWith(".properties")) {
            // file is not a property file - returning empty list
            return new LinkedList<>();
        }

        Matcher        matcher   = filePattern.matcher(file.getName());
        List<FileInfo> filesData = new LinkedList<>();
        File           path      = file.getParentFile();

        if (matcher.find()) {
            File defaultTranslation = findDefaultTranslationFile(matcher.group(1), path);
            if (defaultTranslation != null) {
                filesData.add(new FileInfo(defaultTranslation, null));
            }

            filesData.add(parseFileInfo(file, matcher));
            filesData.addAll(scanPathForFiles(matcher.group(1), path, file.getName()));
        } else {
            filesData.add(new FileInfo(file, null));

            String filename = file.getName();

            filesData.addAll(scanPathForFiles(filename.substring(0, filename.length() - 11), path, null));
        }

        return filesData;
    }

    /**
     * Returns base name of the file without it's extension and language suffix.
     *
     * @param file File.
     * @return Base name.
     */
    @Override
    public String getFileBaseName(File file)
    {
        Matcher matcher = filePattern.matcher(file.getName());

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return file.getName().substring(0, file.getName().lastIndexOf('.'));
        }
    }

    /**
     * Scans directory for language files.
     *
     * @param baseName Base filename to search for (without extension).
     * @param path Path to scan.
     * @param excludedName Filename that is excluded from scan.
     * @return List of file with locale.
     */
    private List<FileInfo> scanPathForFiles(String baseName, File path, String excludedName)
    {
        List<FileInfo> result = new LinkedList<>();
        for (File file : path.listFiles()) {
            if (!file.isDirectory() && file.getName().startsWith(baseName)) {
                if (excludedName != null && file.getName().equals(excludedName)) {
                    continue;
                }

                Matcher matcher = filePattern.matcher(file.getName());
                if (matcher.find()) {
                    result.add(parseFileInfo(file, matcher));
                }
            }
        }

        return result;
    }

    /**
     * Parses file name and returns file with it's locale.
     *
     * @param file Parsed file.
     * @param matcher Filename search result.
     * @return File info.
     */
    private FileInfo parseFileInfo(File file, Matcher matcher)
    {
        Locale locale;
        String country = matcher.group(4);
        if (country != null) {
            locale = new Locale(matcher.group(3), country.substring(1) /* remove _ at the beginning */);
        } else {
            locale = new Locale(matcher.group(3));
        }

        return new FileInfo(file, locale);
    }

    /**
     * Finds file with default language.
     *
     * @param baseName Base file name without extension.
     * @param path Path where the file should exist.
     * @return File with default translation or null if file not exists.
     */
    private File findDefaultTranslationFile(String baseName, File path)
    {
        File defaultFile = new File(path, baseName + ".properties");

        if (defaultFile.exists()) {
            return defaultFile;
        }

        return null;
    }
}
