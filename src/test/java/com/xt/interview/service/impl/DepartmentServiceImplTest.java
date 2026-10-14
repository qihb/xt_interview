package com.xt.interview.service.impl;

import com.xt.interview.dto.DepartmentDTO;
import com.xt.interview.entity.Department;
import com.xt.interview.repository.DepartmentRepository;
import com.xt.interview.repository.AccountRepository;
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
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private DepartmentServiceImpl service;

    @Test
    void createDepartmentTrimsTextAndDefaultsStatus() {
        DepartmentDTO input = department("  研发部 ", " RD ");
        input.setStatus(null);
        Department saved = entity(1L, "研发部", "RD");
        when(departmentRepository.save(any(Department.class))).thenReturn(saved);

        DepartmentDTO result = service.createDepartment(input);

        var captor = org.mockito.ArgumentCaptor.forClass(Department.class);
        verify(departmentRepository).save(captor.capture());
        assertEquals("研发部", captor.getValue().getName());
        assertEquals("RD", captor.getValue().getCode());
        assertEquals(1, captor.getValue().getStatus());
        assertEquals("RD", result.getCode());
    }

    @Test
    void createDepartmentRejectsBlankName() {
        DepartmentDTO input = department("  ", "RD");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.createDepartment(input));

        assertEquals("部门名称不能为空", error.getMessage());
        verifyNoInteractions(departmentRepository);
    }

    @Test
    void createDepartmentRejectsDuplicateCode() {
        when(departmentRepository.countByCodeAndDeleted("RD", 0)).thenReturn(1L);

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> service.createDepartment(department("研发部", "RD")));

        assertEquals("部门编码已存在", error.getMessage());
    }

    @Test
    void updateDepartmentTreatsCodeCaseChangeAsDifferentCode() {
        Department existing = entity(1L, "研发部", "RD");
        when(departmentRepository.findByIdAndDeleted(1L, 0)).thenReturn(Optional.of(existing));
        when(departmentRepository.countByCodeAndDeleted("rd", 0)).thenReturn(0L);
        when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.updateDepartment(1L, department("研发部", "rd"));

        verify(departmentRepository).countByCodeAndDeleted("rd", 0);
        assertEquals("rd", existing.getCode());
    }

    @Test
    void deleteDepartmentMarksDepartmentAsDeleted() {
        Department existing = entity(1L, "研发部", "RD");
        when(departmentRepository.findByIdAndDeleted(1L, 0)).thenReturn(Optional.of(existing));

        service.deleteDepartment(1L);

        assertEquals(1, existing.getDeleted());
        verify(departmentRepository).save(existing);
    }

    @Test
    void deleteDepartmentRejectsWhenActiveAccountReferencesIt() {
        Department existing = entity(1L, "研发部", "RD");
        when(departmentRepository.findByIdAndDeleted(1L, 0)).thenReturn(Optional.of(existing));
        when(accountRepository.existsByDepartment_IdAndDeleted(1L, 0)).thenReturn(true);

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> service.deleteDepartment(1L));

        assertEquals("部门已被用户关联，无法删除", error.getMessage());
        verify(departmentRepository, never()).save(any(Department.class));
    }

    private DepartmentDTO department(String name, String code) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setName(name);
        dto.setCode(code);
        dto.setStatus(1);
        return dto;
    }

    private Department entity(Long id, String name, String code) {
        Department department = new Department();
        department.setId(id);
        department.setName(name);
        department.setCode(code);
        department.setStatus(1);
        department.setDeleted(0);
        return department;
    }
}
