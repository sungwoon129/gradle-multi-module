package com.blog.presentation;

import com.blog.application.BatchFacade;
import com.blog.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BatchController {

    private final BatchFacade batchFacade;

    @PostMapping("/")
    public void saveAnyMember() {
        batchFacade.saveAnyMember();
    }

    @GetMapping("/")
    public Member getNewMember() {
        return batchFacade.findAnyMember();
    }
}
