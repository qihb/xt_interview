package com.xt.interview.service;

import com.xt.interview.dto.AccountDTO;
import com.xt.interview.dto.PageResponse;

import java.util.List;

public interface AccountService {

    AccountDTO createAccount(AccountDTO dto);

    AccountDTO updateAccount(Long id, AccountDTO dto);

    void deleteAccount(Long id);

    AccountDTO getAccountById(Long id);

    List<AccountDTO> getAllAccounts();

    PageResponse<AccountDTO> pageAccounts(
            String username, Integer status, Integer roleLevel, Long departmentId, Integer page, Integer size);

    AccountDTO login(String username, String password);
}
