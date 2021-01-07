package query;

import algorithm.JoinOperation;
import response.UserPhoneCartAndProductRelationResponse;
import response.UserPhoneCartResponse;
import response.UserPhoneResponse;
import table.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
    public List<UserPhoneCartAndProductRelationResponse> query(String method){
        if (method.contains("bnl")){
            Properties prop = new Properties();
            try {
                prop.load(FirstQuery.class.getResourceAsStream("../jdbm.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            int blocksize = Integer.valueOf(prop.getProperty("BLOCKSIZE"));

            Table<User> userTable = new Table<>("user", User.class);
            Table<Phone> phoneTable = new Table<>("phone", Phone.class);
            Table<ShoppingCart> cartTable = new Table<>("cart", ShoppingCart.class);
            Table<ProductInShoppingCart> productRelation = new Table<>("cart_item", ProductInShoppingCart.class);

            userTable.startRead();
            phoneTable.startRead();
            cartTable.startRead();
            productRelation.startRead();

            List<UserPhoneResponse> joinres1 = new ArrayList<>();
            List<UserPhoneCartResponse> joinres2 = new ArrayList<>();
            List<UserPhoneCartAndProductRelationResponse> joins = new ArrayList<>();

            List<User> users = userTable.readRowLimit(blocksize);

            while(users.size()> 0){
                List<Phone> phones = phoneTable.readRowLimit(blocksize);
                while (phones.size() > 0){
                    joinres1.addAll(this.joinOperation.join(users, phones, "userId", "userId",
                            UserPhoneResponse.class, User.class, Phone.class));
                    phones = phoneTable.readRowLimit(blocksize);
                }
                users = userTable.readRowLimit(blocksize);
                phoneTable.endRead();
                phoneTable.startRead();
            }

            List<ShoppingCart> carts = cartTable.readRowLimit(blocksize);
            while (carts.size()>0){
                joinres2.addAll(this.joinOperation.join(joinres1, carts, "userId", "userId",
                        UserPhoneCartResponse.class, UserPhoneResponse.class, ShoppingCart.class));
                carts = cartTable.readRowLimit(blocksize);
            }

            List<ProductInShoppingCart> relations = productRelation.readRowLimit(blocksize);
            while (relations.size()>0){
                joins.addAll(this.joinOperation.join(joinres2, relations, "cartId", "cartId",
                        UserPhoneCartAndProductRelationResponse.class, UserPhoneCartResponse.class, ProductInShoppingCart.class));
                relations = productRelation.readRowLimit(blocksize);
            }

            userTable.endRead();
            phoneTable.endRead();
            cartTable.endRead();
            productRelation.endRead();

            List<UserPhoneCartAndProductRelationResponse> result = new ArrayList<>();
            for (UserPhoneCartAndProductRelationResponse join : joins) {
                if (join.getPhoneNumber().startsWith("123")) {
                    result.add(join);
                }
            }

            return result;
        }
        else if (method.contains("default")){
            Table<User> userTable = new Table<>("user", User.class);
            Table<Phone> phoneTable = new Table<>("phone", Phone.class);
            Table<ShoppingCart> cartTable = new Table<>("cart", ShoppingCart.class);
            Table<ProductInShoppingCart> productRelation = new Table<>("cart_item", ProductInShoppingCart.class);

            userTable.startRead();
            phoneTable.startRead();
            cartTable.startRead();
            productRelation.startRead();

            List<UserPhoneResponse> joinres1 = new ArrayList<>();
            List<UserPhoneCartResponse> joinres2 = new ArrayList<>();
            List<UserPhoneCartAndProductRelationResponse> joins = new ArrayList<>();

            List<User> users = userTable.readRowLimit(1);

            while(users.size()> 0){
                List<Phone> phones = phoneTable.readRowLimit(1);
                while (phones.size() > 0){
                    joinres1.addAll(this.joinOperation.join(users, phones, "userId", "userId",
                            UserPhoneResponse.class, User.class, Phone.class));
                    phones = phoneTable.readRowLimit(1);
                }
                users = userTable.readRowLimit(1);
                phoneTable.endRead();
                phoneTable.startRead();
            }

            List<ShoppingCart> carts = cartTable.readRowLimit(1);
            while (carts.size()>0){
                joinres2.addAll(this.joinOperation.join(joinres1, carts, "userId", "userId",
                        UserPhoneCartResponse.class, UserPhoneResponse.class, ShoppingCart.class));
                carts = cartTable.readRowLimit(1);
            }

            List<ProductInShoppingCart> relations = productRelation.readRowLimit(1);
            while (relations.size()>0){
                joins.addAll(this.joinOperation.join(joinres2, relations, "cartId", "cartId",
                        UserPhoneCartAndProductRelationResponse.class, UserPhoneCartResponse.class, ProductInShoppingCart.class));
                relations = productRelation.readRowLimit(1);
            }

            userTable.endRead();
            phoneTable.endRead();
            cartTable.endRead();
            productRelation.endRead();

            List<UserPhoneCartAndProductRelationResponse> result = new ArrayList<>();
            for (UserPhoneCartAndProductRelationResponse join : joins) {
                if (join.getPhoneNumber().startsWith("123")) {
                    result.add(join);
                }
            }

            return result;
        }
        else
            return this.query();
    }
}