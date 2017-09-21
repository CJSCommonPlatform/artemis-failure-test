package uk.gov.justice.operations;

public class JsonMessageCreator {

    /**
     * Create a JSON message with the given number of lines.
     *
     * @param numberOfLines the number of lines to append to the JSON message
     * @return the message as a String
     */
    public static String createMessageWith(final long numberOfLines) {
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{\n  \"_metadata\": {\n");

        for (long index = 0L; index < numberOfLines - 1; index++) {
            stringBuilder.append("      \"name")
                    .append(index)
                    .append("\": \"some name\",\n");
        }

        stringBuilder.append("      \"name")
                .append(numberOfLines)
                .append("\": \"some name\"\n")
                .append("  }\n}");

        return stringBuilder.toString();
    }
}
