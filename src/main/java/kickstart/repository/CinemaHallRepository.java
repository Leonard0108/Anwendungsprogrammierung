package kickstart.repository;

import kickstart.models.CinemaHall;
import kickstart.models.CinemaShow;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

public interface CinemaHallRepository extends CrudRepository<CinemaHall, Long> {
	Streamable<CinemaHall> findAll();
}
