// Magnus Nording, magnus.nording@iths.se
import utils.UserInput;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static ArrayList<Product> allProducts = new ArrayList<>(); // ArrayList allProducts
    public static List<CartItem> shoppingCart = new ArrayList<>();
    public static boolean isAdmin = false; // isAdmin
    public static void main(String[] args) {

        boolean exitMenu = false;
        // Presentation layer
        System.out.println("Välkommen till FRUKT OCH GRÖNT"); // Program start
        System.out.println("------------------------------");
        System.out.println("Programmet startas.\n");

        allProducts.add(new Product("Nektarin", 10, new String[]{"STENFRUKT", "FRUKT"}, false, 0.0, "Ingen kampanj"));
        allProducts.add(new Product("Morot", 16.48, new String[]{"ROTFRUKT", "GRÖNSAK"}, true, 0.0, "Ingen kampanj"));
        allProducts.add(new Product("Broccoli", 18.83, new String[]{"KÅL", "GRÖNSAK"}, true, 0.0, "Ingen kampanj"));


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
                case 5 -> { if (isAdmin) { addNewProduct(); } else { printAdminOnly(); } }
                case 6 -> { if (isAdmin) { removeProduct(); } else { printAdminOnly(); } }
                case 7 -> { if (isAdmin) { updateProduct(); } else { printAdminOnly(); } }
                case 8 -> {
                    if (!isAdmin) {
                        isAdmin = adminLogin();
                    } else {
                        System.out.println("Vänligen, välj mellan 0 - 7");
                    }
                }
                default -> {
                    if (isAdmin) {
                        System.out.println("Ogiltigt val, välj mellan 0 - 7.");
                    } else {
                        System.out.println("Ogiltigt val, välj mellan 0 - 8.");
                    }
                }
            }

            System.out.println();
        } while (!exitMenu);

        System.out.println("Tack, programmet avslutas."); // Program End
    }

    private static void printAdminOnly() {
        System.out.println("Endast för ADMIN, försök logga in.");}

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
                "7. (ADMIN) Uppdatera produktinfo."
        };
        for (String choice : menu) {
            System.out.println(choice);
        }
        if (!isAdmin) {
            System.out.println("8. Logga in som Admin.");
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
            System.out.println(new String(new char[75]).replace("\0", "-"));
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

        // Fråga användaren om produkten ska ha en kampanj
        System.out.print("Ska produkten ha en kampanj? (j/n): ");
        boolean isCampaignActive = UserInput.readString().trim().equalsIgnoreCase("j");
        Double promotionPrice = null;
        String promotionTerms = null;

        // Om det finns en kampanj, samla in kampanjinformation
        if (isCampaignActive) {
            System.out.print("Ange kampanjpriset: ");
            promotionPrice = UserInput.readDouble();
            System.out.print("Ange kampanjvillkoren: ");
            promotionTerms = UserInput.readString();
        }

        // Skapa och lägg till den nya produkten i listan med eller utan kampanj
        Product product = new Product(nameInput, priceInput, categoryArray, isWeightPrice, promotionPrice, promotionTerms);
        allProducts.add(product);
        System.out.println("Produkten " + product.getName() + " har lagts till med " + (isCampaignActive ? "kampanj." : "ingen kampanj."));
    }

    // 6. Ta bort en produkt
    public static void removeProduct() {
        System.out.println("Ange produkten du vill ta bort: ");
        String productToRemove = UserInput.readString();
        if (productToRemove.isEmpty()) {
            System.out.println("Ingen produkt angiven. Ingen ändring har gjorts.");
            return;
        }
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
                } else {
                    System.out.println("Ingen produkt har tagits bort.");
                }
                return;
            }
        }
        System.out.println("Ingen matchande produkt hittades.");
    }

    // 7. Uppdatera Produkt
    public static void updateProduct() {
        System.out.print("Ange namnet på produkten du vill uppdatera: ");
        String searchTerm = UserInput.readString().toLowerCase();

        if (!searchByProductName(searchTerm)) {
            System.out.println("Produkten hittades inte.");
            return; // Avsluta om produkten inte hittas
        }
        System.out.print("Välj en produkt från listan att uppdatera: ");
        String productName = UserInput.readString();
        Product productToUpdate = getProductByName(productName);

        if (productToUpdate == null) {
            System.out.println("Produkten hittades inte.");
            return;
        }
        System.out.print("1. Uppdatera namn\n2. Uppdatera pris\n3. Hantera kampanj\n> ");
        int updateChoice = UserInput.readInt();
        switch (updateChoice) {
            case 1 -> updateProductName(productToUpdate);
            case 2 -> updateProductPrice(productToUpdate);
            case 3 -> manageCampaign(productToUpdate);
            default -> System.out.println("Ogiltigt val. Vänligen ange ett nummer mellan 1 och 3.");
        }
    }
    public static Product getProductByName(String name) {
        for (Product product : allProducts) {
            if (product.getName().equalsIgnoreCase(name)) {
                return product;
            }
        }
        return null;
    }

    // Uppdatera produktnamn
    public static void updateProductName(Product product) {
        System.out.print("Ange det nya namnet: ");
        String newName = UserInput.readString();
        // Kontrollera att det nya namnet inte redan finns
        if (getProductByName(newName) == null) {
            product.setName(newName);
            System.out.println("Produktens namn har uppdaterats.");
        } else {
            System.out.println("En produkt med det namnet finns redan.");
        }
    }

    // Uppdatera produktpris
    public static void updateProductPrice(Product product) {
        System.out.print("Ange det nya priset: ");
        double newPrice = UserInput.readDouble();
        product.setPrice(newPrice);
        System.out.println("Produktens pris har uppdaterats.");
    }

    // Läs från txtFil
    public static ArrayList<String> readUserDataFromFile() {
        ArrayList<String> userData = new ArrayList<>();
        try {
            File userFile = new File("users.txt");
            Scanner textFromTheFile = new Scanner(userFile);

            // Läs in användarnamn och lösenord från filen
            while (textFromTheFile.hasNextLine()) {
                userData.add(textFromTheFile.nextLine());
            }
            textFromTheFile.close(); // Stäng filen när du är klar med den
        } catch (FileNotFoundException e) {
            System.out.println("Kunde inte hitta filen tyvärr.");
        }
        return userData;
    }

    // 8. Admin login
    public static boolean adminLogin() {
        ArrayList<String> userDataFromFile = readUserDataFromFile();

        boolean loggedIn = false;
        do {
            System.out.print("Ange användarnamn > ");
            String usernameInput = UserInput.readString();
            System.out.print("Ange lösenord > ");
            String passwordInput = UserInput.readString();
            // Jämför användarnamn och lösenord med de som lästs in från filen
            if (userDataFromFile.size() >= 2 &&
                    usernameInput.equals(userDataFromFile.get(0)) &&
                    passwordInput.equals(userDataFromFile.get(1))) {
                System.out.println("Du är nu inloggad.");
                loggedIn = true;
            } else {
                System.out.println("Fel inloggning, försök igen.");
            }
        } while (!loggedIn);

        return loggedIn;
    }

    // Lägg till en kampanj
    public static void manageCampaign(Product product) {
        System.out.println("1. Aktivera/Deaktivera kampanj\n2. Uppdatera kampanjpris\n3. Uppdatera kampanjvillkor\n> ");
        int campaignChoice = UserInput.readInt();
        switch (campaignChoice) {
            case 1 -> toggleCampaignStatus(product);
            case 2 -> updateCampaignPrice(product);
            case 3 -> updateCampaignTerms(product);
            default -> System.out.println("Ogiltigt val. Ange ett nummer mellan 1 och 3.");
        }
    }

    public static void toggleCampaignStatus(Product product) {
        System.out.print("Vill du aktivera kampanjen? (j/n): ");
        boolean isCampaignActive = UserInput.readString().equalsIgnoreCase("j");
        product.setPromotionActive(isCampaignActive);

        if (isCampaignActive) {
            updateCampaignPrice(product); // Använd redan existerande metod för att sätta pris
        } else {
            product.setPromotionPrice(0); // Nollställ kampanjpriset om kampanjen är inaktiv
        }

        String status = isCampaignActive ? "aktiverad" : "deaktiverad";
        System.out.println("Kampanjen har blivit " + status + ".");
        updateCartForPromotions(); // Uppdaterar varukorgen med det nya kampanjpriset eller dess avsaknad
    }

    public static void updateCampaignPrice(Product product) {
        if (!product.isPromotionActive()) {
            System.out.println("Aktivera kampanjen först.");
            return;
        }
        System.out.print("Ange det nya kampanjpriset: ");
        double newPromotionPrice = UserInput.readDouble();
        product.setPromotionPrice(newPromotionPrice);
        System.out.println("Kampanjpriset har uppdaterats.");
        updateCartForPromotions(); // Uppdaterar varukorgen med det nya kampanjpriset
    }

    public static void updateCampaignTerms(Product product) {
        if (!product.isPromotionActive()) {
            System.out.println("Aktivera kampanjen först.");
            return;
        }
        System.out.print("Ange de nya kampanjvillkoren: ");
        String newPromotionTerms = UserInput.readString();
        product.setPromotionTerms(newPromotionTerms);
        System.out.println("Kampanjvillkoren har uppdaterats.");
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
            int isWeightInput = UserInput.readInt();
            if (isWeightInput == 1 || isWeightInput == 2) {
                return isWeightInput == 1;
            } else {
                System.out.println("Felaktig inmatning, välj 1 eller 2.");
            }
        }
    }

    public static String[] getProductCategories() {
        System.out.print("Ange varugrupp/-er (kommaseparerad lista): ");
        String categoryInput = UserInput.readString().toUpperCase();
        return (categoryInput).split(",");
    }

    public static void updateCartForPromotions() {
        for (CartItem item : shoppingCart) {
            Product product = item.getProduct();
            if (product.isPromotionActive() &&
                    product.getPromotionTerms().equalsIgnoreCase("Köp två betala för en")) {
                // Använd den uppdaterade calculatePrice metoden som nu tar hänsyn till kampanjen
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


    /*public static void applyBuyTwoForOneDiscount(String campaignProductName) {
        for (CartItem item : shoppingCart) {
            // Kontrollera om produkten är kampanjprodukten och om kampanjen är aktiv
            Product product = item.getProduct();
            if (product.getName().equalsIgnoreCase(campaignProductName) && product.isPromotionActive()) {
                // Beräkna priset baserat på "Köp två betala för en" erbjudandet
                double totalPrice = CartItem.calculatePrice(product, item.getQuantity());
                item.setTotalPrice(totalPrice);

                // Notera att vi inte behöver uppdatera kvantiteten i varukorgen
                // eftersom erbjudandet påverkar priset, inte antalet artiklar kunden får.
                // Antalet gratis artiklar är redan inbakat i priset.
            }
        }
    }*/
}
