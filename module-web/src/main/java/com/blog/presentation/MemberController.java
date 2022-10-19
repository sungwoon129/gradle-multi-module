package com.blog.presentation;

import com.blog.application.MemberWebService;
import com.blog.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberWebService memberWebService;


    @PostMapping("/")
    public void saveAnyMember() {
        memberWebService.saveAnyMember();
    }

    @GetMapping("/")
    public Member findAnyMember() {
        return memberWebService.findAnyMember();
    }

}
