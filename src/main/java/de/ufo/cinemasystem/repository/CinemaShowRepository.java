package de.ufo.cinemasystem.repository;

import de.ufo.cinemasystem.additionalfiles.AdditionalDateTimeWorker;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import de.ufo.cinemasystem.models.CinemaShow;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface CinemaShowRepository extends CrudRepository<CinemaShow, Long> {

	@Query("SELECT cs FROM CinemaShow cs WHERE cs.startDateTime BETWEEN :fromDateTime AND :toDateTime")
	Streamable<CinemaShow> findCinemaShowsInPeriodOfTime(LocalDateTime fromDateTime, LocalDateTime toDateTime);

	default Streamable<CinemaShow> findCinemaShowsInWeek(int year, int week) {
		return findCinemaShowsInPeriodOfTime(
			AdditionalDateTimeWorker.getStartWeekDateTime(year, week),
			AdditionalDateTimeWorker.getEndWeekDateTime(year, week)
		);
	}

	default Streamable<CinemaShow> findCinemaShowsOnDay(LocalDate date) {
		return findCinemaShowsInPeriodOfTime(
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0),
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59, 59)
		);
	}

	Streamable<CinemaShow> findAll();
}
