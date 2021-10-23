package com.joojeongyong.simplequerydsl.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@ToString(of = {"id", "name"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    // 연관관계의 주인이 아니라서 외래키 매핑을 하지 않음
    @OneToMany(mappedBy = "team")
    private List<Member> memberList = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}
