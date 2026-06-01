package com.demoapi.apicomida.services;

import com.demoapi.apicomida.dtos.RecipePostDtos.RecipePostRequest;
import com.demoapi.apicomida.dtos.RecipePostDtos.RecipePostResponse;
import com.demoapi.apicomida.exception.ApiException;
import com.demoapi.apicomida.models.RecipePost;
import com.demoapi.apicomida.models.RecipePostLike;
import com.demoapi.apicomida.models.UserAccount;
import com.demoapi.apicomida.repositories.RecipePostLikeRepository;
import com.demoapi.apicomida.repositories.RecipePostRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipePostService {

    private final RecipePostRepository recipePostRepository;
    private final RecipePostLikeRepository recipePostLikeRepository;
    private final RecipeService recipeService;
    private final CurrentUserService currentUserService;

    public RecipePostService(
            RecipePostRepository recipePostRepository,
            RecipePostLikeRepository recipePostLikeRepository,
            RecipeService recipeService,
            CurrentUserService currentUserService
    ) {
        this.recipePostRepository = recipePostRepository;
        this.recipePostLikeRepository = recipePostLikeRepository;
        this.recipeService = recipeService;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public RecipePostResponse create(RecipePostRequest request) {
        RecipePost post = new RecipePost();
        post.setRecipe(recipeService.requireVisible(request.recipeId()));
        post.setUser(currentUserService.requireCurrentUser());
        post.setTitle(request.title());
        post.setContent(request.content());
        return toResponse(recipePostRepository.save(post));
    }

    public List<RecipePostResponse> getAll() {
        return recipePostRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    public RecipePostResponse getById(UUID id) {
        return toResponse(requirePost(id));
    }

    @Transactional
    public RecipePostResponse update(UUID id, RecipePostRequest request) {
        RecipePost post = requireOwned(id);
        post.setRecipe(recipeService.requireVisible(request.recipeId()));
        post.setTitle(request.title());
        post.setContent(request.content());
        return toResponse(recipePostRepository.save(post));
    }

    @Transactional
    public void delete(UUID id) {
        recipePostRepository.delete(requireOwned(id));
    }

    @Transactional
    public RecipePostResponse like(UUID id) {
        RecipePost post = requirePost(id);
        UserAccount user = currentUserService.requireCurrentUser();
        recipePostLikeRepository.findByRecipePostAndUser(post, user).orElseGet(() -> {
            RecipePostLike like = new RecipePostLike();
            like.setRecipePost(post);
            like.setUser(user);
            return recipePostLikeRepository.save(like);
        });
        post.setLikesCount((int) recipePostLikeRepository.countByRecipePost(post));
        return toResponse(recipePostRepository.save(post));
    }

    @Transactional
    public RecipePostResponse unlike(UUID id) {
        RecipePost post = requirePost(id);
        UserAccount user = currentUserService.requireCurrentUser();
        recipePostLikeRepository.findByRecipePostAndUser(post, user).ifPresent(recipePostLikeRepository::delete);
        post.setLikesCount((int) recipePostLikeRepository.countByRecipePost(post));
        return toResponse(recipePostRepository.save(post));
    }

    private RecipePost requirePost(UUID id) {
        return recipePostRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Recipe post not found"));
    }

    private RecipePost requireOwned(UUID id) {
        UserAccount user = currentUserService.requireCurrentUser();
        return recipePostRepository.findById(id)
                .filter(post -> post.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Recipe post not found"));
    }

    private RecipePostResponse toResponse(RecipePost post) {
        return new RecipePostResponse(
                post.getId(),
                post.getRecipe().getId(),
                post.getUser().getId(),
                post.getTitle(),
                post.getContent(),
                post.getLikesCount(),
                post.getCommentsCount()
        );
    }
}
