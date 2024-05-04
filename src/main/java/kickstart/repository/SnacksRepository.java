package kickstart.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import kickstart.models.Snacks;

public interface SnacksRepository extends CrudRepository<Snacks, Long> {
    Streamable<Snacks> findAll();
}