package cz.wz.marysidy.spring_genesis_resources.model;

import cz.wz.marysidy.spring_genesis_resources.dto.DetailedPositionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {
    private Position position;

    @BeforeEach
    void setUp() {
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

        position = new Position("Tester", users);
        position.setId(1L);
    }

    @Test
    void testToDtoDetailed() {
        DetailedPositionDto dto = position.toDto(true);
        assertNotNull(dto);
        assertEquals("Tester", dto.getName());
        assertEquals(2, dto.getUsers().size());
        assertTrue(dto.getUsers().stream().anyMatch(user -> user.getId().equals(1L)));
        assertTrue(dto.getUsers().stream().anyMatch(user -> user.getId().equals(2L)));
        dto.getUsers().forEach(user -> {
            assertNotNull(user.getId());
            assertNotNull(user.getName());
            assertNotNull(user.getSurname());
            assertNotNull(user.getDateOfBirth());
            assertNotNull(user.getPersonId());
            assertNotNull(user.getUuid());
        });
    }

    @Test
    void testToDtoNonDetailed() {
        DetailedPositionDto dto = position.toDto(false);
        assertNotNull(dto);
        assertEquals("Tester", dto.getName());
        assertEquals(2, dto.getUsers().size());
        dto.getUsers().forEach(user -> {
            assertNull(user.getName());
            assertNull(user.getSurname());
            assertNull(user.getDateOfBirth());
            assertNull(user.getPersonId());
            assertNull(user.getUuid());
        });
    }
}
