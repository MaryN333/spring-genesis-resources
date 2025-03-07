package cz.wz.marysidy.spring_genesis_resources.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailedUserDto extends ShortUserDto {
    private String personId;
    private String uuid;

    public DetailedUserDto(Long id, String name, String surname, LocalDate dateOfBirth, String personID, String uuid) {
        super(id, name, surname, dateOfBirth);
        this.personId = personID;
        this.uuid = uuid;
    }

    public String getPersonId() { return personId; }

    public void setPersonId(String personId) { this.personId = personId; }

    public String getUuid() { return uuid; }

    public void setUuid(String uuid) { this.uuid = uuid; }
}
