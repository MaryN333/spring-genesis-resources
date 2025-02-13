package cz.wz.marysidy.spring_genesis_resources.dto;

import cz.wz.marysidy.spring_genesis_resources.model.Position;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class CreateUserDto extends BaseUserDataDto {
    private String personId;
    private Set<Position> positions;

    public CreateUserDto(String name, String surname, LocalDate dateOfBirth, String personId, Set<Position> positions) {
        super(name, surname, dateOfBirth);
        this.personId = personId;
        this.positions = positions != null ? positions : new HashSet<>();
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public Set<Position> getPositions() {
        return positions;
    }
}
