package com.demoapi.apicomida.models.base;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseUuidEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

}
