package com.upeu.assignment.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class AssignmentCorrelationIdFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-ID";
    private static final String MDC_TRACE_ID = "traceId";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path == null || !path.startsWith("/api/v1/asignaciones");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        boolean setHere = false;
        String traceId = MDC.get(MDC_TRACE_ID);

        if (traceId == null || traceId.isBlank()) {
            traceId = request.getHeader(TRACE_ID_HEADER);
            if (traceId == null || traceId.isBlank()) {
                traceId = UUID.randomUUID().toString();
            }
            MDC.put(MDC_TRACE_ID, traceId);
            setHere = true;
        }

        response.addHeader(TRACE_ID_HEADER, traceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            if (setHere) {
                MDC.remove(MDC_TRACE_ID);
            }
        }
    }
}
