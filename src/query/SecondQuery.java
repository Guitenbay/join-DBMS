package query;

import algorithm.JoinOperation;
import response.UserPhoneResponse;

import java.util.List;

public class SecondQuery extends AbstractQuery {
    public SecondQuery(JoinOperation joinOperation) {
        super(joinOperation);
    }

    public List<UserPhoneResponse> query() {
        return null;
    }
}
