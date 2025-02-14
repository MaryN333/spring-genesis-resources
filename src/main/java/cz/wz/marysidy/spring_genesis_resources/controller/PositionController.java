package cz.wz.marysidy.spring_genesis_resources.controller;

import cz.wz.marysidy.spring_genesis_resources.dto.CreatePositionDto;
import cz.wz.marysidy.spring_genesis_resources.dto.DetailedPositionDto;
import cz.wz.marysidy.spring_genesis_resources.model.Position;
import cz.wz.marysidy.spring_genesis_resources.service.PositionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/positions")
public class PositionController {
    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @PostMapping
    public ResponseEntity<?> createPosition(@RequestBody @Valid CreatePositionDto positionDto) {
            Position position = this.positionService.createPosition(positionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(position);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetailedPositionDto> getPositionById(@PathVariable Long id, @RequestParam(value = "detail", defaultValue = "false") boolean detail) {
        DetailedPositionDto position = positionService.getPositionById(id, detail);
        return ResponseEntity.ok(position);
    }

    @GetMapping("/by-name")
    public ResponseEntity<List<DetailedPositionDto>> getPositionsByName(
            @RequestParam String name,
            @RequestParam(required = false, defaultValue = "false") boolean exactMatch,
            @RequestParam(required = false, defaultValue = "false") boolean startsWith,
            @RequestParam(required = false, defaultValue = "false") boolean detail) {
        List<DetailedPositionDto> positions = positionService.findPositions(name, exactMatch, startsWith, detail);
        return ResponseEntity.ok(positions);
    }

    @GetMapping
    public ResponseEntity<List<DetailedPositionDto>> getAllPositions(@RequestParam(value = "detail", defaultValue = "false") boolean detail) {
        List<DetailedPositionDto> positions = positionService.getAllPositions(detail);
        return ResponseEntity.ok(positions);
    }

    @PatchMapping
    public ResponseEntity<?> updatePosition(@RequestBody @Valid DetailedPositionDto position) {
        DetailedPositionDto updatedPosition = positionService.updatePosition(position);
        return ResponseEntity.ok(updatedPosition);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePosition(@PathVariable Long id) {
            positionService.deletePosition(id);
            return ResponseEntity.noContent().build();
    }
}
