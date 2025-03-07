package cz.wz.marysidy.spring_genesis_resources.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.wz.marysidy.spring_genesis_resources.model.Position;

import java.time.LocalDate;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuperDetailedUserDto extends DetailedUserDto {
    private Set<Position> positions;
    private Set<Long> missingPositions;

    public SuperDetailedUserDto(Long id, String name, String surname, LocalDate dateOfBirth, String personId, String uuid, Set<Position> positions, Set<Long> missingPositions) {
        super(id, name, surname, dateOfBirth, personId, uuid);
        this.positions = positions;
        this.missingPositions = missingPositions;
    }

    public Set<Position> getPositions() {
        return positions;
    }

    public void setPositions(Set<Position> positions) {
        this.positions = positions;
    }

    public Set<Long> getMissingPositions() {
        return missingPositions;
    }

    public void setMissingPositions(Set<Long> missingPositions) {
        this.missingPositions = missingPositions;
    }
}