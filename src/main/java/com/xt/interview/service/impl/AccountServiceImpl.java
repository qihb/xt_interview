package com.xt.interview.service.impl;

import com.xt.interview.dto.AccountDTO;
import com.xt.interview.dto.PageResponse;
import com.xt.interview.entity.Account;
import com.xt.interview.entity.Department;
import com.xt.interview.repository.AccountRepository;
import com.xt.interview.repository.DepartmentRepository;
import com.xt.interview.service.AccountService;
import com.xt.interview.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private static final Integer NOT_DELETED = 0;
    private static final Integer DELETED = 1;

    private static final String DEFAULT_PASSWORD = "123456";

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public AccountDTO createAccount(AccountDTO dto) {
        String username = normalizeUsername(dto.getUsername());
        if (accountRepository.countByUsernameAndDeleted(username, NOT_DELETED) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        Account account = new Account();
        account.setUsername(username);
        account.setName(dto.getName());
        account.setGender(dto.getGender());
        account.setPhone(dto.getPhone());
        String password = (dto.getPassword() == null || dto.getPassword().isEmpty())
                ? DEFAULT_PASSWORD : dto.getPassword();
        account.setPassword(MD5Util.encode(password));
        account.setRoleLevel(dto.getRoleLevel());
        account.setStatus(dto.getStatus());
        account.setIsSuper(dto.getIsSuper());
        account.setDepartment(resolveDepartment(dto.getDepartmentId()));
        account.setDeleted(NOT_DELETED);
        account = accountRepository.save(account);
        return convertToDTO(account);
    }

    @Override
    public AccountDTO updateAccount(Long id, AccountDTO dto) {
        Account account = accountRepository.findByIdAndDeleted(id, NOT_DELETED)
                .orElseThrow(() -> new RuntimeException("账号不存在"));

        String username = normalizeUsername(dto.getUsername());
        boolean usernameChanged = !account.getUsername().equals(username);
        if (usernameChanged &&
            accountRepository.countByUsernameAndDeleted(username, NOT_DELETED) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        account.setUsername(username);
        account.setName(dto.getName());
        account.setGender(dto.getGender());
        account.setPhone(dto.getPhone());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            account.setPassword(MD5Util.encode(dto.getPassword()));
        }
        account.setRoleLevel(dto.getRoleLevel());
        account.setStatus(dto.getStatus());
        account.setIsSuper(dto.getIsSuper());
        if (usernameChanged) {
            account.setTokenVersion(account.getTokenVersion() == null ? 1 : account.getTokenVersion() + 1);
        }
        account.setDepartment(resolveDepartment(dto.getDepartmentId()));
        account = accountRepository.save(account);
        return convertToDTO(account);
    }

    @Override
    public void deleteAccount(Long id) {
        Account account = accountRepository.findByIdAndDeleted(id, NOT_DELETED)
                .orElseThrow(() -> new RuntimeException("账号不存在"));
        account.setDeleted(DELETED);
        accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDTO getAccountById(Long id) {
        Account account = accountRepository.findByIdAndDeleted(id, NOT_DELETED)
                .orElseThrow(() -> new RuntimeException("账号不存在"));
        return convertToDTO(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findByDeleted(NOT_DELETED).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AccountDTO> pageAccounts(
            String username, Integer status, Integer roleLevel, Integer page, Integer size) {
        int currentPage = page == null ? 0 : page;
        int pageSize = size == null ? 10 : size;
        if (currentPage < 0) {
            throw new IllegalArgumentException("页码不能小于 0");
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new IllegalArgumentException("每页条数必须在 1-100 之间");
        }

        Page<Account> accountPage = accountRepository.findPage(
                username == null ? null : username.trim(),
                status,
                roleLevel,
                PageRequest.of(currentPage, pageSize));
        return new PageResponse<>(
                accountPage.getContent().stream().map(this::convertToDTO).collect(Collectors.toList()),
                accountPage.getTotalElements(),
                accountPage.getTotalPages(),
                accountPage.getNumber(),
                accountPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDTO login(String username, String password) {
        Account account = accountRepository.findByUsernameAndPasswordAndDeleted(
                username, MD5Util.encode(password), NOT_DELETED)
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        if (account.getStatus() != 1) {
            throw new RuntimeException("账号已禁用");
        }
        return convertToDTO(account);
    }

    private AccountDTO convertToDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setUsername(account.getUsername());
        dto.setName(account.getName());
        dto.setGender(account.getGender());
        dto.setPhone(account.getPhone());
        dto.setRoleLevel(account.getRoleLevel());
        dto.setStatus(account.getStatus());
        dto.setIsSuper(account.getIsSuper());
        if (account.getDepartment() != null) {
            dto.setDepartmentId(account.getDepartment().getId());
            dto.setDepartmentName(account.getDepartment().getName());
        }
        dto.setCreateTime(account.getCreateTime());
        dto.setUpdateTime(account.getUpdateTime());
        return dto;
    }

    private Department resolveDepartment(Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        return departmentRepository.findByIdAndDeleted(departmentId, NOT_DELETED)
                .orElseThrow(() -> new IllegalArgumentException("所属部门不存在或已删除"));
    }

    private String normalizeUsername(String username) {
        if (username == null) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        String normalized = username.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        return normalized;
    }
}
