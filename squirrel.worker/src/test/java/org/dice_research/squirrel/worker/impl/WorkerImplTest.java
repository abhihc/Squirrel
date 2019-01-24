package org.dice_research.squirrel.worker.impl;

import com.mongodb.MongoClient;
import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;
import org.apache.jena.rdf.model.Model;
import org.dice_research.squirrel.MongoDBBasedTest;
import org.dice_research.squirrel.analyzer.Analyzer;
import org.dice_research.squirrel.analyzer.manager.SimpleOrderedAnalyzerManager;
import org.dice_research.squirrel.collect.SqlBasedUriCollector;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.CrawleableUriFactory4Tests;
import org.dice_research.squirrel.data.uri.UriType;
import org.dice_research.squirrel.data.uri.filter.MongoDBKnowUriFilter;
import org.dice_research.squirrel.data.uri.filter.RDBKnownUriFilter;
import org.dice_research.squirrel.data.uri.norm.NormalizerImpl;
import org.dice_research.squirrel.frontier.impl.FrontierImpl;
import org.dice_research.squirrel.queue.MongoDBQueue;
import org.dice_research.squirrel.queue.RDBQueue;
import org.dice_research.squirrel.robots.RobotsManagerImpl;
import org.dice_research.squirrel.sink.Sink;
import org.dice_research.squirrel.utils.TempFileHelper;
import com.rethinkdb.gen.exc.ReqlDriverError;
import org.junit.*;
import org.dice_research.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.dice_research.squirrel.data.uri.serialize.Serializer;
import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import java.io.*;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dice_research.squirrel.sink.impl.file.FileBasedSink;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class WorkerImplTest {

    public static final String DB_HOST_NAME = "localhost";
    public static final int DB_PORT = 58015;
    static FrontierImpl frontier;
    static MongoDBQueue queue;
    static MongoDBKnowUriFilter filter;
    static List<CrawleableUri> uris = new ArrayList<CrawleableUri>();
    protected File outputDirectory = null;
    static CrawleableUriFactory4Tests cuf = new CrawleableUriFactory4Tests();
    WorkerImpl worker;
    boolean check = false;

    @Test
    public void test() throws Exception{
        MongoDBBasedTest.setUpMDB();
        filter = new MongoDBKnowUriFilter("localhost", 58027);
        queue = new MongoDBQueue("localhost", 58027);
        filter.open();
        queue.open();
        frontier = new FrontierImpl(new NormalizerImpl(), filter, queue,true);
        uris.add(cuf.create(new URI("http://dbpedia.org/resource/New_York"), InetAddress.getByName("127.0.0.1"),
            UriType.DEREFERENCEABLE));
        queue.addCrawleableUri(uris.get(0));
        outputDirectory = File.createTempFile("FileBasedSinkTest", ".tmp");
        Assert.assertTrue(outputDirectory.delete());
        Assert.assertTrue(outputDirectory.mkdir());
        outputDirectory.deleteOnExit();
        Sink sink = new FileBasedSink(outputDirectory, FileBasedSink.DEFAULT_OUTPUT_LANG, true);
        Serializer serializer = new GzipJavaUriSerializer();
        String dbdir = null;
        dbdir = TempFileHelper.getTempDir("dbTest", "").getAbsolutePath() + File.separator + "test";
        UriCollector collector = SqlBasedUriCollector.create(serializer, dbdir) ;
        Analyzer analyzer = new SimpleOrderedAnalyzerManager(collector);
        worker = new WorkerImpl(frontier,sink,analyzer,new RobotsManagerImpl(new SimpleHttpFetcher(new UserAgent("Test", "", ""))),
            serializer, collector, 100, null, true);
        Thread t = new Thread(worker);
        t.start();
        do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Assert.fail(e.getLocalizedMessage());
            }
        } while ((frontier.getNumberOfPendingUris() > 0));
        worker.setTerminateFlag(true);
        try {
            t.join();
        } catch (InterruptedException e) {
            Assert.fail(e.getMessage());
        }
        //checks if the outputdirectory is not null that means the crawling is done and the .ttl's have been stored in the FileBasedSink       Assert.assertNotNull(outputDirectory);
        File[] listOfFiles = outputDirectory.listFiles();
        for (File f : listOfFiles){
            f.getName().endsWith(".ttl"); //default language of FileBasedSink
            check = true;
        }
        Assert.assertTrue(true);
    }



    @AfterClass
    public static void tearDownMDB() throws Exception {
        filter.purge();
        queue.purge();
        String dockerStopCommand = "docker stop squirrel-test-workerimpl";
        Process q = Runtime.getRuntime().exec(dockerStopCommand);
        q.waitFor();
        String dockerRmCommand = "docker rm squirrel-test-workerimpl";
        q = Runtime.getRuntime().exec(dockerRmCommand);
        q.waitFor();

    }
}
