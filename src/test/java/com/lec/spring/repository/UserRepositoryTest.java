package com.lec.spring.repository;

import com.lec.spring.domain.Authority;
import com.lec.spring.domain.User;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private SqlSession sqlSession;

    @Test
    void test1(){
        UserRepository userRepository = sqlSession.getMapper(UserRepository.class);
        AuthorityRepository authorityRepository = sqlSession.getMapper(AuthorityRepository.class);
        User user = userRepository.findById(1L);
        System.out.println("findById(): " + user);
        List<Authority> list =authorityRepository.findByUser(user);
        System.out.println("권한들: " + list);

        user = userRepository.findByUsername("ADMIN1");
        System.out.println("findByUsername(): " + user);
        list =authorityRepository.findByUser(user);
        System.out.println("권한들: " + list);

    }

}












