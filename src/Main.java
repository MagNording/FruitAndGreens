// Magnus Nording, magnus.nording@iths.se
import utils.UserInput;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static Scanner input = new Scanner(System.in);
    public static ArrayList<Product> allProducts = new ArrayList<>(); // ArrayList allProducts
    private static List<CartItem> shoppingCart = new ArrayList<>();
    public static void main(String[] args) {

        boolean isAdmin = false; // isAdmin
        boolean exitMenu = false;
        // Presentation layer
        System.out.println("Välkommen till FRUKT OCH GRÖNT"); // Program start
        System.out.println("----------------");
        System.out.println("Programmet startas.\n");

        allProducts.add(new Product("Nektarin", 10, new String[]{"STENFRUKT", "FRUKT"}, false,
                7.5, "Två för 15"));
        allProducts.add(new Product("Morot", 16.48, new String[]{"ROTFRUKT", "GRÖNSAK"}, true));
        allProducts.add(new Product("Broccoli", 18.83, new String[]{"KÅL", "GRÖNSAK"}, true));

        do {
            System.out.println(isAdmin ? "Användare: ADMIN" : "Användare: KUND");
            displayMenu();
            System.out.print("Ange ditt menyval: ");
            int menuChoice = UserInput.readInt();

            switch (menuChoice) {
                case 0 -> exitMenu = true;
                case 1 -> displayAllProducts();
                case 2 -> searchProduct();
                case 3 -> addToShoppingCart();
                case 4 -> displayShoppingCart();
                case 5 -> { if (isAdmin) { addNewProduct();
                    } else { printAdminOnly();}
                }
                case 6 -> { if (isAdmin) { removeProduct();
                    } else { printAdminOnly();}
                }
                case 7 -> { if (isAdmin) { updateProduct();
                } else { printAdminOnly();}}
                case 8 -> isAdmin = adminLogin();
            }
            System.out.println();
        } while (!exitMenu);

        System.out.println("Tack, programmet avslutas."); // Program End
    }

    private static void printAdminOnly() {
        System.out.println("\nEndast för ADMIN, försök logga in.");}

        // Visa menyn
    public static void displayMenu() {
        String[] menu = {
                "0. Avsluta programmet.",
                "1. Visa alla varor.",
                "2. Sök vara.",
                "3. Lägg till vara i varukorgen.",
                "4. Visa Varukorg.",
                "5. (ADMIN) Lägg till en produkt.",
                "6. (ADMIM) Ta bort en produkt.",
                "7. (ADMIN) Uppdatera produktinfo.",
                "8. Logga in som Admin."
        };
        for (String choice : menu) {
            System.out.println(choice);
        }
    }

    // 1. Visa alla tillagda produkter
    public static void displayAllProducts() {
        if (!allProducts.isEmpty()) {
            allProducts.sort(Comparator.comparing(Product::getName));
            for (Product product : allProducts) {
                System.out.println(product.toString());
            }
            System.out.println();
        } else {
            System.out.println("Produktlistan är tom.");
        }
    }

    // 2. Söka produkt
    public static void searchProduct() {
        if (allProducts.isEmpty()) {
            System.out.println("Produktlistan är tom.");
            return;
        }
        System.out.println("Vill du söka på:");
        System.out.println("1. Produktnamn");
        System.out.println("2. Varugrupp");
        int searchType = UserInput.readInt();

        System.out.println("Ange sökterm: ");
        String searchTerm = UserInput.readString().toLowerCase();

        boolean productFound = false;

        switch (searchType) {
            // Sökning på produktnamn
            case 1 -> productFound = searchByProductName(searchTerm);
            // Sökning på varugrupp
            case 2 -> productFound = searchByProductGroup(searchTerm);
            default -> {System.out.println("Ogiltigt val, försök igen.");
                return;}
        }
        if (!productFound) {
            System.out.println("Ingen produkt hittades med angiven sökterm.");
        }
    }
    public static boolean searchByProductName(String searchTerm) {
        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(searchTerm)) {
                System.out.println(product);
                return true;
            }
        }
        return false;
    }
    public static boolean searchByProductGroup(String searchTerm) {
        boolean productFound = false; // Flagga för att hålla reda på om någon produkt har hittats

        for (Product product : allProducts) {
            String[] productGroup = product.getProductGroup();
            if (productGroup != null) {
                for (String group : productGroup) {
                    if (group.toLowerCase().contains(searchTerm)) {
                        System.out.println(product);
                        productFound = true; // Sätter flaggan till true om en produkt hittas
                    }
                }
            }
        }
        return productFound;
    }


    // Ta bort fr varukorgen // måste byggas?
    // Ta bort från varukorgen
    public static void removeFromShoppingCart() {
        displayShoppingCart(); // Visa först varukorgen
        System.out.println("Vilken vara vill du ta bort (ange namnet): ");
        String productName = UserInput.readString();

        CartItem toRemove = null;
        for (CartItem item : shoppingCart) {
            if (item.getProduct().getName().equalsIgnoreCase(productName)) {
                toRemove = item;
                break;
            }
        }
        if (toRemove != null) {
            shoppingCart.remove(toRemove);
            System.out.println(productName + " har tagits bort från varukorgen.");
        } else {
            System.out.println("Varan hittades inte i varukorgen.");
        }
    }


    // 4. Visa Varukorg och kunna ta bort en vara // måste byggas
    public static void displayShoppingCart() {
        if (!shoppingCart.isEmpty()) {
            for (CartItem item : shoppingCart) {
                System.out.println(item);
            }
        } else {
            System.out.println("Kundkorgen är tom.");
        }
        // Visa innehållet i varukorgen och totalpriset
        // Loopa igenom shoppingCart och itemPrices för att visa varorna och priserna
        // Beräkna det totala priset baserat på innehållet i varukorgen
    }

    // Admin login
    public static boolean adminLogin() {
        ArrayList<String> userDataFromFile = new ArrayList<>();

        boolean loggedIn = false;
        try {
            File userFile = new File("users.txt");
            Scanner textFromTheFile = new Scanner(userFile);

            // Läs in användarnamn och lösenord från filen
            while (textFromTheFile.hasNextLine()) {
                userDataFromFile.add(textFromTheFile.nextLine());
            }
            textFromTheFile.close(); // Stäng filen när du är klar med den
        } catch (FileNotFoundException e) {
            System.out.println("Kunde inte hitta filen tyvärr.");
        }
        do {
            System.out.println("Ange användarnamn > ");
            String usernameInput = UserInput.readString();
            System.out.println("Ange lösenord > ");
            String passwordInput = UserInput.readString();

            // Jämför användarnamn och lösenord med de som lästs in från filen
            if (usernameInput.equals(userDataFromFile.get(0)) &&
                    passwordInput.equals(userDataFromFile.get(1))) {
                System.out.println("Du är nu inloggad!");
                loggedIn = true;
                return true;
            } else {
                System.out.println("Fel inloggning, försök igen.");
            }
        } while (!loggedIn);

        return false;
    }

    // Lägg till en produkt
    public static void addNewProduct() {
        String nameInput = getProductName();
        // Kontrollera direkt om produktnamnet redan finns i listan
        for (Product existingProduct : allProducts) {
            if (existingProduct.getName().equalsIgnoreCase(nameInput)) {
                System.out.println("En produkt med namnet \"" + nameInput + "\" finns redan i listan.");
                return; // Avslutar metoden tidigt om produkten redan finns
            }
        }
        // Om produktnamnet inte finns, fortsätt
        double priceInput = getProductPrice();
        String[] categoryArray = getProductCategories();
        boolean isWeightPrice = getProductPriceType();
        // Skapa och lägg till den nya produkten i listan
        Product product = new Product(nameInput, priceInput, categoryArray, isWeightPrice);
        allProducts.add(product);
        System.out.println(product.getName() + " har lagts till.");
    }


    // Ta bort en produkt
    public static void removeProduct() {
        System.out.println("Ange produkten du vill ta bort: ");
        String productToRemove = UserInput.readString();
        if (productToRemove.isEmpty()) {
            System.out.println("Ingen produkt angiven. Ingen ändring har gjorts.");
            System.out.println("Välj 2. Ange exakt produktnamn.");
            return;
        }
        boolean removed = false;
        Iterator<Product> iterator = allProducts.iterator();

        while (iterator.hasNext()) {
            Product product = iterator.next();
            String productName = product.getName();

            if (productName.equalsIgnoreCase(productToRemove)) {
                iterator.remove();
                removed = true;
            }
        }
        if (removed) {
            System.out.println(UserInput.capitalize(productToRemove) + " har tagits bort.");
        } else {
            System.out.println("Ingen matchande produkt hittades.");
        }
    }

    // Uppdatera Produkt
    public static void updateProduct() {
        System.out.print("Ange produkten du vill uppdatera: ");
        String productToUpdate = UserInput.readString();

        boolean productFound = false;
        for (Product product : allProducts) {
            if (product.getName().equalsIgnoreCase(productToUpdate)) {
                productFound = true;
                System.out.print("1. Uppdatera namn\n2. Uppdatera pris\n3. Hantera kampanj\n> ");

                int updateChoice = UserInput.readInt();
                switch (updateChoice) {
                    case 1 -> {
                        System.out.print("Ange det nya namnet: ");
                        String newName = UserInput.readString();
                        product.setName(newName);
                        System.out.println("Namnet har uppdaterats.");}
                    case 2 -> {
                        System.out.print("Ange det nya priset: ");
                        double newPrice = UserInput.readDouble();
                        product.setPrice(newPrice);
                        System.out.println("Priset har uppdaterats.");}
                    case 3 -> manageCampaign(product);
                    default -> System.out.println("Ogiltigt val. Vänligen ange ett nummer mellan 1 och 3.");
                }
                break;
            }
        }
        if (!productFound) {
            System.out.println("Produkten hittades inte.");
            System.out.print("Vill du försöka igen (j/n): ");
            String answer = UserInput.readString();
            if (answer.equalsIgnoreCase("j")) {
                updateProduct();
            } else {
                displayMenu();
            }
        }
    }

    // Lägg till en kampanj
    public static void manageCampaign(Product product) {
        System.out.println("1. Aktivera/Deaktivera kampanj\n2. Uppdatera kampanjpris\n3. Uppdatera kampanjvillkor\n> ");
        int campaignChoice = UserInput.readInt();
        switch (campaignChoice) {
            case 1 -> {
                System.out.print("Vill du aktivera kampanjen? (j/n): ");
                boolean isCampaignActive = UserInput.readString().equalsIgnoreCase("j");
                product.setPromotionActive(isCampaignActive);
                System.out.print("Ange det nya kampanjpriset: ");
                double newPromotionPrice = UserInput.readDouble();
                product.setPromotionPrice(newPromotionPrice);
                System.out.println("Kampanjpriset har uppdaterats.");
                String status = isCampaignActive ? "aktiverad" : "deaktiverad";
                System.out.println("Kampanjen har blivit " + status + ".");}
            case 2 -> {
                System.out.print("Ange det nya kampanjpriset: ");
                double newPromotionPrice = UserInput.readDouble();
                product.setPromotionPrice(newPromotionPrice);
                System.out.println("Kampanjpriset har uppdaterats.");}
            case 3 -> {
                System.out.print("Ange de nya kampanjvillkoren: ");
                String newPromotionTerms = UserInput.readString();
                product.setPromotionTerms(newPromotionTerms);
                System.out.println("Kampanjvillkoren har uppdaterats.");}
            default -> System.out.println("Ogiltigt val. Ange ett nummer mellan 1 och 3.");
        }
    }

    // Varukorg?
    public static void addToShoppingCart() {
        Product productToCheck = null;
        System.out.println("Ange sökterm: ");
        String searchTerm = UserInput.readString();

        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                productToCheck = product;
                System.out.println(product);
                break;
            }
        }
        if (productToCheck != null) {
            double quantity = 0;

            // Fråga användaren efter kvantiteten beroende på om vikt- eller styckprisprodukt
            if (productToCheck.isWeightPrice()) {
                System.out.println("Ange vikten du önskar köpa (i kg): ");
                quantity = UserInput.readDouble();
            } else {
                System.out.println("Ange antalet enheter du önskar köpa: ");
                quantity = UserInput.readInt();
            }

            // Fråga om användaren vill lägga till produkten i varukorgen
            System.out.println("Vill du lägga till denna produkt i din varukorg? (j/n): ");
            String answer = UserInput.readString();
            if (answer.equalsIgnoreCase("j")) {
                // Skapa ett nytt CartItem objekt med produkten och kvantiteten
                CartItem newItem = new CartItem(productToCheck, quantity);
                shoppingCart.add(newItem);
                System.out.println(UserInput.capitalize(productToCheck.getName()) + " har lagts till i varukorgen.");
            }
        } else {
            System.out.println("Produkten kunde inte hittas.");
        }
    }



    public static void weightPrice(Product productToCheck) {
        System.out.println("Ange vikten: ");
        double weightInput = UserInput.readDouble();

        // Använd priset från produkten
        double priceInput = productToCheck.getPrice();
        double result = priceInput * weightInput;

        System.out.printf("Priset för %.2f kg: %.2f kr.\n", weightInput, result);
    }


    public static void unitPrice(Product productToCheck) {
        System.out.print("Ange antalet enheter: ");
        int numOfUnits = UserInput.readInt();

        // Använd priset från produkten
        double pricePerUnit = productToCheck.getPrice();
        double result = pricePerUnit * numOfUnits;

        System.out.printf("Priset för %d st: %.2f kr.\n", numOfUnits, result);
    }

    public static String getProductName() {
        String nameInput;
        while (true) {
            System.out.print("Ange produktens namn: ");
            nameInput = UserInput.readString();
            if (!nameInput.isEmpty()) {
                return UserInput.capitalize(nameInput);
            }
            System.out.println("Du måste ange ett namn.");
        }
    }

    public static double getProductPrice() {
        while (true) {
            try {
                System.out.print("Ange pris: ");
                String price = input.nextLine();
                price = price.replace(",", ".");
                return Double.parseDouble(price);
            } catch (NumberFormatException nfe) {
                System.out.println("Felaktig inmatning, försök igen.");
            }
        }
    }

    public static boolean getProductPriceType() {
        while (true) {
            System.out.println("1. Viktpris\n2. Styckpris");
            try {
                int isWeightInput = input.nextInt();
                if (isWeightInput == 1 || isWeightInput == 2) {
                    return isWeightInput == 1;
                } else {
                    System.out.println("Felaktig inmatning, välj 1 eller 2.");
                }
            } catch (InputMismatchException e) {
                input.nextLine();
                System.out.println("Felaktig inmatning, välj 1 eller 2.");
            }
        }
    }

    // Skapa färdiga kategorier och numrera dem??
    public static String[] getProductCategories() {
        System.out.print("Ange varugrupp/-er (kommaseparerad lista): ");
        String categoryInput = input.nextLine().toUpperCase();
        return (categoryInput).split(",");
    }
    public static void applyBuyTwoGetOneFreeDiscount(Product product) {
        // Check if the quantity of the product is eligible for the discount
        // If eligible, adjust the total price accordingly
    }

    public static void applyPercentageDiscount(Product product, double weight) {
        // If the weight exceeds 2 kg, apply 15 % lower price per kilo to the product
        // Adjust the total price based on the new price per kilo
    }




}
