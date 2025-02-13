package cz.wz.marysidy.spring_genesis_resources.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ShortUserDto extends BaseUserDataDto {
    @NotNull(message = "ID  cannot be null.")
    private Long id;

    public ShortUserDto(Long id, String name, String surname, LocalDate dateOfBirth) {
        super(name, surname, dateOfBirth);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

