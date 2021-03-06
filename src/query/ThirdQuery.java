package query;

import algorithm.JoinOperation;
import response.CartAndProductRelationResponse;
import table.ProductInShoppingCart;
import table.ShoppingCart;
import table.Table;

import java.util.ArrayList;
import java.util.List;

public class ThirdQuery extends AbstractQuery implements Queryable {
    public ThirdQuery(JoinOperation joinOperation) {
        super(joinOperation);
    }

    /**
     * 查询用户ID为123的用户购物车中数量大于2的物品ID
     * @return
     */
    @Override
    public List<CartAndProductRelationResponse> query(String method) {
        Table<ShoppingCart> cartTable = new Table<>("cart", ShoppingCart.class);
        Table<ProductInShoppingCart> productRelation = new Table<>("cart_item", ProductInShoppingCart.class);

        List<CartAndProductRelationResponse> cartAndProductRelationResponses = new ArrayList<>();
        if (method.contains("memory")){
            cartTable.startRead();
            productRelation.startRead();

            List<ShoppingCart> carts = cartTable.readRow();
            List<ProductInShoppingCart> relations = productRelation.readRow();

            cartTable.endRead();
            productRelation.endRead();
            cartAndProductRelationResponses = this.joinOperation.joinForMemory(
                    carts, relations, "cartId", "cartId",
                    CartAndProductRelationResponse.class, ShoppingCart.class, ProductInShoppingCart.class
            );
        } else {
            cartAndProductRelationResponses = this.joinOperation.join(
                    cartTable, productRelation, "cartId", "cartId",
                    CartAndProductRelationResponse.class, ShoppingCart.class, ProductInShoppingCart.class
            );
        }

        List<CartAndProductRelationResponse> result = new ArrayList<>();
        for (CartAndProductRelationResponse cartAndProductRelationResponse : cartAndProductRelationResponses) {
            if (cartAndProductRelationResponse.getUserId().equals("123")
                    && cartAndProductRelationResponse.getNumber() > 1) {
                result.add(cartAndProductRelationResponse);
            }
        }

        return result;

    }
}