package cz.wz.marysidy.spring_genesis_resources.controller;

import cz.wz.marysidy.spring_genesis_resources.dto.CreatePositionDto;
import cz.wz.marysidy.spring_genesis_resources.dto.DetailedPositionDto;
import cz.wz.marysidy.spring_genesis_resources.exception.NotFoundException;
import cz.wz.marysidy.spring_genesis_resources.exception.ValidationException;
import cz.wz.marysidy.spring_genesis_resources.model.Position;
import cz.wz.marysidy.spring_genesis_resources.repository.PositionRepository;
import cz.wz.marysidy.spring_genesis_resources.repository.UserRepository;
import cz.wz.marysidy.spring_genesis_resources.service.PositionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/positions")
public class PositionWebController {
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;
    private final PositionService positionService;

    public PositionWebController(PositionRepository positionRepository, UserRepository userRepository, PositionService positionService) {
        this.positionRepository = positionRepository;
        this.userRepository = userRepository;
        this.positionService = positionService;
    }

    @GetMapping
    public String listPositions(Model model) {
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("title", "Positions List");
        model.addAttribute("contentTemplate", "positions/list :: content");
        return "layout";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("createPositionDto", new CreatePositionDto());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("contentTemplate", "positions/create :: content");
        return "layout";
    }

    @PostMapping("/create")
    public String createPosition(@ModelAttribute CreatePositionDto createPositionDto, RedirectAttributes redirectAttributes, Model model) {
        try {
            Position position = positionService.createPosition(createPositionDto);
            redirectAttributes.addFlashAttribute("message", "Position created successfully!");
            return "redirect:/positions";
        } catch (ValidationException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("createPositionDto", createPositionDto);
            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("contentTemplate", "positions/create :: content");
            return "layout";
        }
    }

    @GetMapping("/searchById")
    public String searchPositionById(@RequestParam Long id, Model model) {
        try {
            DetailedPositionDto foundPosition = positionService.getPositionById(id, true);
            model.addAttribute("foundPosition", foundPosition);
        } catch (NotFoundException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("contentTemplate", "positions/list :: content");
        return "layout";
    }

    @GetMapping("/searchByName")
    public String searchByName(@RequestParam String name, Model model) {
        List<DetailedPositionDto> foundPositions = positionService.findPositions(name, false, true, true);
        if (foundPositions.isEmpty()) {
            model.addAttribute("error", "Position not found.");
        } else {
            model.addAttribute("foundPositions", foundPositions);
        }
        model.addAttribute("positions", positionRepository.findAll());
        model.addAttribute("contentTemplate", "positions/list :: content");
        return "layout";
    }
}
