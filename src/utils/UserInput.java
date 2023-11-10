package utils;
// magnus nording, magnus.nording@iths.se
import java.util.InputMismatchException;
import java.util.Scanner;

public class UserInput {
    public static Scanner input = new Scanner(System.in);

    public static String readString() {
        String stringValue;
        do {
            stringValue = input.nextLine().trim();
            if (stringValue.isBlank()) {
                System.out.print("Ingen inmatning gjord, försök igen.\n > ");
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
        int intValue = 0;
        while (true) {
            try {
                String inputLine = input.nextLine();
                if (inputLine.isEmpty()) {
                    System.out.println("Ingen inmatning gjord, försök igen.");
                } else {
                    intValue = Integer.parseInt(inputLine);
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Felaktig inmatning, försök igen.");
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

}
