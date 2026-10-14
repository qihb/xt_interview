package com.xt.interview.controller;

import com.xt.interview.dto.AccountDTO;
import com.xt.interview.dto.DepartmentDTO;
import com.xt.interview.service.AccountService;
import com.xt.interview.service.DepartmentService;
import com.xt.interview.exception.DepartmentInUseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.util.ReflectionTestUtils;

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
        mockMvc = MockMvcBuilders.standaloneSetup(accountController, departmentController).build();
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
    void deletingReferencedDepartmentReturnsConflict() throws Exception {
        doThrow(new DepartmentInUseException("部门已被用户关联，无法删除"))
                .when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/api/departments/1"))
                .andExpect(status().isConflict());
    }
}
