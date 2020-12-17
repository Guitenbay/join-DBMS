package table;

public class Product {
    private String id;
    private int price;
    private String name;

    public Product() {
    }

    public Product(String id, int price, String name) {
        this.id = id;
        this.price = price;
        this.name = name;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
