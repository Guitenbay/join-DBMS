package table;

public class ProductInShoppingCart {
    private String shoppingCartId;
    private String productId;
    private int number;

    public ProductInShoppingCart() {
    }

    public ProductInShoppingCart(String shoppingCartId, String productId, int number) {
        this.shoppingCartId = shoppingCartId;
        this.productId = productId;
        this.number = number;
    }

    public String getShoppingCartId() {
        return shoppingCartId;
    }

    public void setShoppingCartId(String shoppingCartId) {
        this.shoppingCartId = shoppingCartId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
