package com.hyun.udong.auth.oauth;

import com.hyun.udong.auth.application.service.AuthService;
import com.hyun.udong.auth.presentation.resolver.AuthenticationArgumentResolver;
import com.hyun.udong.member.domain.Member;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class MockArgumentResolver extends AuthenticationArgumentResolver {
    public MockArgumentResolver(AuthService authService) {
        super(authService);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        return Member.createForTest(1L, "hyun");
    }
}
