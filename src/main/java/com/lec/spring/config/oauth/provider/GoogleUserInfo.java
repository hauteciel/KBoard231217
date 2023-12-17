package com.lec.spring.config.oauth.provider;

import java.util.Map;

/*  Google 의 attributes
    {
      "sub": "11628888954xxxxxxxxx",
      "name": "Sam Coding",
      "given_name": "Sam",
      "family_name": "Coding",
      "picture": "https://lh3.googleusercontent.com/......",
      "email": "fteam73@gmail.com",
      "email_verified": true,
      "locale": "ko"
    }
 */
public class GoogleUserInfo implements OAuth2UserInfo{

    // ↓ loadUser() 로 받아온 OAuth2User.getAttributes() 결과를 담을거다
    private Map<String, Object> attributes;

    public GoogleUserInfo(Map<String, Object> attributes){
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return (String)attributes.get("sub");
    }

    @Override
    public String getEmail() {
        return (String)attributes.get("email");
    }

    @Override
    public String getName() {
        return (String)attributes.get("name");
    }
} // end GoogleUserInfo
