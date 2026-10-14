package com.xt.interview.repository;

import com.xt.interview.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByDeleted(Integer deleted);
    Optional<Account> findByIdAndDeleted(Long id, Integer deleted);
    Optional<Account> findByUsernameAndDeleted(String username, Integer deleted);
    Optional<Account> findByUsernameAndPasswordAndDeleted(String username, String password, Integer deleted);
    boolean existsByUsernameAndDeleted(String username, Integer deleted);
    boolean existsByDepartment_IdAndDeleted(Long departmentId, Integer deleted);

    @Query("""
            select a from Account a
            where a.deleted = 0
              and (:username is null or :username = '' or lower(a.username) like lower(concat('%', :username, '%')))
              and (:status is null or a.status = :status)
              and (:roleLevel is null or a.roleLevel = :roleLevel)
            order by a.createTime desc, a.id desc
            """)
    Page<Account> findPage(@Param("username") String username,
                           @Param("status") Integer status,
                           @Param("roleLevel") Integer roleLevel,
                           Pageable pageable);
}
