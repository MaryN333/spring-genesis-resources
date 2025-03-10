package cz.wz.marysidy.spring_genesis_resources.dto;

import jakarta.validation.constraints.NotBlank;

public class CreatePositionDto {
    @NotBlank (message = "The name can`t be empty.")
    private String name;

    public CreatePositionDto() {
    }

    public CreatePositionDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
