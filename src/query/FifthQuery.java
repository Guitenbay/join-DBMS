package query;

import algorithm.JoinOperation;
import response.UserCartAndProductRelationResponse;
import response.UserCartResponse;
import table.*;

import java.util.ArrayList;
import java.util.List;

public class FifthQuery extends AbstractQuery {
    public FifthQuery(JoinOperation joinOperation) {
        super(joinOperation);
    }

    /**
     * 查询浙江省购买物品ID1234的用户ID
     * @return
     */
    public List<UserCartAndProductRelationResponse> query() {
        Table<User> userTable = new Table<>("user", User.class);
        Table<ShoppingCart> cartTable = new Table<>("cart", ShoppingCart.class);
        Table<ProductInShoppingCart> productRelation = new Table<>("cart_item", ProductInShoppingCart.class);

        userTable.startRead();
        cartTable.startRead();
        productRelation.startRead();

        List<User> users = userTable.readRow();
        List<ShoppingCart> carts = cartTable.readRow();
        List<ProductInShoppingCart> relations = productRelation.readRow();

        userTable.endRead();
        cartTable.endRead();
        productRelation.endRead();

        List<UserCartAndProductRelationResponse> joins = this.joinOperation.join(
                this.joinOperation.join(users, carts, "userId", "userId",
                        UserCartResponse.class, User.class, ShoppingCart.class),
                relations, "cartId", "cartId",
                UserCartAndProductRelationResponse.class, UserCartResponse.class, ProductInShoppingCart.class
        );

        List<UserCartAndProductRelationResponse> result = new ArrayList<>();
        for (UserCartAndProductRelationResponse join : joins) {
            if (join.getProvince().equals("浙") && join.getProductId().equals("1234")) {
                result.add(join);
            }
        }

        return result;
    }
}
