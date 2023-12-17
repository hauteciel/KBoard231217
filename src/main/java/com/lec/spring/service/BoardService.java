package com.lec.spring.service;

import com.lec.spring.domain.Post;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface BoardService {

    // 글 작성    
    int write(Post post
            , Map<String, MultipartFile> files   // 첨부 파일들.
    );

    // 특정 id 의 글 조회
    // 트랜잭션 처리
    // 1. 조회수 증가 (UPDATE)
    // 2. 글 읽어오기 (SELECT)
    @Transactional
    Post detail(long id);

    // 특정 id 의 글 읽어오기
    // 조회수 증가 없음
    public Post selectById(long id);
    
    List<Post> list();

    // 페이징 리스트
    List<Post> list(Integer page, Model model);

    int update(Post post
            , Map<String, MultipartFile> files   // 새로 추가된 첨부파일들
            , Long[] delfile) // end update
    ;

    // 특정 글 (id) 삭제
    int deleteById(long id);
}
