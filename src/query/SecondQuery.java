package query;

import algorithm.JoinOperation;
import response.UserPhoneResponse;
import table.*;

import java.util.*;

public class SecondQuery extends AbstractQuery implements Queryable {
    public SecondQuery(JoinOperation joinOperation) {
        super(joinOperation);
    }

    /**
     * 查询拥有两个手机号的男性用户ID、年龄及手机号
     * @return
     */
    @Override
    public List<UserPhoneResponse> query(String method){
        Table<User> userTable = new Table<>("user", User.class);
        Table<Phone> phoneTable = new Table<>("phone", Phone.class);

        List<String> moreThen2PhoneUserIds = new ArrayList<>();
        List<UserPhoneResponse> userPhoneResponses;
        if (method.contains("memory")){
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

            groupPhones.forEach((key, value) -> {
                if (value.size() > 1) moreThen2PhoneUserIds.add(key);
            });

            userPhoneResponses = this.joinOperation.joinForMemory(
                    users, phones, "userId", "userId",
                    UserPhoneResponse.class, User.class, Phone.class
            );
        } else {
            Map<String, List<Phone>> groupPhones = new HashMap<>();
            phoneTable.startRead();
            for (Phone phone = phoneTable.readRowOnlyOne(); phone != null; phone = phoneTable.readRowOnlyOne()) {
                if (!groupPhones.containsKey(phone.getUserId())) {
                    groupPhones.put(phone.getUserId(), new ArrayList<>());
                }
                groupPhones.get(phone.getUserId()).add(phone);
            }
            phoneTable.endRead();

            groupPhones.forEach((key, value) -> {
                if (value.size() > 1) moreThen2PhoneUserIds.add(key);
            });

            userPhoneResponses = this.joinOperation.join(
                    userTable, phoneTable, "userId", "userId",
                    UserPhoneResponse.class, User.class, Phone.class
            );
        }
        List<UserPhoneResponse> result = new ArrayList<>();
        for (UserPhoneResponse userPhoneResponse : userPhoneResponses) {
            if (moreThen2PhoneUserIds.contains(userPhoneResponse.getUserId())) {
                result.add(userPhoneResponse);
            }
        }
        return result;
    }
}