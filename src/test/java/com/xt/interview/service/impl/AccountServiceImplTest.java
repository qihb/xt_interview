package com.xt.interview.service.impl;

import com.xt.interview.dto.AccountDTO;
import com.xt.interview.entity.Account;
import com.xt.interview.repository.AccountRepository;
import com.xt.interview.util.MD5Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl service;

    @Test
    void createAccountUsesDefaultPasswordAndInitializesSoftDelete() {
        AccountDTO input = account("alice");
        input.setPassword(null);
        Account saved = accountEntity(1L, "alice");
        when(accountRepository.save(any(Account.class))).thenReturn(saved);

        AccountDTO result = service.createAccount(input);

        var captor = org.mockito.ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertEquals(MD5Util.encode("123456"), captor.getValue().getPassword());
        assertEquals(0, captor.getValue().getDeleted());
        assertEquals("alice", result.getUsername());
    }

    @Test
    void createAccountRejectsDuplicateUsername() {
        when(accountRepository.existsByUsernameAndDeleted("alice", 0)).thenReturn(true);

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> service.createAccount(account("alice")));

        assertEquals("用户名已存在", error.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void updateAccountOnlyChangesPasswordWhenProvided() {
        Account existing = accountEntity(1L, "alice");
        existing.setPassword("old-hash");
        when(accountRepository.findByIdAndDeleted(1L, 0)).thenReturn(Optional.of(existing));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountDTO input = account("alice");
        input.setPassword("");
        service.updateAccount(1L, input);

        assertEquals("old-hash", existing.getPassword());
        verify(accountRepository).save(existing);
    }

    @Test
    void deleteAccountMarksAccountAsDeleted() {
        Account existing = accountEntity(1L, "alice");
        when(accountRepository.findByIdAndDeleted(1L, 0)).thenReturn(Optional.of(existing));

        service.deleteAccount(1L);

        assertEquals(1, existing.getDeleted());
        verify(accountRepository).save(existing);
    }

    @Test
    void loginRejectsDisabledAccount() {
        Account existing = accountEntity(1L, "alice");
        existing.setStatus(0);
        when(accountRepository.findByUsernameAndPasswordAndDeleted(
                "alice", MD5Util.encode("secret"), 0)).thenReturn(Optional.of(existing));

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> service.login("alice", "secret"));

        assertEquals("账号已禁用", error.getMessage());
    }

    private AccountDTO account(String username) {
        AccountDTO dto = new AccountDTO();
        dto.setUsername(username);
        dto.setName("Alice");
        dto.setPassword("secret");
        dto.setRoleLevel(1);
        dto.setStatus(1);
        dto.setIsSuper(0);
        return dto;
    }

    private Account accountEntity(Long id, String username) {
        Account account = new Account();
        account.setId(id);
        account.setUsername(username);
        account.setName("Alice");
        account.setStatus(1);
        account.setRoleLevel(1);
        account.setIsSuper(0);
        account.setDeleted(0);
        return account;
    }
}
