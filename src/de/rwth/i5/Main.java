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

import javax.jws.soap.SOAPBinding.Style;

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

			int i=0;

			for (BibTeXObject bibTeXObject : bibTeXDatabase.getObjects()) {
				try {
					BibTeXEntry bibTeXEntry = (BibTeXEntry) bibTeXObject;
					String newKey = i5BibTexKey.generate(bibTeXEntry);
					BibTeXEntry modifiedEntry = new BibTeXEntry(bibTeXEntry.getType(), new Key(newKey));

					// reformat authors to "<lastname>, <firstnames>"
					StringValue oldAuthors = (StringValue) bibTeXEntry.getField(BibTeXEntry.KEY_AUTHOR);
					String reformattedAuthors = i5BibTexKey.reformatAuthors(oldAuthors.toUserString());
					
					Value newAuthors = new StringValue(reformattedAuthors,org.jbibtex.StringValue.Style.BRACED);
					bibTeXEntry.removeField(BibTeXEntry.KEY_AUTHOR);
					modifiedEntry.addField(BibTeXEntry.KEY_AUTHOR, newAuthors);
					modifiedEntry.addAllFields(bibTeXEntry.getFields());
					modifiedData.put(bibTeXEntry.getKey(), modifiedEntry);
					
					// print out keys of papers, where author was reformatted
					if(!oldAuthors.toUserString().trim().equals(reformattedAuthors)){
						System.out.println(newKey + " - reformatted authors -> double-check!");
					}
					
					 if(i5BibTexKey.isAuthorFirstNameAbbreviated(modifiedEntry.getField(BibTeXEntry.KEY_AUTHOR).toUserString())){
			            System.out.println(newKey + " - author first name abbreviated -> add complete first names!");
			         }
					
					// print out keys of papers, where no abstract was found 
					Key abstractKey = new Key("abstract");
					if(bibTeXEntry.getField(abstractKey) == null){
						System.out.println(newKey + " - no abstract found -> add abstract!");
					}
					
					// print out keys of papers, where no url was found 
					Key urlKey = new Key("url");
					if(bibTeXEntry.getField(urlKey) == null){
						System.out.println(newKey + " - no url found -> add url!");
					}
					
					// print out keys of papers, where no keywords were found 
					Key keywordsKey = new Key("keywords");
					if(bibTeXEntry.getField(keywordsKey) == null){
						System.out.println(newKey + " - no keywords found -> add keywords!");
					}
					
					

				} catch (IllegalArgumentException ignored) {
					String ignoredKey = ((BibTeXEntry) bibTeXObject).getKey().toString();
					System.err.println(MessageFormat.format("Skipped entry with key: {0}\t Couldn''t generate i5 compatible key. Author field might missing?", ignoredKey));
				} catch (Exception ex) {
					ex.printStackTrace();
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
