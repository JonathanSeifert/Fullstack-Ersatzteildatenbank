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

import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
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
                .get("/api/land/" + deutschland.getIso_3166_2())
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
            for  (int j=0; j< badName.size(); j++){
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
        Mockito.when(landRepository.findByIso_3166_2(deutschland.getIso_3166_2())).thenReturn(deutschland);
        Mockito.when(landRepository.save(land)).thenReturn(land);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/land/" + deutschland.getIso_3166_2())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(land));
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name").value("Schweiz"));
    }
    @Test void updateLandByIso_3166_2_isNoContent() throws Exception {
        Land land = new Land("CH", "Schweiz");
        Mockito.when(landRepository.findByIso_3166_2("AT")).thenReturn(null);
        Mockito.when(landRepository.save(land)).thenReturn(land);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/land/AT")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(land));
        MvcResult mvc = mockMvc.perform(mockRequest)
                .andExpect(status().isNoContent())
                .andReturn();

        String response = mvc.getResponse().getContentAsString();
        String expected = "Land mit ISO 'AT' existiert nicht.";
        assertEquals(expected, response);
    }
    @Test void updateLandByIso_3166_2_isBadRequest_UrlFormat() throws Exception {
        Land land = new Land("CH", "Schweiz");
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/land/12")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(land));
        MvcResult mvcResult = mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andReturn();

        String firstResponse = mvcResult.getResponse().getContentAsString();
        String expected_falsches_URL_Format = "Falsches ISO-Format in der URL";
        assertEquals(expected_falsches_URL_Format, firstResponse);
    }
    @Test void updateLandByIso_3166_2_isBadRequest_null() throws Exception {
        Land emptyLand = new Land();
        Mockito.when(landRepository.findByIso_3166_2(deutschland.getIso_3166_2())).thenReturn(deutschland);
        Mockito.when(landRepository.save(emptyLand)).thenReturn(emptyLand);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/land/DE")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(emptyLand));
        MvcResult mvcResult = mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String expected_notNull = "Land-Attribute duerfen nicht 'null' sein.";
        assertEquals(expected_notNull, response);
    }
    @Test void updateLandByIso_3166_2_isBadRequest_AttributeFormat() throws Exception {
        Land invalidLand = new Land("5g", "21fmn.g23");
        Mockito.when(landRepository.findByIso_3166_2(deutschland.getIso_3166_2())).thenReturn(deutschland);
        Mockito.when(landRepository.save(invalidLand)).thenReturn(invalidLand);
        MockHttpServletRequestBuilder thirdMockRequest = MockMvcRequestBuilders.put("/api/land/" + deutschland.getIso_3166_2())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(invalidLand));
        MvcResult mvcResult = mockMvc.perform(thirdMockRequest)
                .andExpect(status().isBadRequest())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        String expected_falsches_attribut_format = "ISO- oder Name-Format falsch";
        assertEquals(expected_falsches_attribut_format, result);

    }
    @Test void deleteLandByIso_3166_2_isNoContent_success() throws Exception {
        Mockito.when(landRepository.findByIso_3166_2(deutschland.getIso_3166_2())).thenReturn(deutschland);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/land/" + deutschland.getIso_3166_2())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String expected = "Land mit Iso " + deutschland.getIso_3166_2() + " erfolgreich entfernt.";
        assertEquals(expected, response);
    }
    @Test void deleteLandByIso_3166_2_isBadRequest_UrlFormat() throws Exception {
        Mockito.when(landRepository.findByIso_3166_2(deutschland.getIso_3166_2())).thenReturn(deutschland);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/land/tl")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String expected = "Falsches ISO-Format in der URL.";
        assertEquals(expected, response);
    }
    @Test void deleteLandByIso_3166_2_isNoContent_landDoesNotExist() throws Exception {
        Mockito.when(landRepository.findByIso_3166_2("TL")).thenReturn(null);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/land/TL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String expected = "Land mit Iso TL existiert nicht.";
        assertEquals(expected, response);
    }
    @Test void deleteAllLaender_isNoContent() throws Exception{
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/land")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String expected = "Alle Laender entfernt.";
        assertEquals(expected, response);
    }
}
