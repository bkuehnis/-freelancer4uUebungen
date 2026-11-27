package ch.zhaw.freelancer4u.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpHeaders;

import ch.zhaw.freelancer4u.model.Company;
import ch.zhaw.freelancer4u.security.TestSecurityConfig;
import ch.zhaw.freelancer4u.service.MailValidatorService;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;


@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@TestMethodOrder(OrderAnnotation.class)

public class CompanyControllerTest {
    private final MockMvc mvc;

    @MockitoBean
    private MailValidatorService mailValidatorService;

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static String company_id = "";

    private static final String TEST_COMPANY = "TestCompany";
    private static final String TEST_EMAIL = "test@testcompany.com";

    @Test
    @Order(1)
    void testCreateCompany() throws Exception{
        when(mailValidatorService.isValid(TEST_EMAIL)).thenReturn(true);
        Company company = new Company(TEST_COMPANY, TEST_EMAIL);
        String jsonBody = objectMapper.writeValueAsString(company);

        var result = mvc.perform(post("/api/company")
              .contentType(MediaType.APPLICATION_JSON)
              .header(HttpHeaders.AUTHORIZATION, TestSecurityConfig.ADMIN)
              .content(jsonBody))
              .andDo(print())
              .andExpect(status().isCreated())
              .andReturn();  

        // get the id of the created company
        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        company_id = jsonNode.get("id").asString();
    }

    @Test
    @Order(2)
    public void testGetCompany() throws Exception {
        mvc.perform(get("/api/company/" + company_id)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, TestSecurityConfig.ADMIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(TEST_COMPANY))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andReturn();
    }

    @Test
    @Order(3)
    public void testDeleteCompany() throws Exception {
        mvc.perform(delete("/api/company/" + company_id)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, TestSecurityConfig.ADMIN))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testCreateCompany_RoleNotAllowed() throws Exception {
            mvc.perform(post("/api/company")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}")
                    .header(HttpHeaders.AUTHORIZATION, TestSecurityConfig.USER))
                    .andDo(print())
                    .andExpect(status().isForbidden());
    }    
}
