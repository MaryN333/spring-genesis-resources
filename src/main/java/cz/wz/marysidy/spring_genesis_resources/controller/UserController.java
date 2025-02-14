package cz.wz.marysidy.spring_genesis_resources.controller;

import cz.wz.marysidy.spring_genesis_resources.dto.CreateUserDto;
import cz.wz.marysidy.spring_genesis_resources.dto.DetailedUserDto;
import cz.wz.marysidy.spring_genesis_resources.dto.ShortUserDto;
import cz.wz.marysidy.spring_genesis_resources.dto.SuperDetailedUserDto;
import cz.wz.marysidy.spring_genesis_resources.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Valid CreateUserDto createUserDto) {
            DetailedUserDto createdUser = userService.createUser(createUserDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, @RequestParam (value = "detail", defaultValue = "false") boolean detail) {
        if (detail) {
            DetailedUserDto user = userService.getDetailedUserById(id);
            return ResponseEntity.ok(user);
        } else {
            ShortUserDto user = userService.getShortUserById(id);
            return ResponseEntity.ok(user);
        }
    }

    @GetMapping("/super-detailed/{id}")
    public ResponseEntity<?> getSuperDetailedUserById(@PathVariable Long id) {
        SuperDetailedUserDto user = userService.getSuperDetailedUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestParam(value = "detail", defaultValue = "false") boolean detail) {
        if (detail) {
            List<DetailedUserDto> detailedUsers = userService.getAllUsersDetailed();
            return ResponseEntity.ok(detailedUsers);
        } else {
            List<ShortUserDto> users = userService.getAllUsersShort();
            return ResponseEntity.ok(users);
        }
    }

    @GetMapping("/super-detailed")
    public ResponseEntity<List<SuperDetailedUserDto>> getAllUsersSuperDetailed() {
        List<SuperDetailedUserDto> superDetailedUsers = userService.getAllUsersSuperDetailed();
        return ResponseEntity.ok(superDetailedUsers);
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody @Valid ShortUserDto userUpdateDto) {
            ShortUserDto updatedUser = userService.updateUser(userUpdateDto);
            return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build(); // 204 No Content для успешного удаления
    }

    @PutMapping("/{userId}/positions/add")
    public ResponseEntity<SuperDetailedUserDto> addPositionsToUser(@PathVariable Long userId, @RequestBody List<Long> positionIds) {
        SuperDetailedUserDto updatedUser = userService.addPositionsToUser(userId, positionIds);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{userId}/positions/remove")
    public ResponseEntity<?> removePositionsFromUser(@PathVariable Long userId, @RequestBody List<Long> positionIds) {
            SuperDetailedUserDto updatedUser = userService.removePositionsFromUser(userId, positionIds);
            return ResponseEntity.ok(updatedUser);
    }
}



