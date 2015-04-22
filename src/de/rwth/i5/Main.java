package de.rwth.i5;

import org.jbibtex.*;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ExampleMode;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class Main {
    @Option(name="-i",required = true, usage="input bibtex file",metaVar="INPUT")
    private File infile = new File(".");


    @Option(name="-o",required = true,usage="output bibtex file",metaVar="OUTPUT")
    private File outfile = new File(".");

    private BibTeXDatabase bibTeXDatabase;

    public static void main(String[] args) {
        new Main().doMain(args);
    }

    private void doMain(String[] args) {

        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch( CmdLineException e ) {
            System.err.println(e.getMessage());
            System.err.println("java SampleMain [options...] arguments...");
            parser.printUsage(System.err);
            System.err.println();
            System.err.println("  Example: java SampleMain"+parser.printExample(ExampleMode.ALL));
            return;
        }


        try {
            BibTeXParser bibTeXParser = new BibTeXParser();
            bibTeXDatabase = bibTeXParser.parseFully(new FileReader(infile));

            Map<Key, BibTeXEntry> modifiedData = new HashMap<Key, BibTeXEntry>();

            for (BibTeXObject bibTeXObject : bibTeXDatabase.getObjects()) {
                try {
                    BibTeXEntry bibTeXEntry = (BibTeXEntry) bibTeXObject;
                    String newKey = i5BibTexKey.generate(bibTeXEntry);
                    BibTeXEntry modifiedEntry = new BibTeXEntry(bibTeXEntry.getType(), new Key(newKey));
                    modifiedEntry.addAllFields(bibTeXEntry.getFields());
                    modifiedData.put(bibTeXEntry.getKey(), modifiedEntry);

                } catch (IllegalArgumentException ignored) {
                    String ignoredKey = ((BibTeXEntry) bibTeXObject).getKey().toString();
                    System.err.println(MessageFormat.format("Skipped entry with key: {0}\t Couldn''t generate i5 compatible key. Author field might missing?", ignoredKey));
                } catch (Exception ex) {
                    String ignoredKey = ((BibTeXEntry) bibTeXObject).getKey().toString();
                    System.err.println(MessageFormat.format("Skipped entry with key: {0}\t Couldn''t generate i5 compatible key.", ignoredKey));
                }
            }

            for (Map.Entry<Key, BibTeXEntry> modifiedDataEntry : modifiedData.entrySet()) {
                bibTeXDatabase.removeObject(bibTeXDatabase.resolveEntry(modifiedDataEntry.getKey()));
                bibTeXDatabase.addObject(modifiedDataEntry.getValue());
            }


            BibTeXFormatter bibtexFormatter = new BibTeXFormatter();
            if(!outfile.exists()) {
                outfile.createNewFile();
            }
            bibtexFormatter.format(bibTeXDatabase, new FileWriter(outfile));

        } catch (Exception e) {
            System.err.println("Problem at conversion, please try some other!");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
