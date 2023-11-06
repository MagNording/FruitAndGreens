// magnus.nording@iths.se
import java.util.Objects;

public class Product {
    private String name;
    private double price;
    private String[] productGroup;
    private boolean isWeightPrice;
    private double promotionPrice;
    private String promotionTerms;
    private int quantity;
    private boolean isPromotionActive; // promotion status


    public Product(String name, double price, String[] productGroup, boolean isWeightPrice,
                   double promotionPrice, String promotionTerms) {
        this.name = name;
        this.price = price;
        this.productGroup = productGroup;
        this.isWeightPrice = isWeightPrice;
        this.promotionPrice = promotionPrice;
        this.promotionTerms = promotionTerms;
    }

    public Product(String name, double price, String[] productGroup, boolean isWeightPrice) {
        this.name = name;
        this.price = price;
        this.productGroup = productGroup;
        this.isWeightPrice = isWeightPrice;
    }

    // villkora om det är isPromotional?
    @Override
    public String toString() {
        String priceType = isWeightPrice ? "Pris/kg" : "Pris/st";

        String productGroupStr = "";
        if (productGroup != null && productGroup.length > 0) {
            productGroupStr = String.join(", ", productGroup);
        }
        String promotionInfo = "";
        if (promotionPrice > 0) {
            promotionInfo = String.format(" Kampanjpris: %.2f", promotionPrice);
            if (promotionTerms != null && !promotionTerms.isEmpty()) {
                promotionInfo += String.format(" Kampanjvillkor: %s", promotionTerms);
            }
        }
        return String.format("Produkt: %-10s %s: %.2f Varugrupp: %-20s%s",
                name, priceType, price, productGroupStr, promotionInfo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(name, product.name);
    }

    public int hashCode() {
        return Objects.hashCode(name);
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isPromotionActive() {
        return this.isPromotionActive;
    }

    public void setPromotionActive(boolean isPromotionActive) {
        this.isPromotionActive = isPromotionActive;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String[] getProductGroup() {
        return productGroup;
    }

    public double getPromotionPrice() {
        return promotionPrice;
    }

    public void setPromotionPrice(double promotionPrice) {
        if (promotionPrice > 0) {
            this.isPromotionActive = true;
        }
        this.promotionPrice = promotionPrice;
    }

    public String getPromotionTerms() {
        return promotionTerms;
    }

    public void setPromotionTerms(String promotionTerms) {
        this.promotionTerms = promotionTerms;
    }

    public void setProductGroup(String[] productGroup) {
        this.productGroup = productGroup;
    }

    public boolean isWeightPrice() {
        return isWeightPrice;
    }

    public void setWeightPrice(boolean weightPrice) {
        isWeightPrice = weightPrice;
    }

}