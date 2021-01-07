package query;

import algorithm.JoinOperation;
import response.UserCartAndProductRelationResponse;
import response.UserCartAndProductResponse;
import response.UserCartResponse;
import table.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FirstQuery extends AbstractQuery implements Queryable {

    public FirstQuery(JoinOperation joinOperation) {
        super(joinOperation);
    }

    @Override
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
    @Override
    public List<UserCartAndProductResponse> query(String method) {
        if (method.contains("bnl")){
            Properties prop = new Properties();
            try {
                prop.load(FirstQuery.class.getResourceAsStream("../jdbm.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            int blockSize = Integer.valueOf(prop.getProperty("BLOCKSIZE"));
            Table<User> userTable = new Table<>("user", User.class);
            Table<ShoppingCart> cartTable = new Table<>("cart", ShoppingCart.class);
            Table<ProductInShoppingCart> productRelation = new Table<>("cart_item", ProductInShoppingCart.class);
            Table<Product> productTable = new Table<>("item", Product.class);
            userTable.startRead();
            cartTable.startRead();
            productRelation.startRead();
            productTable.startRead();

            List<UserCartResponse> joinres1 = new ArrayList<>();
            List<UserCartAndProductRelationResponse> joinres2 = new ArrayList<>();
            List<UserCartAndProductResponse> joins = new ArrayList<>();

            List<User> users = userTable.readRowLimit(blockSize);

            while(users.size() > 0){
                List<ShoppingCart> carts = cartTable.readRowLimit(blockSize);
                while(carts.size() > 0){
                    joinres1.addAll(this.joinOperation.join(users, carts, "userId", "userId",
                            UserCartResponse.class, User.class, ShoppingCart.class));
                    carts = cartTable.readRowLimit(blockSize);
                }
                users = userTable.readRowLimit(blockSize);
                cartTable.endRead();
                cartTable.startRead();
            }

            List<ProductInShoppingCart> relations = productRelation.readRowLimit(blockSize);
            while (relations.size()>0){
                joinres2.addAll(this.joinOperation.join(joinres1, relations, "cartId", "cartId",
                        UserCartAndProductRelationResponse.class, UserCartResponse.class, ProductInShoppingCart.class));
                relations = productRelation.readRowLimit(blockSize);
            }

            List<Product> products = productTable.readRowLimit(blockSize);
            while (products.size() > 0){
                joins.addAll(this.joinOperation.join(joinres2,products,"productId", "productId",
                        UserCartAndProductResponse.class, UserCartAndProductRelationResponse.class, Product.class));
                products = productTable.readRowLimit(blockSize);
            }

            userTable.endRead();
            cartTable.endRead();
            productRelation.endRead();
            productTable.endRead();
            List<UserCartAndProductResponse> result = new ArrayList<>();
            for (UserCartAndProductResponse join : joins) {
                if (join.getGender().equals("女") && join.getAge() > 35) {
                    result.add(join);
                }
            }
            return result;
        }
        else if (method.contains("default")){
            Table<User> userTable = new Table<>("user", User.class);
            Table<ShoppingCart> cartTable = new Table<>("cart", ShoppingCart.class);
            Table<ProductInShoppingCart> productRelation = new Table<>("cart_item", ProductInShoppingCart.class);
            Table<Product> productTable = new Table<>("item", Product.class);
            userTable.startRead();
            cartTable.startRead();
            productRelation.startRead();
            productTable.startRead();

            List<UserCartResponse> joinres1 = new ArrayList<>();
            List<UserCartAndProductRelationResponse> joinres2 = new ArrayList<>();
            List<UserCartAndProductResponse> joins = new ArrayList<>();

            List<User> users = userTable.readRowLimit(1);
            while(users.size()> 0){
                List<ShoppingCart> carts = cartTable.readRowLimit(1);
                while(carts.size() >0){
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
                joinres2.addAll(this.joinOperation.join(joinres1, relations, "cartId", "cartId",
                        UserCartAndProductRelationResponse.class, UserCartResponse.class, ProductInShoppingCart.class));
                relations = productRelation.readRowLimit(1);
            }

            List<Product> products = productTable.readRowLimit(1);
            while (products.size()>0){
                joins.addAll(this.joinOperation.join(joinres2,products,"productId", "productId",
                        UserCartAndProductResponse.class, UserCartAndProductRelationResponse.class, Product.class));
                products = productTable.readRowLimit(1);
            }

            userTable.endRead();
            cartTable.endRead();
            productRelation.endRead();
            productTable.endRead();
            List<UserCartAndProductResponse> result = new ArrayList<>();
            for (UserCartAndProductResponse join : joins) {
                if (join.getGender().equals("女") && join.getAge() > 35) {
                    result.add(join);
                }
            }
            return result;
        }
        else
            return this.query();
    }
}
