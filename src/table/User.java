package table;

public class User {
    private String id;
    private int age;
    private String province;
    private String gender;
    private String password;

    public User() {
    }

    public User(String id, int age, String province, String gender, String password) {
        this.id = id;
        this.age = age;
        this.province = province;
        this.gender = gender;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
