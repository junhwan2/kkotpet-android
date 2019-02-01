package kr.co.ibks.platformteam.android.kkotpet.sns;

/**
 * @brief SNS 사용자 프로필 정보
 */

public class SNSUserInfoDTO {

    private int snstype = -1;
    private String email;
    private String name;
    private String gender;
    private String id;
    private String imgurl;
    private String age;
    private String birth;
    private String snslink;
    private String locale;
    private String timezone;
    private String nickname;
    private String phoneNumber;
    private String accessToken;

    public int getSnstype() {
        return snstype;
    }

    public void setSnstype(int snstype) {
        this.snstype = snstype;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getSnslink() {
        return snslink;
    }

    public void setSnslink(String snslink) {
        this.snslink = snslink;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
