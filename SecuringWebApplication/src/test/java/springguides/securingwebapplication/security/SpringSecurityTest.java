package springguides.securingwebapplication.security;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class SpringSecurityTest {

    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    @DisplayName("'/' 경로는 권한여부 필요없이 home 을 불러온다.")
    void homePageWithoutLoginTest() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    @DisplayName("csrf 토큰은 request 객체의 _csrf 필드에 담겨서 리턴된다.")
    void returnCsrfTokenTest() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(request().attribute("_csrf", new Matcher<Object>() {
                    @Override
                    public void describeTo(Description description) {

                    }

                    @Override
                    public boolean matches(Object actual) {
                        System.out.println("actual = " + actual);
                        return true;
                    }

                    @Override
                    public void describeMismatch(Object actual, Description mismatchDescription) {

                    }

                    @Override
                    public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {

                    }
                }))
                .andDo(print());
    }

    @Test
    @DisplayName("'/hello' 경로는 로그인 권한이 필요하여 login 페이지로 리다이렉트 된다..")
    void helloWithoutLoginTest() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(value = "user", roles = {"USER"})
    @DisplayName("권한이 있는 사용자가 '/hello' 경로를 요청하면 정상적인 응답을 돌려준다.")
    void helloWithLoginTest() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(view().name("hello"));
    }
}
