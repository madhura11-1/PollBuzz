package com.PollBuzz.pollbuzz;

public class UserDetails {
    String age;
    String birthdate;
    String gender;
    String pic;
    String username;
    String name;
    String uid;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getGender() {
        return gender;
    }

    public String getPic() {
        return pic;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public UserDetails() {
        age="";
        birthdate="";
        gender="";
        pic=null;
        username="";
        name="";
        uid="";
    }

}
