# BibTeX-KeyGenerator

The ACIS group at Chair of Computer Science 5 (Information Systems & Databases) uses a standardized scheme for BibTeX keys as part of its research literature management. BibTeX-KeyGenerator transforms a given BibTeX file with BibTeX keys incompliant to this scheme to a new BibTeX file with all keys compliant to the ACIS scheme.

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

## Usage
Open a console window and user the following command to generate correct bibtex keys into the out.txt file
```console
java -jar i5BibTexConverter.jar -i "C:\PathToYourReferences\papers.bib" -o .\out.txt
```

##Build
First of all you can easily open the source in intellij and set up things there and just edit the source.
But there is also a way to build to jar file using ant script, just need to do 2 steps:

 1. Set up your maven repository in the bibtex-keygenerator.properties file
 2. Build using `ant all`

##Known problems:
If an author's name has a special character (like {\"O}zyurt, {\"O}zcan) which would be part of the generated key, the generation won't be correct without any notice. In this case in your generated bibtex key you will find # signs.
