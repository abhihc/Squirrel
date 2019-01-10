package org.dice_research.squirrel.worker.impl;

import org.dice_research.squirrel.analyzer.Analyzer;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.Sink;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.Callable;

public class MyCallable implements Callable<Iterator<byte[]>> {
    private File decompressedFile;
    private Analyzer analyzer;
    private Sink sink;
    private CrawleableUri uri;


    @Override
    public Iterator<byte[]> call() {
        return analyzer.analyze(uri, decompressedFile, sink);
    }

    MyCallable(File file, CrawleableUri uri, Analyzer analyzer, Sink sink) {
        this.decompressedFile = file;
        this.uri = uri;
        this.analyzer = analyzer;
        this.sink = sink;
    }
}
