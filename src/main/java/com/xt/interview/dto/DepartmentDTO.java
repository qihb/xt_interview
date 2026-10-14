package com.xt.interview.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class DepartmentDTO {
    private Long id;

    @NotBlank(message = "部门名称不能为空")
    @Size(max = 100, message = "部门名称长度不能超过 100 个字符")
    private String name;

    @NotBlank(message = "部门编码不能为空")
    @Size(max = 50, message = "部门编码长度不能超过 50 个字符")
    private String code;

    @Size(max = 255, message = "部门描述长度不能超过 255 个字符")
    private String description;

    @Min(value = 0, message = "状态必须是 0 或 1")
    @Max(value = 1, message = "状态必须是 0 或 1")
    private Integer status;
    private Boolean unassignUsers;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public DepartmentDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Boolean getUnassignUsers() { return unassignUsers; }
    public void setUnassignUsers(Boolean unassignUsers) { this.unassignUsers = unassignUsers; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
