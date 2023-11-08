public class CartItem {
    private Product product;
    private double quantity;
    private double totalPrice;

    public CartItem(Product product, double quantity) {
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = calculatePrice(product, quantity);
    }

    public Product getProduct() {
        return product;
    }
    public double getQuantity() {
        return quantity;
    }
    public double getTotalPrice() {
        return totalPrice;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = roundToNearestHalf(totalPrice);
    }
    public static double roundToNearestHalf(double value) {
        return Math.round(value * 2) / 2.0;
    }
    static double calculatePrice(Product product, double quantity) {
        double price = product.getPrice();
        double totalPrice = 0.0;

        if (product.isPromotionActive()) {
            if (product.getPromotionPrice() > 0) {
                price = product.getPromotionPrice();  // Använd kampanjpriset
            }

            if (product.isBuyTwoGetOne()) {
                // Betala fullt pris för varje par av produkten och lägg till en extra utan kostnad om det är en udda kvantitet
                int pairs = (int) quantity / 2;
                totalPrice = (pairs * 2 + (quantity % 2)) * price;
            } else if (product.isWeightPrice() && quantity > 2) {
                // För viktpriser över 2 kg, applicera en 20% rabatt
                totalPrice = price * quantity * 0.8;
            } else {
                totalPrice = price * quantity; // Standardpris
            }
        } else {
            totalPrice = price * quantity; // Inga kampanjer, beräkna standardpriset
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
