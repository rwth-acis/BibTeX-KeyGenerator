# BibTeX-KeyGenerator
Use this way from a command prompt in the jar:
java -jar i5BibTexConverter.jar -i "C:\PathToYourReferences\papers.bib" -o .\out.txt

Known problems:
If an author's name has a special character (like {\"O}zyurt, {\"O}zcan) which would be part of the generated key, the generation won't be correct without any notice. In this case in your generated bibtex key you will find # signs.
