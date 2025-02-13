package cz.wz.marysidy.spring_genesis_resources.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public abstract class BaseUserDataDto {
    @NotBlank(message = "The name cannot be empty.")
    private String name;
    @NotBlank(message = "The last name cannot be empty.")
    private String surname;
    @NotNull(message = "Date of birth cannot be null.")
    private LocalDate dateOfBirth;

    public BaseUserDataDto(String name, String surname, LocalDate dateOfBirth) {
        this.name = name;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
