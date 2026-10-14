package com.xt.interview.controller;

import com.xt.interview.dto.AccountDTO;
import com.xt.interview.dto.PageResponse;
import com.xt.interview.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
@org.springframework.validation.annotation.Validated
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAll() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/page")
    public ResponseEntity<PageResponse<AccountDTO>> page(
            @RequestParam(required = false) @Size(max = 50, message = "用户名长度不能超过 50 个字符") String username,
            @RequestParam(required = false) @Min(value = 0, message = "状态必须是 0 或 1") @Max(value = 1, message = "状态必须是 0 或 1") Integer status,
            @RequestParam(required = false) @Min(value = 1, message = "权限级别必须在 1-3 之间") @Max(value = 3, message = "权限级别必须在 1-3 之间") Integer roleLevel,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "页码不能小于 0") Integer page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页条数必须在 1-100 之间") @Max(value = 100, message = "每页条数必须在 1-100 之间") Integer size) {
        return ResponseEntity.ok(accountService.pageAccounts(username, status, roleLevel, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getById(@PathVariable @Positive(message = "ID 必须大于 0") Long id) {
        validateId(id);
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PostMapping
    public ResponseEntity<AccountDTO> create(@Valid @RequestBody AccountDTO dto) {
        return ResponseEntity.ok(accountService.createAccount(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> update(
            @PathVariable @Positive(message = "ID 必须大于 0") Long id,
            @Valid @RequestBody AccountDTO dto) {
        validateId(id);
        return ResponseEntity.ok(accountService.updateAccount(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive(message = "ID 必须大于 0") Long id) {
        validateId(id);
        accountService.deleteAccount(id);
        return ResponseEntity.ok().build();
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID 必须大于 0");
        }
    }
}
