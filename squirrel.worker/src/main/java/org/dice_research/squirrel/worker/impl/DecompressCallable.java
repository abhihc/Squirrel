package org.dice_research.squirrel.worker.impl;

import org.dice_research.squirrel.analyzer.compress.impl.FileManager;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

public class DecompressCallable implements Callable<List<File>> {

    private File fetched;


    @Override
    public List<File> call() {

        return decompressFile(fetched);

    }

    DecompressCallable(File fetched) {

        this.fetched = fetched;
    }


    public List<File> decompressFile(File data) {

        FileManager fm = new FileManager();
        return (fm.decompressFile(data));
    }


}



