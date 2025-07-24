package com.example.forever.infrastructure.persistence;

import com.example.forever.domain.Member;
import com.example.forever.domain.notification.DeviceToken;
import com.example.forever.domain.notification.DeviceTokenRepository;
import com.example.forever.domain.notification.FcmToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 디바이스 토큰 JPA 리포지토리 구현체
 * DDD Infrastructure 패턴 적용
 */
interface DeviceTokenJpaRepositoryInterface extends JpaRepository<DeviceToken, Long> {
    
    @Query("SELECT dt FROM DeviceToken dt WHERE dt.member = :member AND dt.fcmToken = :fcmToken")
    Optional<DeviceToken> findByMemberAndFcmToken(@Param("member") Member member, @Param("fcmToken") FcmToken fcmToken);
    
    @Query("SELECT dt FROM DeviceToken dt WHERE dt.member = :member AND dt.active = true")
    List<DeviceToken> findByMemberAndActiveTrue(@Param("member") Member member);
}

@Repository
@RequiredArgsConstructor
public class DeviceTokenJpaRepository implements DeviceTokenRepository {
    
    private final DeviceTokenJpaRepositoryInterface jpaRepository;
    
    @Override
    public DeviceToken save(DeviceToken deviceToken) {
        return jpaRepository.save(deviceToken);
    }
    
    @Override
    public Optional<DeviceToken> findByMemberAndFcmToken(Member member, FcmToken fcmToken) {
        return jpaRepository.findByMemberAndFcmToken(member, fcmToken);
    }
    
    @Override
    public List<DeviceToken> findActiveTokensByMember(Member member) {
        return jpaRepository.findByMemberAndActiveTrue(member);
    }
    
    @Override
    public void delete(DeviceToken deviceToken) {
        jpaRepository.delete(deviceToken);
    }
    
    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
}
