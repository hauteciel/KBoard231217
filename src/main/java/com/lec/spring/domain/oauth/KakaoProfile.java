package com.lec.spring.domain.oauth;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoProfile {

    public Long id;
    @JsonProperty("connected_at")
    public String connectedAt;
    public Properties properties;
    @JsonProperty("kakao_account")
    public KakaoAccount kakaoAccount;

    @Data
    public class Properties {
        public String nickname;
    } // end Properties

    @Data
    public class KakaoAccount {

        @JsonProperty("profile_nickname_needs_agreement")
        public Boolean profileNicknameNeedsAgreement;
        public Profile profile;
        @JsonProperty("has_email")
        public Boolean hasEmail;
        @JsonProperty("email_needs_agreement")
        public Boolean emailNeedsAgreement;
        @JsonProperty("is_email_valid")
        public Boolean isEmailValid;
        @JsonProperty("is_email_verified")
        public Boolean isEmailVerified;
        public String email;

        @Data
        public class Profile {
            public String nickname;
        } // end Profile
    } // end KakaoAccount
} // end KakaoProfile


