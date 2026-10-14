package com.xt.interview.repository;

import com.xt.interview.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByDeleted(Integer deleted);
    Optional<Account> findByIdAndDeleted(Long id, Integer deleted);
    @Query(value = """
            SELECT * FROM sys_account
            WHERE BINARY username = BINARY :username
              AND deleted = :deleted
            LIMIT 1
            """, nativeQuery = true)
    Optional<Account> findByUsernameAndDeleted(@Param("username") String username,
                                               @Param("deleted") Integer deleted);

    @Query(value = """
            SELECT * FROM sys_account
            WHERE BINARY username = BINARY :username
              AND BINARY password = BINARY :password
              AND deleted = :deleted
            LIMIT 1
            """, nativeQuery = true)
    Optional<Account> findByUsernameAndPasswordAndDeleted(@Param("username") String username,
                                                           @Param("password") String password,
                                                           @Param("deleted") Integer deleted);

    @Query(value = """
            SELECT COUNT(*) FROM sys_account
            WHERE BINARY username = BINARY :username
              AND deleted = :deleted
            """, nativeQuery = true)
    long countByUsernameAndDeleted(@Param("username") String username,
                                   @Param("deleted") Integer deleted);
    boolean existsByDepartment_IdAndDeleted(Long departmentId, Integer deleted);

    long countByDepartment_IdAndDeleted(Long departmentId, Integer deleted);

    @Modifying
    @Query("""
            update Account a
               set a.department = null
             where a.department.id = :departmentId
               and a.deleted = 0
            """)
    int clearDepartmentForActiveAccounts(@Param("departmentId") Long departmentId);

    @Query("""
            select a from Account a
            where a.deleted = 0
              and (:username is null or :username = '' or lower(a.username) like lower(concat('%', :username, '%')))
              and (:status is null or a.status = :status)
              and (:roleLevel is null or a.roleLevel = :roleLevel)
              and (:departmentId is null or a.department.id = :departmentId)
            order by a.createTime desc, a.id desc
            """)
    Page<Account> findPage(@Param("username") String username,
                           @Param("status") Integer status,
                           @Param("roleLevel") Integer roleLevel,
                           @Param("departmentId") Long departmentId,
                           Pageable pageable);
}
