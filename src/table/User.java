package table;

public class User {
    private String userId;
    private int age;
    private String province;
    private String gender;
    private String password;

    public User() {
    }

    public User(String userId, int age, String province, String gender, String password) {
        this.userId = userId;
        this.age = age;
        this.province = province;
        this.gender = gender;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
