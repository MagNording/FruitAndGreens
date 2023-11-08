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

        // Kontrollera om det är en "Köp två betala för en"-kampanj och att kampanjvillkoren inte är null
        if (product.isPromotionActive() && product.getPromotionTerms() != null &&
                product.getPromotionTerms().equalsIgnoreCase("Köp två betala för en")) {
            int itemsToChargeFor = (int)quantity / 2 + (int)quantity % 2;
            price = price * itemsToChargeFor;
        } else if (product.isWeightPrice() && quantity > 2) {
            // Beräkna priset för "över 2 kg - 20% rabatt"-kampanjen
            price = price * quantity * 0.8; // Ger 20% rabatt
        } else {
            // Inga kampanjer, beräkna standardpriset
            price = price * quantity;
        }

        return price;
    }

    @Override
    public String toString() {
        String unit = product.isWeightPrice() ? "kg" : "st";
        String priceInfo = String.format("Enhetpris: %.2f kr", product.getPrice());
        String promotionInfo = "";

        if (product.isPromotionActive()) {
            promotionInfo = String.format(" (Kampanj! %s - Kampanjpris: %.2f kr %s)",
                    product.getPromotionTerms(),
                    product.getPromotionPrice(), unit);
            // Här antar vi att totalPrice har justerats för att reflektera kampanjpriset,
            // så vi behöver inte justera det här igen
        }

        return String.format("Produkt: %-10s Kvantitet: %5.2f %s %s Summa: %.2f kr%s",
                product.getName(), quantity, unit, priceInfo, totalPrice, promotionInfo);
    }



}
