package com.joojeongyong.simplequerydsl;

import com.joojeongyong.simplequerydsl.dto.MemberDto;
import com.joojeongyong.simplequerydsl.dto.QMemberDto;
import com.joojeongyong.simplequerydsl.dto.UserDto;
import com.joojeongyong.simplequerydsl.entity.Member;
import com.joojeongyong.simplequerydsl.entity.QMember;
import com.joojeongyong.simplequerydsl.entity.QTeam;
import com.joojeongyong.simplequerydsl.entity.Team;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;
import java.util.Objects;

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

        queryFactory = new JPAQueryFactory(manager);
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
        QMember qMember = QMember.member;

        Member member = queryFactory.select(qMember)
                .from(qMember)
                .where(qMember.username.eq("member1"))
                .fetchOne();

        Assertions.assertThat(member.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() {
        Member member = queryFactory.selectFrom(QMember.member)
                .where(QMember.member.username.eq("member1")
                        .and(QMember.member.age.eq(10)))
                .fetchOne();

        Assertions.assertThat(member.getUsername()).isEqualTo("member1");
    }

    @Test
    public void searchAndParam() {
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

    @Test
    public void resultFetch() {
        List<Member> fetch = queryFactory.selectFrom(QMember.member)
                .fetch();

        Member fetchFirst = queryFactory.selectFrom(QMember.member).fetchFirst();

        // 페이징 제공
        QueryResults<Member> results = queryFactory.selectFrom(QMember.member)
                .fetchResults();

        // query 하나
        results.getTotal();

        // query 둘
        List<Member> content = results.getResults();
    }

    /**
     * 1. 회원 니이 내림차순
     * 2. 회원 이름 올림차순
     * 이름이 없으면 마지막에 출력 null last
     */
    @Test
    public void sort() {
        manager.persist(new Member(null, 100));
        manager.persist(new Member("member5", 100));
        manager.persist(new Member("member6", 100));

        List<Member> result = queryFactory.selectFrom(QMember.member)
                .where(QMember.member.age.eq(100))
                .orderBy(
                        QMember.member.age.desc(),
                        QMember.member.username.asc().nullsLast()
                ).fetch();
        result.forEach(System.out::println);
        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("member5");
        Assertions.assertThat(result.get(1).getUsername()).isEqualTo("member6");
        Assertions.assertThat(result.get(2).getUsername()).isNull();
    }

    @Test
    public void paging() {
        List<Member> list = queryFactory.selectFrom(QMember.member)
                .orderBy(QMember.member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();

        Assertions.assertThat(list.size()).isEqualTo(2);
    }

    @Test
    public void pagingFetchResults() {
        QueryResults<Member> results = queryFactory.selectFrom(QMember.member)
                .orderBy(QMember.member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();

        Assertions.assertThat(results.getTotal()).isEqualTo(4);
        Assertions.assertThat(results.getLimit()).isEqualTo(2);
        Assertions.assertThat(results.getOffset()).isEqualTo(1);
        Assertions.assertThat(results.getResults().size()).isEqualTo(2);
    }

    @Test
    public void aggregation() {
        List<Tuple> result = queryFactory.select(
                        QMember.member.count(),
                        QMember.member.age.sum(),
                        QMember.member.age.avg(),
                        QMember.member.age.max(),
                        QMember.member.age.min()
                )
                .from(QMember.member).fetch();

        Tuple tuple = result.get(0);
        Assertions.assertThat(tuple.get(QMember.member.count())).isEqualTo(4);
        Assertions.assertThat(tuple.get(QMember.member.age.sum())).isEqualTo(100);
        Assertions.assertThat(tuple.get(QMember.member.age.avg())).isEqualTo(25);
        Assertions.assertThat(tuple.get(QMember.member.age.max())).isEqualTo(40);
        Assertions.assertThat(tuple.get(QMember.member.age.min())).isEqualTo(10);
    }

    @Test
    public void groupBy() {
        List<Tuple> result = queryFactory.select(QTeam.team.name, QMember.member.age.avg())
                .from(QMember.member)
                .join(QMember.member.team, QTeam.team)
                .groupBy(QTeam.team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        Assertions.assertThat(teamA.get(QTeam.team.name)).isEqualTo("teamA");
        Assertions.assertThat(teamA.get(QMember.member.age.avg())).isEqualTo(15);
        Assertions.assertThat(teamB.get(QTeam.team.name)).isEqualTo("teamB");
        Assertions.assertThat(teamB.get(QMember.member.age.avg())).isEqualTo(35);
    }

    // 조인 - 기본
    @Test
    public void join() {
        QMember member = QMember.member;
        QTeam team = QTeam.team;

        List<Member> result = queryFactory.selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        Assertions.assertThat(result).extracting("username")
                .contains("member1", "member2");
    }


    // 세타 조인 - 아무런 연관관계가 없는 테이블 간 조인
    @Test
    public void thetaJoin() {
        manager.persist(new Member("teamA"));

        List<Member> result = queryFactory.select(QMember.member)
                .from(QMember.member, QTeam.team)
                .where(QMember.member.username.eq(QTeam.team.name))
                .fetch();

        Assertions.assertThat(result).extracting("username")
                .contains("teamA");
    }

    // 조인의 on절 - 조인 대상의 필터링
    @Test
    public void joinOnIsFiltering() {
        List<Tuple> result = queryFactory.select(QMember.member, QTeam.team)
                .from(QMember.member)
                // left outer join
                // member를 기준으로 member.team이 teamA이면 조인하고,
                // 아니면 team을 조인하지 않는다
                .leftJoin(QMember.member.team, QTeam.team)
                .on(QTeam.team.name.eq("teamA"))
                .fetch();

        result.forEach(System.out::println);
    }

    @PersistenceUnit
    EntityManagerFactory factory;

    @Test
    public void notUsingFetchJoin() {
        manager.flush();
        manager.clear();

        // 페치 조인 X
        Member member = queryFactory.selectFrom(QMember.member)
                .where(QMember.member.username.eq("member1"))
                .fetchOne();
        boolean isLoaded = factory.getPersistenceUnitUtil().isLoaded(member.getTeam());
        Assertions.assertThat(isLoaded).as("페치 조인이 적용되지 않음").isFalse();
    }

    // 페치조인 - 최적화를 위해 JPA가 제공한다.
    @Test
    public void fetchJoin() {
        manager.flush();
        manager.clear();

        Member member = queryFactory.selectFrom(QMember.member)
                .innerJoin(QMember.member.team, QTeam.team)
                // join 메서드들 뒤에 배치하는 게 명확함
                .fetchJoin()
                // 검색 대상이 아닌 조인 대상에 대해서 on 절로 필터링을 하면 fetchJoin()을 수행할 수 없다 --> exception
                .where(QMember.member.username.eq("member1"))
                .fetchOne();


        boolean isLoaded = factory.getPersistenceUnitUtil().isLoaded(member.getTeam());
        Assertions.assertThat(isLoaded).as("페치 조인이 적용됨").isTrue();
    }

    @Test
    public void subquery() {
        QMember memberSub = new QMember("sub");
        List<Member> result = queryFactory.selectFrom(QMember.member)
                .where(QMember.member.age.eq(
                        JPAExpressions.select(memberSub.age.max())
                                .from(memberSub))
                )
                .fetch();

        Assertions.assertThat(result).extracting("age").containsExactly(40);
    }

    @Test
    public void subqueryGreaterOrEqual() {
        QMember memberSub = new QMember("sub");

        List<Member> result = queryFactory.selectFrom(QMember.member)
                .where(QMember.member.age.goe(
                                JPAExpressions.select(memberSub.age.avg())
                                        .from(memberSub)
                        )
                )
                .fetch();

        Assertions.assertThat(result).extracting("age").containsExactly(30, 40);
    }

    @Test
    public void subqueryIn() {
        QMember memberSub = new QMember("sub");

        List<Member> result = queryFactory.selectFrom(QMember.member)
                .where(QMember.member.age.in(
                                JPAExpressions.select(memberSub.age)
                                        .from(memberSub)
                                        .where(memberSub.age.gt(10))
                        )
                )
                .fetch();

        Assertions.assertThat(result).extracting("age").containsExactly(20, 30, 40);
    }

    @Test
    public void subqueryInSelect() {
        QMember memberSub = new QMember("sub");
        List<Tuple> result = queryFactory.select(QMember.member.username, JPAExpressions
                        .select(memberSub.age.avg())
                        .from(memberSub)
                )
                .from(QMember.member)
                .fetch();

        result.forEach(tuple -> {
            System.out.println("username : " + tuple.get(QMember.member.username));
            System.out.println("ageAvg : " + tuple.get(
                    JPAExpressions
                            .select(memberSub.age.avg())
                            .from(memberSub))
            );
        });
    }

    // 순수 JPA에서 DTO로 조회 결과 받기
    @Test
    public void jpqlWithDto() {
        List<MemberDto> result = manager.createQuery("select new com.joojeongyong.simplequerydsl.dto.MemberDto(m.username, m.age) " +
                        "from Member m", MemberDto.class)
                .getResultList();
        result.forEach(System.out::println);
        Assertions.assertThat(result).isNotNull();
    }

    // Dto projection

    // 프로퍼티 접근
    @Test
    public void setterProjection() {
        List<MemberDto> result = queryFactory.select(
                        Projections.bean(
                                MemberDto.class,
                                QMember.member.username,
                                QMember.member.age
                        )
                )
                .from(QMember.member)
                .fetch();
        result.forEach(System.out::println);
        Assertions.assertThat(result).isNotNull();
    }

    // 필드 직접 접근
    @Test
    public void fieldProjection() {
        List<MemberDto> result = queryFactory.select(
                        Projections.fields(
                                MemberDto.class,
                                QMember.member.username,
                                QMember.member.age
                        )
                )
                .from(QMember.member)
                .fetch();
        result.forEach(System.out::println);
        Assertions.assertThat(result).isNotNull();
    }

    // 별칭으로 받기
    @Test
    public void fieldWithAliasProjection() {
        List<UserDto> result = queryFactory.select(
                        Projections.fields(
                                UserDto.class,
                                QMember.member.username.as("name"),
                                QMember.member.age
                        )
                )
                .from(QMember.member)
                .fetch();
        result.forEach(System.out::println);
        Assertions.assertThat(result).isNotNull();
    }

    // 생성자 접근
    @Test
    public void constructorProjection() {
        List<MemberDto> result = queryFactory
                .select(Projections.constructor(
                        MemberDto.class,
                        QMember.member.username,
                        QMember.member.username,
                        QMember.member.age
                ))
                .from(QMember.member)
                .fetch();

        result.forEach(System.out::println);
        Assertions.assertThat(result).isNotNull();
    }

    //결과를 담을 DTO 생성자에 @QueryProjection 사용
    // DTO도 QType이 생성되어야 한다
    @Test
    public void usingQueryProjection() {
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(QMember.member.username, QMember.member.age))
                .from(QMember.member)
                .fetch();

        result.forEach(System.out::println);
        Assertions.assertThat(result).isNotNull();
    }

    // BooleanBuilder를 이용한 다중 조건 검색
    @Test
    public void dynamicQueryBooleanBuilder() {
        String username = "member01";
        Integer age = 10;

        List<Member> result = searchMemberUsingBooleanBuilder(username, age);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMemberUsingBooleanBuilder(String username, Integer age) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (Objects.nonNull(username)) {
            booleanBuilder.and(QMember.member.username.eq(username));
        }

        if (Objects.nonNull(age)) {
            booleanBuilder.and(QMember.member.age.eq(age));
        }

        return queryFactory.selectFrom(QMember.member)
                .where(booleanBuilder)
                .fetch();
    }

    // BooleanExpression을 반환하는 method를 이용한 다중 조건 검색
    @Test
    public void dynamicQueryWithWhereParameter() {
        String username = "member01";
        Integer age = 10;

        List<Member> result = searchMemberUsingWhereParam(username, age);
    }

    private List<Member> searchMemberUsingWhereParam(String username, Integer age) {
        return queryFactory.selectFrom(QMember.member)
                .where(this.usernameEq(username), this.ageEq(age))
                .fetch();

    }

    private BooleanExpression usernameEq(String username) {
        return Objects.nonNull(username) ? QMember.member.username.eq(username) : null;
    }

    private BooleanExpression ageEq(Integer age) {
        return Objects.nonNull(age) ? QMember.member.age.eq(age) : null;
    }
}
