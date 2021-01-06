package query;

import algorithm.JoinOperation;
import response.UserPhoneCartAndProductRelationResponse;
import response.UserPhoneCartResponse;
import response.UserPhoneResponse;
import table.*;

import java.util.ArrayList;
import java.util.List;

public class ForthQuery extends AbstractQuery {
    public ForthQuery(JoinOperation joinOperation) {
        super(joinOperation);
    }

    /**
     * 查询手机号123开头的用户购物车中的物品ID
     * @return
     */
    public List<UserPhoneCartAndProductRelationResponse> query() {
        Table<User> userTable = new Table<>("user", User.class);
        Table<Phone> phoneTable = new Table<>("phone", Phone.class);
        Table<ShoppingCart> cartTable = new Table<>("cart", ShoppingCart.class);
        Table<ProductInShoppingCart> productRelation = new Table<>("cart_item", ProductInShoppingCart.class);

        userTable.startRead();
        phoneTable.startRead();
        cartTable.startRead();
        productRelation.startRead();

        List<User> users = userTable.readRow();
        List<Phone> phones = phoneTable.readRow();
        List<ShoppingCart> carts = cartTable.readRow();
        List<ProductInShoppingCart> relations = productRelation.readRow();

        userTable.endRead();
        phoneTable.endRead();
        cartTable.endRead();
        productRelation.endRead();

        List<UserPhoneCartAndProductRelationResponse> joins = this.joinOperation.join(
                this.joinOperation.join(
                        this.joinOperation.join(users, phones, "userId", "userId",
                                UserPhoneResponse.class, User.class, Phone.class),
                        carts, "userId", "userId",
                        UserPhoneCartResponse.class, UserPhoneResponse.class, ShoppingCart.class
                ),
                relations, "cartId", "cartId",
                UserPhoneCartAndProductRelationResponse.class, UserPhoneCartResponse.class, ProductInShoppingCart.class
        );

        List<UserPhoneCartAndProductRelationResponse> result = new ArrayList<>();
        for (UserPhoneCartAndProductRelationResponse join : joins) {
            if (join.getPhoneNumber().startsWith("123")) {
                result.add(join);
            }
        }

        return result;
    }
}
