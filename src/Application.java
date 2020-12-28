import algorithm.JoinOperation;
import algorithm.impl.TestJoinOperationImpl;
import response.TestUserPhoneResponse;
import table.Phone;
import table.Table;
import table.User;

import java.util.ArrayList;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        List[] lists = initData();
        JoinOperation joinImpl = new TestJoinOperationImpl();
        List responses = joinImpl.join(lists[0], lists[1], "id", "userId",
                TestUserPhoneResponse.class, User.class, Phone.class);
        responses.forEach(System.out::println);
    }

    private static List[] initData() {
        List<User> users;
        List<Phone> phones;
//        users.add(new User("1", 20, "宁夏", "男", "1234567"));
//        users.add(new User("2", 50, "宁夏", "女", "123456743"));
//        users.add(new User("3", 23, "北京", "男", "12345"));
//        phones.add(new Phone("1", "1761234567"));
//        phones.add(new Phone("1", "1761234568"));
//        phones.add(new Phone("2", "1761234987"));
//        phones.add(new Phone("1", "1769876540"));
        Table<User> userTable = new Table<>("user", User.class);
        userTable.startRead();
        users = userTable.readDataLimit(3);
        Table<Phone> phoneTable = new Table<>("phone", Phone.class);
        phoneTable.startRead();
        phones = phoneTable.readDataLimit(4);
        return new List[]{users, phones};
    }

}
