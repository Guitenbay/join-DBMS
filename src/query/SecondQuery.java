package query;

import algorithm.JoinOperation;
import response.UserPhoneResponse;
import table.*;

import java.util.*;

public class SecondQuery extends AbstractQuery {
    public SecondQuery(JoinOperation joinOperation) {
        super(joinOperation);
    }

    /**
     * 查询拥有两个手机号的男性用户ID、年龄及手机号
     * @return
     */
    public List<UserPhoneResponse> query() {
        Table<User> userTable = new Table<>("user", User.class);
        Table<Phone> phoneTable = new Table<>("phone", Phone.class);

        userTable.startRead();
        phoneTable.startRead();

        List<User> users = userTable.readRow();
        List<Phone> phones = phoneTable.readRow();

        userTable.endRead();
        phoneTable.endRead();

        Map<String, List<Phone>> groupPhones = new HashMap<>();
        for (Phone phone : phones) {
            if (!groupPhones.containsKey(phone.getUserId())) {
                groupPhones.put(phone.getUserId(), new ArrayList<>());
            }
            groupPhones.get(phone.getUserId()).add(phone);
        }
        List<String> moreThen2PhoneUserIds = new ArrayList<>();
        groupPhones.forEach((key, value) -> {
            if (value.size() > 1) moreThen2PhoneUserIds.add(key);
        });

        List<UserPhoneResponse> userPhoneResponses = this.joinOperation.join(
                users, phones, "userId", "userId",
                UserPhoneResponse.class, User.class, Phone.class
        );
        List<UserPhoneResponse> result = new ArrayList<>();
        for (UserPhoneResponse userPhoneResponse : userPhoneResponses) {
            if (moreThen2PhoneUserIds.contains(userPhoneResponse.getUserId())) {
                result.add(userPhoneResponse);
            }
        }
        return result;
    }
}
