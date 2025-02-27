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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PositionRepository positionRepository;

    @InjectMocks
    private UserService userService;

    @TempDir
    Path tempDir;

    private Path testFilePath;
    private User user;

    @BeforeEach
    void setUp() throws IOException {
        testFilePath = tempDir.resolve("testPersonIds.txt");
        Files.write(testFilePath, List.of("jXa4g3H7oPq2", "yB9fR6tK0wLm"));
        userService = new UserService(userRepository, positionRepository, testFilePath.toString());
        userService.initPersonIDs();

        user = new User("Testa", "Testova",
                LocalDate.of(2000, 12, 24),
                "jXa4g3H7oPq2", new HashSet<>());
        user.setId(1L);
    }

    @Test
    void testInitPersonIDs_WhenEmptyLines_TrimsEmptyLines() throws IOException {
        Files.writeString(testFilePath, "jXa4g3H7oPq2\n\nyB9fR6tK0wLm\n\n");
        userService.initPersonIDs();

        assertEquals(2, userService.getAvailablePersonIDs().size());
        assertTrue(userService.getAvailablePersonIDs().contains("jXa4g3H7oPq2"));
        assertTrue(userService.getAvailablePersonIDs().contains("yB9fR6tK0wLm"));
    }

    @Test
    void testInitPersonIDs_WhenFileNotExists_ThrowFileNotFoundException() {
        Path nonExistFilePath = tempDir.resolve("nonExistent.txt");
        userService = new UserService(userRepository, positionRepository, nonExistFilePath.toString());

        FileNotFoundException exception = assertThrows(FileNotFoundException.class, () -> userService.initPersonIDs());
        assertEquals("PersonID file not found: " + nonExistFilePath, exception.getMessage());
    }

    @Test
    void testInitPersonIDs_WhenEmptyFile_ThrowIllegalStateException() throws IOException {
        Files.write(testFilePath, List.of());
        IllegalStateException exception =  assertThrows(IllegalStateException.class, () -> userService.initPersonIDs());
        assertTrue(exception.getMessage().contains("PersonID file is empty: " + testFilePath));
    }

    @Test
    void testCreateUser_WhenPersonIdNotExists_ThrowValidationException() {
        CreateUserDto userDto = new CreateUserDto("Testa", "Testova",
                LocalDate.of(2000, 12, 24),
                "notExist", new HashSet<>());

        ValidationException exception = assertThrows(ValidationException.class, () -> userService.createUser(userDto));
        assertEquals("personID is missing in the available values.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUser_WhenPersonIdAlreadyUsed_ThrowValidationException() {
        CreateUserDto userDto = new CreateUserDto("Testa", "Testova",
                LocalDate.of(2000, 12, 24),
                "jXa4g3H7oPq2", new HashSet<>());

        when(userRepository.findByPersonId("jXa4g3H7oPq2")).thenReturn(Optional.of(user));

        ValidationException exception = assertThrows(ValidationException.class, () -> userService.createUser(userDto));
        assertEquals("personID has already been used.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUser_WhenAvailablePersonId_ReturnsDetailedUserDto() {
        CreateUserDto userDto = new CreateUserDto("Testa", "Testova",
                LocalDate.of(2000, 12, 24),
                "jXa4g3H7oPq2", new HashSet<>());

        when(userRepository.findByPersonId("jXa4g3H7oPq2")).thenReturn(Optional.empty());

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        DetailedUserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Testa", result.getName());
        assertEquals("jXa4g3H7oPq2", result.getPersonId());

        verify(userRepository).findByPersonId("jXa4g3H7oPq2");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testGetShortUserById_WhenNotExists_ThrowsNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException actualException = assertThrows(NotFoundException.class,
                () -> userService.getShortUserById(999L));

        assertEquals("User with id 999 not found.", actualException.getMessage());
        verify(userRepository).findById(999L);
    }

    @Test
    void testGetShortUserById_WhenExists_ReturnsShortUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ShortUserDto actualUser = userService.getShortUserById(1L);

        assertNotNull(actualUser);
        assertEquals(1L, actualUser.getId());
        assertEquals("Testa", actualUser.getName());
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetDetailedUserById_WhenNotExists_ThrowsNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException actualException = assertThrows(NotFoundException.class,
                () -> userService.getDetailedUserById(999L));

        assertEquals("User with id 999 not found.", actualException.getMessage());
        verify(userRepository).findById(999L);
    }

    @Test
    void testGetDetailedUserById_WhenExists_ReturnsDetailedUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        DetailedUserDto actualUser = userService.getDetailedUserById(1L);

        assertNotNull(actualUser);
        assertEquals(1L, actualUser.getId());
        assertEquals("Testa", actualUser.getName());
        assertEquals("jXa4g3H7oPq2", actualUser.getPersonId());
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetSuperDetailedUserById_WhenNotExists_ThrowsNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException actualException = assertThrows(NotFoundException.class,
                () -> userService.getSuperDetailedUserById(999L));

        assertEquals("User with id 999 not found.", actualException.getMessage());
        verify(userRepository).findById(999L);
    }

    @Test
    void testGetSuperDetailedUserById_WhenExists_ReturnsSuperDetailedUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        SuperDetailedUserDto actualUser = userService.getSuperDetailedUserById(1L);

        assertNotNull(actualUser);
        assertEquals(1L, actualUser.getId());
        assertEquals("Testa", actualUser.getName());
        assertNotNull(actualUser.getPositions());
        assertTrue(actualUser.getPositions().isEmpty());
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetAllUsersShort_ReturnsShortUserDtoList() {
        User user2 = new User("Mark", "Muller",
                LocalDate.of(2010, 10, 30),
                "yB9fR6tK0wLm", new HashSet<>());
        user2.setId(2L);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));
        List<ShortUserDto> result = userService.getAllUsersShort();

        assertEquals(2, result.size());
        assertEquals("Testa", result.get(0).getName());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Mark", result.get(1).getName());
        verify(userRepository).findAll();
    }

    @Test
    void testGetAllUsersDetailed_ReturnsDetailedUserDtoList() {
        User user2 = new User("Mark", "Muller",
                LocalDate.of(2010, 10, 30),
                "yB9fR6tK0wLm", new HashSet<>());
        user2.setId(2L);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));
        List<DetailedUserDto> result = userService.getAllUsersDetailed();

        assertEquals(2, result.size());
        assertEquals("Testa", result.get(0).getName());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Mark", result.get(1).getName());
        assertEquals("jXa4g3H7oPq2", user.getPersonId());
        verify(userRepository).findAll();
    }

    @Test
    void testGetAllUsersSuperDetailed_ReturnsSuperDetailedUserDtoList() {
        User user2 = new User("Mark", "Muller",
                LocalDate.of(2010, 10, 30),
                "yB9fR6tK0wLm", new HashSet<>());
        user2.setId(2L);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));
        List<SuperDetailedUserDto> result = userService.getAllUsersSuperDetailed();

        assertEquals(2, result.size());
        assertEquals("Testa", result.get(0).getName());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Mark", result.get(1).getName());
        assertEquals("jXa4g3H7oPq2", user.getPersonId());
        assertEquals(result.get(1).getPersonId(), user2.getPersonId());
        assertNotNull(result.get(0).getPositions());
        assertTrue(result.get(0).getPositions().isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void testUpdateUser_WhenNotExists_ThrowsNotFoundException() {
        ShortUserDto userDto = new ShortUserDto(999L,"newName",
                "newSurname",LocalDate.of(2000,10,10));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userDto));
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser_WhenExists_ReturnsShortUserDto() {
        ShortUserDto userDto = new ShortUserDto(1L,"newName",
                "newSurname",LocalDate.of(2000,10,10));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ShortUserDto expected = userService.updateUser(userDto);
        assertAll(
                () -> assertEquals("newName", expected.getName()),
                () -> assertEquals("newSurname", expected.getSurname()),
                () -> assertEquals(LocalDate.of(2000, 10, 10), expected.getDateOfBirth()),
                () -> assertEquals(1L, expected.getId())
        );

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDeleteUserById_WhenInvalidId_ThrowsValidationException() {
        ValidationException exception1 = assertThrows(ValidationException.class, () -> userService.deleteUserById(null));
        assertEquals("User ID cannot be null/negative number.", exception1.getMessage());

        ValidationException exception2 = assertThrows(ValidationException.class, () -> userService.deleteUserById(0L));
        assertEquals("User ID cannot be null/negative number.", exception2.getMessage());

        ValidationException exception3 = assertThrows(ValidationException.class, () -> userService.deleteUserById(-1L));
        assertEquals("User ID cannot be null/negative number.", exception3.getMessage());
    }

    @Test
    void testDeleteUserById_WhenUserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.deleteUserById(999L));
        assertEquals("User with id 999 not found.", exception.getMessage());
        verify(userRepository).findById(999L);
        verify(userRepository, never()).delete(any());
    }

    @Test
    void testDeleteUserById_WhenUserHasPositions_ThrowsValidationException() {
        Position position = new Position("Tester", new HashSet<>());
        user.getPositions().add(position);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ValidationException exception = assertThrows(ValidationException.class, () -> userService.deleteUserById(1L));
        assertEquals("Cannot delete user with active positions. Please delete all user positions first.", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository, never()).delete(any());
    }

    @Test
    void testDeleteUserById_WhenUserHasNoPositions_DeletesUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUserById(1L);
        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
    }

    @Test
    void testAddPositionsToUser_WhenUserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.addPositionsToUser
                (999L, Arrays.asList(1L, 2L)));
        assertEquals("User with id 999 not found.", exception.getMessage());

        verify(userRepository).findById(999L);
        verify(positionRepository, never()).findAllById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testAddPositionsToUser_WhenPositionsNotFound_ThrowsValidationException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(positionRepository.findAllById(Arrays.asList(555L, 777L))).thenReturn(new ArrayList<>());

        ValidationException exception = assertThrows(ValidationException.class,
                ()-> userService.addPositionsToUser(1L, Arrays.asList(555L, 777L)));
        assertEquals("This position/s either already exists for the user or does not exist. No changes occurred.", exception.getMessage());

        verify(userRepository).findById(1L);
        verify(positionRepository).findAllById(Arrays.asList(555L, 777L));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testAddPositionsToUser_WhenUserAlreadyHasPosition_ThrowsValidationException() {
        Position position = new Position("Tester", new HashSet<>());
        position.setId(1L);
        user.getPositions().add(position);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(positionRepository.findAllById(List.of(1L))).thenReturn(List.of(position));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.addPositionsToUser(1L, List.of(1L)));
        assertEquals("This position/s either already exists for the user or does not exist." +
                " No changes occurred.", exception.getMessage());

        verify(userRepository).findById(1L);
        verify(positionRepository).findAllById(List.of(1L));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testAddPositionsToUser_WhenSomePositionsNotExist_ThrowsPartialSuccessException() {
        Position position1 = new Position("Tester1", new HashSet<>());
        position1.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(positionRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(List.of(position1));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PartialSuccessException exception = assertThrows(PartialSuccessException.class,
                () -> userService.addPositionsToUser(1L, Arrays.asList(1L, 2L)));

        assertTrue(exception.getMessage().contains("Positions added successfully"));
        assertTrue(exception.getMessage().contains("Position/s [2] does/do not exist"));

        verify(userRepository).findById(1L);
        verify(positionRepository).findAllById(Arrays.asList(1L, 2L));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testAddPositionsToUser_WhenPositionsAddedSuccessfully_ReturnsSuperDetailedUserDto() {
        Position position1 = new Position("Tester1", new HashSet<>());
        position1.setId(1L);
        Position position2 = new Position("Tester2", new HashSet<>());
        position2.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(positionRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(List.of(position1, position2));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SuperDetailedUserDto result = userService.addPositionsToUser(1L, Arrays.asList(1L, 2L));

        assertNotNull(result);
        assertEquals(2, result.getPositions().size());
        verify(userRepository).findById(1L);
        verify(positionRepository).findAllById(Arrays.asList(1L, 2L));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRemovePositionsFromUser_WhenUserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.removePositionsFromUser(999L, Arrays.asList(1L, 2L)));
        assertEquals("User with id 999 not found.", exception.getMessage());

        verify(userRepository).findById(999L);
        verify(positionRepository, never()).findAllById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRemovePositionsFromUser_WhenPositionsNotFound_ThrowsValidationException() {
        Position position1 = new Position("Tester1", new HashSet<>());
        position1.setId(1L);
        user.getPositions().add(position1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(positionRepository.findAllById(List.of(777L))).thenReturn(List.of());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.removePositionsFromUser(1L, List.of(777L)));
        assertEquals("Position/s [777] not found for the given user with ID 1 - Testa Testova", exception.getMessage());

        verify(userRepository).findById(1L);
        verify(positionRepository).findAllById(List.of(777L));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRemovePositionsFromUser_WhenPositionsRemovedSuccessf_ReturnsSuperDetailedUserDto() {
        Position position1 = new Position("Tester1", new HashSet<>());
        position1.setId(1L);
        Position position2 = new Position("Tester2", new HashSet<>());
        position2.setId(2L);
        user.getPositions().addAll(Arrays.asList(position1, position2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(positionRepository.findAllById(List.of(1L))).thenReturn(List.of(position1));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SuperDetailedUserDto result = userService.removePositionsFromUser(1L, List.of(1L));

        assertNotNull(result);
        assertEquals(1, result.getPositions().size());
        assertFalse(result.getPositions().contains(position1));
        verify(userRepository).findById(1L);
        verify(positionRepository).findAllById(List.of(1L));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRemovePositionsFromUser_WhenSomePositionsNotExist_ThrowsPartialSuccessException() {
        Position position1 = new Position("Tester1", new HashSet<>());
        position1.setId(1L);
        user.getPositions().add(position1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(positionRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(List.of(position1));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PartialSuccessException exception = assertThrows(PartialSuccessException.class,
                () -> userService.removePositionsFromUser(1L, Arrays.asList(1L, 2L)));

        assertEquals("Positions deleted successfully - [id=1, position='Tester1']. Position/s [2] are " +
                "not assigned to user 1 - Testa Testova", exception.getMessage());

        verify(userRepository).findById(1L);
        verify(positionRepository).findAllById(Arrays.asList(1L, 2L));
        verify(userRepository).save(any(User.class));
    }
}