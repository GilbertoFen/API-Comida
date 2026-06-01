package com.demoapi.apicomida;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.demoapi.apicomida.dtos.OpenFoodFactsDtos.OpenFoodFactsNutrimentsResponse;
import com.demoapi.apicomida.dtos.OpenFoodFactsDtos.OpenFoodFactsProductResponse;
import com.demoapi.apicomida.models.Food;
import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.repositories.FoodRepository;
import com.demoapi.apicomida.repositories.UserAccountRepository;
import com.demoapi.apicomida.services.ExternalServiceException;
import com.demoapi.apicomida.services.OpenFoodFactsClient;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class FoodControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private com.demoapi.apicomida.security.JwtService jwtService;

    @MockBean
    private OpenFoodFactsClient openFoodFactsClient;

    private UserAccount user;
    private String authorizationHeader;

    @BeforeEach
    void setUp() {
        foodRepository.deleteAll();
        userAccountRepository.deleteAll();

        user = new UserAccount();
        user.setEmail("foods-controller-" + System.currentTimeMillis() + "@appfoodspring.local");
        user.setPasswordHash("hash");
        user.setActive(true);
        user = userAccountRepository.save(user);

        String token = jwtService.generateAccessToken(user.getId(), user.getEmail());
        authorizationHeader = "Bearer " + token;
    }

    @Test
    void shouldListFoods() throws Exception {
        foodRepository.save(createFood("Rice", "MANUAL", user, null, "111"));

        mockMvc.perform(get("/foods").header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Rice"));
    }

    @Test
    void shouldGetFoodById() throws Exception {
        Food food = foodRepository.save(createFood("Rice", "MANUAL", user, null, "111"));

        mockMvc.perform(get("/foods/{id}", food.getId()).header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(food.getId().toString()))
                .andExpect(jsonPath("$.name").value("Rice"));
    }

    @Test
    void shouldCreateCustomFood() throws Exception {
        mockMvc.perform(post("/foods/custom")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Pollo preparado casero",
                                  "brand": null,
                                  "category": "Comida casera",
                                  "servingSize": 100,
                                  "servingUnit": "g",
                                  "caloriesPer100g": 180,
                                  "proteinPer100g": 25,
                                  "carbsPer100g": 2,
                                  "fatPer100g": 8,
                                  "fiberPer100g": 0,
                                  "sugarPer100g": 0,
                                  "sodiumPer100g": 250
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pollo preparado casero"))
                .andExpect(jsonPath("$.externalSource").value("MANUAL"));
    }

    @Test
    void shouldCreateUpdateAndDeleteFood() throws Exception {
        String body = """
                {
                  "name": "Greek Yogurt",
                  "brand": "Demo Brand",
                  "category": "Dairy",
                  "servingSize": 150,
                  "servingUnit": "g",
                  "caloriesPer100g": 120,
                  "proteinPer100g": 12,
                  "carbsPer100g": 8,
                  "fatPer100g": 4,
                  "fiberPer100g": 0,
                  "sugarPer100g": 7,
                  "sodiumPer100g": 55,
                  "barcode": "1234567890123",
                  "externalSource": "MANUAL"
                }
                """;

        String updateBody = """
                {
                  "name": "Greek Yogurt Light",
                  "brand": "Demo Brand",
                  "category": "Dairy",
                  "servingSize": 160,
                  "servingUnit": "g",
                  "caloriesPer100g": 110,
                  "proteinPer100g": 13,
                  "carbsPer100g": 7,
                  "fatPer100g": 2,
                  "fiberPer100g": 0,
                  "sugarPer100g": 6,
                  "sodiumPer100g": 50,
                  "barcode": "1234567890123",
                  "externalSource": "MANUAL"
                }
                """;

        String createdId = mockMvc.perform(post("/foods")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Greek Yogurt"))
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

        mockMvc.perform(patch("/foods/{id}", createdId)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Greek Yogurt Light"));

        mockMvc.perform(delete("/foods/{id}", createdId)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Food deleted"));
    }

    @Test
    void shouldSearchLocalFoods() throws Exception {
        foodRepository.save(createFood("Rice", "MANUAL", user, null, "111"));

        mockMvc.perform(get("/foods/search")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .param("query", "rice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Rice"));
    }

    @Test
    void shouldSearchExternalFoods() throws Exception {
        when(openFoodFactsClient.search("nutella")).thenReturn(List.of(openFoodFactsProduct("3017624010701", "Nutella")));

        mockMvc.perform(get("/foods/search")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .param("query", "nutella"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Nutella"))
                .andExpect(jsonPath("$[0].externalSource").value("OPEN_FOOD_FACTS"));
    }

    @Test
    void shouldFallbackToEmptyListWhenExternalSearchFails() throws Exception {
        when(openFoodFactsClient.search("broken")).thenThrow(new ExternalServiceException("down", new RuntimeException()));

        mockMvc.perform(get("/foods/search")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                        .param("query", "broken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetFoodByBarcodeFromExternal() throws Exception {
        when(openFoodFactsClient.findByBarcode("3017624010701"))
                .thenReturn(Optional.of(openFoodFactsProduct("3017624010701", "Nutella")));

        mockMvc.perform(get("/foods/barcode/{barcode}", "3017624010701")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.barcode").value("3017624010701"))
                .andExpect(jsonPath("$.name").value("Nutella"));
    }

    @Test
    void shouldGetFoodByBarcodeFromLocal() throws Exception {
        foodRepository.save(createFood("Nutella", "OPEN_FOOD_FACTS", null, "3017624010701", "3017624010701"));

        mockMvc.perform(get("/foods/barcode/{barcode}", "3017624010701")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nutella"));
    }

    private Food createFood(String name, String source, UserAccount owner, String externalId, String barcode) {
        return createFood(name, source, owner, externalId, barcode, barcode);
    }

    private Food createFood(String name, String source, UserAccount owner, String externalId, String barcode, String externalCode) {
        Food food = new Food();
        food.setUser(owner);
        food.setName(name);
        food.setBrand("Brand");
        food.setCategory("Category");
        food.setServingSize(BigDecimal.valueOf(100));
        food.setServingUnit("g");
        food.setCaloriesPer100g(BigDecimal.valueOf(100));
        food.setProteinPer100g(BigDecimal.TEN);
        food.setCarbsPer100g(BigDecimal.TEN);
        food.setFatPer100g(BigDecimal.ONE);
        food.setBarcode(barcode);
        food.setExternalSource(source);
        food.setExternalId(externalId);
        food.setVerified(false);
        return food;
    }

    private OpenFoodFactsProductResponse openFoodFactsProduct(String code, String name) {
        return new OpenFoodFactsProductResponse(
                code,
                name,
                "Ferrero",
                "Spreads,Chocolate spreads",
                new OpenFoodFactsNutrimentsResponse(
                        BigDecimal.valueOf(539),
                        BigDecimal.valueOf(6.3),
                        BigDecimal.valueOf(57.5),
                        BigDecimal.valueOf(30.9),
                        BigDecimal.ONE,
                        BigDecimal.valueOf(56.3),
                        BigDecimal.valueOf(0.04)
                ),
                null
        );
    }
}
