package table;

public class Product {
    private String productId;
    private int price;
    private String productName;

    public Product() {
    }

    public Product(String productId, int price, String productName) {
        this.productId = productId;
        this.price = price;
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String id) {
        this.productId = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
