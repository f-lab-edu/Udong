package com.hyun.udong.auth.presentation.resolver;

import com.hyun.udong.auth.application.service.AuthService;
import com.hyun.udong.auth.exception.UnAuthenticatedMemberException;
import com.hyun.udong.common.annotation.LoginMember;
import com.hyun.udong.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthenticationArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String PREFIX = "Bearer ";

    private final AuthService authService;

    public AuthenticationArgumentResolver(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class)
                && parameter.getParameterType().equals(Member.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String token = request.getHeader("Authorization");

        isValidToken(token);

        return authService.findMemberFromToken(token.substring(PREFIX.length()));
    }

    private void isValidToken(String token) {
        if (token == null || !token.startsWith(PREFIX)) {
            throw UnAuthenticatedMemberException.EXCEPTION;
        }
    }
}
