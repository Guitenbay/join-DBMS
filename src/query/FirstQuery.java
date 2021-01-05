package query;

import algorithm.JoinOperation;
import response.UserCartAndProductRelationResponse;
import response.UserCartAndProductResponse;
import response.UserCartResponse;
import table.*;

import java.util.ArrayList;
import java.util.List;

public class FirstQuery extends AbstractQuery {

    public FirstQuery(JoinOperation joinOperation) {
        super(joinOperation);
    }

    public List<UserCartAndProductResponse> query() {
        Table<User> userTable = new Table<>("user", User.class);
        Table<ShoppingCart> cartTable = new Table<>("cart", ShoppingCart.class);
        Table<ProductInShoppingCart> productRelation = new Table<>("cart_item", ProductInShoppingCart.class);
        Table<Product> productTable = new Table<>("item", Product.class);

        userTable.startRead();
        cartTable.startRead();
        productRelation.startRead();
        productTable.startRead();

        List<User> users = userTable.readRow();
        List<ShoppingCart> carts = cartTable.readRow();
        List<ProductInShoppingCart> relations = productRelation.readRow();
        List<Product> products = productTable.readRow();

        userTable.endRead();
        cartTable.endRead();
        productRelation.endRead();
        productTable.endRead();

        List<UserCartAndProductResponse> joins = this.joinOperation.join(
                this.joinOperation.join(
                        this.joinOperation.join(users, carts, "userId", "userId",
                                UserCartResponse.class, User.class, ShoppingCart.class),
                        relations, "cartId", "cartId",
                        UserCartAndProductRelationResponse.class, UserCartResponse.class, ProductInShoppingCart.class
                ),
                products, "productId", "productId",
                UserCartAndProductResponse.class, UserCartAndProductRelationResponse.class, Product.class
        );

        List<UserCartAndProductResponse> result = new ArrayList<>();
        for (UserCartAndProductResponse join : joins) {
            if (join.getGender().equals("女") && join.getAge() > 35) {
                result.add(join);
            }
        }
        return result;
    }
}
