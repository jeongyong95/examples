package com.joojeongyong.simplequerydsl.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;


@Transactional
@SpringBootTest
class MemberTest {

    @PersistenceContext
    EntityManager manager;

    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        manager.persist(teamA);
        manager.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        manager.persist(member1);
        manager.persist(member2);
        manager.persist(member3);
        manager.persist(member4);

        manager.flush();
        manager.clear();

        List<Member> memberList = manager.createQuery("select m from Member m", Member.class).getResultList();

        memberList.forEach(member -> {
            System.out.println("member : "+member);
            System.out.println("member.team : "+member.getTeam());
        });
    }

}