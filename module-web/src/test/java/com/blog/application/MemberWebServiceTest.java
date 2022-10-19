package com.blog.application;

import com.blog.domain.Member;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MemberWebServiceTest {

    @Autowired
    private MemberService memberService;

    @Test
    public void 회원을_가입시킨다() {
        Long id = memberService.signup(Member.builder().name("web").build());
        Member savedMember = memberService.findById(id);
        assertThat(id, is(savedMember.getId()));
        assertThat(savedMember.getName(), is("web"));
    }

}