package com.allobank.splitbill;

import com.allobank.splitbill.dto.request.CreateExpenseRequest;
import com.allobank.splitbill.dto.request.CreateGroupRequest;
import com.allobank.splitbill.dto.request.ExpenseSplitRequest;
import com.allobank.splitbill.dto.request.ParticipantRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SplitBillIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("End-to-end: Create group, record expenses, retrieve expenses, and calculate settlement")
    void testCompleteSplitBillFlow() throws Exception {
        // 1. Create Group
        CreateGroupRequest groupReq = CreateGroupRequest.builder()
                .name("Bali Trip 2026")
                .participants(List.of(
                        ParticipantRequest.builder().name("Andi").build(),
                        ParticipantRequest.builder().name("Budi").build(),
                        ParticipantRequest.builder().name("Cici").build()
                ))
                .build();

        MvcResult groupResult = mockMvc.perform(post("/api/v1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(groupReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.name", is("Bali Trip 2026")))
                .andExpect(jsonPath("$.data.participants", hasSize(3)))
                .andReturn();

        String groupResponseJson = groupResult.getResponse().getContentAsString();
        String groupId = objectMapper.readTree(groupResponseJson).get("data").get("group_id").asText();
        String andiId = objectMapper.readTree(groupResponseJson).get("data").get("participants").get(0).get("participant_id").asText();
        String budiId = objectMapper.readTree(groupResponseJson).get("data").get("participants").get(1).get("participant_id").asText();
        String ciciId = objectMapper.readTree(groupResponseJson).get("data").get("participants").get(2).get("participant_id").asText();

        // 2. Add Expense 1: Andi pays 600,000 for Hotel (200k each)
        CreateExpenseRequest exp1 = CreateExpenseRequest.builder()
                .paidBy(andiId)
                .description("Hotel stay")
                .amount(new BigDecimal("600000.00"))
                .splits(List.of(
                        ExpenseSplitRequest.builder().participantId(andiId).amount(new BigDecimal("200000.00")).build(),
                        ExpenseSplitRequest.builder().participantId(budiId).amount(new BigDecimal("200000.00")).build(),
                        ExpenseSplitRequest.builder().participantId(ciciId).amount(new BigDecimal("200000.00")).build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/groups/{groupId}/expenses", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exp1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.amount", is("600000.00")));

        // 3. Add Expense 2: Budi pays 300,000 for Dinner (100k each)
        CreateExpenseRequest exp2 = CreateExpenseRequest.builder()
                .paidBy(budiId)
                .description("Seafood Dinner")
                .amount(new BigDecimal("300000.00"))
                .splits(List.of(
                        ExpenseSplitRequest.builder().participantId(andiId).amount(new BigDecimal("100000.00")).build(),
                        ExpenseSplitRequest.builder().participantId(budiId).amount(new BigDecimal("100000.00")).build(),
                        ExpenseSplitRequest.builder().participantId(ciciId).amount(new BigDecimal("100000.00")).build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/groups/{groupId}/expenses", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exp2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.amount", is("300000.00")));

        // 4. Get Expenses list
        mockMvc.perform(get("/api/v1/groups/{groupId}/expenses", groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", hasSize(2)));

        // 5. Calculate Settlement
        // Total: 900,000.00
        // Andi: paid 600k, consumed 300k -> Net +300k
        // Budi: paid 300k, consumed 300k -> Net 0
        // Cici: paid 0, consumed 300k -> Net -300k
        // Simplified settlement: Cici -> Andi 300,000.00
        // Service charge for 'harycp' (7%): 900,000.00 * 0.07 = 63,000.00
        mockMvc.perform(get("/api/v1/groups/{groupId}/settlement", groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data.total_expenses", is("900000.00")))
                .andExpect(jsonPath("$.data.service_charge_pct", is(7)))
                .andExpect(jsonPath("$.data.service_charge_amount", is("63000.00")))
                .andExpect(jsonPath("$.data.settlements", hasSize(1)))
                .andExpect(jsonPath("$.data.settlements[0].from", is("Cici")))
                .andExpect(jsonPath("$.data.settlements[0].to", is("Andi")))
                .andExpect(jsonPath("$.data.settlements[0].amount", is("300000.00")));
    }

    @Test
    @DisplayName("Validation Error: Split sum does not match total expense amount")
    void testMismatchedSplitAmount() throws Exception {
        // Create group
        CreateGroupRequest groupReq = CreateGroupRequest.builder()
                .name("Weekend Outing")
                .participants(List.of(
                        ParticipantRequest.builder().name("Andi").build(),
                        ParticipantRequest.builder().name("Budi").build()
                ))
                .build();

        MvcResult groupResult = mockMvc.perform(post("/api/v1/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(groupReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String groupResponseJson = groupResult.getResponse().getContentAsString();
        String groupId = objectMapper.readTree(groupResponseJson).get("data").get("group_id").asText();
        String andiId = objectMapper.readTree(groupResponseJson).get("data").get("participants").get(0).get("participant_id").asText();
        String budiId = objectMapper.readTree(groupResponseJson).get("data").get("participants").get(1).get("participant_id").asText();

        // Expense amount 100,000, but splits only sum to 80,000
        CreateExpenseRequest exp = CreateExpenseRequest.builder()
                .paidBy(andiId)
                .description("Coffee")
                .amount(new BigDecimal("100000.00"))
                .splits(List.of(
                        ExpenseSplitRequest.builder().participantId(andiId).amount(new BigDecimal("40000.00")).build(),
                        ExpenseSplitRequest.builder().participantId(budiId).amount(new BigDecimal("40000.00")).build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/groups/{groupId}/expenses", groupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exp)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")));
    }

    @Test
    @DisplayName("ResourceNotFound: Group does not exist returns 404")
    void testNonExistentGroup() throws Exception {
        mockMvc.perform(get("/api/v1/groups/{groupId}", "non-existent-uuid"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("error")));
    }
}
