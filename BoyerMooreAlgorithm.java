/**
 * Implementation of the original Boyer-Moore String Matching Algorithm.
 * This implementation utilizes BOTH the Bad Character Heuristic and 
 * the Good Suffix Heuristic to achieve optimal right-to-left scanning.
 */
public class BoyerMooreAlgorithm {

    // Total number of characters in the standard ASCII alphabet
    private static final int ALPHABET_SIZE = 256;

    // Helper method to return the maximum of two values
    private static int max(int a, int b) {
        return (a > b) ? a : b;
    }

    /* -----------------------------------------------------------------------
     * PREPROCESSING: BAD CHARACTER RULE
     * Creates a table indicating the rightmost position of each character.
     * -----------------------------------------------------------------------*/
    private static void buildBadCharacterTable(char[] pattern, int patternLength, int[] badCharTable) {
        // Initialize all occurrences to -1 (indicating character is not in pattern)
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            badCharTable[i] = -1;
        }
        // Fill in the actual index of the rightmost occurrence of each character
        for (int i = 0; i < patternLength; i++) {
            badCharTable[(int) pattern[i]] = i;
        }
    }

    /* -----------------------------------------------------------------------
     * PREPROCESSING: GOOD SUFFIX RULE
     * Creates a table indicating shift distances based on matched suffixes.
     * -----------------------------------------------------------------------*/
    private static void buildGoodSuffixTable(char[] pattern, int patternLength, int[] goodSuffixTable) {
        int[] borderPosition = new int[patternLength + 1];
        int i = patternLength;
        int j = patternLength + 1;
        borderPosition[i] = j;

        // Case 1: Strong Good Suffix
        while (i > 0) {
            while (j <= patternLength && pattern[i - 1] != pattern[j - 1]) {
                if (goodSuffixTable[j] == 0) {
                    goodSuffixTable[j] = j - i;
                }
                j = borderPosition[j];
            }
            i--;
            j--;
            borderPosition[i] = j;
        }

        // Case 2: Prefix Matching
        j = borderPosition[0];
        for (i = 0; i <= patternLength; i++) {
            if (goodSuffixTable[i] == 0) {
                goodSuffixTable[i] = j;
            }
            if (i == j) {
                j = borderPosition[j];
            }
        }
    }

    /* -----------------------------------------------------------------------
     * MAIN SEARCH FUNCTION
     * -----------------------------------------------------------------------*/
    public static void search(String textStr, String patternStr) {
        char[] text = textStr.toCharArray();
        char[] pattern = patternStr.toCharArray();
        int textLength = text.length;
        int patternLength = pattern.length;

        // Step 1: Initialize and build both heuristic tables
        int[] badCharTable = new int[ALPHABET_SIZE];
        int[] goodSuffixTable = new int[patternLength + 1];

        buildBadCharacterTable(pattern, patternLength, badCharTable);
        buildGoodSuffixTable(pattern, patternLength, goodSuffixTable);

        // Step 2: Begin searching
        int shift = 0; // Represents the current alignment of pattern against text
        boolean found = false;

        System.out.println("Starting search...");

        // Loop until the pattern goes past the end of the text
        while (shift <= (textLength - patternLength)) {
            int j = patternLength - 1; // Start comparing from the rightmost character

            // Keep moving left as long as characters match
            while (j >= 0 && pattern[j] == text[shift + j]) {
                j--;
            }

            // If j drops below 0, every character matched
            if (j < 0) {
                System.out.println("-> SUCCESS: Pattern found at index " + shift);
                found = true;
                
                // Shift forward using the good suffix table to look for overlapping/next matches
                shift += goodSuffixTable[0];
            } 
            else {
                // MISMATCH OCCURRED
                // Calculate proposed shifts from both rules
                int badCharShift = j - badCharTable[text[shift + j]];
                int suffixShift = goodSuffixTable[j + 1];
                int actualShift = max(badCharShift, suffixShift);
                
                // Print statement to help with the report's Output Analysis
                System.out.println("Mismatch at text index " + (shift + j) + 
                                   " ('" + text[shift + j] + "'). " +
                                   "Skipping forward by " + actualShift + " positions.");

                // Always take the maximum safe shift allowed by either rule
                shift += actualShift;
            }
        }

        if (!found) {
            System.out.println("Pattern not found in the text.");
        }
    }

    /* -----------------------------------------------------------------------
     * DRIVER METHOD (Tests the Algorithm)
     * -----------------------------------------------------------------------*/
    public static void main(String[] args) {
        String text = "ABAAABCDABC";
        String pattern = "ABC";

        System.out.println("Text:    " + text);
        System.out.println("Pattern: " + pattern);
        System.out.println("--------------------------------------------------");
        
        search(text, pattern);
    }
}

