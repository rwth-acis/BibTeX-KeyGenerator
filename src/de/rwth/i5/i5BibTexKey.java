package de.rwth.i5;

import org.jbibtex.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Adam Gavronek <gavronek@dbis.rwth-aachen.de>
 * @since 4/1/2015
 */
public class i5BibTexKey {
    public static String generate(BibTeXEntry bibTeXEntry) throws IllegalArgumentException{
        if (bibTeXEntry.getField(BibTeXEntry.KEY_AUTHOR) == null)
            throw new IllegalArgumentException("No author field was given, cannot generate code!");

        String authorsString = ((StringValue)bibTeXEntry.getField(BibTeXEntry.KEY_AUTHOR)).getString();
        String year = ((StringValue)bibTeXEntry.getField(BibTeXEntry.KEY_YEAR)).getString();
        Map<String, String> escapedAuthorNames = getNamePartsToEscape(authorsString);
        String escapedAuthor = escape(authorsString,escapedAuthorNames);
        List<Author> authors = splitToAuthors(escapedAuthor);
        resolveNameParts(authors,escapedAuthorNames);
        String generatedKey = generate(authors, year);
        return generatedKey;
    }

    private static String generate(List<Author> authors, String year) {

        int numberOfAuthors = authors.size();
        StringBuilder bibtexKeyBuilder = new StringBuilder();
        switch (numberOfAuthors) {
            case 1:
                bibtexKeyBuilder.append(authors.get(0).getKeyPart(4));
                break;
            case 2:
                for (Author author : authors) {
                    bibtexKeyBuilder.append(author.getKeyPart(2));
                }
                break;
            case 3:
                for (int i = 0; i < authors.size(); i++) {
                    Author author = authors.get(i);
                    if (i == 2)
                        bibtexKeyBuilder.append(author.getKeyPart(2));
                    else
                        bibtexKeyBuilder.append(author.getKeyPart(1));
                }
                break;
            default:
                for (int i = 0; i < 3; i++) {
                    Author author = authors.get(i);
                    bibtexKeyBuilder.append(author.getKeyPart(1));
                }
                bibtexKeyBuilder.append("*");
                break;
        }

        String yearCode = year.substring(year.length() - 2);
        bibtexKeyBuilder.append(yearCode);
        return bibtexKeyBuilder.toString();
    }

    private static void resolveNameParts(List<Author> authors, Map<String, String> escapedAuthorNames) {
        for (Author author : authors) {
            if (escapedAuthorNames.containsKey(author.getFamilyName()))
                author.setFamilyName(escapedAuthorNames.get(author.getFamilyName()));

            if (escapedAuthorNames.containsKey(author.getGivenName()))
                author.setGivenName(escapedAuthorNames.get(author.getGivenName()));
        }

    }

    private static List<Author> splitToAuthors(String escapedAuthor) {
        List<Author> authorList = new ArrayList<Author>();
        for (String authorString : escapedAuthor.split(" and")) {
            authorString = authorString.trim();
            Author author = new Author();
            if (authorString.contains(",")) {
                String[] authorNameParts = authorString.split(",");
                author.setFamilyName(authorNameParts[0].trim());
                author.setGivenName(authorNameParts[1].trim());
            } else {
                String[] authorNameParts = authorString.split(" ");
                author.setGivenName(authorNameParts[0].trim());
                author.setFamilyName(authorNameParts[1].trim());
            }
            authorList.add(author);
        }
        return authorList;
    }

    private static Map<String, String> getNamePartsToEscape(String authors) {
        Map<String,String> escapedAuthorNames = new HashMap<String, String>();
        boolean hasEscapeStarted = false;
        StringBuilder builder = new StringBuilder();
        int numberOfEscapedAuthorName = 1;
        for (char c : authors.toCharArray()){
            if (c == '{') {
                hasEscapeStarted = true;
                continue;
            }
            if (c == '}') {
                String escapeKey = "#" + numberOfEscapedAuthorName + "#";
                escapedAuthorNames.put(escapeKey,builder.toString());
                builder = new StringBuilder();
                numberOfEscapedAuthorName++;
                hasEscapeStarted = false;
                continue;
            }
            if (hasEscapeStarted)
                builder.append(c);
        }
        return escapedAuthorNames;
    }

    private static String escape(String authors, Map<String, String> escapedAuthorNames) {
        String escapedAuthorString = authors;
        for (Map.Entry<String, String> escapedAuthors : escapedAuthorNames.entrySet()) {
            escapedAuthorString = escapedAuthorString.replace("{" + escapedAuthors.getValue() + "}",escapedAuthors.getKey());
        }
        return escapedAuthorString;
    }

    private static class Author {
        private String givenName;
        private String familyName;

        public String getFamilyName() {
            return familyName;
        }

        public void setFamilyName(String familyName) {
            this.familyName = familyName;
        }

        public String getGivenName() {
            return givenName;
        }

        public void setGivenName(String givenName) {
            this.givenName = givenName;
        }

        public String getKeyPart(int numberOfChars) {
            return getFamilyName().substring(0, numberOfChars);
        }
    }
}
