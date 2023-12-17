package com.lec.spring.config;

import com.lec.spring.domain.Authority;
import com.lec.spring.domain.User;
import com.lec.spring.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

//시큐리티가 /user/login (POST) 주소요청이 오면 낚아채서 로그인을 진행시킨다.
//로그인(인증) 진행이 완료되면 '시큐리티 session' 에 넣어주게 된다.
//우리가 익히 알고 있는 같은 session 공간이긴 한데..
//시큐리티가 자신이 사용하기 위한 공간을 가집니다.
//=> Security ContextHolder 라는 키값에다가 session 정보를 저장합니다.
//여기에 들어갈수 있는 객체는 Authentication 객체이어야 한다.
//Authentication 안에 User 정보가 있어야 됨.
//User 정보 객체는 ==> UserDetails 타입 객체이어야 한다.

//따라서 로그인한 User 정보를 꺼내려면
//Security Session 에서
//   => Authentication 객체를 꺼내고, 그 안에서
//        => UserDetails 정보를 꺼내면 된다.

public class PrincipalDetails implements UserDetails
                                        , OAuth2User  // ← OAuth2 Client 사용시
{

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    // 로그인한 사용자 정보 추출
    private User user;

    public User getUser() {
        return user;
    }

    // 일반 로그인 용 생성자
    public PrincipalDetails(User user){
        System.out.println("UserDetails(user) 생성: " + user);
        this.user = user;
    }
    // OAuth 로그인 용 생성자
    public PrincipalDetails(User user, Map<String, Object> attributes){
        System.out.println("""
            UserDetails(user, oauth attributes) 생성:
                user: %s
                attributes: %s 
            """.formatted(user, attributes));
        this.user = user;
        this.attributes = attributes;
    }


    // 해당 User 의 '권한(들)'을 리턴
    // 현재 로그인한 사용자의 권한정보가 필요할때마다 호출된다. 혹은 필요할때마다 직접 호출해 사용할수도 있다

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        System.out.println("getAuthorities() 호출");

        Collection<GrantedAuthority> collect = new ArrayList<>();

        List<Authority> list = userService.selectAuthoritiesById(user.getId());  // DB 에서 user 의 권한(들) 읽어오기

        for(Authority auth : list){
            collect.add(new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return auth.getName();
                }

                // thymeleaf 등에서 활용할 문자열  (학습목적)
                @Override
                public String toString() {
                    return auth.getName();
                }
            });
        }

        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 계정이 만료되지 않았는지?
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠긴건 아닌지?
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 계정 credential 이 만료 된건 아닌지?
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 활성화 되었는지?
    @Override
    public boolean isEnabled() {
        return true;
        // ex)
        // 사이트에서 1년동안 회원이 로그인을 안하면 휴면계정으로 하기로 했다면?
        // 현재시간 - 로그인시간 => 1년을 초과하면 false

    }

    // ----------------------------------------------------------------
    // OAuth2 Client 사용

    private Map<String, Object> attributes;   // ← OAuth2User 의 getAttributes() 값

    // OAuth2User 를 implement 하게 되면 구현할 메소드들
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        //return (String)attributes.get("sub");
        return null;  // 사용하지 않을 예정이라 null 리턴
    }
} // end PrincipalDetails
