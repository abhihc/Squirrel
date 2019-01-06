package org.dice_research.squirrel.worker.impl;

import org.dice_research.squirrel.analyzer.Analyzer;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.fetcher.Fetcher;
import org.dice_research.squirrel.sink.Sink;

import java.util.Iterator;
import java.util.concurrent.Callable;

public class Approach3 implements Callable<Iterator<byte[]>> {
    private CrawleableUri uri;
    private Analyzer analyzer;
    private Sink sink;
    private Fetcher fetcher;


    @Override
    public Iterator<byte[]> call() {
        //Do the complete work flow from fetching the URI content to analyzing the fetched data



        return null;
    }

    Approach3(CrawleableUri uri,Fetcher fetcher,Analyzer analyzer, Sink sink){
        this.fetcher = fetcher;
        this.uri = uri;
        this.analyzer = analyzer;
        this.sink = sink;

    }


}
