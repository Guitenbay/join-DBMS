import algorithm.JoinOperation;

import algorithm.impl.TestJoinOperationImpl;
import algorithm.impl.bnl.BlockNestedLoopImpl;
import query.FirstQuery;
import query.SecondQuery;
import query.ForthQuery;
import query.ThirdQuery;
import query.FifthQuery;
import response.*;

import java.util.List;

import static java.lang.System.*;

public class Application {

    public static void main(String[] args) {
        JoinOperation bnlJoin = new BlockNestedLoopImpl();

        JoinOperation joinImpl = new TestJoinOperationImpl();
//        JoinOperation joinImpl = new IndexNestedLoopJoinImpl();
//        JoinOperation joinImpl = new HashJoinImpl();
        
        final String BNL = "bnl";
        final String DEFAULT = "default";

        /* ----------------- all memory/bnl/normal join on Query 1 ----------------- */
        List<UserCartAndProductResponse> responses = new FirstQuery(joinImpl).query();
        responses.forEach(out::println);
        List<UserCartAndProductResponse> responseQ1Bnl = new FirstQuery(bnlJoin).query(BNL);
        responseQ1Bnl.forEach(out::println);
        List<UserCartAndProductResponse> responseQ1Default = new FirstQuery(bnlJoin).query(DEFAULT);
        responseQ1Default.forEach(out::println);

        /* ----------------- all memory/bnl/normal join on Query 2 ----------------- */
        List<UserPhoneResponse> responseQ2Test = new SecondQuery(joinImpl).query();
        responseQ2Test.forEach(out::println);
        List<UserPhoneResponse> responseQ2Bnl = new SecondQuery(bnlJoin).query(BNL);
        responseQ2Bnl.forEach(out::println);
        List<UserPhoneResponse> responseQ2Default = new SecondQuery(bnlJoin).query(DEFAULT);
        responseQ2Default.forEach(out::println);


        /* ----------------- all memory/bnl/normal join on Query 3 ----------------- */
        List<CartAndProductRelationResponse> responseQ3Test = new ThirdQuery(joinImpl).query();
        responseQ3Test.forEach(out::println);
        List<CartAndProductRelationResponse> responseQ3Bnl = new ThirdQuery(bnlJoin).query(BNL);
        responseQ3Bnl.forEach(out::println);
        List<CartAndProductRelationResponse> responseQ3Default = new ThirdQuery(bnlJoin).query(DEFAULT);
        responseQ3Default.forEach(out::println);


        /* ----------------- all memory/bnl/normal join on Query 4 ----------------- */
        List<UserPhoneCartAndProductRelationResponse> responseQ4Test = new ForthQuery(joinImpl).query();
        responseQ4Test.forEach(out::println);
        List<UserPhoneCartAndProductRelationResponse> responseQ4Bnl = new ForthQuery(bnlJoin).query(BNL);
        responseQ4Bnl.forEach(out::println);
        List<UserPhoneCartAndProductRelationResponse> responseQ4Default = new ForthQuery(bnlJoin).query(DEFAULT);
        responseQ4Default.forEach(out::println);


        /* ----------------- all memory/bnl/normal join on Query 5 ----------------- */
        List<UserCartAndProductRelationResponse> responseQ5Test = new FifthQuery(joinImpl).query();
        responseQ5Test.forEach(out::println);
        List<UserCartAndProductRelationResponse> responseQ5Bnl = new FifthQuery(bnlJoin).query(BNL);
        responseQ5Bnl.forEach(out::println);
        List<UserCartAndProductRelationResponse> responseQ5Default = new FifthQuery(bnlJoin).query(DEFAULT);
        responseQ5Default.forEach(out::println);
    }

}
