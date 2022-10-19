package com.blog.application;

import com.blog.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BatchFacade {

    private final MemberService memberService;

    public void saveAnyMember() {
        memberService.saveAnyMember();
    }

    public Member findAnyMember() {
        return memberService.findAnyMember();
    }
}
