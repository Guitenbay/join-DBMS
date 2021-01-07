package query;

import algorithm.JoinOperation;
import response.UserCartAndProductRelationResponse;
import response.UserCartResponse;
import table.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
    public List<UserCartAndProductRelationResponse> query(String method){
        if (method.contains("bnl")){
            Properties prop = new Properties();
            try {
                prop.load(FirstQuery.class.getResourceAsStream("../jdbm.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            int blocksize = Integer.valueOf(prop.getProperty("BLOCKSIZE"));

            Table<User> userTable = new Table<>("user", User.class);
            Table<ShoppingCart> cartTable = new Table<>("cart", ShoppingCart.class);
            Table<ProductInShoppingCart> productRelation = new Table<>("cart_item", ProductInShoppingCart.class);

            userTable.startRead();
            cartTable.startRead();
            productRelation.startRead();

//            userTable.endRead();
//            cartTable.endRead();
//            productRelation.endRead();

            List<UserCartResponse> joinres1 = new ArrayList<>();
            List<UserCartAndProductRelationResponse> joins = new ArrayList<>();

            List<User> users = userTable.readRowLimit(blocksize);
            while (users.size()>0){
                List<ShoppingCart> carts = cartTable.readRowLimit(blocksize);
                while (carts.size()>0){
                    joinres1.addAll(this.joinOperation.join(users, carts, "userId", "userId",
                            UserCartResponse.class, User.class, ShoppingCart.class));
                    carts = cartTable.readRowLimit(blocksize);
                }
                users = userTable.readRowLimit(blocksize);
                cartTable.endRead();
                cartTable.startRead();
            }

            List<ProductInShoppingCart> relations = productRelation.readRowLimit(blocksize);
            while (relations.size()>0){
                joins.addAll(this.joinOperation.join(
                        joinres1, relations, "cartId", "cartId",
                        UserCartAndProductRelationResponse.class, UserCartResponse.class, ProductInShoppingCart.class));
                relations = productRelation.readRowLimit(blocksize);
            }

            userTable.endRead();
            cartTable.endRead();
            productRelation.endRead();

            List<UserCartAndProductRelationResponse> result = new ArrayList<>();
            for (UserCartAndProductRelationResponse join : joins) {
                if (join.getProvince().equals("浙") && join.getProductId().equals("1234")) {
                    result.add(join);
                }
            }
            return result;
        }
        else if (method.contains("default")){
            Table<User> userTable = new Table<>("user", User.class);
            Table<ShoppingCart> cartTable = new Table<>("cart", ShoppingCart.class);
            Table<ProductInShoppingCart> productRelation = new Table<>("cart_item", ProductInShoppingCart.class);

            userTable.startRead();
            cartTable.startRead();
            productRelation.startRead();

//            userTable.endRead();
//            cartTable.endRead();
//            productRelation.endRead();

            List<UserCartResponse> joinres1 = new ArrayList<>();
            List<UserCartAndProductRelationResponse> joins = new ArrayList<>();

            List<User> users = userTable.readRowLimit(1);
            while (users.size()>0){
                List<ShoppingCart> carts = cartTable.readRowLimit(1);
                while (carts.size()>0){
                    joinres1.addAll(this.joinOperation.join(users, carts, "userId", "userId",
                            UserCartResponse.class, User.class, ShoppingCart.class));
                    carts = cartTable.readRowLimit(1);
                }
                users = userTable.readRowLimit(1);
                cartTable.endRead();
                cartTable.startRead();
            }

            List<ProductInShoppingCart> relations = productRelation.readRowLimit(1);
            while (relations.size()>0){
                joins.addAll(this.joinOperation.join(
                        joinres1, relations, "cartId", "cartId",
                        UserCartAndProductRelationResponse.class, UserCartResponse.class, ProductInShoppingCart.class));
                relations = productRelation.readRowLimit(1);
            }

            userTable.endRead();
            cartTable.endRead();
            productRelation.endRead();

            List<UserCartAndProductRelationResponse> result = new ArrayList<>();
            for (UserCartAndProductRelationResponse join : joins) {
                if (join.getProvince().equals("浙") && join.getProductId().equals("1234")) {
                    result.add(join);
                }
            }
            return result;
        }
        else
            return this.query();
    }
}