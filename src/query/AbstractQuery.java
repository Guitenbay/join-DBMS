package query;

import algorithm.JoinOperation;

import java.util.List;

public abstract class AbstractQuery {
    JoinOperation joinOperation;

    AbstractQuery(JoinOperation joinOperation) {
        this.joinOperation = joinOperation;
    }
}
