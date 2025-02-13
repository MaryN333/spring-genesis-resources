package cz.wz.marysidy.spring_genesis_resources.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User extends BaseEntity{
    @NotBlank
    @Column(name = "first_name")
    private String name;

    @NotBlank
    @Column(name = "last_name")
    private String surname;

    @NotNull
    @Column(name = "birth_date")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthDate;

    @ManyToMany
    @JoinTable(
            name = "user_position",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "position_id")
    )
    private Set<Position> positions;

    @Size(min = 12, max = 12)
    @NotBlank
    @Column(name = "person_id", unique = true, nullable = false)
    private String personId;

    @NotBlank
    @Column(name = "uuid", unique = true, nullable = false)
    private final String uuid = UUID.randomUUID().toString();

    public User() {
        this.positions = new HashSet<>();
    }

    public User(String name, String surname, LocalDate birthDate, String personId, Set<Position> positions) {
        this.name = name;
        this.surname = surname;
        this.personId = personId;
        this.birthDate = birthDate;
        this.positions = positions != null ? positions : new HashSet<>();
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Set<Position> getPositions() {
        return positions;
    }

    public void setPositions(Set<Position> positions) {
        this.positions = positions;
    }

    public String getPersonId() {
        return personId;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        // je to bezpecne, primy pristup k personID, proto ze je final
        return personId.equals(user.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getPersonId());
    }
}
