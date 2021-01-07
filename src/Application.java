import algorithm.JoinOperation;

import algorithm.impl.TestJoinOperationImpl;
import algorithm.impl.bnl.BlockNestedLoopImpl;
import algorithm.impl.hash.HashJoinImpl;
import algorithm.impl.index.IndexNestedLoopJoinImpl;
import proxy.RunTimeHandler;
import query.*;
import util.ClassUtils;

import java.lang.reflect.InvocationTargetException;

public class Application {

    private static final JoinOperation bnlJoin = new BlockNestedLoopImpl();
    private static final JoinOperation testJoin = new TestJoinOperationImpl();
    private static final JoinOperation hashJoin = new HashJoinImpl();
    private static final JoinOperation indexNestedLoopJoin = new IndexNestedLoopJoinImpl();

    public static void main(String[] args) {
        String divider = "-----------------";
        try {
            /* all memory/bnl/normal join on Query 1 */
//            researchTestForQuery(FirstQuery.class, "Query 1");
//            System.out.println(divider);
            /* all memory/bnl/normal join on Query 2 */
//            researchTestForQuery(SecondQuery.class, "Query 2");
//            System.out.println(divider);
            /* all memory/bnl/normal join on Query 3 */
//            researchTestForQuery(ThirdQuery.class, "Query 3");
//            System.out.println(divider);
            /* all memory/bnl/normal join on Query 4 */
            researchTestForQuery(ForthQuery.class, "Query 4");
//            System.out.println(divider);
            /* all memory/bnl/normal join on Query 5 */
//            researchTestForQuery(FifthQuery.class, "Query 5");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private static void researchTestForQuery(Class<? extends AbstractQuery> clazz, String prefix) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final String BNL = "bnl";
        final String DEFAULT = "default";

        Queryable queryableQ1Test =
                (Queryable) new RunTimeHandler(prefix + "(TEST)", clazz.getConstructor(JoinOperation.class).newInstance(testJoin)).createProxy();
        queryableQ1Test.query();

        Queryable queryableQ1Hash =
                (Queryable) new RunTimeHandler(prefix + "(HASH)", clazz.getConstructor(JoinOperation.class).newInstance(hashJoin)).createProxy();
        queryableQ1Hash.query();

        Queryable queryableQ1INL =
                (Queryable) new RunTimeHandler(prefix + "(INL)", clazz.getConstructor(JoinOperation.class).newInstance(indexNestedLoopJoin)).createProxy();
        queryableQ1INL.query();

        Queryable queryableQ1Bnl =
                (Queryable) new RunTimeHandler(prefix + "(BNL)", clazz.getConstructor(JoinOperation.class).newInstance(bnlJoin)).createProxy();
        queryableQ1Bnl.query(BNL);

        Queryable queryableQ1Default =
                (Queryable) new RunTimeHandler(prefix + "(DEFAULT)", clazz.getConstructor(JoinOperation.class).newInstance(bnlJoin)).createProxy();
        queryableQ1Default.query(DEFAULT);
    }

}
