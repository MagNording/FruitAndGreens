// magnus nording, magnus.nording@iths.se
public class CartItem {
    private Product product;
    private double quantity;
    private double totalPrice;

    public CartItem(Product product, double quantity) {
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = calculatePrice(product, quantity);
    }
    // Getters
    public Product getProduct() {
        return product;
    }
    public double getQuantity() {
        return quantity;
    }
    public double getTotalPrice() {
        return totalPrice;
    }
    // Setters
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = roundToNearestHalf(totalPrice);
    }
    // Avrunda totalpriset
    public static double roundToNearestHalf(double value) {
        return Math.round(value * 2) / 2.0;
    }
    // Kalkyleringsmetoden(Promotion)
    static double calculatePrice(Product product, double quantity) {
        double price = product.getPrice();
        double totalPrice;

        if (product.isPromotionActive()) {
            if (product.isBuyTwoGetOne()) {
                // För varje par, betala för en, om minst ett par
                int pairs = (int) quantity / 2;
                double oddItem = quantity % 2;
                totalPrice = (pairs * price) + (oddItem * price); // Betala för en i varje par plus en udda
            } else if (product.getPromotionPrice() > 0) {
                // Använd kampanjpris om överstiger 0
                totalPrice = product.getPromotionPrice() * quantity;
            } else {
                //  Obs!Andra kampanjer kan komma att läggas till här
                totalPrice = price * quantity;
            }
        } else {
            // ingen kampanj, standard priset
            totalPrice = price * quantity;
        }

        return totalPrice;
    }

    @Override
    public String toString() {
        String unit = product.isWeightPrice() ? "kg" : "st";
        String priceInfo = String.format("Enhetpris: %.2f kr", product.getPrice());
        String promotionInfo = "";

        if (product.isPromotionActive()) {
            if (product.getPromotionPrice() > 0) {
                promotionInfo = String.format(" (Kampanjpris: %.2f kr %s)", product.getPromotionPrice(), unit);
            } else if (product.isBuyTwoGetOne()) {
                promotionInfo = " (Köp två betala för en)";
            }
            // Anta att totalPrice har justerats för att reflektera kampanjen
        }

        return String.format("Produkt: %-10s Kvantitet: %5.2f %s %s Summa: %.2f kr%s",
                product.getName(), quantity, unit, priceInfo, totalPrice, promotionInfo);
    }






}
