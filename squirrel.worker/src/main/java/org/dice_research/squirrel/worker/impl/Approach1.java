package org.dice_research.squirrel.worker.impl;

import org.dice_research.squirrel.analyzer.Analyzer;
import org.dice_research.squirrel.analyzer.compress.impl.FileManager;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.Sink;
import org.dice_research.squirrel.utils.TempPathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

public class Approach1 implements Callable<Iterator<byte[]>> {
    private List<File> compressedFiles;
    private CrawleableUri uri;
    private Analyzer analyzer;
    private Sink sink;
    private UriCollector collector;
    private List<File> decompressedFiles;

    @Override
    public Iterator<byte[]> call() {
        return analyzer.analyze(uri, decompressedFiles.remove(0), sink);//Still need to work on iterating the decompressedFiles
    }

    Approach1(File file, CrawleableUri uri, Analyzer analyzer, Sink sink, UriCollector collector) {
        this.compressedFiles = extractFilesFromDirectory(file);
        this.uri = uri;
        this.analyzer = analyzer;
        this.sink = sink;
        this.collector = collector;
    }

    List<File> extractFilesFromDirectory(File file) {
        List<File> fetchedFiles = new ArrayList<>();
        if (file != null && file.isDirectory()) {
            fetchedFiles.addAll(TempPathUtils.searchPath4Files(file));
        } else {
            fetchedFiles.add(file);
        }
        return fetchedFiles;
    }

    void decompressFiles() {
        if (compressedFiles.size() > 0) {
            FileManager fm = new FileManager();

            // open the sink only if a fetcher has been found
            sink.openSinkForUri(uri);
            collector.openSinkForUri(uri);
            // Go over all files and analyze them
            for (File data : compressedFiles) {
                if (data != null) {
                    decompressedFiles = fm.decompressFile(data);
                }
            }
        }
    }
}
