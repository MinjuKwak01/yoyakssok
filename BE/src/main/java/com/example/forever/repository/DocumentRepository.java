package com.example.forever.repository;

import com.example.forever.domain.Document;
import com.example.forever.domain.Folder;
import com.example.forever.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    // Soft Delete를 고려한 조회 메소드
    List<Document> findByFolderAndIsDeletedFalse(Folder folder);
     List<Document> findByFolder(Folder folder);
    List<Document> findByMemberIdAndIsDeletedTrue(Long memberId);
    
    // 삭제되지 않은 문서만 ID로 조회
    Optional<Document> findByIdAndIsDeletedFalse(Long id);
    
    /**
     * 특정 회원의 문서 개수 조회 (삭제되지 않은 것만)
     */
    @Query("SELECT COUNT(d) FROM Document d WHERE d.member = :member AND d.isDeleted = false")
    long countByMember(@Param("member") Member member);
    
    /**
     * 특정 기간에 생성된 문서 개수 조회
     */
    @Query("SELECT COUNT(d) FROM Document d " +
           "WHERE d.member = :member " +
           "AND d.createdAt >= :startDate " +
           "AND d.createdAt < :endDate " +
           "AND d.isDeleted = false")
    long countByMemberAndCreatedAtBetween(
            @Param("member") Member member,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * 특정 회원의 최근 문서 조회
     */
    @Query("SELECT d FROM Document d " +
           "WHERE d.member = :member " +
           "AND d.isDeleted = false " +
           "ORDER BY d.createdAt DESC")
    List<Document> findRecentDocumentsByMember(@Param("member") Member member);
}
