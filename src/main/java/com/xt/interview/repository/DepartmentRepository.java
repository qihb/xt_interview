package com.xt.interview.repository;

import com.xt.interview.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByDeleted(Integer deleted);

    Optional<Department> findByIdAndDeleted(Long id, Integer deleted);

    @Query(value = """
            SELECT EXISTS(
                SELECT 1 FROM sys_department
                WHERE BINARY code = BINARY :code
                  AND deleted = :deleted
            )
            """, nativeQuery = true)
    boolean existsByCodeAndDeleted(@Param("code") String code,
                                   @Param("deleted") Integer deleted);
}
