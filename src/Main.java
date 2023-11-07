// Magnus Nording, magnus.nording@iths.se
import utils.UserInput;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static Scanner input = new Scanner(System.in);
    public static ArrayList<Product> allProducts = new ArrayList<>(); // ArrayList allProducts
    static List<CartItem> shoppingCart = new ArrayList<>();
    public static void main(String[] args) {

        boolean isAdmin = false; // isAdmin
        boolean exitMenu = false;
        // Presentation layer
        System.out.println("Välkommen till FRUKT OCH GRÖNT"); // Program start
        System.out.println("-----------------------");
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
            System.out.println();

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
                default -> System.out.println("Ogiltigt val, välj mellan 0 - 8.");
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
                "6. (ADMIN) Ta bort en produkt.",
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
        int searchType;
        do {
            System.out.println("Vill du söka på:");
            System.out.println("1. Produktnamn");
            System.out.println("2. Varugrupp");
            searchType = UserInput.readInt();
            if (searchType != 1 && searchType != 2) {
                System.out.println("Ogiltigt val, vänligen ange 1 eller 2.");
            }
        } while (searchType != 1 && searchType != 2);

        System.out.println("Ange sökterm: ");
        String searchTerm = UserInput.readString().toLowerCase();
        boolean productFound = false;
        switch (searchType) {
            // Sökning på produktnamn
            case 1 -> productFound = searchByProductName(searchTerm);
            // Sökning på varugrupp
            case 2 -> productFound = searchByProductGroup(searchTerm);
            // Ingen default-fall behövs då vi redan kontrollerat inmatningen
        }
        if (!productFound) {
            System.out.println("Ingen produkt hittades med angiven sökterm.");
        }
    }

    public static boolean searchByProductName(String searchTerm) {
        boolean productFound = false;
        for (Product product : allProducts) {
            if (product.getName().toLowerCase().contains(searchTerm)) {
                System.out.println(product);
                productFound = true;
            }
        }
        return productFound;
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

    // 3. Lägg till i varukorgen
    public static void addToShoppingCart() {
        System.out.println("Ange sökterm: ");
        String searchTerm = UserInput.readString();
        Product productToCheck = allProducts.stream()
                .filter(product -> product.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                .findFirst()
                .orElse(null);

        if (productToCheck != null) {
            System.out.println(productToCheck);

            // Fråga användaren efter kvantiteten
            System.out.println("Ange " + (productToCheck.isWeightPrice() ? "vikten du önskar köpa (i kg): " : "antalet enheter du önskar köpa: "));
            double quantity = productToCheck.isWeightPrice() ? UserInput.readDouble() : UserInput.readInt();

            // Fråga om användaren vill lägga till produkten i varukorgen
            System.out.println("Vill du lägga till denna produkt i din varukorg? (j/n): ");
            if (UserInput.readString().equalsIgnoreCase("j")) {
                CartItem existingItem = findCartItemByProduct(productToCheck);
                if (existingItem != null) {
                    existingItem.setQuantity(existingItem.getQuantity() + quantity);
                    // Beräkna det nya totalpriset och uppdatera det befintliga CartItem
                    double additionalPrice = CartItem.calculatePrice(productToCheck, quantity);
                    existingItem.setTotalPrice(existingItem.getTotalPrice() + additionalPrice);
                    System.out.println("Kvantiteten för " + productToCheck.getName() + " har uppdaterats i varukorgen.");
                } else {
                    // Lägg till en ny produkt i varukorgen
                    CartItem newItem = new CartItem(productToCheck, quantity);
                    shoppingCart.add(newItem);
                    System.out.println(UserInput.capitalize(productToCheck.getName()) + " har lagts till i varukorgen.");
                }

                // Visa totalpriset för varukorgen
                displayCartTotalPrice();
            }
        } else {
            System.out.println("Produkten kunde inte hittas.");
        }
    }

    // 4. Visa varukorg
    public static void displayShoppingCart() {
        if (!shoppingCart.isEmpty()) {
            for (CartItem item : shoppingCart) {
                System.out.println(item);
            }
            System.out.println(new String(new char[50]).replace("\0", "-"));
            displayCartSummary();
        } else {
            System.out.println("Varukorgen är tom.");
        }
    }

    public static void displayCartSummary() {
        double totalPrice = 0;
        for (CartItem item : shoppingCart) {
            totalPrice += item.getTotalPrice();
        }
        totalPrice = CartItem.roundToNearestHalf(totalPrice);
        System.out.printf("Totalpris: %.2f kr\n", totalPrice);
    }

    // 5. Lägg till en produkt
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
        boolean isWeightPrice = getProductPriceType();
        double priceInput = getProductPrice();
        String[] categoryArray = getProductCategories();

        // Skapa och lägg till den nya produkten i listan
        Product product = new Product(nameInput, priceInput, categoryArray, isWeightPrice);
        allProducts.add(product);
        System.out.println(product.getName() + " har lagts till.");
    }

    // 6. Ta bort en produkt
    public static void removeProduct() {
        System.out.println("Ange produkten du vill ta bort: ");
        String productToRemove = UserInput.readString();
        if (productToRemove.isEmpty()) {
            System.out.println("Ingen produkt angiven. Ingen ändring har gjorts.");
            return;
        }
        boolean isRemoved = false; // Flagga för att hålla reda på om en produkt har tagits bort

        Iterator<Product> iterator = allProducts.iterator();

        while (iterator.hasNext()) {
            Product product = iterator.next();
            String productName = product.getName();
            if (productName.toLowerCase().startsWith(productToRemove.toLowerCase())) {
                System.out.println(product);
                System.out.println("Är du säker på att du vill ta bort produkten? (j/n): ");
                String confirmation = UserInput.readString();
                if (confirmation.equalsIgnoreCase("j")) {
                    iterator.remove();
                    System.out.println(UserInput.capitalize(productName) + " har tagits bort.");
                    isRemoved = true; // Sätt flaggan till true eftersom en produkt har tagits bort
                    break;
                } else {
                    System.out.println("Ingen produkt har tagits bort.");
                    return;
                }
            }
        }
        if (!isRemoved) { // Använd flaggan för att avgöra om detta meddelande ska visas
            System.out.println("Ingen matchande produkt hittades.");
        }
    }

    // 7. Uppdatera Produkt
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

    // 8. Admin login
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
            System.out.print("Ange användarnamn > ");
            String usernameInput = UserInput.readString();
            System.out.print("Ange lösenord > ");
            String passwordInput = UserInput.readString();

            // Jämför användarnamn och lösenord med de som lästs in från filen
            if (usernameInput.equals(userDataFromFile.get(0)) &&
                    passwordInput.equals(userDataFromFile.get(1))) {
                System.out.println("Du är nu inloggad.");
                loggedIn = true;
                return true;
            } else {
                System.out.println("Fel inloggning, försök igen.");
            }
        } while (!loggedIn);

        return false;
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
                if (isCampaignActive) {
                    System.out.print("Ange det nya kampanjpriset: ");
                    double newPromotionPrice = UserInput.readDouble();
                    product.setPromotionPrice(newPromotionPrice);
                    System.out.println("Kampanjpriset har uppdaterats.");
                } else {
                    product.setPromotionPrice(0); // Nollställ kampanjpriset om kampanjen är inaktiv
                }
                String status = isCampaignActive ? "aktiverad" : "deaktiverad";
                System.out.println("Kampanjen har blivit " + status + ".");
                updateCartForPromotions(); // Uppdaterar varukorgen med det nya kampanjpriset
            }
            case 2 -> {
                System.out.print("Ange det nya kampanjpriset: ");
                double newPromotionPrice = UserInput.readDouble();
                product.setPromotionPrice(newPromotionPrice);
                System.out.println("Kampanjpriset har uppdaterats.");
                updateCartForPromotions(); // Uppdaterar varukorgen med det nya kampanjpriset
            }
            case 3 -> {
                System.out.print("Ange de nya kampanjvillkoren: ");
                String newPromotionTerms = UserInput.readString();
                product.setPromotionTerms(newPromotionTerms);
                System.out.println("Kampanjvillkoren har uppdaterats.");
            }
            default -> System.out.println("Ogiltigt val. Ange ett nummer mellan 1 och 3.");
        }
    }

    public static CartItem findCartItemByProduct(Product product) {
        for (CartItem item : shoppingCart) {
            if (item.getProduct().equals(product)) {
                return item;
            }
        }
        return null;
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
        System.out.print("Ange pris: ");
        return UserInput.readDouble();
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
        String categoryInput = UserInput.readString().toUpperCase();
        return (categoryInput).split(",");
    }
    public static void applyBuyTwoGetOneFreeDiscount(Product product) {
        // Kolla om kvantiteten uppnår discount
        // Om så är fallet, justera totala priset
    }

    public static void applyPercentageDiscount(Product product, double weight) {
        // lägg ett 15 % lägre kilopris på produkten
        // Justera totala priset baserat på nya pris/kg el. pris/enhet
    }

    public static void updateCartForPromotions() {
        for (CartItem item : shoppingCart) {
            Product product = item.getProduct();
            if (product.isPromotionActive()) {
                double newPrice = CartItem.calculatePrice(product, item.getQuantity());
                item.setTotalPrice(newPrice);
            }
        }
        displayShoppingCart(); // Visa den uppdaterade varukorgen
    }


    public static void displayCartTotalPrice() {
        double total = 0;
        for (CartItem item : shoppingCart) {
            total += item.getTotalPrice();
        }
        System.out.printf("Totalpris för varukorgen: %.2f kr\n", total);
    }
}
