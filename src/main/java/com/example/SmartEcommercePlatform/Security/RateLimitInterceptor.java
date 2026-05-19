package com.example.SmartEcommercePlatform.Security;

import com.example.SmartEcommercePlatform.Service.RateLimitingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitingService rateLimitingService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String ipAddress = request.getRemoteAddr();
        boolean isAllowed = rateLimitingService.isRequestAllowed(ipAddress);

        if (isAllowed) {
            return true;
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"status\": 429, \"error\": \"Too Many Requests\", \"message\": \"You have exhausted your API Request Quota (10 per minute). Please wait.\"}");
            return false;
        }
    }
}