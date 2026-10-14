package com.xt.interview.controller;

import com.xt.interview.dto.AccountDTO;
import com.xt.interview.dto.DepartmentDTO;
import com.xt.interview.dto.PageResponse;
import com.xt.interview.service.AccountService;
import com.xt.interview.service.DepartmentService;
import com.xt.interview.exception.DepartmentInUseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CrudControllerTest {

    @Mock
    private AccountService accountService;

    @Mock
    private DepartmentService departmentService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AccountController accountController = new AccountController();
        DepartmentController departmentController = new DepartmentController();
        ReflectionTestUtils.setField(accountController, "accountService", accountService);
        ReflectionTestUtils.setField(departmentController, "departmentService", departmentService);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(accountController, departmentController)
                .setValidator(validator)
                .setControllerAdvice(new com.xt.interview.exception.GlobalExceptionHandler())
                .build();
    }

    @Test
    void accountEndpointsDelegateToService() throws Exception {
        AccountDTO account = new AccountDTO();
        account.setId(1L);
        account.setUsername("alice");
        when(accountService.getAllAccounts()).thenReturn(List.of(account));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("alice"));

        mockMvc.perform(delete("/api/accounts/1"))
                .andExpect(status().isOk());

        verify(accountService).getAllAccounts();
        verify(accountService).deleteAccount(1L);
    }

    @Test
    void accountPageEndpointBindsFiltersAndPagination() throws Exception {
        AccountDTO account = new AccountDTO();
        account.setId(1L);
        account.setUsername("alice");
        when(accountService.pageAccounts("ali", 1, 2, 7L, 1, 5))
                .thenReturn(new PageResponse<>(List.of(account), 1, 1, 1, 5));

        mockMvc.perform(get("/api/accounts/page")
                        .param("username", "ali")
                        .param("status", "1")
                        .param("roleLevel", "2")
                        .param("departmentId", "7")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list[0].username").value("alice"))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.pageSize").value(5));

        verify(accountService).pageAccounts("ali", 1, 2, 7L, 1, 5);
    }

    @Test
    void departmentEndpointsAcceptJsonPayload() throws Exception {
        DepartmentDTO department = new DepartmentDTO();
        department.setId(2L);
        department.setName("研发部");
        department.setCode("RD");
        when(departmentService.createDepartment(any(DepartmentDTO.class))).thenReturn(department);

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"研发部","code":"RD","status":1}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("RD"));

        verify(departmentService).createDepartment(any(DepartmentDTO.class));
    }

    @Test
    void accountCreateReturnsFieldErrorsForInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"","name":"","roleLevel":9,"status":2,"isSuper":0,"departmentId":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("请求参数校验失败"))
                .andExpect(jsonPath("$.details.username").value("用户名不能为空"))
                .andExpect(jsonPath("$.details.name").value("姓名不能为空"))
                .andExpect(jsonPath("$.details.roleLevel").value("权限级别必须在 1-3 之间"))
                .andExpect(jsonPath("$.details.status").value("状态必须是 0 或 1"))
                .andExpect(jsonPath("$.details.departmentId").value("部门 ID 必须大于 0"));

        verifyNoInteractions(accountService);
    }

    @Test
    void departmentCreateReturnsFieldErrorsForInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","code":"","status":3}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("请求参数校验失败"))
                .andExpect(jsonPath("$.details.name").value("部门名称不能为空"))
                .andExpect(jsonPath("$.details.code").value("部门编码不能为空"))
                .andExpect(jsonPath("$.details.status").value("状态必须是 0 或 1"));

        verifyNoInteractions(departmentService);
    }

    @Test
    void accountGetRejectsNonPositiveIdWithFieldError() throws Exception {
        mockMvc.perform(get("/api/accounts/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("ID 必须大于 0"))
                .andExpect(jsonPath("$.details.id").value("ID 必须大于 0"));

        verifyNoInteractions(accountService);
    }

    @Test
    void accountCreateReturnsConflictForDuplicateUsername() throws Exception {
        doThrow(new DataIntegrityViolationException(
                "Duplicate entry 'qihb' for key 'sys_account.uk_account_active_username'"))
                .when(accountService).createAccount(any(AccountDTO.class));

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"qihb","name":"齐海兵","gender":"男","phone":"13511061687",
                                 "password":"123456","roleLevel":2,"status":1,"isSuper":0,"departmentId":1}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("用户名已存在"))
                .andExpect(jsonPath("$.details.username").value("用户名已存在"));
    }

    @Test
    void deletingReferencedDepartmentReturnsConflict() throws Exception {
        doThrow(new DepartmentInUseException("部门已被用户关联，无法删除"))
                .when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/api/departments/1"))
                .andExpect(status().isConflict());
    }
}
