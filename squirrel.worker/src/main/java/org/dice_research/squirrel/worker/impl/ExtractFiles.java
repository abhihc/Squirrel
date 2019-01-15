package org.dice_research.squirrel.worker.impl;

import org.dice_research.squirrel.utils.TempPathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ExtractFiles implements Callable<List<File>> {

    private File fetched;

    @Override
    public List<File> call() {

        return extractFilesFromDirectory(fetched);

    }

    ExtractFiles(File fetched) {
        this.fetched = fetched;
    }


    public List<File> extractFilesFromDirectory(File file) {
        List<File> fetchedFiles = new ArrayList<>();
        if (file != null && file.isDirectory()) {
            fetchedFiles.addAll(TempPathUtils.searchPath4Files(file));
        } else {
            fetchedFiles.add(file);
        }
        return fetchedFiles;
    }

}
