package com.JS.Ersatzteildatenbank.controller;

import com.JS.Ersatzteildatenbank.model.Land;
import com.JS.Ersatzteildatenbank.repository.LandRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LandController.class)
public class LandControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    LandRepository landRepository;

    Land deutschland = new Land(0, "DE", "Deutschland");
    Land frankreich = new Land(1, "FR", "Frankreich");
    Land china = new Land(2, "CN", "China");
    Land brasilien = new Land(3, "BR", "Brasilien");

    @Test void getAllLaender_isOk() throws Exception {
        List<Land> laender = new ArrayList<>
                (Arrays.asList(deutschland, frankreich, china, brasilien));

        Mockito.when(landRepository.findAll()).thenReturn(laender);
        mockMvc.perform(MockMvcRequestBuilders
                    .get("/api/land")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("[0].id").value(deutschland.getId()))
                .andExpect(jsonPath("[1].iso_3166_2").value(frankreich.getIso_3166_2()))
                .andExpect(jsonPath("[2].name").value(china.getName()));

    }
    @Test void getAllLaender_isEmpty() throws Exception{
        MvcResult mvc = mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/land")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        String response = mvc.getResponse().getContentAsString();
        String expected = "Keine Laender gefunden.";

        assertEquals(expected, response);
    }
    @Test void getLandByIso_3166_2_isOk() throws Exception {
        Mockito.when(landRepository.findByIso_3166_2(deutschland.getIso_3166_2())).thenReturn(deutschland);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/land/0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test void getLandByIso_3166_2_isNoContent() throws Exception {
        List<Land> laender = new ArrayList<>(Arrays.asList(deutschland, frankreich, china, brasilien));
        String iso = "AT";
        Mockito.when(landRepository.findAll()).thenReturn(laender);
        MvcResult mvc = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/land/" + iso)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        String response = mvc.getResponse().getContentAsString();
        String expected = "Land mit ISO-3166-2 \"" + iso + "\" existiert nicht.";

        assertEquals(expected, response);
    }
    @Test void getLandByIso_3166_2_isBadRequest() throws Exception {
        List<String> badRequest = new ArrayList<>(Arrays.asList
                    ("66", "5A", "4a", "A", "GVÖ"));
        for (int i=0; i< badRequest.toArray().length; i++) {
            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/land/" + badRequest.get(i))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/land/AT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
    @Test void createLand_isOk() throws Exception {
        String iso = "AT";
        String name = "Österreich";
        Land oesterreich = new Land("AT", "Österreich");
        Mockito.when(landRepository.save(oesterreich)).thenReturn(oesterreich);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post("/api/land")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(oesterreich));
        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name").value(oesterreich.getName()));
    }
    @Test void createLand_isBadRequest() throws Exception {
        List<String> badIso = new ArrayList<>(Arrays.asList("A", "AAA", "7B", "Aa"));
        List<String> badName = new ArrayList<>(Arrays.asList("1asda", "hq!g"));
        List <Land> badLaender = new ArrayList<>();
        for(int i=0; i< badIso.size(); i++) {
            for  (int j=0; j< badLaender.size(); j++){
                badLaender.add(new Land(badIso.get(i), badName.get(j)));
            }
        }
        for (int i=0; i< badLaender.size(); i++) {
            MockHttpServletRequestBuilder mockRequest =
                    MockMvcRequestBuilders.post("/api/land")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(this.mapper.writeValueAsString(badLaender.get(i)));
            mockMvc.perform(mockRequest)
                    .andExpect(status().isBadRequest());
        }
    }
    @Test void updateLandByIso_3166_2_isOk() throws Exception {
        Land land = new Land("CH", "Schweiz");

    }
}
