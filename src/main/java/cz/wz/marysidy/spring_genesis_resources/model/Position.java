package cz.wz.marysidy.spring_genesis_resources.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import cz.wz.marysidy.spring_genesis_resources.dto.DetailedPositionDto;
import cz.wz.marysidy.spring_genesis_resources.dto.DetailedUserDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "positions")
public class Position extends BaseEntity {
    @NotBlank
    @Column(name = "position_name", nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "positions")
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    public Position() {
    }

    public Position(String name) {
        this.name = name;
    }
    public Position(String name, Set<User> users) {
        this.name = name;
        this.users = users != null ? users : new HashSet<>();
    }

    public @NotBlank String getName() {
        return name;
    }

    public void setName(@NotBlank String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public DetailedPositionDto toDto(boolean detailed) {
        if (detailed) {
            return new DetailedPositionDto(getId(), name, users.stream()
                    .map(user -> new DetailedUserDto(
                    user.getId(),
                    user.getName(),
                    user.getSurname(),
                    user.getBirthDate(),
                    user.getPersonId(),
                    user.getUuid())).toList());
        } else {
            List<DetailedUserDto> userIdsOnly = users.stream()
                    .map(user -> new DetailedUserDto(user.getId(), null, null, null, null, null))
                    .toList();
            return new DetailedPositionDto(getId(), name, userIdsOnly);
        }
    }

    @Override
    public String toString() {
        return "id=" + getId() + ", position='" + getName() + "'";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return name.equals(position.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }
}
