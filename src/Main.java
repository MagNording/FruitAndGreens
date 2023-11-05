// Magnus Nording, magnus.nording@iths.se
import utils.UserInput;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static Scanner input = new Scanner(System.in);
    public static ArrayList<Product> allProducts = new ArrayList<>(); // ArrayList allProducts
    public static ArrayList<Product> shoppingCart = new ArrayList<>();
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
        } else {
            boolean returnToMenu = false;
            do {
                System.out.println("Ange sökterm: ");
                String searchTerm = UserInput.readString();
                boolean productFound = false;

                for (Product product : allProducts) {
                    if (productMatchesSearchTerm(product, searchTerm)) {
                        System.out.println(product);
                        productFound = true;
                        break;
                    }
                }
                if (productFound) {
                    returnToMenu = true;
                }
                else {
                    System.out.println("Produkten hittades inte.");
                    System.out.println("Välj en åtgärd:");
                    System.out.println("1. Försök igen");
                    System.out.println("2. Tillbaka till menyn");
                    int choice = UserInput.readInt();
                    if (choice == 2) {
                        returnToMenu = true;
                    }
                }
            } while (!returnToMenu);
        }
    }

    public static boolean productMatchesSearchTerm(Product product, String searchTerm) {
        if (product.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
            return true;
        } else {
            String[] productGroup = product.getProductGroup();
            if (productGroup != null) {
                for (String group : productGroup) {
                    if (group.toLowerCase().contains(searchTerm.toLowerCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }



    // Ta bort fr varukorgen // måste byggas?
    public static void removeFromShoppingcart() {}

    // 4. Visa Varukorg och kunna ta bort en vara // måste byggas
    public static void displayShoppingCart() {
        if (!shoppingCart.isEmpty()) {
            for (Product product : shoppingCart) {
                System.out.println(product);
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
        double priceInput = getProductPrice();
        String[] categoryArray = getProductCategories();
        boolean isWeightPrice = getProductPriceType();

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
            if (productToCheck.isWeightPrice()) {
                weightPrice(productToCheck);
            } else {
                unitPrice(productToCheck);
            }
            // Vill användaren lägga till produkten i varukorgen?
            System.out.println("Vill du lägga till denna produkt i din varukorg? (j/n): ");
            String answer = UserInput.readString();
            if (answer.equalsIgnoreCase("j")) {
                shoppingCart.add(productToCheck);
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
    // Skapa metod för discount "köp två betala för en"
    // Skapa metod för discount "över 2 kg lägre kilopris"



}
