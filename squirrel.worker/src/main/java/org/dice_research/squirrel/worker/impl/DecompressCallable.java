package org.dice_research.squirrel.worker.impl;

import org.dice_research.squirrel.analyzer.compress.impl.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DecompressCallable implements Callable<List<File>> {

    private Future<File> fetched;


    @Override
    public List<File> call() throws ExecutionException, InterruptedException {

        return decompressFile(fetched.get());
    }

    DecompressCallable(Future fetched) {

        this.fetched = fetched;
    }


    private List<File> decompressFile(File data) {

        FileManager fm = new FileManager();
        return (fm.decompressFile(data));
    }


}



