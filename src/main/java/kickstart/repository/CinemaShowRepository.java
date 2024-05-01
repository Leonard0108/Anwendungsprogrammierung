package kickstart.repository;

import kickstart.models.CinemaShow;
import org.salespointframework.core.SalespointRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CinemaShowRepository extends CrudRepository<CinemaShow, Long> {

	@Query("SELECT cs FROM CinemaShow cs WHERE cs.startDateTime BETWEEN :fromDateTime AND :toDateTime")
	Streamable<CinemaShow> findCinemaShowInPeriodOfTime(LocalDateTime fromDateTime, LocalDateTime toDateTime);

	Streamable<CinemaShow> findAll();
}
