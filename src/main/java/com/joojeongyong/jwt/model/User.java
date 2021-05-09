package com.joojeongyong.jwt.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;
    private String roles;

    public List<String> getRoleList() {
        if (this.roles.length()>0) {
            return Arrays.asList(this.roles.split(","));
        }
        return Collections.EMPTY_LIST;
    }
}
