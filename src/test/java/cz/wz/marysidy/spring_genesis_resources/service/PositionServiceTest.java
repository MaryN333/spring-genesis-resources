package cz.wz.marysidy.spring_genesis_resources.service;

import cz.wz.marysidy.spring_genesis_resources.dto.CreatePositionDto;
import cz.wz.marysidy.spring_genesis_resources.dto.DetailedPositionDto;
import cz.wz.marysidy.spring_genesis_resources.exception.NotFoundException;
import cz.wz.marysidy.spring_genesis_resources.exception.ValidationException;
import cz.wz.marysidy.spring_genesis_resources.model.Position;
import cz.wz.marysidy.spring_genesis_resources.model.User;
import cz.wz.marysidy.spring_genesis_resources.repository.PositionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private PositionRepository positionRepository;

    @InjectMocks
    private PositionService positionService;

    private Position position;

    @BeforeEach
    void setUp() {
        position = new Position("Manager");
        position.setId(1L);
    }

    @Test
    void testCreatePosition_WhenUniqueName_ReturnsSavedPosition() {
        String expectedName = "NewPositionName";
        CreatePositionDto dto = new CreatePositionDto(expectedName);

        when(positionRepository.findByName(expectedName)).thenReturn(Optional.empty());
        when(positionRepository.save(any(Position.class))).thenAnswer(invocation -> {
            Position savedPosition = invocation.getArgument(0);
            savedPosition.setId(1L);
            return savedPosition;
        });

        Position resultPosition = positionService.createPosition(dto);

        assertNotNull(resultPosition);
        assertEquals(expectedName, resultPosition.getName());
        assertEquals(1L, resultPosition.getId());
        verify(positionRepository).findByName(expectedName);
        verify(positionRepository).save(any(Position.class));
    }

    @Test
    void createPosition_WhenNameExists_ThrowsValidationException() {
        CreatePositionDto dto = new CreatePositionDto("Manager");
        when(positionRepository.findByName("Manager")).thenReturn(Optional.of(position));

        ValidationException actual = assertThrows(ValidationException.class,
                () -> positionService.createPosition(dto));

        assertEquals("Position with name 'Manager' already exists.", actual.getMessage());
        verify(positionRepository).findByName("Manager");
        verify(positionRepository, never()).save(any());
    }

    @Test
    void testGetPositionById_WhenExists_ReturnsDto() {
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));

        DetailedPositionDto actualPosition = positionService.getPositionById(1L, true);

        assertNotNull(actualPosition);
        assertEquals(1L, actualPosition.getId());
        assertEquals("Manager", actualPosition.getName());
        verify(positionRepository).findById(1L);
    }

    @Test
    void testGetPositionById_WhenNotExists_ThrowsNotFoundException() {
        when(positionRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException actualException = assertThrows(NotFoundException.class,
                () -> positionService.getPositionById(999L, true));

        assertEquals("Position with id 999 not found.", actualException.getMessage());
        verify(positionRepository).findById(999L);
    }

    @Test
    void testFindPositions_WhenExactMatch_ReturnsDtoList() {
        when(positionRepository.findByName("Manager")).thenReturn(Optional.of(position));

        List<DetailedPositionDto> result = positionService.findPositions("Manager", true, false, true);

        assertEquals(1, result.size());
        assertEquals("Manager", result.get(0).getName());
        verify(positionRepository).findByName("Manager");
        verifyNoMoreInteractions(positionRepository);
    }

    @Test
    void testFindPositions_WhenStartsWith_ReturnsDtoList() {
        Position position2 = new Position("Manufacturer");
        when(positionRepository.findByNameWithPrefix("Man")).thenReturn(List.of(position, position2));

        List<DetailedPositionDto> actual = positionService.findPositions("Man", false, true, true);

        assertEquals(2, actual.size());
        assertTrue(actual.stream().anyMatch(dto -> "Manager".equals(dto.getName())));
        assertTrue(actual.stream().anyMatch(dto -> "Manufacturer".equals(dto.getName())));
        verify(positionRepository).findByNameWithPrefix("Man");
        verifyNoMoreInteractions(positionRepository);
    }

    @Test
    void testFindPositions_WhenContains_ReturnsDtoList() {
        Position position2 = new Position("Manufacturer");
        when(positionRepository.findByNameContaining("an")).thenReturn(List.of(position, position2));

        List<DetailedPositionDto> actual = positionService.findPositions("an", false, false, true);

        assertEquals(2, actual.size());
        assertTrue(actual.stream().anyMatch(dto -> "Manager".equals(dto.getName())));
        assertTrue(actual.stream().anyMatch(dto -> "Manufacturer".equals(dto.getName())));
        verify(positionRepository).findByNameContaining("an");
        verifyNoMoreInteractions(positionRepository);
    }

    @Test
    public void testGetAllPositions_WhenDetailedTrue_ReturnsDtoList() {
        Position position2 = new Position("Manufacturer");
        when(positionRepository.findAll()).thenReturn(Arrays.asList(position, position2));
        List<DetailedPositionDto> result = positionService.getAllPositions(true);

        assertEquals(2, result.size());
        assertEquals("Manager", result.get(0).getName());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Manufacturer", result.get(1).getName());
        verify(positionRepository).findAll();
    }

    @Test
    public void testGetAllPositions_WhenDetailedFalse_ReturnsDtoList() {
        Position position2 = new Position("Manufacturer");
        when(positionRepository.findAll()).thenReturn(Arrays.asList(position, position2));
        List<DetailedPositionDto> result = positionService.getAllPositions(false);

        assertEquals(2, result.size());
        assertEquals("Manager", result.get(0).getName());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Manufacturer", result.get(1).getName());
        verify(positionRepository).findAll();
    }

    @Test
    public void testUpdatePosition_WhenNotExists_ThrowsNotFoundException() {
        DetailedPositionDto dto = new DetailedPositionDto(999L,"expectedName", null);
        when(positionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> positionService.updatePosition(dto));
        verify(positionRepository).findById(999L);
    }

    @Test
    public void testUpdatePosition_WhenExists_ReturnsDto() {
        DetailedPositionDto dto = new DetailedPositionDto(1L,"newName", null);
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));

        DetailedPositionDto expected = positionService.updatePosition(dto);
        assertEquals("newName", expected.getName());
        verify(positionRepository).findById(1L);
    }

    @Test
    void testDeletePosition_WhenNoUsers() {
        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));
        positionService.deletePosition(1L);
        verify(positionRepository).delete(position);
    }

    @Test
    void testDeletePosition_WithUsers_ThrowsValidationException() {
        User user1 = new User("Testa", "Testova",
                LocalDate.of(2000, 12, 24),
                "jXa4g3H7oPq2", new HashSet<>());
        user1.setId(1L);

        User user2 = new User("Test", "Testovac",
                LocalDate.of(2000, 1, 10),
                "yB9fR6tK0wLm", new HashSet<>());
        user2.setId(2L);

        Set<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);

        position.setUsers(users);

        when(positionRepository.findById(1L)).thenReturn(Optional.of(position));
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            positionService.deletePosition(1L);
        });

        assertTrue(exception.getMessage().contains("Can not delete position with active users"));
        verify(positionRepository, never()).delete(any(Position.class));
    }
}
