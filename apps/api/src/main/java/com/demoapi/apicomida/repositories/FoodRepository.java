package com.demoapi.apicomida.repositories;

import com.demoapi.apicomida.models.Food;
import com.demoapi.apicomida.models.UserAccount;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FoodRepository extends JpaRepository<Food, UUID> {
    @Query("""
            select f
            from Food f
            where (f.user is null or f.user = :user)
              and (
                lower(f.name) like lower(concat('%', :query, '%'))
                or lower(coalesce(f.brand, '')) like lower(concat('%', :query, '%'))
                or lower(coalesce(f.category, '')) like lower(concat('%', :query, '%'))
              )
            order by f.updatedAt desc
            """)
    List<Food> searchLocal(@Param("user") UserAccount user, @Param("query") String query);

    @Query("""
            select f
            from Food f
            where f.barcode = :barcode
              and (f.user is null or f.user = :user)
            order by case when f.user is null then 1 else 0 end
            """)
    List<Food> findAccessibleByBarcode(@Param("user") UserAccount user, @Param("barcode") String barcode);

    Optional<Food> findByExternalSourceAndExternalId(String externalSource, String externalId);

    Optional<Food> findByBarcode(String barcode);

    @Query("""
            select f
            from Food f
            where f.category is not null
              and lower(f.category) = lower(:category)
              and (f.user is null or f.user = :user)
            order by f.updatedAt desc
            """)
    List<Food> findByCategoryIgnoreCaseAndAccessible(@Param("user") UserAccount user, @Param("category") String category);

    @Query("""
            select f
            from Food f
            where f.user is null or f.user = :user
            order by f.updatedAt desc
            """)
    List<Food> findAccessibleFoods(@Param("user") UserAccount user);
}
