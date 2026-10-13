package com.xt.interview.controller;

import com.xt.interview.entity.Account;
import com.xt.interview.repository.AccountRepository;
import com.xt.interview.util.JwtUtil;
import com.xt.interview.util.MD5Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private JwtUtil jwtUtil;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController();
        ReflectionTestUtils.setField(controller, "accountRepository", accountRepository);
        ReflectionTestUtils.setField(controller, "jwtUtil", jwtUtil);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void loginRejectsMissingCredentials() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"username":"","password":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名不能为空"));

        verifyNoInteractions(accountRepository);
    }

    @Test
    void loginReturnsTokenForValidEnabledAccount() throws Exception {
        Account account = new Account();
        account.setId(1L);
        account.setUsername("alice");
        account.setName("Alice");
        account.setPassword(MD5Util.encode("secret"));
        account.setRoleLevel(1);
        account.setStatus(1);
        when(accountRepository.findByUsernameAndDeleted("alice", 0)).thenReturn(Optional.of(account));
        when(jwtUtil.generateToken(1L, "alice", 1)).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"username":"alice","password":"secret"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.user.username").value("alice"));
    }

    @Test
    void validateReturnsFalseWhenAuthorizationHeaderIsMissing() throws Exception {
        mockMvc.perform(get("/api/auth/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("未提供token"));
    }
}
