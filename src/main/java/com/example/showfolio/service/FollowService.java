package com.example.showfolio.service;

import com.example.showfolio.DTO.response.FollowCountResponse;
import com.example.showfolio.DTO.response.FollowResponse;
import com.example.showfolio.entity.Follow;
import com.example.showfolio.entity.Member;
import com.example.showfolio.repository.FollowRepository;
import com.example.showfolio.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    // 팔로우
    @Transactional
    public void follow(Long followerId, Long followingId) {

        // 1. 자기 자신 팔로우 방지
        if (followerId.equals(followingId)) {
            throw new RuntimeException("자기 자신을 팔로우할 수 없습니다");
        }

        // 2. 팔로우 하는 사람 찾기
        Member follower = memberRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        // 3. 팔로우 받는 사람 찾기
        Member following = memberRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        // 4. 이미 팔로우 했는지 확인
        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new RuntimeException("이미 팔로우한 유저입니다");
        }

        // 5. 팔로우 저장
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        followRepository.save(follow);
    }

    // 언팔로우
    @Transactional
    public void unfollow(Long followerId, Long followingId) {

        // 1. 팔로우 하는 사람 찾기
        Member follower = memberRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        // 2. 팔로우 받는 사람 찾기
        Member following = memberRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        // 3. 팔로우 찾기
        Follow follow = followRepository
                .findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new RuntimeException("팔로우하지 않은 유저입니다"));

        // 4. 팔로우 삭제
        followRepository.delete(follow);
    }

    // 팔로워 목록 (나를 팔로우하는 사람들)
    @Transactional(readOnly = true)
    public List<FollowResponse> getFollowers(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        return followRepository.findByFollowing(member)
                .stream()
                .map(follow -> new FollowResponse(follow.getFollower()))
                .collect(Collectors.toList());
    }

    // 팔로잉 목록 (내가 팔로우하는 사람들)
    @Transactional(readOnly = true)
    public List<FollowResponse> getFollowings(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        return followRepository.findByFollower(member)
                .stream()
                .map(follow -> new FollowResponse(follow.getFollowing()))
                .collect(Collectors.toList());
    }

    // 팔로워 수 + 팔로잉 수
    @Transactional(readOnly = true)
    public FollowCountResponse getFollowCount(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        long followerCount = followRepository.countByFollowing(member);
        long followingCount = followRepository.countByFollower(member);

        return new FollowCountResponse(followerCount, followingCount);
    }

    // 팔로우 여부 확인
    @Transactional(readOnly = true)
    public boolean isFollowing(Long followerId, Long followingId) {

        Member follower = memberRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        Member following = memberRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다"));

        return followRepository.existsByFollowerAndFollowing(follower, following);
    }
}