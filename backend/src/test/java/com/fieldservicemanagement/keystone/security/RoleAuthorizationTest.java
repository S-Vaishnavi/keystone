package com.fieldservicemanagement.keystone.security;

import com.fieldservicemanagement.keystone.controller.ReportController;
import com.fieldservicemanagement.keystone.controller.TimeLogController;
import com.fieldservicemanagement.keystone.controller.WorkOrderController;
import com.fieldservicemanagement.keystone.repository.UserRepository;
import com.fieldservicemanagement.keystone.service.ReportService;
import com.fieldservicemanagement.keystone.service.TimeLogService;
import com.fieldservicemanagement.keystone.service.WorkOrderLifecycleService;
import com.fieldservicemanagement.keystone.service.WorkOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({WorkOrderController.class, ReportController.class, TimeLogController.class})
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "keystone.jwt.secret=bXlTdXBlclNlY3JldEtleUZvckpXVFRlc3RpbmdQdXJwb3Nlcw==",
        "keystone.jwt.expiration-ms=86400000"
})
public class RoleAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkOrderService workOrderService;

    @MockitoBean
    private WorkOrderLifecycleService workOrderLifecycleService;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private TimeLogService timeLogService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerRole_shouldAccessWorkOrderCreation() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/work-orders")
                        .contentType("application/json")
                        .content("{}"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isNotEqualTo(403);
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void technicianRole_shouldBeDeniedWorkOrderCreation() throws Exception {
        mockMvc.perform(post("/api/work-orders")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DISPATCHER")
    void dispatcherRole_shouldBeDeniedWorkOrderCreation() throws Exception {
        mockMvc.perform(post("/api/work-orders")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerRole_shouldAccessWorkOrderCreation() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/work-orders")
                        .contentType("application/json")
                        .content("{}"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isNotEqualTo(403);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerRole_shouldAccessReports() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/reports"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isNotEqualTo(403);
    }

    @Test
    @WithMockUser(roles = "DISPATCHER")
    void dispatcherRole_shouldBeDeniedReports() throws Exception {
        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void technicianRole_shouldAccessTimeLogs() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/time-logs"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isNotEqualTo(403);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerRole_shouldBeDeniedTimeLogs() throws Exception {
        mockMvc.perform(get("/api/time-logs"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerRole_shouldAccessWorkOrderAssignment() throws Exception {
        String uuid = UUID.randomUUID().toString();
        MvcResult result = mockMvc.perform(patch("/api/work-orders/" + uuid + "/assign")
                        .contentType("application/json")
                        .content("{}"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isNotEqualTo(403);
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void technicianRole_shouldBeDeniedWorkOrderAssignment() throws Exception {
        String uuid = UUID.randomUUID().toString();
        mockMvc.perform(patch("/api/work-orders/" + uuid + "/assign")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerRole_shouldBeDeniedWorkOrderAssignment() throws Exception {
        String uuid = UUID.randomUUID().toString();
        mockMvc.perform(patch("/api/work-orders/" + uuid + "/assign")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "DISPATCHER")
    void dispatcherRole_shouldAccessWorkOrderAssignment() throws Exception {
        String uuid = UUID.randomUUID().toString();
        MvcResult result = mockMvc.perform(patch("/api/work-orders/" + uuid + "/assign")
                        .contentType("application/json")
                        .content("{}"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isNotEqualTo(403);
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    void technicianRole_shouldAccessStatusTransition() throws Exception {
        String uuid = UUID.randomUUID().toString();
        MvcResult result = mockMvc.perform(patch("/api/work-orders/" + uuid + "/status")
                        .contentType("application/json")
                        .content("{}"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isNotEqualTo(403);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void customerRole_shouldBeDeniedStatusTransition() throws Exception {
        String uuid = UUID.randomUUID().toString();
        mockMvc.perform(patch("/api/work-orders/" + uuid + "/status")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isForbidden());
    }
}
