# BibTeX-KeyGenerator

The ACIS group at Chair of Computer Science 5 (Information Systems & Databases) uses a standardized scheme for BibTeX keys as part of its research literature management. BibTeX-KeyGenerator transforms a given BibTeX file with BibTeX keys incompliant to this scheme to a new BibTeX file with all keys compliant to the ACIS scheme. Furthermore, BibTeX-KeyGenerator transforms all author names to the standard format <lastname>, <firstnames>. At the same time, the tool executes a couple of BibTeX quality checks and provides directions for improvement for all entries in the passed BibTeX file. Quality checks include abbreviated author names as well as missing fields like abstract, url, and keywords.

## Scheme

The scheme defines a 6-char sequence of the form `NNNNYY`, where 

- `YY` - year of publication (`15` for 2015; `77` for 1977)

- `NNNN` - abbreviations of authors' last names

  - 1 author: max first 4 letters of lastname (Li 2015 -> `Li15`; Renzel, 2015 -> `Renz15`)
  - 2 authors: first two letters of both last names (Klamma & Renzel, 2015 -> `KlRe15`)
  - 3 authors: first letters of first two authors' last names, first two letters of last author's last name (Nicolaescu, Toubekis, Klamma, 2015 -> `NTKl15`)
  - 4 authors: first letters of authors' last names (Koren, Nicolaescu, Renzel, Klamma, 2015 -> `KNRK15`)
  - >4 authors: first letters of first three authors' last names, followed by an asterisk (Koren, Nicolaescu, Shahriari, Renzel, Klamma, 2015 -> `KNS*15`)
  - neglect last name prefixes like "de", "van", etc. (van der Aalst, 2014 -> `Aals14`; de Lange, 2014 -> `Lang14`)
  - for multiple last names, use the first one only (Ortiz-Ruiz, 2013 -> `Orti13`)
  - in case of conflicts use alphabetic modifiers (Derntl, Erdtmann, Renzel, Nicolaescu, 2014 -> `DERN14`, Derntl 2014 -> `Dern2014`, conflict since BibTeX is not case sensitive, `DERN14`, `Dern14b`)(conflict resolution NYI...) 

## Usage
Open a console window and use the following command to transform a given BibTeX file in.bib into a curated BibTeX file out.bib:
```console
java -jar i5BibTexConverter.jar -m regeneratekeys -i /path/to/in.bib -o /path/to/out.bib
```

Quality issues per BibTeX entry are reported on stdout, including suggestions for improvement. These issues should be processed in the generated BibTeX file. 

To copy PDFs with a a name starting in the Format "[BibT15]" where "BibT15" is the BibTeX key from one folder to another, if there is a matching BibTeX key
 in the BibTeX file, run the following command:
```console
java -jar i5BibTexConverter.jar -m matchpdfs -i /path/to/in.bib -pi /path/to/input/pdfs -po /path/to/output/pdfs
```

To include fulltexts in the output.sql, use the method `readfulltexts` and supply the textfile input path as in the following command:
```console
java -jar i5BibTexConverter.jar -m readfulltexts -i /path/to/in.bib -fi /path/to/input/textfiles
```

##Build
First of all you can easily open the source in intellij, set up things there and just edit the source.
But there is also a way to build to jar file using ant script, just need to do 2 steps:

 1. Set up your maven repository in the bibtex-keygenerator.properties file
 2. Build using `ant all`

##Known problems:
If an author's name has a special character (like {\"O}zyurt, {\"O}zcan) which would be part of the generated key, the generation won't be correct without any notice. In this case in your generated bibtex key you will find # signs.
