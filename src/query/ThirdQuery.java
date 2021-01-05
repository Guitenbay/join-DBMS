package query;

import algorithm.JoinOperation;
import response.CartAndProductRelationResponse;

import java.util.List;

public class ThirdQuery extends AbstractQuery {
    public ThirdQuery(JoinOperation joinOperation) {
        super(joinOperation);
    }
    public List<CartAndProductRelationResponse> query() {
        return null;
    }
}
