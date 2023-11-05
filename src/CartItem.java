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
        this.totalPrice = totalPrice;
    }

    private static double calculatePrice(Product product, double quantity) {
        double price = product.isPromotionActive() && product.getPromotionPrice() > 0 ?
                product.getPromotionPrice() :
                product.getPrice();
        return product.isWeightPrice() ? price * quantity : price * (int)quantity;
    }

    @Override
    public String toString() {
        // Modifiera denna strängrepresentation för att inkludera all den information du vill visa för varje CartItem
        return String.format("Produkt: %s, Kvantitet: %.2f, Totalpris: %.2f kr",
                product.getName(), quantity, totalPrice);
    }
}
