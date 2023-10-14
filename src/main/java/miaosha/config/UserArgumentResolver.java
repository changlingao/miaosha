package miaosha.config;

import miaosha.domain.MiaoshaUser;
import miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

// amazing way to insert customized argument
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    MiaoshaUserService userService;

    // if it's user-defined param, resolveArgument
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz == MiaoshaUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);

        String paramToken = request.getParameter(MiaoshaUserService.COOKI_NAME_TOKEN);
        String cookieToken = getCookieToken(request, MiaoshaUserService.COOKI_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }

        String token = StringUtils.isEmpty(paramToken)? cookieToken : paramToken;
        return userService.getByToken(response, token);
    }

    private String getCookieToken(HttpServletRequest request, String cookiNameToken) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie: cookies) {
            if (cookie.getName().equals(cookiNameToken)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
