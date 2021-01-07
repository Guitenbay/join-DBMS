package query;

import algorithm.JoinOperation;
import response.CartAndProductRelationResponse;
import table.ProductInShoppingCart;
import table.ShoppingCart;
import table.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ThirdQuery extends AbstractQuery implements Queryable {
    public ThirdQuery(JoinOperation joinOperation) {
        super(joinOperation);
    }

    /**
     * 查询用户ID为123的用户购物车中数量大于2的物品ID
     * @return
     */
    @Override
    public List<CartAndProductRelationResponse> query() {
        Table<ShoppingCart> cartTable = new Table<>("cart", ShoppingCart.class);
        Table<ProductInShoppingCart> productRelation = new Table<>("cart_item", ProductInShoppingCart.class);

        cartTable.startRead();
        productRelation.startRead();

        List<ShoppingCart> carts = cartTable.readRow();
        List<ProductInShoppingCart> relations = productRelation.readRow();

        cartTable.endRead();
        productRelation.endRead();

        List<CartAndProductRelationResponse> cartAndProductRelationResponses = this.joinOperation.join(
                carts, relations, "cartId", "cartId",
                CartAndProductRelationResponse.class, ShoppingCart.class, ProductInShoppingCart.class
        );

        List<CartAndProductRelationResponse> result = new ArrayList<>();
        for (CartAndProductRelationResponse cartAndProductRelationResponse : cartAndProductRelationResponses) {
            if (cartAndProductRelationResponse.getUserId().equals("123")
                    && cartAndProductRelationResponse.getNumber() > 1) {
                result.add(cartAndProductRelationResponse);
            }
        }

        return result;
    }
    @Override
    public List<CartAndProductRelationResponse> query(String method){
        if (method.contains("bnl")){
            Properties prop = new Properties();
            try {
                prop.load(FirstQuery.class.getResourceAsStream("../jdbm.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            int blocksize = Integer.valueOf(prop.getProperty("BLOCKSIZE"));

            Table<ShoppingCart> cartTable = new Table<>("cart", ShoppingCart.class);
            Table<ProductInShoppingCart> productRelation = new Table<>("cart_item", ProductInShoppingCart.class);

            cartTable.startRead();
            productRelation.startRead();


            List<CartAndProductRelationResponse> joinres1 = new ArrayList<>();

            List<ShoppingCart> carts = cartTable.readRowLimit(blocksize);
            while (carts.size()>0){
                List<ProductInShoppingCart> relations = productRelation.readRowLimit(blocksize);
                while (relations.size()>0){
                    joinres1.addAll(this.joinOperation.join(
                            carts, relations, "cartId", "cartId",
                            CartAndProductRelationResponse.class, ShoppingCart.class, ProductInShoppingCart.class
                    ));
                    relations =  productRelation.readRowLimit(blocksize);
                }
                carts = cartTable.readRowLimit(blocksize);
                productRelation.endRead();
                productRelation.startRead();
            }

            cartTable.endRead();
            productRelation.endRead();

            List<CartAndProductRelationResponse> result = new ArrayList<>();
            for (CartAndProductRelationResponse cartAndProductRelationResponse : joinres1) {
                if (cartAndProductRelationResponse.getUserId().equals("123")
                        && cartAndProductRelationResponse.getNumber() > 1) {
                    result.add(cartAndProductRelationResponse);
                }
            }

            return result;
        }
        else if (method.contains("default")){
            Table<ShoppingCart> cartTable = new Table<>("cart", ShoppingCart.class);
            Table<ProductInShoppingCart> productRelation = new Table<>("cart_item", ProductInShoppingCart.class);

            cartTable.startRead();
            productRelation.startRead();


            List<CartAndProductRelationResponse> joinres1 = new ArrayList<>();

            List<ShoppingCart> carts = cartTable.readRowLimit(1);
            while (carts.size()>0){
                List<ProductInShoppingCart> relations = productRelation.readRowLimit(1);
                while (relations.size()>0){
                    joinres1.addAll(this.joinOperation.join(
                            carts, relations, "cartId", "cartId",
                            CartAndProductRelationResponse.class, ShoppingCart.class, ProductInShoppingCart.class
                    ));
                    relations =  productRelation.readRowLimit(1);
                }
                carts = cartTable.readRowLimit(1);
                productRelation.endRead();
                productRelation.startRead();
            }

            cartTable.endRead();
            productRelation.endRead();

            List<CartAndProductRelationResponse> result = new ArrayList<>();
            for (CartAndProductRelationResponse cartAndProductRelationResponse : joinres1) {
                if (cartAndProductRelationResponse.getUserId().equals("123")
                        && cartAndProductRelationResponse.getNumber() > 1) {
                    result.add(cartAndProductRelationResponse);
                }
            }
            return result;
        }
        else
            return this.query();
    }
}