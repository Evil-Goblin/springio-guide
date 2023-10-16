## SpringSecurity 적용법의 변경
- `WebSecurityConfigurerAdapter` 의 `configure(HttpSecurity)` 를 구현하는 방법에서 `SecurityFilterChain` `Bean`을 등록하는 방법으로 변경되었다.

## SecurityFilterChain Bean 등록
```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/", "home").permitAll() // 1
                        .anyRequest().authenticated() // 2
                )
                .formLogin(form -> form // 3
                        .loginPage("/login")
                        .permitAll()
                )
                .logout(LogoutConfigurer::permitAll);

        return http.build();
    }
}
```
- `1` `requestMatchers` 에 등록한 `url`에 대해서 `permitAll` 모두 허용한다.
- `2` `anyRequest` 다른 모든 요청에 대해서 `authenticated` 인증을 요구한다.
- `3` 권한이 없는 페이지요청시 `Login` 페이지로 `redirect`된다. (`LoginPage`의 권한은 `permitAll` 모두 허용인다.)

## UserDetails
- 일종의 회원 정보와 같은 로그인 인증 정보이다.
- `UserDetailsService` 는 `UserDetails` 를 관리하는 역할을 한다.

## UserDetailsService
- `public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException` 메소드를 정의하는 인터페이스이다.
- `username`, `password`를 이용해 인증 요청을 하는 경우 `DaoAuthenticationProvider` 에 의해 `loadUserByUsername` 이 호출되어 `UserDetails` 를 반환한다.
- 대표적인 구현체로 `InMemoryUserDetailsManager` , `JdbcUserDetailsManager` 가 있다.
```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.
                withUsername("user")
                .passwordEncoder(password -> passwordEncoder().encode(password))
                .password("password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```
- `withDefaultPasswordEncoder` 메소드가 `Deprecated` 됨에 따라 위와 같이 `PasswordEncoder` 를 `Bean` 으로 등록하여 비밀번호를 `Encode` 한다.

## AuthenticationManager 의 동작 그림
![AuthenticationManager](https://docs.spring.io/spring-security/reference/_images/servlet/authentication/unpwd/daoauthenticationprovider.png)

## DaoAuthenticationProvider 의 loadUserByUsername 호출
![DaoAuthenticationProvider](https://github.com/Evil-Goblin/spring-lecture/assets/74400861/e8033983-d9ad-4703-8996-05c6422cc52f)

## SpringSecurity Test
```java
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
```
- 작성한 샘플 코드가 `@Controller` 를 이용하지 않고 `ViewControllerRegistry` 에 직접 등록하는 방식을 사용했기 때문에 `Controller` 를 가져오는 `WebMvcTest` 는 사용할 수 없었다.
- 이에 `SpringBootTest` 를 이용해 테스트하게 되었다.
- `RestController` 가 아닌 `view` 를 리턴하는 구조이기 때문에 `401 Unauthorized` 상태코드에 대한 테스트는 작성할 수 없었다. (권한이 없는 경우 `LoginPage` 로 `redirect` 되기 때문)
- 권한이 없어서 `redirect` 가 되는 경우와 권한이 있어서 요청에 성공하는 경우의 테스트를 작성하였다.
- `WithMockUser` 를 이용해 `User` 의 설정한 권한이 있다고 가정하여 테스트한다.
