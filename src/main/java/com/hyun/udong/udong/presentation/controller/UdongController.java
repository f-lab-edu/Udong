package com.hyun.udong.udong.presentation.controller;

import com.hyun.udong.common.annotation.LoginMember;
import com.hyun.udong.member.domain.Member;
import com.hyun.udong.udong.application.service.UdongService;
import com.hyun.udong.udong.presentation.dto.CreateUdongRequest;
import com.hyun.udong.udong.presentation.dto.UdongResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/udongs")
public class UdongController {
    private final UdongService udongService;

    @PostMapping
    public ResponseEntity<UdongResponse> createUdong(@Valid @RequestBody CreateUdongRequest request,
                                                     @LoginMember Member member) {
        return ResponseEntity.ok(udongService.createUdong(request, member.getId()));
    }
}
