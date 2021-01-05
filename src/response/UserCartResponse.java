package response;

public class UserCartResponse {
    private String userId;
    private int age;
    private String province;
    private String gender;
    private String password;
    private String cartId;

    public UserCartResponse() {
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

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    @Override
    public String toString() {
        return "UserCartResponse{" +
                "userId='" + userId + '\'' +
                ", age=" + age +
                ", province='" + province + '\'' +
                ", gender='" + gender + '\'' +
                ", password='" + password + '\'' +
                ", cartId='" + cartId + '\'' +
                '}';
    }
}
