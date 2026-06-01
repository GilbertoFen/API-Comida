package com.demoapi.apicomida;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.demoapi.apicomida.dtos.FoodDtos.FoodResponse;
import com.demoapi.apicomida.dtos.OpenFoodFactsDtos.OpenFoodFactsNutrimentsResponse;
import com.demoapi.apicomida.dtos.OpenFoodFactsDtos.OpenFoodFactsProductResponse;
import com.demoapi.apicomida.exception.ApiException;
import com.demoapi.apicomida.models.Food;
import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.repositories.FoodRepository;
import com.demoapi.apicomida.repositories.UserAccountRepository;
import com.demoapi.apicomida.services.ExternalServiceException;
import com.demoapi.apicomida.services.FoodService;
import com.demoapi.apicomida.services.OpenFoodFactsClient;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest
class FoodServiceIntegrationTest {

    @Autowired
    private FoodService foodService;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @MockBean
    private OpenFoodFactsClient openFoodFactsClient;

    private UserAccount currentUser;

    @BeforeEach
    void setUp() {
        foodRepository.deleteAll();
        userAccountRepository.deleteAll();

        currentUser = new UserAccount();
        currentUser.setEmail("foods-" + System.currentTimeMillis() + "@appfoodspring.local");
        currentUser.setPasswordHash("hash");
        currentUser.setActive(true);
        currentUser = userAccountRepository.save(currentUser);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser.getId().toString(), null, List.of())
        );
    }

    @Test
    void searchFoodsReturnsLocalResultsIfTheyExist() {
        foodRepository.save(createFood("Rice", "MANUAL", null, "111"));

        List<FoodResponse> results = foodService.searchFoods("rice");

        assertEquals(1, results.size());
        verify(openFoodFactsClient, never()).search(anyString());
    }

    @Test
    void searchFoodsCallsOpenFoodFactsIfNoLocalResultsExist() {
        when(openFoodFactsClient.search("nutella")).thenReturn(List.of(openFoodFactsProduct("3017624010701", "Nutella")));

        List<FoodResponse> results = foodService.searchFoods("nutella");

        assertEquals(1, results.size());
        assertEquals("OPEN_FOOD_FACTS", results.get(0).externalSource());
        assertEquals(1, foodRepository.count());
    }

    @Test
    void findByBarcodeReturnsLocalIfExists() {
        foodRepository.save(createFood("Nutella", "OPEN_FOOD_FACTS", "3017624010701", "3017624010701"));

        FoodResponse result = foodService.findByBarcode("3017624010701");

        assertEquals("Nutella", result.name());
        verify(openFoodFactsClient, never()).findByBarcode(anyString());
    }

    @Test
    void findByBarcodeCallsOpenFoodFactsIfLocalDoesNotExist() {
        when(openFoodFactsClient.findByBarcode("3017624010701"))
                .thenReturn(Optional.of(openFoodFactsProduct("3017624010701", "Nutella")));

        FoodResponse result = foodService.findByBarcode("3017624010701");

        assertEquals("Nutella", result.name());
        assertEquals(1, foodRepository.count());
    }

    @Test
    void searchFoodsDoesNotDuplicateExternalFoods() {
        foodRepository.save(createFood("Nutella", "OPEN_FOOD_FACTS", "3017624010701", "3017624010701"));
        when(openFoodFactsClient.search("nutella")).thenReturn(List.of(openFoodFactsProduct("3017624010701", "Nutella")));

        List<FoodResponse> results = foodService.searchFoods("nutella");

        assertEquals(1, results.size());
        assertEquals(1, foodRepository.count());
    }

    @Test
    void searchFoodsDoesNotSaveInvalidExternalProducts() {
        when(openFoodFactsClient.search("unknown")).thenReturn(List.of(
                new OpenFoodFactsProductResponse("123", "", "Brand", "Cat", new OpenFoodFactsNutrimentsResponse(null, null, null, null, null, null, null), null)
        ));

        List<FoodResponse> results = foodService.searchFoods("unknown");

        assertEquals(0, results.size());
        assertEquals(0, foodRepository.count());
    }

    @Test
    void searchFoodsReturnsEmptyListIfExternalFailsWithoutLocalResults() {
        when(openFoodFactsClient.search("nutella")).thenThrow(new ExternalServiceException("down", new RuntimeException()));

        List<FoodResponse> results = foodService.searchFoods("nutella");

        assertEquals(0, results.size());
    }

    private Food createFood(String name, String source, String externalId, String barcode) {
        Food food = new Food();
        food.setUser(null);
        food.setName(name);
        food.setBrand("Brand");
        food.setCategory("Spread");
        food.setServingSize(BigDecimal.valueOf(100));
        food.setServingUnit("g");
        food.setCaloriesPer100g(BigDecimal.valueOf(539));
        food.setProteinPer100g(BigDecimal.valueOf(6.3));
        food.setCarbsPer100g(BigDecimal.valueOf(57.5));
        food.setFatPer100g(BigDecimal.valueOf(30.9));
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
