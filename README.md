# BibTeX-KeyGenerator

The ACIS group at Chair of Computer Science 5 (Information Systems & Databases) uses a standardized scheme for BibTeX keys as part of its research literature management. BibTeX-KeyGenerator transforms a given BibTeX file with BibTeX keys incompliant to this scheme to a new BibTeX file with all keys compliant to the ACIS scheme.

## Usage
Open a console window and user the following command to generate correct bibtex keys into the out.txt file
```console
java -jar i5BibTexConverter.jar -i "C:\PathToYourReferences\papers.bib" -o .\out.txt
```

##Build
First of all you can easily open the source in intellij and set up things there and just edit the source.
But there is also a way to build to jar file using ant script, just need to do 2 steps:

 1. Set up your maven repository in the bibtex-keygenerator.properties file
 2. Build using `ant -f bibtex-keygenerator.xml all`

##Known problems:
If an author's name has a special character (like {\"O}zyurt, {\"O}zcan) which would be part of the generated key, the generation won't be correct without any notice. In this case in your generated bibtex key you will find # signs.
