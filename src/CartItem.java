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
        double price = product.isPromotionActive() && product.getPromotionPrice() > 0 ?
                product.getPromotionPrice() :
                product.getPrice();
        return product.isWeightPrice() ? price * quantity : price * (int)quantity;
    }

    @Override
    public String toString() {
        // Bestämmer enheten baserat på om priset är per vikt eller styck.
        String unit = product.isWeightPrice() ? "kg" : "st";

        // Lägger till kampanjinformation om en kampanj är aktiv.
        String promotionInfo = product.isPromotionActive() ?
                String.format("(Kampanjpris: %.2f kr)", product.getPromotionPrice()) : "";

        // Formaterar strängen för att inkludera all relevant information.
        return String.format("Produkt: %-10s Kvantitet: %5.2f %s Enhetpris: %.2f kr Summa: %.2f kr %s",
                product.getName(), quantity, unit, product.getPrice(), totalPrice, promotionInfo);
    }


}
