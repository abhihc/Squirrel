package org.dice_research.squirrel.worker.impl;

import org.dice_research.squirrel.metadata.CrawlingActivity;
import org.dice_research.squirrel.utils.TempPathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ExtractFiles implements Callable<List<File>> {

    private final CrawlingActivity activity;
    private File fetched;

    private List<File> fetchedFiles = new ArrayList<>();

    @Override
    public List<File> call() {

        return extractFilesFromDirectory(fetched);

    }

    ExtractFiles(File fetched, CrawlingActivity activity) {
        this.fetched = fetched;
        this.activity = activity;
    }


    private List<File> extractFilesFromDirectory(File file) {
        if (file == null) {
            // There are no files
            activity.addStep(getClass(), "No files for analysis available.");
            activity.setState(CrawlingActivity.CrawlingURIState.FAILED);
        } else {
            if (file.isDirectory()) {
                fetchedFiles.addAll(TempPathUtils.searchPath4Files(file));
            } else {
                fetchedFiles.add(file);
            }
        }
        return fetchedFiles;
    }
}
