package query;

import algorithm.JoinOperation;
import response.UserCartAndProductRelationResponse;
import response.UserCartResponse;
import table.*;

import java.util.ArrayList;
import java.util.List;

public class FifthQuery extends AbstractQuery implements Queryable {
    public FifthQuery(JoinOperation joinOperation) {
        super(joinOperation);
    }

    /**
     * 查询浙江省购买物品ID1234的用户ID
     * @return
     */
    @Override
    public List<UserCartAndProductRelationResponse> query(String method){
        Table<User> userTable = new Table<>("user", User.class);
        Table<ShoppingCart> cartTable = new Table<>("cart", ShoppingCart.class);
        Table<ProductInShoppingCart> productRelation = new Table<>("cart_item", ProductInShoppingCart.class);

        List<UserCartAndProductRelationResponse> joins;
        if (method.contains("memory")){
            userTable.startRead();
            cartTable.startRead();
            productRelation.startRead();

            List<User> users = userTable.readRow();
            List<ShoppingCart> carts = cartTable.readRow();
            List<ProductInShoppingCart> relations = productRelation.readRow();

            userTable.endRead();
            cartTable.endRead();
            productRelation.endRead();

            joins = this.joinOperation.joinForMemory(
                    this.joinOperation.joinForMemory(users, carts, "userId", "userId",
                            UserCartResponse.class, User.class, ShoppingCart.class),
                    relations, "cartId", "cartId",
                    UserCartAndProductRelationResponse.class, UserCartResponse.class, ProductInShoppingCart.class
            );
        } else {
            joins = this.joinOperation.join(
                    this.joinOperation.join(userTable, cartTable, "userId", "userId",
                            UserCartResponse.class, User.class, ShoppingCart.class),
                    productRelation, "cartId", "cartId",
                    UserCartAndProductRelationResponse.class, UserCartResponse.class, ProductInShoppingCart.class
            );
        }
        List<UserCartAndProductRelationResponse> result = new ArrayList<>();
        for (UserCartAndProductRelationResponse join : joins) {
            if (join.getProvince().equals("浙") && join.getProductId().equals("1234")) {
                result.add(join);
            }
        }

        return result;
    }
}