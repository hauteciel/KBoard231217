package com.lec.spring.repository;

// Repository layer
// DataSource (DB) 등에 대한 직접적인 접근

import com.lec.spring.domain.Post;

import java.util.List;


public interface PostRepository {

    // 새글 작성 <- Write
    int save(Post post);

    // 특정 id 글 내용 읽기
    Post findById(long id);

    // 특정 id 글 조회수 +1 증가
    int incViewCnt(long id);

    // 전체 글 목록 : 최신순
    List<Post> findAll();

    // 특정 id 글 수정 (제목, 내용)
    int update(Post post);

    // 특정 id 글 삭제하기
    int delete(Post post);

    // 페이징
    // from 부터 rows 개 만큼 select
    List<Post> selectFromRow(int from, int rows);

    // 전체 글의 개수
    int countAll();

}













