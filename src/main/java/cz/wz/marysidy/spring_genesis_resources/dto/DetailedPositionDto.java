package cz.wz.marysidy.spring_genesis_resources.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailedPositionDto {
    private Long id;
    private String name;
    private List<DetailedUserDto> users;


    public DetailedPositionDto(Long id, String name, List<DetailedUserDto> users) {
        this.id = id;
        this.name = name;
        this.users = users;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DetailedUserDto> getUsers() {
        return users;
    }

    public void setUsers(List<DetailedUserDto> users) {
        this.users = users;
    }
}
