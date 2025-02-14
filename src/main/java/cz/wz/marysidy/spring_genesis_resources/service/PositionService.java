package cz.wz.marysidy.spring_genesis_resources.service;

import cz.wz.marysidy.spring_genesis_resources.dto.CreatePositionDto;
import cz.wz.marysidy.spring_genesis_resources.dto.DetailedPositionDto;
import cz.wz.marysidy.spring_genesis_resources.dto.DetailedUserDto;
import cz.wz.marysidy.spring_genesis_resources.exception.NotFoundException;
import cz.wz.marysidy.spring_genesis_resources.exception.ValidationException;
import cz.wz.marysidy.spring_genesis_resources.model.Position;
import cz.wz.marysidy.spring_genesis_resources.model.User;
import cz.wz.marysidy.spring_genesis_resources.repository.PositionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PositionService {
    private final PositionRepository positionRepository;

    public PositionService(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    private void validatePositionName(String name) {
        Optional<Position> existingPosition = positionRepository.findByName(name);
        if (existingPosition.isPresent()) {
            throw new ValidationException("Position with name '" + name + "' already exists.");
        }
    }

    public Position createPosition(CreatePositionDto createPosDto) {
        validatePositionName(createPosDto.getName());
        Position position = new Position(createPosDto.getName());
        positionRepository.save(position);
        return position;
    }

    public DetailedPositionDto getPositionById(Long id, boolean detailed) {
        return positionRepository.findById(id)
                .map(position -> position.toDto(detailed))
                .orElseThrow(() -> new NotFoundException("Position", "id " + id));
    }

    public List<DetailedPositionDto> findPositions(String name, boolean exactMatch, boolean startsWith, boolean detail) {
        return exactMatch ? findByName(name, detail) :
                (startsWith ? findByNameWithPrefix(name, detail) : findByNameContaining(name, detail));
    }

    private List<DetailedPositionDto> findByName(String name, boolean detail) {
        return positionRepository.findByName(name).stream()
                    .map(position -> position.toDto(detail))
                    .toList();
    }

    private List<DetailedPositionDto> findByNameWithPrefix(String name, boolean detail) {
        return positionRepository.findByNameWithPrefix(name).stream()
                .map(position -> position.toDto(detail))
                .toList();
    }

    private List<DetailedPositionDto> findByNameContaining(String name, boolean detail) {
        return positionRepository.findByNameContaining(name).stream()
                .map(position -> position.toDto(detail))
                .toList();
    }

    public List<DetailedPositionDto> getAllPositions(boolean detailed) {
        return positionRepository.findAll().stream()
                .map(position -> position.toDto(detailed))
                .toList();
    }

    public DetailedPositionDto updatePosition(DetailedPositionDto position) {
        Position updatedPosition = positionRepository.findById(position.getId())
                .orElseThrow(() -> new NotFoundException("Position", "id " + position.getId()));

        updatedPosition.setName(position.getName());
        positionRepository.save(updatedPosition);
        return new DetailedPositionDto(updatedPosition.getId(), updatedPosition.getName(), updatedPosition.getUsers().stream()
                .map(user -> new DetailedUserDto(user.getId(), user.getName(), user.getSurname(), user.getBirthDate(), user.getPersonId(), user.getUuid()))
                .toList());
    }

    public void deletePosition(Long id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Position", "id " + id));

        Set<User> usersWithPosition = position.getUsers();
        if (!usersWithPosition.isEmpty()) {
            String userIds = usersWithPosition.stream()
                    .map(user -> user.getId().toString())
                    .collect(Collectors.joining(", "));

            throw new ValidationException("Can not delete position with active users. " +
                    "Please delete all positions from users first. User IDs with this position: "
                    + userIds);
        }

        positionRepository.delete(position);
    }
}
