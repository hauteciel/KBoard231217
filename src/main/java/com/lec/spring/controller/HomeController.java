package com.lec.spring.controller;

import com.lec.spring.config.PrincipalDetails;
import com.lec.spring.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/")
public class HomeController {
    public HomeController() {
        System.out.println(getClass().getName() + "() 생성");
    }

    @RequestMapping("/")
    public String home(Model model){
        return "redirect:/home";
    }

    // /home
    @RequestMapping("/home")
    public void home() {}

    //-------------------------------------------------------------------------
    // 현재 Authentication 보기 (디버깅 등 용도로 활용)
    @RequestMapping("/auth")
    @ResponseBody
    public Authentication auth() { // org.springframework.security.core.Authentication
        return SecurityContextHolder.getContext().getAuthentication();
    }

    // 매개변수에 Authentication 을 명시해도 주입된다.  (인증후에만 가능)
    @RequestMapping("/userDetails")
    @ResponseBody
    public PrincipalDetails userDetails(Authentication authentication){
        // getPrincipal() 은 Object 를 리턴한다.
        return (PrincipalDetails) authentication.getPrincipal();
    }

    // @AuthenticationPrincipal 을 사용하여 로그인한 사용자 정보 주입받을수 있다.
    // org.springframework.security.core.annotation.AuthenticationPrincipal
    @RequestMapping("/user")
    @ResponseBody
    public User username(@AuthenticationPrincipal PrincipalDetails userDetails){
        return (userDetails != null) ? userDetails.getUser() : null;
    }



    @Autowired
    private PasswordEncoder passwordEncoder;

    // 암호화 객체 (PasswordEncoder) 동작 확인
    @RequestMapping("test1")
    @ResponseBody
    public String test1(@RequestParam(defaultValue = "1234") String password){
        return password + " => " + passwordEncoder.encode(password);
        // ↑ 여러번 시도해보면 할때마다 암호화된 결과 다르다.
    }


    // OAuth2 Client 를 사용하여 로그인 경우.
    // Principal 객체는 OAuth2User 타입으로 받아올수도 있다.
    // AuthenticatedPrincipal(I)
    //  └─ OAuth2AuthenticatedPrincipal(I)
    //       └─ OAuth2User (I)
    @RequestMapping("/oauth2")
    @ResponseBody
    public OAuth2User oauth2(Authentication authentication){
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        return oAuth2User;
    }

    @RequestMapping("/oauth2user")
    @ResponseBody
    public Map<String, Object> oauth2user(@AuthenticationPrincipal OAuth2User oAuth2User){
        return (oAuth2User != null) ? oAuth2User.getAttributes() : null;
    }

} // end Controller
