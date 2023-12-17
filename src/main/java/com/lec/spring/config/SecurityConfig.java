package com.lec.spring.config;

// WebSecurityConfigurerAdapter
// deprecated 공식 : https://docs.spring.io/spring-security/site/docs/5.7.0-M2/api/org/springframework/security/config/annotation/web/configuration/WebSecurityConfigurerAdapter.html
//    ↑ 읽어보면  WebSecurityConfigurerAdapter가 Deprecated 되었으니 SecurityFilterChain를 Bean으로 등록해서 사용하라는 말.
// 대안 공식문서 참조 : https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter

//  Spring Security 6 에선
// authorizeRequests() 는 deprecated 되고
// antMathers(), mvcMathcers(), regexMatchers() @EnableGlobalMethodSecurity 들은  없어졌다?
// https://stackoverflow.com/questions/74683225/updating-to-spring-security-6-0-replacing-removed-and-deprecated-functionality

// What's new Sprint Security 6
// https://docs.spring.io/spring-security/reference/whats-new.html

import com.lec.spring.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // ↓ 이건 MvcConfiguration 으로 옮기자 ★ 이유: OAuth2 등에서 Service 를 DI 받을때 순환참조 발생.
    // PasswordEncoder 를 bean 으로 IoC 에 등록
    // IoC 에 등록된다, IoC 내에선 '어디서든' 가져다가 사용할수 있다.
//    @Bean
//    public PasswordEncoder encoder(){
//        System.out.println("PasswordEncoder bean 생성");
//        return new BCryptPasswordEncoder();
//    }

    
    // ↓ Security 를 동작 시키지 않기
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer(){
//        return web -> web.ignoring().anyRequest();  // 어떠한 request 도 security 가 무시함.
//    }

    // OAuth2 Client
    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;   // 이거 바람직하지 않다. 나중에는 순환 DI 때문에 에러! 발생


    // ↓ SecurityFilterChain 을 Bean 으로 등록해서 사용
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(csrf -> csrf.disable())      // CSRF 비활성화

                /**********************************************
                 * ① request URL 에 대한 접근 권한 세팅  : authorizeHttpRequests()
                 * .authorizeHttpRequests( AuthorizationManagerRequestMatcherRegistry)
                 **********************************************/
                .authorizeHttpRequests(auth -> auth   // AuthorizationManagerRequestMatcherRegistry
                        // URL 과 접근권한 세팅(들)
                        // ↓ /board/detail/** URL로 들어오는 요청은 '인증'만 필요.
                        .requestMatchers("/board/detail/**").authenticated()
                        // ↓ "/board/write/**", "/board/update/**", "/board/delete/**" URL로 들어오는 요청은 '인증' 뿐 아니라 ROLE_MEMBER 나 ROLE_ADMIN 권한을 갖고 있어야 한다. ('인가')
                        .requestMatchers("/board/write/**", "/board/update/**", "/board/delete/**").hasAnyRole("MEMBER", "ADMIN")
                        // ↓ 그 밖의 다른 요청은 모두 permit!
                        .anyRequest().permitAll()
                )

                /********************************************
                 * ② 폼 로그인 설정
                 * .formLogin(HttpSecurityFormLoginConfigurer)
                 *  form 기반 인증 페이지 활성화.
                 *  만약 .loginPage(url) 가 세팅되어 있지 않으면 '디폴트 로그인' form 페이지가 활성화 된다
                 ********************************************/
                .formLogin(form -> form
                        .loginPage("/user/login")  // 로그인 필요한 상황 발생시 매개변수의 url (로그인 폼) 으로 request 발생
                        .loginProcessingUrl("/user/login")  // "/user/login" url 로 POST request 가 들어오면 시큐리티가 낚아채서 처리, 대신 로그인을 진행해준다(인증).
                                                        // 이와 같이 하면 Controller 에서 /user/login (POST) 를 굳이 만들지 않아도 된다!
                                                        // 위 요청이 오면 자동으로 UserDetailsService 타입 빈객체의 loadUserByUsername() 가 실행되어 인증여부 확인진행 <- 이를 제공해주어야 한다.
                        .defaultSuccessUrl("/")  // '직접 /login' → /login(post) 에서 성공하면 "/" 로 이동시키기
                           // 만약 다른 특정페이지에 진입하려다 로그인 하여 성공하면 해당 페이지로 이동 (너무 편리!)

                        // 로그인 성공직후 수행할코드
                        //.successHandler(AuthenticationSuccessHandler)  // 로그인 성공후 수행할 코드.
                        .successHandler(new CustomLoginSuccessHandler("/home"))

                        // 로그인 실패하면 수행할 코드
                        // .failureHandler(AuthenticationFailureHandler)
                        .failureHandler(new CustomLoginFailureHandler())

                        //.usernameParameter()   기본 name="username" 이어햐 함.
                        //.passwordParameter()   기본 name="password" 이어야 함
                )
                /********************************************
                 * ③ 로그아웃 설정
                 * .logout(LogoutConfigurer)
                 ********************************************/
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .logoutUrl("/user/logout")       // 로그아웃 수행 url
                        //.logoutSuccessUrl("/user/login?logout")    // 로그아웃 성공후 redirect url
                        .invalidateHttpSession(false)   // session invalidate (디폴트 true)
                        // 이따가 CustomLogoutSuccessHandler 에서 꺼낼 정보가 있기 때문에
                        // false 로 세팅한다
                        // .deleteCookies("JSESSIONID")   // 쿠키 제거

                        // 로그아웃 성공후 수행할 코드
                        // .logoutSuccessHandler(LogoutSuccessHandler)
                        .logoutSuccessHandler(new CustomLogoutSuccessHandler())
                )
                /********************************************
                 * ④ 예외처리 설정
                 * .exceptionHandling(ExceptionHandlingConfigure)
                 ********************************************/
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        // 권한(Authorization) 오류 발생시 수행할 코드
                        // .accessDeniedHandler(AccessDeniedHandler)
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                )
                /********************************************
                 * OAuth2 로그인
                 * .oauth2Login(OAuth2LoginConfigurer)
                 ********************************************/
                .oauth2Login(httpSecurityOAuth2LoginConfigurer -> httpSecurityOAuth2LoginConfigurer
                                .loginPage("/user/login")  // 로그인 페이지를 동일한 url 로 지정.
                                // ↑ 구글 로그인 완료된 뒤에 후처리가 필요하다!

                                // code 를 받아오는 것이 아니라, 'AccessToken' 과 사용자 '프로필정보'를 한번에 받아온다
                                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                        // 인증서버의 UserInfo Endpoint. 설정진행
                                        .userService(principalOauth2UserService)  // userService(OAuth2UserService<OAuth2UserRequest, OAuth2User>)
                                )

                        // 아래 방식은 6.1 이후 deprecated 됨.
                        //userInfoEndpoint()  // OAuth2LoginConfigurer.UserInfoEndpointConfig 리턴
                                            // 이를 통해 인증서버의 UserInfo Endpoint. 설정진행
                        //.userService(principalOauth2UserService)  // userService(OAuth2UserService<OAuth2UserRequest, OAuth2User>)
                )

                .build();
    }


    //--------------------------------------------------------
    // OAuth 로그인
    // AuthenciationManager bean 생성
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
} // end Config












