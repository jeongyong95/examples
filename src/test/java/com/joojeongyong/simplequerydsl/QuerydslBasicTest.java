package com.joojeongyong.simplequerydsl;

import com.joojeongyong.simplequerydsl.entity.Member;
import com.joojeongyong.simplequerydsl.entity.QMember;
import com.joojeongyong.simplequerydsl.entity.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Transactional
@SpringBootTest
public class QuerydslBasicTest {

    @PersistenceContext
    EntityManager manager;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void contextLoad() {
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
            System.out.println("member : " + member);
            System.out.println("member.team : " + member.getTeam());
        });
    }

    @Test
    public void startJPQL() {
        Member member = manager.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        Assertions.assertThat(member.getUsername().equals("member1"));
    }

    @Test
    public void startQuerydsl() {
        queryFactory = new JPAQueryFactory(manager);
        QMember qMember = QMember.member;

        Member member = queryFactory.select(qMember)
                .from(qMember)
                .where(qMember.username.eq("member1"))
                .fetchOne();

        Assertions.assertThat(member.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() {
        queryFactory = new JPAQueryFactory(manager);
        Member member = queryFactory.selectFrom(QMember.member)
                .where(QMember.member.username.eq("member1")
                        .and(QMember.member.age.eq(10)))
                .fetchOne();

        Assertions.assertThat(member.getUsername()).isEqualTo("member1");
    }

    @Test
    public void searchAndParam() {
        queryFactory = new JPAQueryFactory(manager);
        Member member = queryFactory.selectFrom(QMember.member)
                .where(
                        QMember.member.username.eq("member1"),
                        QMember.member.age.eq(10),
                        // querydsl에서 and 요소로 null이 들어가면 무시한다
                        null
                )
                .fetchOne();

        Assertions.assertThat(member.getUsername()).isEqualTo("member1");
    }
}
