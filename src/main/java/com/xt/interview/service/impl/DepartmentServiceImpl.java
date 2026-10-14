package com.xt.interview.service.impl;

import com.xt.interview.dto.DepartmentDTO;
import com.xt.interview.entity.Department;
import com.xt.interview.exception.DepartmentInUseException;
import com.xt.interview.repository.AccountRepository;
import com.xt.interview.repository.DepartmentRepository;
import com.xt.interview.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private static final Integer NOT_DELETED = 0;
    private static final Integer DELETED = 1;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public DepartmentDTO createDepartment(DepartmentDTO dto) {
        String name = requireText(dto.getName(), "部门名称不能为空");
        String code = requireText(dto.getCode(), "部门编码不能为空");
        if (departmentRepository.countByCodeAndDeleted(code, NOT_DELETED) > 0) {
            throw new RuntimeException("部门编码已存在");
        }

        Department department = new Department();
        department.setName(name);
        department.setCode(code);
        department.setDescription(normalize(dto.getDescription()));
        department.setStatus(normalizeStatus(dto.getStatus()));
        department.setDeleted(NOT_DELETED);
        return convertToDTO(departmentRepository.save(department));
    }

    @Override
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO dto) {
        Department department = departmentRepository.findByIdAndDeleted(id, NOT_DELETED)
                .orElseThrow(() -> new RuntimeException("部门不存在"));

        String name = requireText(dto.getName(), "部门名称不能为空");
        String code = requireText(dto.getCode(), "部门编码不能为空");
        if (!department.getCode().equals(code)
                && departmentRepository.countByCodeAndDeleted(code, NOT_DELETED) > 0) {
            throw new RuntimeException("部门编码已存在");
        }

        department.setName(name);
        department.setCode(code);
        department.setDescription(normalize(dto.getDescription()));
        department.setStatus(normalizeStatus(dto.getStatus()));
        return convertToDTO(departmentRepository.save(department));
    }

    @Override
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findByIdAndDeleted(id, NOT_DELETED)
                .orElseThrow(() -> new RuntimeException("部门不存在"));
        if (accountRepository.existsByDepartment_IdAndDeleted(id, NOT_DELETED)) {
            throw new DepartmentInUseException("部门已被用户关联，无法删除");
        }
        department.setDeleted(DELETED);
        departmentRepository.save(department);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findByIdAndDeleted(id, NOT_DELETED)
                .orElseThrow(() -> new RuntimeException("部门不存在"));
        return convertToDTO(department);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findByDeleted(NOT_DELETED).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private DepartmentDTO convertToDTO(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setCode(department.getCode());
        dto.setDescription(department.getDescription());
        dto.setStatus(department.getStatus());
        dto.setCreateTime(department.getCreateTime());
        dto.setUpdateTime(department.getUpdateTime());
        return dto;
    }

    private String requireText(String value, String message) {
        String normalized = normalize(value);
        if (normalized == null) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private Integer normalizeStatus(Integer status) {
        return status == null ? 1 : status;
    }
}
