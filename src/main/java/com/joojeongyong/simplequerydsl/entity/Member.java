package com.joojeongyong.simplequerydsl.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//toString에서 연관관계 필드를 지정하면 stackOverFlow
@ToString(of = {"id", "username", "age"})
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private Integer age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String username) {
        this.username = username;
        this.age = 0;
    }

    public Member(String username, Integer age) {
        this.username = username;
        this.age = age;
    }

    public Member(String username, Integer age, Team team) {
        this.username = username;
        this.age = age;
        if (Objects.nonNull(team)) {
            changeTeam(team);
        }
    }

    public void changeTeam(Team team) {
        this.team = team;
        team.getMemberList().add(this);
    }
}
