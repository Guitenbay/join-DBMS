package table;

public class Phone {
    private String userId;
    private String phoneNumber;

    public Phone() {
    }

    public Phone(String userId, String phoneNumber) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
