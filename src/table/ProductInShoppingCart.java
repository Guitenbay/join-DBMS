package table;

public class ProductInShoppingCart {
    private String cartId;
    private String productId;
    private int number;

    public ProductInShoppingCart() {
    }

    public ProductInShoppingCart(String cartId, String productId, int number) {
        this.cartId = cartId;
        this.productId = productId;
        this.number = number;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
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
