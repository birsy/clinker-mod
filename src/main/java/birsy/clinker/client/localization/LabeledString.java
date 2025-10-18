package birsy.clinker.client.localization;

import com.google.common.collect.ImmutableList;

public record LabeledString(String rawText, String text, ImmutableList<Label> labels) {

    public static LabeledString parse(String text) {
        StringBuilder visible = new StringBuilder();
        ImmutableList.Builder<Label> labels = ImmutableList.builder();

        int visibleIndex = 0; // position in visible text
        int i = 0;
        boolean escaped = false;
        while (i < text.length()) {
            char character = text.charAt(i);

            // special character handling
            if (!escaped) {
                // escape character '\'
                if (character == '\\') {
                    escaped = true;
                    i++;
                    continue;

                // tag start character '<'
                } else if (character == '<') {
                    // find the end of the tag
                    int end = text.indexOf('>', i);

                    // malformed tags will skip this check, and be treated as literals.
                    if (end != -1) {
                        String tagName = text.substring(i + 1, end).trim();
                        // store label
                        labels.add(new Label(tagName, visibleIndex));

                        i = end + 1; // skip past closing '>'
                        continue;
                    }
                }
            }

            // literal character handling
            escaped = false;
            visible.append(character);
            visibleIndex++;
            i++;
        }

        return new LabeledString(text, visible.toString(), labels.build());
    }

    public record Label(String identifier, int index) {}
}
