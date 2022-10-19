package com.blog.application;

import com.blog.domain.Member;
import com.blog.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void saveAnyMember() {
        memberRepository.save(Member.builder().name("sungwoony").build());
    }

    @Transactional
    public Long signup (Member member) {
        return memberRepository.save(member).getId();
    }

    @Transactional
    public Member findAnyMember() {
        return memberRepository.findById(1L).get();
    }

    @Transactional
    public Member findById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 id를 가진 회원이 존재하지 않습니다."));
    }




}
