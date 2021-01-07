package query;

import java.util.List;

public interface Queryable {
    List<?> query();
    List<?> query(String method);
}
