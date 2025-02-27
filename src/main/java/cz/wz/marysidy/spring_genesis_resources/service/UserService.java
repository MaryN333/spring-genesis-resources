package cz.wz.marysidy.spring_genesis_resources.service;

import cz.wz.marysidy.spring_genesis_resources.dto.CreateUserDto;
import cz.wz.marysidy.spring_genesis_resources.dto.DetailedUserDto;
import cz.wz.marysidy.spring_genesis_resources.dto.ShortUserDto;
import cz.wz.marysidy.spring_genesis_resources.dto.SuperDetailedUserDto;
import cz.wz.marysidy.spring_genesis_resources.exception.NotFoundException;
import cz.wz.marysidy.spring_genesis_resources.exception.PartialSuccessException;
import cz.wz.marysidy.spring_genesis_resources.exception.ValidationException;
import cz.wz.marysidy.spring_genesis_resources.model.Position;
import cz.wz.marysidy.spring_genesis_resources.model.User;
import cz.wz.marysidy.spring_genesis_resources.repository.PositionRepository;
import cz.wz.marysidy.spring_genesis_resources.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PositionRepository positionRepository;
    private final Set<String> availablePersonIDs;

    private final String personIdFilePath;

    @Autowired
    public UserService(UserRepository userRepository, PositionRepository positionRepository,
                       @Value("${app.personid.filepath}") String personIdFilePath) {
        this.userRepository = userRepository;
        this.positionRepository = positionRepository;
        this.personIdFilePath = personIdFilePath;
        this.availablePersonIDs = new HashSet<>();
    }

    @PostConstruct
    public void initPersonIDs() throws FileNotFoundException, AccessDeniedException {
        try {
            Path filePath = Paths.get(personIdFilePath);
            if (!Files.exists(filePath)) {
                throw new FileNotFoundException("PersonID file not found: " + personIdFilePath);
            }

            if (!Files.isReadable(filePath)) {
                throw new AccessDeniedException("Cannot read personID file: " + personIdFilePath);
            }

            List<String> lines = Files.readAllLines(filePath);

            if (lines.isEmpty()) {
                throw new IllegalStateException("PersonID file is empty: " + personIdFilePath);
            }

            availablePersonIDs.addAll(lines.stream()
                    .filter(line -> !line.trim().isEmpty())
                    .collect(Collectors.toSet()));

//            System.out.println("Loaded " + availablePersonIDs.size() + " personIDs from file: " + personIdFilePath + " " + availablePersonIDs);

        } catch (FileNotFoundException e) {
            throw e;
        } catch (AccessDeniedException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load personIDs from file: " + personIdFilePath, e);
        }
    }

    public Set<String> getAvailablePersonIDs() {
        return availablePersonIDs;
    }

    public DetailedUserDto createUser(CreateUserDto createUserDto) {
        String personID = createUserDto.getPersonId();
        if (!availablePersonIDs.contains(personID)) {
            throw new ValidationException("personID is missing in the available values.");
        }

        if (userRepository.findByPersonId(personID).isPresent()) {
            throw new ValidationException("personID has already been used.");
        }

        User user = new User(createUserDto.getName(), createUserDto.getSurname(), createUserDto.getDateOfBirth(),
                personID, createUserDto.getPositions());
        userRepository.save(user);
        return new DetailedUserDto(user.getId(), user.getName(), user.getSurname(),user.getBirthDate(),
                user.getPersonId(), user.getUuid());
    }

    public ShortUserDto getShortUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    ShortUserDto dto = new ShortUserDto(user.getId(), user.getName(),
                            user.getSurname(), user.getBirthDate());
                    return dto;
                })
                .orElseThrow(() -> new NotFoundException("User", "id " + id));
    }

    public DetailedUserDto getDetailedUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> new DetailedUserDto(user.getId(),
                        user.getName(), user.getSurname(), user.getBirthDate(),
                        user.getPersonId(), user.getUuid()))
                .orElseThrow(() -> new NotFoundException("User", "id " + id));
    }

    public SuperDetailedUserDto getSuperDetailedUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> new SuperDetailedUserDto(user.getId(),
                        user.getName(), user.getSurname(), user.getBirthDate(),
                        user.getPersonId(), user.getUuid(), user.getPositions(), null))
                .orElseThrow(() -> new NotFoundException("User", "id " + id));
    }

    public <T> List<T> getAllUsers(Function<User, T> mapper) {
        return userRepository.findAll().stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    public List<ShortUserDto> getAllUsersShort() {
        return getAllUsers(user -> new ShortUserDto(
                        user.getId(),
                        user.getName(),
                        user.getSurname(),
                        user.getBirthDate()));
    }

    public List<DetailedUserDto> getAllUsersDetailed() {
        return getAllUsers(user -> new DetailedUserDto(
                        user.getId(),
                        user.getName(),
                        user.getSurname(),
                        user.getBirthDate(),
                        user.getPersonId(),
                        user.getUuid()));
    }

    public List<SuperDetailedUserDto> getAllUsersSuperDetailed() {
        return getAllUsers(user -> new SuperDetailedUserDto(
                        user.getId(),
                        user.getName(),
                        user.getSurname(),
                        user.getBirthDate(),
                        user.getPersonId(),
                        user.getUuid(),
                        user.getPositions(), null));
    }

    public ShortUserDto updateUser(ShortUserDto userDto) {
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new NotFoundException("User", "id " + userDto.getId()));
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setBirthDate(userDto.getDateOfBirth());
        userRepository.save(user);
        return new ShortUserDto(user.getId(), user.getName(), user.getSurname(), user.getBirthDate());
    }

    public void deleteUserById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("User ID cannot be null/negative number.");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User", "id " + id));

        if (!user.getPositions().isEmpty()) {
            throw new ValidationException("Cannot delete user with active positions. " +
                    "Please delete all user positions first.");
        }
        userRepository.delete(user);
    }

    public SuperDetailedUserDto addPositionsToUser(Long userId, List<Long> positionIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", "id " + userId));

        Set<Position> userPositions = new HashSet<>(user.getPositions());

        Set<Position> positionsToAdd = positionRepository.findAllById(positionIds)
                .stream()
                .filter(position -> !userPositions.contains(position))
                .collect(Collectors.toSet());

        if (positionsToAdd.isEmpty()) {
            throw new ValidationException("This position/s either already exists for the user or does not exist. No changes occurred.");
        }

        for (Position position : positionsToAdd) {
            position.getUsers().add(user);
        }

        user.getPositions().addAll(positionsToAdd);
        userRepository.save(user);

        Set<Long> ids = positionIds.stream()
                .filter(id -> userPositions.stream().noneMatch(pos -> pos.getId().equals(id)))
                .collect(Collectors.toSet());

        Set<Long> nonExistPositions = ids.stream()
                .filter(id -> positionsToAdd.stream().noneMatch(pos -> pos.getId().equals(id)))
                .collect(Collectors.toSet());

        if (!nonExistPositions.isEmpty()) {
            throw new PartialSuccessException(
                    "Positions added successfully - " + positionsToAdd + " for the given user with ID "
                            + userId + " - " + user.getName() + " " + user.getSurname() +
                            ". Position/s " + nonExistPositions + " does/do not exist, cannot be added.");
        }
        return new SuperDetailedUserDto(user.getId(), user.getName(), user.getSurname(), user.getBirthDate(),
                user.getPersonId(), user.getUuid(), user.getPositions(), null);
    }

    public SuperDetailedUserDto removePositionsFromUser(Long userId, List<Long> positionIdsToRemove) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", "id " + userId));

        Set<Position> userPositions = new HashSet<>(user.getPositions());

        Set<Position> positionsToRemove = findPositionsToRemove(userPositions, positionIdsToRemove);
        Set<Long> missingPositions = findMissingPositions(userPositions, positionIdsToRemove);

        if (positionsToRemove.isEmpty()) {
            throw new ValidationException("Position/s " + missingPositions +
                    " not found for the given user with ID " + userId +
                    " - " + user.getName() + " " + user.getSurname());
        }

        removePositionsFromUserAndUpdate(user, positionsToRemove);
        validateAndHandlePartialSuccess(positionsToRemove, missingPositions, user);

        return new SuperDetailedUserDto(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getBirthDate(),
                user.getPersonId(),
                user.getUuid(),
                user.getPositions(),
                missingPositions);
    }

    private Set<Position> findPositionsToRemove(Set<Position> userPositions, List<Long> positionIds) {
        return positionRepository.findAllById(positionIds)
                .stream()
                .filter(userPositions::contains)
                .collect(Collectors.toSet());
    }

    private Set<Long> findMissingPositions(Set<Position> userPositions, List<Long> positionIds) {
        return positionIds.stream()
                .filter(id -> userPositions.stream()
                        .noneMatch(pos -> pos.getId().equals(id)))
                .collect(Collectors.toSet());
    }

    private void removePositionsFromUserAndUpdate(User user, Set<Position> positionsToRemove) {
        for (Position position : positionsToRemove) {
            position.getUsers().remove(user);
        }
        user.getPositions().removeAll(positionsToRemove);
        userRepository.save(user);
    }

    private void validateAndHandlePartialSuccess(Set<Position> positionsToRemove,
                                                 Set<Long> missingPositions, User user) {
        if (!missingPositions.isEmpty()) {
            throw new PartialSuccessException(
                    "Positions deleted successfully - " + positionsToRemove +
                            ". Position/s " + missingPositions + " are not assigned to user "
                            + user.getId() + " - " + user.getName() + " " + user.getSurname());
        }
    }
}
