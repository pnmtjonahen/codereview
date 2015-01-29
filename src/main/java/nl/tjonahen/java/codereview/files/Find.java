/*
 * Copyright (C) 2015 Philippe Tjon - A - Hen, philippe@tjonahen.nl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.tjonahen.java.codereview.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Recursive file listing under a specified directory.
 *
 */
public final class Find {

    private final File aStartingDir;

    /**
     * Constr.
     *
     * @param aStartingDir -
     */
    public Find(File aStartingDir) {
        this.aStartingDir = aStartingDir;
    }

    /**
     * Recursively walk a directory tree and return a List of all
     * Files found; the List is sorted using File.compareTo().
     * @return -
     * @throws FileNotFoundException -
     */
    public List<File> find() throws FileNotFoundException {
        validateDirectory(aStartingDir);

        final List<File> result = new ArrayList<File>();
        for (File file : getFileListingNoSort(aStartingDir)) {
            if (file.getName().endsWith(".java")) {
                result.add(file);
            }
        }
        return result;
    }

    // PRIVATE

    private List<File> getFileListingNoSort(final File pStartingDir) throws FileNotFoundException {
        final List<File> result = new ArrayList<File>();
        final File[] filesAndDirs = pStartingDir.listFiles();
        final List<File> filesDirs = Arrays.asList(filesAndDirs);
        for(File file : filesDirs) {
            result.add(file);
            if (!file.isFile()) {
                //must be a directory
                //recursive call!
                List<File> deeperList = getFileListingNoSort(file);
                result.addAll(deeperList);
            }
        }
        return result;
    }

    /**
     * Directory is valid if it exists, does not represent a file, and can be read.
     */
    private void validateDirectory(final File aDirectory) throws FileNotFoundException {
        if (aDirectory == null) {
            throw new IllegalArgumentException("Directory should not be null.");
        }
        if (!aDirectory.exists()) {
            throw new FileNotFoundException("Directory does not exist: " + aDirectory);
        }
        if (!aDirectory.isDirectory()) {
            throw new IllegalArgumentException("Is not a directory: " + aDirectory);
        }
        if (!aDirectory.canRead()) {
            throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
        }
    }
}