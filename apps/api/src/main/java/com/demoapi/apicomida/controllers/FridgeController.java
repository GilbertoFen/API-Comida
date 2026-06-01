package com.demoapi.apicomida.controllers;

import com.demoapi.apicomida.dtos.FridgeDtos.FridgeItemRequest;
import com.demoapi.apicomida.services.FridgeService;
import com.demoapi.apicomida.util.ApiResponses;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FridgeController {

    private final FridgeService fridgeService;

    public FridgeController(FridgeService fridgeService) {
        this.fridgeService = fridgeService;
    }

    @PostMapping("/fridge/items")
    public Object create(@Valid @RequestBody FridgeItemRequest request) {
        return fridgeService.create(request);
    }

    @GetMapping("/fridge/items")
    public Object getItems() {
        return fridgeService.getItems();
    }

    @PatchMapping("/fridge/items/{id}")
    public Object update(@PathVariable UUID id, @Valid @RequestBody FridgeItemRequest request) {
        return fridgeService.update(id, request);
    }

    @DeleteMapping("/fridge/items/{id}")
    public Object delete(@PathVariable UUID id) {
        fridgeService.delete(id);
        return ApiResponses.message("Fridge item deleted");
    }

    @PostMapping("/fridge/match-recipes")
    public Object matchRecipes() {
        return fridgeService.matchRecipes();
    }
}
