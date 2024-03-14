package com.drrr.auth;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.drrr.auth.payload.response.AccessTokenResponse;
import com.drrr.auth.payload.response.SignInResponse;
import com.drrr.auth.payload.response.SignUpResponse;
import com.drrr.domain.category.entity.Category;
import com.drrr.domain.category.repository.CategoryRepository;
import com.drrr.domain.email.entity.Email;
import com.drrr.domain.email.repository.EmailRepository;
import com.drrr.domain.member.entity.Member;
import com.drrr.domain.member.repository.MemberRepository;
import com.drrr.domain.util.DatabaseCleaner;
import com.drrr.web.jwt.util.JwtProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthE2ETest {
    @LocalServerPort
    int port;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private EmailRepository emailRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private DatabaseCleaner databaseCleaner;

    private final int CATEGORY_COUNT = 3;


    @BeforeEach
    private void setup() {
        RestAssured.port = port;
        databaseCleaner.clear();

        String email = "user@example.com";
        String nickname = "user";
        String provider = "provider";
        String providerId = "1324";
        String imageUrl = "http://example.com/image.jpg";
        Member member = Member.createMember(email, nickname, provider, providerId, imageUrl);
        memberRepository.save(member);

        List<Category> categories = IntStream.rangeClosed(1, CATEGORY_COUNT).mapToObj(i -> {
            String categoryDisplayName = "Display Category" + i;
            return Category.builder()
                    .name(categoryDisplayName)
                    .build();
        }).collect(Collectors.toList());

        categoryRepository.saveAll(categories);

        Email verificationEmail = Email.builder()
                .isVerified(false)
                .providerId("1324")
                .verificationCode("1234")
                .email("drrr@drrr.com")
                .build();
        emailRepository.save(verificationEmail);
    }

    @Test
    public void 회원가입이_잘_작동합니다() throws JsonProcessingException {
        //when
        ObjectMapper objectMapper = new ObjectMapper();
        SignUpResponse response =
                objectMapper.readValue(
                        given()
                                .log().all()
                                .when()
                                .contentType(ContentType.APPLICATION_JSON.toString())
                                .body("""
                                               {
                                                   "email": "drrr@drrr.com",
                                                   "nickname" : "drrr",
                                                   "provider" : "kakao",
                                                   "providerId" : 123456,
                                                   "profileImageUrl" : "test-image",
                                                   "categoryIds" : [1, 2, 3]
                                               }
                                        """)
                                .post("/api/v1/auth/signup")
                                .then()
                                .statusCode(HttpStatus.OK.value())
                                .extract().body().asString(), new TypeReference<SignUpResponse>() {
                        });
        //then
        Optional<Member> memberOptional = memberRepository.findByProviderId("123456");
        Member member = memberOptional.get();
        Long tokenMemberId = jwtProvider.extractToValueFrom(response.accessToken());
        List<Category> categories = categoryRepository.findAllById(List.of(1L, 2L, 3L));

        assertThat(categories.stream().map(category -> category.getId()).toList()).containsExactly(1L, 2L, 3L);
        assertThat(member.getId()).isEqualTo(tokenMemberId);
        assertThat(memberOptional).isPresent();
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isNotNull();
        assertThat(response.refreshToken()).isNotNull();
    }

    @Test
    public void 로그인이_잘_작동합니다() throws JsonProcessingException {
        //when
        ObjectMapper objectMapper = new ObjectMapper();
        SignInResponse response =
                objectMapper.readValue(
                        given()
                                .log().all()
                                .when()
                                .contentType(ContentType.APPLICATION_JSON.toString())
                                .body("""
                                               {
                                                   "providerId" : 1324
                                               }
                                        """)
                                .post("/api/v1/auth/signin")
                                .then()
                                .statusCode(HttpStatus.OK.value())
                                .extract().body().asString(), new TypeReference<SignInResponse>() {
                        });
        //then
        Optional<Member> memberOptional = memberRepository.findByProviderId("1324");
        Member member = memberOptional.get();
        Long tokenMemberId = jwtProvider.extractToValueFrom(response.accessToken());
        List<Category> categories = categoryRepository.findAllById(List.of(1L, 2L, 3L));

        assertThat(categories.stream().map(category -> category.getId()).toList()).containsExactly(1L, 2L, 3L);
        assertThat(member.getId()).isEqualTo(tokenMemberId);
        assertThat(memberOptional).isPresent();
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isNotNull();
        assertThat(response.refreshToken()).isNotNull();
    }

    @Test
    public void 토큰이_재발급됩니다() throws JsonProcessingException {
        //given
        String accessToken = jwtProvider.createAccessToken(1L, LocalDateTime.now().toInstant(ZoneOffset.UTC));
        String refreshToken = jwtProvider.createRefreshToken(1L, LocalDateTime.now().toInstant(ZoneOffset.UTC));

        //when
        ObjectMapper objectMapper = new ObjectMapper();
        AccessTokenResponse response =
                objectMapper.readValue(
                        given()
                                .log().all()
                                .when()
                                .contentType(ContentType.APPLICATION_JSON.toString())
                                .header("Authorization", "Bearer " + accessToken)
                                .body("""
                                        {
                                            "accessToken" : """ + "\"" + accessToken + "\"" + """
                                        ,
                                            "refreshToken" : """ + "\"" + refreshToken + "\"" + """
                                               }
                                        """)
                                .post("/api/v1/auth/access-token")
                                .then()
                                .statusCode(HttpStatus.OK.value())
                                .extract().body().asString(), new TypeReference<AccessTokenResponse>() {
                        });
        //then
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isNotNull();
    }

    @Test
    public void 회원이_정상적으로_탈퇴됩니다() {
        //given
        String accessToken = jwtProvider.createAccessToken(1L, LocalDateTime.now().toInstant(ZoneOffset.UTC));

        //when
        given()
                .log().all()
                .when()
                .contentType(ContentType.APPLICATION_JSON.toString())
                .header("Authorization", "Bearer " + accessToken)
                .delete("/api/v1/auth/members/me/deletion")
                .then()
                .statusCode(HttpStatus.OK.value());
        //then
        Optional<Member> memberOptional = memberRepository.findByProviderId("1324");
        assertThat(memberOptional).isPresent();
        assertThat(memberOptional.get().isActive()).isFalse();
    }

}
