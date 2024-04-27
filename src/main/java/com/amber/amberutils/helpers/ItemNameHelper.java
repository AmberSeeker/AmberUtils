package com.amber.amberutils.helpers;

public class ItemNameHelper {

    //Callable method to convet snake_case into Title Case
    public static String snakeCaseToTitleCase(String input) {

        // Split the input string by colon (":")
        String[] parts = input.split(":");
        
        // Get the last part of the split (the actual item name)
        String itemName = parts[parts.length - 1];
        
        // Split the item name by underscore
        String[] words = itemName.split("_");

        // Create a StringBuilder to store the result
        StringBuilder result = new StringBuilder();
        
        // Capitalize the first letter of each word and append to result
        for (String word : words) {
            if (!word.isEmpty()) { // Skip empty strings
                // Capitalize the first letter of the word and append to result
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase()) // Lowercase the rest of the word
                      .append(" "); // Add space between words
            }
        }

        // Remove the trailing space and return the result
        return result.toString().trim();
    }
    
}
