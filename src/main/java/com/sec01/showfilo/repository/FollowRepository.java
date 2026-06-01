package com.sec01.showfilo.repository;

import com.sec01.showfilo.entity.Follow;
import com.sec01.showfilo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 팔로우 여부 확인
    boolean existsByFollowerAndFollowing(Member follower, Member following);

    // 팔로우 찾기 (언팔로우할 때)
    Optional<Follow> findByFollowerAndFollowing(Member follower, Member following);

    // 팔로워 목록 (나를 팔로우하는 사람들)
    List<Follow> findByFollowing(Member following);

    // 팔로잉 목록 (내가 팔로우하는 사람들)
    List<Follow> findByFollower(Member follower);

    // 팔로워 수
    long countByFollowing(Member following);

    // 팔로잉 수
    long countByFollower(Member follower);

    // 회원탈퇴할 때 삭제
    void deleteByFollower(Member follower);
    void deleteByFollowing(Member following);
}