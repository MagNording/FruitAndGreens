// magnus nording, magnus.nording@iths.se
import java.util.Objects;

public class Product {
    private String name;
    private double price;
    private String[] productGroup;
    private boolean isWeightPrice;
    private double promotionPrice;
    private boolean isBuyTwoGetOne;
    private boolean isPromotionActive;

    public Product(String name, double price, String[] productGroup, boolean isWeightPrice) {
        this.name = name;
        this.price = price;
        this.productGroup = productGroup;
        this.isWeightPrice = isWeightPrice;
        // Standardvärden för kampanjattribut
        this.promotionPrice = 0.0;
        this.isBuyTwoGetOne = false;
        this.isPromotionActive = false;
    }

    public Product(String name, double price, String[] productGroup, boolean isWeightPrice,
                   double promotionPrice, boolean isBuyTwoGetOne) {
        this(name, price, productGroup, isWeightPrice); // Anropa den ursprungliga konstruktorn
        // Sätt kampanjattributen
        this.promotionPrice = promotionPrice;
        this.isBuyTwoGetOne = isBuyTwoGetOne;
        this.isPromotionActive = promotionPrice > 0 || isBuyTwoGetOne;
    }

    @Override
    public String toString() {
        String priceType = isWeightPrice ? "Pris/kg" : "Pris/st";
        String productGroupStr = (productGroup != null && productGroup.length > 0) ?
                String.join(", ", productGroup) : "Ingen kategori";

        // Bygg upp strängen för kampanjinformation baserat på vilken kampanj aktiv.
        String promotionInfo = "";
        if (isBuyTwoGetOne) {
            promotionInfo = " Kampanj: Köp två betala för en";
        } else if (isPromotionActive && promotionPrice > 0) {
            promotionInfo = String.format(" Kampanjpris: %.2f kr", promotionPrice);
        }
        String formattedPrice = String.format("%.2f kr", price);
        return String.format("Produkt: %-10s %s: %-10s Varugrupp: %-20s %s",
                name, priceType, formattedPrice, productGroupStr, promotionInfo);


    }

    // Om två `Product` objekt är lika baserat på deras `name`
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(name, product.name);
    }

    // Genererar hashkod för `Product` objekt baserat på `name`
    public int hashCode() {
        return Objects.hashCode(name);
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

    public boolean isWeightPrice() {
        return isWeightPrice;
    }

    public boolean isBuyTwoGetOne() {
        return isBuyTwoGetOne;
    }
    public void setBuyTwoGetOne(boolean buyTwoGetOne) {
        isBuyTwoGetOne = buyTwoGetOne;
        isPromotionActive = buyTwoGetOne || (promotionPrice > 0);
    }
    public boolean isPromotionActive() {
        return this.isPromotionActive;
    }
    public void setPromotionActive(boolean isPromotionActive) {
        this.isPromotionActive = isPromotionActive;
    }
}