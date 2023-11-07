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

    static double calculatePrice(Product product, double quantity) {
        double price = product.isPromotionActive() && product.getPromotionPrice() > 0 ?
                product.getPromotionPrice() :
                product.getPrice();
        return product.isWeightPrice() ? price * quantity : price * (int)quantity;
    }

    @Override
    public String toString() {
        String promotionInfo = product.isPromotionActive() ?
                String.format("(Kampanj! Enhet: %.2f kr)", product.getPromotionPrice()) : "";
        return String.format("Produkt: %-10s Kvantitet: %-5.2f Enhetpris: %.2f kr Totalpris: %.2f kr %s",
                product.getName(), quantity, product.getPrice(), totalPrice, promotionInfo);
    }

}
