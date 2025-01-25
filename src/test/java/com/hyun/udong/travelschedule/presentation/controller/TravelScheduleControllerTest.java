package com.hyun.udong.travelschedule.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyun.udong.auth.oauth.MockArgumentResolver;
import com.hyun.udong.travelschedule.application.service.TravelScheduleService;
import com.hyun.udong.travelschedule.presentation.dto.TravelScheduleRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql("/member.sql")
class TravelScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private TravelScheduleRequest request;

    private String accessToken;

    @Autowired
    private TravelScheduleService travelScheduleService;

    @BeforeEach
    void setUp() {
        accessToken = "Bearer mockToken";
        request = new TravelScheduleRequest(
                LocalDate.of(2025, 1, 25),
                LocalDate.of(2025, 2, 10),
                List.of(1L, 2L));

        this.mockMvc = MockMvcBuilders.standaloneSetup(new TravelScheduleController(travelScheduleService))
                .setCustomArgumentResolvers(new MockArgumentResolver(null))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void updateTravelSchedule_ReturnsOk() throws Exception {
        mockMvc.perform(post("/travel/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value(String.valueOf(request.getStartDate())))
                .andExpect(jsonPath("$.endDate").value(String.valueOf(request.getEndDate())))
                .andExpect(jsonPath("$.travelScheduleCities[0].cityId").value(1L))
                .andExpect(jsonPath("$.travelScheduleCities[1].cityId").value(2L));
    }

    @Test
    void updateTravelSchedule_WithNullRequest_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/travel/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken))
                .andExpect(status().isBadRequest());
    }
}
