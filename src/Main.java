// magnus nording, magnus.nording@iths.se
import utils.UserInput;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static ArrayList<Product> allProducts = new ArrayList<>(); // ArrayList allProducts
    public static List<CartItem> shoppingCart = new ArrayList<>();
    static String GREEN = "\u001B[32m";
    static String PURPLE = "\u001B[95m";
    static String RESET = "\u001B[0m";
    public static boolean isAdmin = false; // isAdmin
    public static void main(String[] args) {

        boolean exitMenu = false;
        // Presentation layer
        System.out.println(PURPLE + "Välkommen till FRUKT OCH GRÖNT"); // Program start
        System.out.println("------------------------------");
        System.out.println("Programmet startas.\n" + RESET);

        theProducts();

        do {
            System.out.println(isAdmin ? GREEN + "Användare: ADMIN" : PURPLE + "Användare: KUND");
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
            System.out.println("PRODUKTLISTAN:");
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
        System.out.print("1. Produktnamn\n2. Varugrupp\n> ");
        int searchType = UserInput.readInt();

        if (searchType != 1 && searchType != 2) {
            System.out.println("Ogiltigt val, vänligen ange 1 eller 2.");
            return; // Återvänder till menyn om ett ogiltigt val görs.
        }
        System.out.print("Ange sökterm: ");
        String searchTerm = UserInput.readString().toLowerCase();
        boolean productFound;

        if (searchType == 1) {
            productFound = searchByProductName(searchTerm);
            if (!productFound) {
                System.out.println("Ingen produkt hittades med angivet produktnamn.");
                System.out.print("Vill du söka efter en varugrupp istället? (j/n): ");
                if (UserInput.readString().trim().equalsIgnoreCase("j")) {
                    System.out.print("Ange sökterm för varugrupp: ");
                    searchTerm = UserInput.readString().toLowerCase();
                    productFound = searchByProductGroup(searchTerm);
                }
            }
        } else {
            productFound = searchByProductGroup(searchTerm);
        }

        if (!productFound) {
            System.out.println("Ingen produkt hittades med angiven sökterm.");
        }
    }
    // Sök på produktnamn
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
    // Produktgruppssök
    public static boolean searchByProductGroup(String searchTerm) {
        boolean productFound = false;
        Set<String> displayedProductNames = new HashSet<>(); // Set för att undvika dubletter
        for (Product product : allProducts) {
            String[] productGroup = product.getProductGroup();
            if (productGroup != null) {
                for (String group : productGroup) {
                    if (group.toLowerCase().contains(searchTerm)) {
                        // Kontrollera om produktnamnet redan har visats
                        if (!displayedProductNames.contains(product.getName())) {
                            System.out.println(product);
                            productFound = true;
                            displayedProductNames.add(product.getName()); // Lägg till produktnamnet i setet
                        }
                    }
                }
            }
        }
        return productFound;
    }
    // 3. Lägg till i varukorgen
    private static void addToShoppingCart() {
        displayProductsToBuy();
        int productIndex = selectProductFromList();

        if (isValidProductIndex(productIndex)) {
            Product selectedProduct = allProducts.get(productIndex);
            double quantity = requestQuantity(selectedProduct);
            if (confirmAddToCart()) {
                addOrUpdateCartItem(selectedProduct, quantity);
                displayCartSummary();
            }
        } else {
            System.out.println("Ogiltigt val, försök igen.");
        }
    }
    // Köplistan
    private static void displayProductsToBuy() {

        for (int i = 0; i < allProducts.size(); i++) {
            Product product = allProducts.get(i);
            String priceFormat = product.isWeightPrice() ? "(%.2f kr/kg)" : "(%.2f kr/st)";
            String priceInfo = String.format(priceFormat, product.getPrice());
            String promotionInfo = "";
            if (product.isBuyTwoGetOne()) {
                promotionInfo = " - Kampanj: Köp två betala för en";
            } else if (product.isPromotionActive() && product.getPromotionPrice() > 0) {
                promotionInfo = String.format(" - Kampanjpris: %.2f kr", product.getPromotionPrice());
            }
            System.out.printf("%d. %-10s %s%s\n", (i + 1), product.getName(), priceInfo, promotionInfo);
        }



    }

    private static int selectProductFromList() {
        System.out.print("\nVälj numret för den produkt du vill lägga till i varukorgen: ");
        return UserInput.readInt() - 1;  // Justerad för indexbaserad användning
    }
    private static boolean isValidProductIndex(int index) {
        return index >= 0 && index < allProducts.size();
    }
    private static double requestQuantity(Product product) {
        // Fråga användaren efter kvantiteten
        System.out.print("Ange " + (product.isWeightPrice() ? "vikten du önskar köpa (i kg): " :
                "antalet enheter du önskar köpa: "));
        return product.isWeightPrice() ? UserInput.readDouble() : UserInput.readInt();
    }
    private static boolean confirmAddToCart() {
        System.out.print("Vill du lägga till denna produkt i din varukorg? (j/n): ");
        return UserInput.readString().equalsIgnoreCase("j");
    }
    private static void addOrUpdateCartItem(Product selectedProduct, double quantity) {
        CartItem existingItem = findCartItemByProduct(selectedProduct);
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            updateCartForPromotions(existingItem);
            System.out.println("Kvantiteten för " + selectedProduct.getName() + " har uppdaterats i varukorgen.");
        } else {
            CartItem newItem = new CartItem(selectedProduct, quantity);
            shoppingCart.add(newItem);
            updateCartForPromotions(newItem);
            System.out.println(UserInput.capitalize(selectedProduct.getName()) + " har lagts till i varukorgen.");
        }
    }

    // 4. Visa varukorg
    public static void displayShoppingCart() {
        System.out.println("VARUKORG:");
        if (!shoppingCart.isEmpty()) {
            for (CartItem item : shoppingCart) {
                System.out.println(item);
            }
            System.out.println(new String(new char[75]).replace("\0", "-"));
            displayCartSummary();
            shoppingCartMenu();
        } else {
            System.out.println("Varukorgen är tom.");
        }
    }
    // Varukorgsmeny
    public static void shoppingCartMenu() {
        int menuChoice;
        do {
            System.out.print("""
                      
            1. Ta bort vara från varukorgen.
            2. Töm varukorgen.
            3. Visa varukorgen.
            4. Fortsätt handla.
            > \s""");
            menuChoice = UserInput.readInt();
            System.out.println();

            switch(menuChoice){
                case 1 -> removeItem(shoppingCart);
                case 2 -> emptyCart();
                case 3 -> {
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
                case 4 -> {return;}
                default -> System.out.println("Ogiltigt val, försök igen.");
            }
        } while (menuChoice != 4);
    }
    // Ta bort vara
    private static void removeItem(List<CartItem> shoppingCart) {
        if (shoppingCart.isEmpty()) {
            System.out.println("Varukorgen är redan tom.");
            return;
        }
        System.out.println("Vilken vara vill du ta bort?");
        for (int i = 0; i < shoppingCart.size(); i++) {
            CartItem item = shoppingCart.get(i);
            System.out.printf("%d: %s\n", i + 1, item.getProduct().getName());
        }
        System.out.print("Ange numret på varan du vill ta bort: ");
        int itemNumber = UserInput.readInt();
        // Kontrollera att numret är giltigt
        if (itemNumber < 1 || itemNumber > shoppingCart.size()) {
            System.out.println("Ogiltigt nummer, försök igen.");
            return;
        }
        // Ta bort varan från varukorgen
        shoppingCart.remove(itemNumber - 1);
        System.out.println("Varan har tagits bort från varukorgen.");
    }
    // Töm varukorg
    public static void emptyCart() {
        if (shoppingCart.isEmpty()){
            System.out.println("Varukorgen är redan tom.");
            return;
        }
        shoppingCart = new ArrayList<>();
        System.out.println("Varukorgen är nu tom.");
    }

    public static CartItem findCartItemByProduct(Product product) {
        for (CartItem item : shoppingCart) {
            if (item.getProduct().equals(product)) {
                return item;
            }
        }
        return null;
    }
    // Visa totalpris
    public static void displayCartSummary() {
        double totalPrice = 0;
        for (CartItem item : shoppingCart) {
            totalPrice += item.getTotalPrice();
        }
        totalPrice = CartItem.roundToNearestHalf(totalPrice);
        System.out.printf("Totalpris: %.2f kr\n", totalPrice);
    }

    public static void updateCartForPromotions() {
        for (CartItem item : shoppingCart) {
            Product product = item.getProduct();
            if (product.isPromotionActive()) {
                // Uppdatera priset baserat på om produkten har en aktuell kampanj
                double newPrice = CartItem.calculatePrice(product, item.getQuantity());
                item.setTotalPrice(newPrice);
            }
        }
    }

    public static void updateCartForPromotions(CartItem item) {
        Product product = item.getProduct();
        if (product.isPromotionActive()) {
            double newPrice = CartItem.calculatePrice(product, item.getQuantity());
            item.setTotalPrice(newPrice);
        }
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

        boolean isBuyTwoGetOne = false;
        if (!isWeightPrice) {
            System.out.print("Ska produkten ha en 'Köp två betala för en'-kampanj? (j/n): ");
            isBuyTwoGetOne = UserInput.readString().trim().equalsIgnoreCase("j");
        }
        double promotionPrice = 0.0;  // Sätt ett standardvärde för promotionPrice

        // Skapa och lägg till den nya produkten i listan
        Product product = new Product(nameInput, priceInput, categoryArray, isWeightPrice, promotionPrice, isBuyTwoGetOne);
        allProducts.add(product);
        String confirmationMessage = "Produkten " + nameInput + " har lagts till";
        if (!isWeightPrice && isBuyTwoGetOne) {
            confirmationMessage += " med 'Köp två betala för en'-kampanj.";
        }
        System.out.println(confirmationMessage + ".");
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
        String productName = UserInput.readString();
        Product productToUpdate = getProductByName(productName);

        if (productToUpdate == null) {
            System.out.println("Produkten hittades inte.");
            return; // Avslutar metoden tidigt om produkten inte finns
        }
        // Visa produktinformation till användaren
        System.out.println(productToUpdate);

        // Menyn för vad användaren kan uppdatera
        System.out.print("Vad vill du uppdatera?\n1. Namn\n2. Pris\n");
        if (!productToUpdate.isWeightPrice()) {
            System.out.print("3. Hantera kampanj\n");
        }
        System.out.print("> ");
        int updateChoice = UserInput.readInt();

        switch (updateChoice) {
            case 1 -> updateProductName(productToUpdate);
            case 2 -> updateProductPrice(productToUpdate);
            case 3 -> {
                if (!productToUpdate.isWeightPrice()) {
                    manageCampaign(productToUpdate);
                } else {
                    System.out.println("Kampanjer kan inte appliceras på produkter som säljs per vikt.");
                }
            }
            default -> System.out.println("Ogiltigt val. Försök igen.");
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
        System.out.print("1. Aktivera/Deaktivera kampanj\n2. Uppdatera kampanjpris\n> ");
        int campaignChoice = UserInput.readInt();
        switch (campaignChoice) {
            case 1 -> toggleCampaignStatus(product);
            case 2 -> updateCampaignPrice(product);
            default -> System.out.println("Ogiltigt val. Ange antingen 1. eller 2.");
        }
    }
    // Kampanj av eller på
    public static void toggleCampaignStatus(Product product) {
        // Om kampanjen redan är aktiv, ge alternativet att deaktivera den
        if (product.isBuyTwoGetOne()) {
            System.out.print("Produkten har redan 'Köp två betala för en'-kampanjen aktiv. Vill du deaktivera den? (j/n): ");
            boolean deactivate = UserInput.readString().equalsIgnoreCase("j");
            if (deactivate) {
                product.setBuyTwoGetOne(false);
                System.out.println("'Köp två betala för en'-kampanjen har blivit deaktiverad.");
            } else {
                System.out.println("'Köp två betala för en'-kampanjen är fortfarande aktiv.");
            }
        } else {
            // Om kampanjen inte är aktiv, ge alternativet att aktivera den
            System.out.print("Produkten har ingen aktiv 'Köp två betala för en'-kampanj. Vill du aktivera den? (j/n): ");
            boolean activate = UserInput.readString().equalsIgnoreCase("j");
            if (activate) {
                product.setBuyTwoGetOne(true);
                System.out.println("'Köp två betala för en'-kampanjen har blivit aktiverad.");
            } else {
                System.out.println("Ingen kampanj har aktiverats.");
            }
        }
        // Uppdaterar varukorg oavsett om aktiverad eller deaktiverad
        updateCartForPromotions();
    }
    // Uppdatera kampanjpris
    public static void updateCampaignPrice(Product product) {
        // Kontrollera om det finns en "Köp två betala för en"-kampanj aktiv
        if (product.isBuyTwoGetOne()) {
            System.out.println("Produkten har en 'Köp två betala för en'-kampanj aktiv. Kampanjpriset kan inte uppdateras.");
        } else {
            // uppdatera kampanjpriset
            System.out.print("Ange det nya kampanjpriset: ");
            double newPromotionPrice = UserInput.readDouble();
            product.setPromotionPrice(newPromotionPrice);
            System.out.println("Kampanjpriset har uppdaterats.");
        }
        // Uppdaterar varukorgen med det nytt kampanjpris
        updateCartForPromotions();
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
    // Produktlistan
    public static void theProducts() {
        allProducts.add(new Product("Nektarin", 10, new String[]{"FRUKT", "STENFRUKT"}, false,
                0.0, true));

        allProducts.add(new Product("Apelsin", 5, new String[]{"FRUKT", "CITRUSVÄXT"}, false,
                0.0, true));

        allProducts.add(new Product("Kiwi", 5, new String[]{"FRUKT"}, false,
                0.0, false));

        allProducts.add(new Product("Morot", 16.48, new String[]{"GRÖNSAK", "ROTFRUKT"}, true));
        allProducts.add(new Product("Broccoli", 18.83, new String[]{"GRÖNSAK", "KÅL"}, true));
    }

}
