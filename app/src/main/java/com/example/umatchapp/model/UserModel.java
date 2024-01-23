package com.example.umatchapp.model;

import java.io.Serializable;
import java.util.ArrayList;

public class UserModel implements Serializable {

    private String user_name;
    private int user_score;
    private int user_image_num;
    private String user_id;
    private String user_email;
    private String user_phone;
    private String user_major;
    private String user_campus;
    private String user_nationality;
    private String user_gender;
    private String user_description;
    private ArrayList user_subjects;
    private String user_image;

    private String target_subject;

    private Double user_db_score;

    // Old Constructor
//    public UserModel(String user_name,int user_image) {
//        this.user_name = user_name;
////        this.user_score = user_score;
//        this.user_image_num = user_image;
//    }

    public UserModel(String user_name,String user_image) {
        this.user_name = user_name;
//        this.user_score = user_score;
        if(user_image == "" || user_image == null){
            this.user_image = "https://firebasestorage.googleapis.com/v0/b/umatch-f3f6b.appspot.com/o/images%2Fuser2.png?alt=media&token=6bcb5d22-3b87-4391-ae86-3f37c8b14d04";
        }else{
            this.user_image = user_image;
        }
    }


    public UserModel(String user_name,String user_image, String target_subject, String user_campus) {
        this.user_name = user_name;
        if(user_image == "" || user_image == null){
            this.user_image = "https://firebasestorage.googleapis.com/v0/b/umatch-f3f6b.appspot.com/o/images%2Fuser2.png?alt=media&token=6bcb5d22-3b87-4391-ae86-3f37c8b14d04";
        }else{
            this.user_image = user_image;
        }
        this.user_campus = user_campus;
        this.target_subject = target_subject;
    }

    // New Constructor(using in notificationsFragment)
    public UserModel(String user_name,String user_image, String target_subject, String user_campus, String user_id){
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_campus = user_campus;
        if(user_image == "" || user_image == null){
            this.user_image = "https://firebasestorage.googleapis.com/v0/b/umatch-f3f6b.appspot.com/o/images%2Fuser2.png?alt=media&token=6bcb5d22-3b87-4391-ae86-3f37c8b14d04";
        }else{
            this.user_image = user_image;
        }
        this.target_subject = target_subject;
    }

    // New Constructor (using overloading method)
    public UserModel(String user_id, String user_name, Double user_db_score) {
        this(user_id, user_name, "", "", "", "", new ArrayList<>(), "", "", "", user_db_score, "https://firebasestorage.googleapis.com/v0/b/umatch-f3f6b.appspot.com/o/images%2Fuser2.png?alt=media&token=6bcb5d22-3b87-4391-ae86-3f37c8b14d04");
    }
    public UserModel(String user_id, String user_name,String user_email, String user_phone, String user_major, String user_campus,ArrayList user_subjects, String user_nationality, String user_gender, String user_description, Double user_db_score, String user_image) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_db_score = user_db_score;
        this.user_email = user_email;
        this.user_phone = user_phone;
        this.user_major = user_major;
        this.user_campus = user_campus;
        this.user_subjects = user_subjects;
        this.user_nationality = user_nationality;
        this.user_gender = user_gender;
        this.user_description = user_description;
        if(user_image == "" || user_image == null){
            this.user_image = "https://firebasestorage.googleapis.com/v0/b/umatch-f3f6b.appspot.com/o/images%2Fuser2.png?alt=media&token=6bcb5d22-3b87-4391-ae86-3f37c8b14d04";
        }else{
            this.user_image = user_image;
        }
    }

    // Getter and Setter
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }
    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getUser_score() {
        return user_score;
    }
    public void setUser_score(int user_score) {
        this.user_score = user_score;
    }

    public String getUser_email() {
        return user_email;
    }
    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_phone() {
        return user_phone;
    }
    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_major() {
        return user_major;
    }
    public void setUser_major(String user_major) {
        this.user_major = user_major;
    }

    public String getUser_campus() {
        return user_campus;
    }
    public void setUser_campus(String user_campus) {
        this.user_campus = user_campus;
    }

    public ArrayList getUser_subjects() {
        return user_subjects;
    }
    public void getUser_subjects(ArrayList user_subjects) {
        this.user_subjects = user_subjects;
    }

    public String getUser_nationality() {
        return user_nationality;
    }
    public void setUser_nationality(String user_nationality) {
        this.user_nationality = user_nationality;
    }

    public String getUser_gender() {
        return user_gender;
    }
    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public String getUser_description() {
        return user_description;
    }
    public void setUser_description(String user_description) {
        this.user_description = user_description;
    }

    public Double getUser_db_score() {
        return user_db_score;
    }
    public void setUser_db_score(Double user_db_score) {
        this.user_db_score = user_db_score;
    }

    public String getUser_image() {
        return user_image;
    }
    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public void setTarget_subject(String target_subject) {
        this.target_subject = target_subject;
    }
    public String getTarget_subject() {
        return target_subject;
    }

    public void setUser_image_num(int user_image_num) {
        this.user_image_num = user_image_num;
    }
    public int getUser_image_num() {
        return user_image_num;
    }
}
