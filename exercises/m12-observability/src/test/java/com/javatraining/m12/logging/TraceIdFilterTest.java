package com.javatraining.m12.logging;

import org.junit.jupiter.api.*;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ══════════════════════════════════════════════════════════════
 *  GRADING TESTS — DO NOT MODIFY
 * ══════════════════════════════════════════════════════════════
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("M12-T1: Structured Logging & MDC — Grading Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TraceIdFilterTest {

    @Autowired private TraceIdFilter filter;
    @Autowired private OrderProcessingService orderService;
    @Autowired private MockMvc mockMvc;

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    // ── TraceIdFilter ────────────────────────────────────────────────────────

    @Test @Order(1)
    @DisplayName("Filter populates MDC traceId before chain executes")
    void filterPopulatesMdc() throws Exception {
        var request  = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        // Capture MDC value inside the chain
        String[] capturedTraceId = {null};
        MockFilterChain chain = new MockFilterChain(null, (req, res, c) -> {
            capturedTraceId[0] = MDC.get(MdcKeys.TRACE_ID);
        });

        filter.doFilter(request, response, chain);

        assertThat(capturedTraceId[0])
            .as("MDC traceId must be set before chain runs")
            .isNotBlank();
    }

    @Test @Order(2)
    @DisplayName("Filter uses X-Trace-Id header value when present")
    void filterUsesExistingTraceId() throws Exception {
        var request  = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        request.addHeader("X-Trace-Id", "my-custom-trace-123");

        String[] capturedTraceId = {null};
        MockFilterChain chain = new MockFilterChain(null, (req, res, c) -> {
            capturedTraceId[0] = MDC.get(MdcKeys.TRACE_ID);
        });

        filter.doFilter(request, response, chain);

        assertThat(capturedTraceId[0]).isEqualTo("my-custom-trace-123");
    }

    @Test @Order(3)
    @DisplayName("Filter generates UUID trace-id when header is absent")
    void filterGeneratesTraceIdWhenAbsent() throws Exception {
        var request  = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        String[] capturedTraceId = {null};
        MockFilterChain chain = new MockFilterChain(null, (req, res, c) -> {
            capturedTraceId[0] = MDC.get(MdcKeys.TRACE_ID);
        });

        filter.doFilter(request, response, chain);

        // Must be a valid UUID
        assertThatNoException().isThrownBy(() ->
            java.util.UUID.fromString(capturedTraceId[0]));
    }

    @Test @Order(4)
    @DisplayName("Filter sets X-Trace-Id on response")
    void filterSetsResponseHeader() throws Exception {
        var request  = new MockHttpServletRequest();
        request.addHeader("X-Trace-Id", "resp-header-test-456");
        var response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(response.getHeader("X-Trace-Id"))
            .isEqualTo("resp-header-test-456");
    }

    @Test @Order(5)
    @DisplayName("Filter clears MDC after request completes")
    void filterClearsMdcAfterRequest() throws Exception {
        var request  = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(MDC.get(MdcKeys.TRACE_ID))
            .as("MDC must be cleared after request")
            .isNull();
    }

    @Test @Order(6)
    @DisplayName("Filter clears MDC even when chain throws exception")
    void filterClearsMdcOnException() {
        var request  = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain(null, (req, res, c) -> {
            throw new RuntimeException("simulated chain error");
        });

        try {
            filter.doFilter(request, response, chain);
        } catch (Exception ignored) {}

        assertThat(MDC.get(MdcKeys.TRACE_ID))
            .as("MDC must be cleared even after exception")
            .isNull();
    }

    @Test @Order(7)
    @DisplayName("Two sequential requests get different trace IDs")
    void eachRequestGetsUniqueTraceId() throws Exception {
        String[] ids = new String[2];
        for (int i = 0; i < 2; i++) {
            int idx = i;
            var req = new MockHttpServletRequest();
            var res = new MockHttpServletResponse();
            filter.doFilter(req, res, new MockFilterChain(null, (rq, rs, c) -> {
                ids[idx] = MDC.get(MdcKeys.TRACE_ID);
            }));
        }
        assertThat(ids[0]).isNotEqualTo(ids[1]);
    }

    @Test @Order(8)
    @DisplayName("requestId is also populated in MDC (different from traceId)")
    void requestIdInMdc() throws Exception {
        String[] traceId = {null}, requestId = {null};
        var req = new MockHttpServletRequest();
        var res = new MockHttpServletResponse();
        filter.doFilter(req, res, new MockFilterChain(null, (rq, rs, c) -> {
            traceId[0]   = MDC.get(MdcKeys.TRACE_ID);
            requestId[0] = MDC.get(MdcKeys.REQUEST_ID);
        }));
        assertThat(requestId[0]).isNotBlank();
        assertThat(requestId[0]).isNotEqualTo(traceId[0]);
    }

    // ── OrderProcessingService ────────────────────────────────────────────────

    @Test @Order(9)
    @DisplayName("processOrder() does not throw with valid inputs")
    void processOrderNoException() {
        MDC.put(MdcKeys.TRACE_ID, "test-trace-abc");
        assertThatNoException().isThrownBy(
            () -> orderService.processOrder("ORD001", "C001", 5000.0));
    }

    @Test @Order(10)
    @DisplayName("completeOrder() does not throw")
    void completeOrderNoException() {
        MDC.put(MdcKeys.TRACE_ID, "test-trace-def");
        assertThatNoException().isThrownBy(
            () -> orderService.completeOrder("ORD001"));
    }

    @Test @Order(11)
    @DisplayName("failOrder() does not throw")
    void failOrderNoException() {
        MDC.put(MdcKeys.TRACE_ID, "test-trace-ghi");
        assertThatNoException().isThrownBy(
            () -> orderService.failOrder("ORD001", "PAYMENT_DECLINED"));
    }

    @Test @Order(12)
    @DisplayName("HTTP request via MockMvc carries X-Trace-Id in response")
    void httpRequestHasTraceIdHeader() throws Exception {
        mockMvc.perform(get("/actuator/health")
                .header("X-Trace-Id", "integration-trace-999"))
            .andExpect(status().isOk())
            .andExpect(header().string("X-Trace-Id", "integration-trace-999"));
    }
}
