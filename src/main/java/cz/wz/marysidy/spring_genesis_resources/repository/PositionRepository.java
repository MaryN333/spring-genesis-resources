package cz.wz.marysidy.spring_genesis_resources.repository;

import cz.wz.marysidy.spring_genesis_resources.model.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    Optional<Position> findByName(String name); // exact match

    @Query("SELECT p FROM Position p WHERE p.name LIKE :prefix%")
    List<Position> findByNameWithPrefix(@Param("prefix") String prefix);

    List<Position> findByNameContaining(String name); // partial match
}
