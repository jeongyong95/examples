package com.joojeongyong.simplequerydsl;

import com.joojeongyong.simplequerydsl.entity.Hello;
import com.joojeongyong.simplequerydsl.entity.QHello;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

// 이거 해야 커밋함
@Commit
// 트랜잭셔널만 있으면 기본이 롤백
@Transactional
@SpringBootTest
class SimpleQuerydslApplicationTests {

    @PersistenceContext
    EntityManager manager;

    @Test
    void contextLoads() {
        Hello hello = new Hello();
        manager.persist(hello);

        JPAQueryFactory queryFactory = new JPAQueryFactory(manager);
        QHello qHello = QHello.hello;

        Hello result = queryFactory
                .selectFrom(qHello)
                .fetchOne();

        Assertions.assertThat(result).isEqualTo(hello);
        Assertions.assertThat(result.getId()).isEqualTo(hello.getId());
    }

}
