import algorithm.JoinOperation;

import algorithm.impl.TestJoinOperationImpl;
import algorithm.impl.bnl.BlockNestedLoopImpl;
import algorithm.impl.hash.HashJoinImpl;
import algorithm.impl.index.IndexNestedLoopJoinImpl;
import proxy.RunTimeHandler;
import query.*;

import java.lang.reflect.InvocationTargetException;

public class Application {

    private static final JoinOperation bnlJoin = new BlockNestedLoopImpl();
    private static final JoinOperation testJoin = new TestJoinOperationImpl();
    private static final JoinOperation hashJoin = new HashJoinImpl();
    private static final JoinOperation indexNestedLoopJoin = new IndexNestedLoopJoinImpl();

    public static void main(String[] args) {
        String divider = "-----------------";
        final String type = "like disk";
        try {
            researchTestForQuery(FirstQuery.class, "Query 1", type);
            System.gc();
            System.out.println(divider);

            researchTestForQuery(SecondQuery.class, "Query 2", type);
            System.gc();
            System.out.println(divider);

            researchTestForQuery(ThirdQuery.class, "Query 3", type);
            System.gc();
            System.out.println(divider);

            researchTestForQuery(ForthQuery.class, "Query 4", type);
            System.gc();
            System.out.println(divider);

            researchTestForQuery(FifthQuery.class, "Query 5", type);
            System.gc();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private static void researchTestForQuery(Class<? extends AbstractQuery> clazz, String prefix, String type) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Queryable queryableQ1Test =
                (Queryable) new RunTimeHandler(prefix + " (D) " + type, clazz.getConstructor(JoinOperation.class).newInstance(testJoin)).createProxy();
        queryableQ1Test.query(type);

        Queryable queryableQ1Hash =
                (Queryable) new RunTimeHandler(prefix + " (H) " + type, clazz.getConstructor(JoinOperation.class).newInstance(hashJoin)).createProxy();
        queryableQ1Hash.query(type);

        Queryable queryableQ1INL =
                (Queryable) new RunTimeHandler(prefix + " (I) " + type, clazz.getConstructor(JoinOperation.class).newInstance(indexNestedLoopJoin)).createProxy();
        queryableQ1INL.query(type);

        Queryable queryableQ1Bnl =
                (Queryable) new RunTimeHandler(prefix + " (B) " + type, clazz.getConstructor(JoinOperation.class).newInstance(bnlJoin)).createProxy();
        queryableQ1Bnl.query(type);
    }

}
