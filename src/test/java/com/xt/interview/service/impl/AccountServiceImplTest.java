package com.xt.interview.service.impl;

import com.xt.interview.dto.AccountDTO;
import com.xt.interview.entity.Account;
import com.xt.interview.entity.Department;
import com.xt.interview.repository.AccountRepository;
import com.xt.interview.repository.DepartmentRepository;
import com.xt.interview.util.MD5Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private DepartmentRepository departmentRepository;

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
        when(accountRepository.countByUsernameAndDeleted("alice", 0)).thenReturn(1L);

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> service.createAccount(account("alice")));

        assertEquals("用户名已存在", error.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void createAccountAllowsUsernameUsedByDeletedAccount() {
        when(accountRepository.countByUsernameAndDeleted("alice", 0)).thenReturn(0L);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(2L);
            return account;
        });

        AccountDTO result = service.createAccount(account("alice"));

        assertEquals("alice", result.getUsername());
        verify(accountRepository).save(any(Account.class));
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
    void updateAccountTreatsUsernameCaseChangeAsDifferentUsername() {
        Account existing = accountEntity(1L, "alice");
        existing.setTokenVersion(0);
        when(accountRepository.findByIdAndDeleted(1L, 0)).thenReturn(Optional.of(existing));
        when(accountRepository.countByUsernameAndDeleted("ALICE", 0)).thenReturn(0L);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountDTO input = account("ALICE");
        service.updateAccount(1L, input);

        verify(accountRepository).countByUsernameAndDeleted("ALICE", 0);
        assertEquals("ALICE", existing.getUsername());
        assertEquals(1, existing.getTokenVersion());
    }

    @Test
    void updateAccountIncrementsTokenVersionWhenUsernameChanges() {
        Account existing = accountEntity(1L, "alice");
        existing.setTokenVersion(2);
        when(accountRepository.findByIdAndDeleted(1L, 0)).thenReturn(Optional.of(existing));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountDTO input = account("bob");
        service.updateAccount(1L, input);

        assertEquals("bob", existing.getUsername());
        assertEquals(3, existing.getTokenVersion());
    }

    @Test
    void createAccountStoresDepartmentAndReturnsDepartmentName() {
        Department department = departmentEntity(7L, "研发部", 1);
        when(departmentRepository.findByIdAndDeleted(7L, 0)).thenReturn(Optional.of(department));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(1L);
            return account;
        });

        AccountDTO input = account("alice");
        input.setDepartmentId(7L);

        AccountDTO result = service.createAccount(input);

        var captor = org.mockito.ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        assertEquals(7L, captor.getValue().getDepartment().getId());
        assertEquals(7L, result.getDepartmentId());
        assertEquals("研发部", result.getDepartmentName());
    }

    @Test
    void updateAccountCanClearDepartment() {
        Account existing = accountEntity(1L, "alice");
        existing.setDepartment(departmentEntity(7L, "研发部", 1));
        when(accountRepository.findByIdAndDeleted(1L, 0)).thenReturn(Optional.of(existing));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountDTO input = account("alice");
        input.setDepartmentId(null);
        service.updateAccount(1L, input);

        assertNull(existing.getDepartment());
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

    @Test
    void pageAccountsUsesZeroBasedDefaultsAndMapsPageMetadata() {
        Account alice = accountEntity(1L, "alice");
        when(accountRepository.findPage(null, null, null, null, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(alice), PageRequest.of(0, 10), 21));

        var result = service.pageAccounts(null, null, null, null, null, null);

        assertEquals(List.of("alice"), result.getList().stream().map(AccountDTO::getUsername).toList());
        assertEquals(21, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertEquals(0, result.getCurrentPage());
        assertEquals(10, result.getPageSize());
    }

    @Test
    void pageAccountsPassesCombinedFiltersAndExcludesDeletedAccounts() {
        when(accountRepository.findPage("ali", 1, 2, null, PageRequest.of(1, 5)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(1, 5), 0));

        service.pageAccounts("ali", 1, 2, null, 1, 5);

        verify(accountRepository).findPage("ali", 1, 2, null, PageRequest.of(1, 5));
    }

    @Test
    void pageAccountsPassesDepartmentFilter() {
        when(accountRepository.findPage("ali", null, null, 7L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        service.pageAccounts("ali", null, null, 7L, 0, 10);

        verify(accountRepository).findPage("ali", null, null, 7L, PageRequest.of(0, 10));
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

    private Department departmentEntity(Long id, String name, Integer status) {
        Department department = new Department();
        department.setId(id);
        department.setName(name);
        department.setCode("D" + id);
        department.setStatus(status);
        department.setDeleted(0);
        return department;
    }
}
