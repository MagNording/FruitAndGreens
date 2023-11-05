package utils;// magnus.nording@iths.se

import java.util.InputMismatchException;
import java.util.Scanner;

public class UserInput {
    public static Scanner input = new Scanner(System.in);

    public static String readString() {
        String stringValue;
        do {
            stringValue = input.nextLine().trim();
            if (stringValue.isBlank()) {
                System.out.println("Felaktig inmatning, fältet får inte vara tomt.");
            }
        } while (stringValue.isBlank());
        return stringValue;
    }

    public static String capitalize(String originalString) {
        if (originalString.isEmpty()) {
            return originalString;
        }
        return originalString.substring(0, 1).toUpperCase() +
                originalString.substring(1);
    }

    public static int readInt() {
        int intValue;
        while (true) {
            try {
                if (input.hasNextInt()) {
                    intValue = input.nextInt();
                    input.nextLine(); // Rensa bufferten
                    break;
                } else {
                    System.out.println("Felaktig inmatning, försök igen.");
                    input.nextLine();
                }
            } catch (InputMismatchException e) {
                System.out.println("Felaktig inmatning, försök igen.");
                input.nextLine();
            }
        }
        return intValue;
    }

    public static double readDouble() {
        double doubleValue;
        while (true) {
            String userInput = input.nextLine();
            userInput = userInput.replace(",", ".");
            try {
                doubleValue = Double.parseDouble(userInput);
                return doubleValue;
            } catch (NumberFormatException nfe) {
                System.out.println("Felaktig inmatning, försök igen.");
            }
        }
    }

    public static boolean readBoolean() {
        return input.nextBoolean();
    }

}
