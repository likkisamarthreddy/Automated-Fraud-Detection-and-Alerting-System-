package com.frauddetection.fraudengine.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frauddetection.fraudengine.controller.FraudRuleController;
import com.frauddetection.fraudengine.entity.FraudRule;
import com.frauddetection.fraudengine.service.FraudRuleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FraudRuleController.class)
class FraudRuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FraudRuleService fraudRuleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllRules_BlackBox() throws Exception {
        // Arrange (Setup mock)
        FraudRule rule = new FraudRule();
        rule.setRuleId(1L);
        rule.setRuleName("Test Rule");
        rule.setEnabled(true);

        when(fraudRuleService.getAllRules()).thenReturn(Collections.singletonList(rule));

        // Act & Assert (Testing external API behavior without knowing internal DB logic)
        mockMvc.perform(get("/api/rules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ruleName").value("Test Rule"))
                .andExpect(jsonPath("$[0].ruleId").value(1));
    }

    @Test
    void testCreateRule_BlackBox() throws Exception {
       // Arrange
        FraudRule newRule = new FraudRule();
        newRule.setRuleName("API Created Rule");
        newRule.setRuleType("AMOUNT");
        newRule.setThresholdValue(5000);
        newRule.setRiskScoreWeight(80);

        FraudRule savedRule = new FraudRule();
        savedRule.setRuleId(10L); // Mocked ID after save
        savedRule.setRuleName("API Created Rule");

        when(fraudRuleService.createRule(any(FraudRule.class))).thenReturn(savedRule);

        // Act & Assert (Functional testing of POST endpoint)
        mockMvc.perform(post("/api/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRule)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ruleId").value(10))
                .andExpect(jsonPath("$.ruleName").value("API Created Rule"));
    }
}
