package cz.wz.marysidy.spring_genesis_resources.controller;

import cz.wz.marysidy.spring_genesis_resources.dto.CreateUserDto;
import cz.wz.marysidy.spring_genesis_resources.dto.DetailedUserDto;
import cz.wz.marysidy.spring_genesis_resources.dto.SuperDetailedUserDto;
import cz.wz.marysidy.spring_genesis_resources.exception.NotFoundException;
import cz.wz.marysidy.spring_genesis_resources.exception.PartialSuccessException;
import cz.wz.marysidy.spring_genesis_resources.exception.ValidationException;
import cz.wz.marysidy.spring_genesis_resources.repository.PositionRepository;
import cz.wz.marysidy.spring_genesis_resources.repository.UserRepository;
import cz.wz.marysidy.spring_genesis_resources.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserWebController {
    private final UserRepository userRepository;
    private final PositionRepository positionRepository;
    private final UserService userService;

    public UserWebController(UserRepository userRepository, PositionRepository positionRepository, UserService userService) {
        this.userRepository = userRepository;
        this.positionRepository = positionRepository;
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("title", "Users List");
        model.addAttribute("contentTemplate", "users/list :: content");
        return "layout";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("createUserDto", new CreateUserDto(null, null, null, null, null));
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("contentTemplate", "users/create :: content");
        return "layout";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute CreateUserDto createUserDto, RedirectAttributes redirectAttributes, Model model) {
        try {
            DetailedUserDto detailedUserDto = userService.createUser(createUserDto);
            redirectAttributes.addFlashAttribute("message", "User created successfully!");
            return "redirect:/users";
        } catch (ValidationException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("createUserDto", createUserDto); // Возвращаем данные формы для отображения
            model.addAttribute("positions", positionRepository.findAll()); // Добавляем список позиций
            model.addAttribute("contentTemplate", "users/create :: content");
            return "layout";
        }
    }

    @GetMapping("/searchById")
    public String searchUserById(@RequestParam Long id, Model model) {
        try {
            SuperDetailedUserDto foundUser = userService.getSuperDetailedUserById(id);
            model.addAttribute("foundUser", foundUser);
        } catch (NotFoundException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("contentTemplate", "users/list :: content");
        return "layout";
    }

    @GetMapping("/searchBySurname")
    public String searchUserBySurname(@RequestParam String surname, Model model) {
        List<SuperDetailedUserDto> foundUsers = userService.getSuperDetailedUsersBySurname(surname);
        if (foundUsers.isEmpty()) {
            model.addAttribute("error", "User not found.");
        } else {
            model.addAttribute("foundUsers", foundUsers);
        }
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("contentTemplate", "users/list :: content");
        return "layout";
    }

    @GetMapping("/goToAddPositions")
    public String goToAddPositions(@RequestParam Long userId, Model model) {
        try {
            userService.getSuperDetailedUserById(userId);
            return "redirect:/users/" + userId + "/addPositions";
        } catch (NotFoundException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("contentTemplate", "users/list :: content");
            return "layout";
        }
    }

    @GetMapping("/{userId}/addPositions")
    public String showAddPositionsForm(@PathVariable Long userId, Model model) {
        SuperDetailedUserDto user = userService.getSuperDetailedUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("contentTemplate", "users/add_positions :: content");
        return "layout";
    }

    @PostMapping("/{userId}/addPositions")
    public String addPositionsToUser(@PathVariable Long userId,
                                     @RequestParam("positionIds") List<Long> positionIds,
                                     RedirectAttributes redirectAttributes, Model model) {
        try {
            SuperDetailedUserDto updatedUser = userService.addPositionsToUser(userId, positionIds);
            redirectAttributes.addFlashAttribute("foundUser", updatedUser);
            redirectAttributes.addFlashAttribute("message", "Positions added successfully!");
            return "redirect:/users";
        } catch (NotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users";
        } catch (ValidationException | PartialSuccessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/users/" + userId + "/addPositions";
        }
    }
}
