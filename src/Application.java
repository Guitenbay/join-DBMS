import algorithm.JoinOperation;
import algorithm.impl.*;
import query.*;
import response.*;

import java.util.List;

public class Application {

    public static void main(String[] args) {
        JoinOperation joinImpl = new TestJoinOperationImpl();
//        JoinOperation joinImpl = new IndexNestedLoopJoinImpl();
//        JoinOperation joinImpl = new HashJoinImpl();
        List<UserCartAndProductResponse> firstResponse = new FirstQuery(joinImpl).query();
        System.out.println("For First Query:");
        firstResponse.forEach(System.out::println);

        List<UserPhoneResponse> secondResponse = new SecondQuery(joinImpl).query();
        System.out.println("For Second Query:");
        secondResponse.forEach(System.out::println);

        List<CartAndProductRelationResponse> thirdResponse = new ThirdQuery(joinImpl).query();
        System.out.println("For Third Query:");
        thirdResponse.forEach(System.out::println);

        List<UserPhoneCartAndProductRelationResponse> forthResponse = new ForthQuery(joinImpl).query();
        System.out.println("For Forth Query:");
        forthResponse.forEach(System.out::println);

        List<UserCartAndProductRelationResponse> fifthResponse = new FifthQuery(joinImpl).query();
        System.out.println("For Fifth Query:");
        fifthResponse.forEach(System.out::println);
    }

//    private static List[] initData() {
//        List<User> users;
//        List<Phone> phones;
//        users.add(new User("1", 20, "宁夏", "男", "1234567"));
//        users.add(new User("2", 50, "宁夏", "女", "123456743"));
//        users.add(new User("3", 23, "北京", "男", "12345"));
//        phones.add(new Phone("1", "1761234567"));
//        phones.add(new Phone("1", "1761234568"));
//        phones.add(new Phone("2", "1761234987"));
//        phones.add(new Phone("1", "1769876540"));
//        Table<User> userTable = new Table<>("user", User.class);
//        userTable.startRead();
//        users = userTable.readRowLimit(3);
//        Table<Phone> phoneTable = new Table<>("phone", Phone.class);
//        phoneTable.startRead();
//        phones = phoneTable.readRowLimit(4);
//        return new List[]{users, phones};
//    }

}
