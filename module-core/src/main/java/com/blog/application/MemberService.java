package com.blog.application;

import com.blog.domain.Member;
import com.blog.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void saveAnyMember() {
        memberRepository.save(Member.builder().name("random").build());
    }

    @Transactional
    public Long signup (Member member) {
        return memberRepository.save(member).getId();
    }

    @Transactional
    public Member findAnyMember() {
        return memberRepository.findById(1L).orElseThrow(() -> new NoSuchElementException("해당 id를 가진 회원이 존재하지 않습니다."));
    }

    @Transactional
    public Member findById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 id를 가진 회원이 존재하지 않습니다."));
    }

}
