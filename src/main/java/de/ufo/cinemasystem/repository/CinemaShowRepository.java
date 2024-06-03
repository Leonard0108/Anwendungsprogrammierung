package de.ufo.cinemasystem.repository;

import de.ufo.cinemasystem.additionalfiles.AdditionalDateTimeWorker;
import de.ufo.cinemasystem.models.Film;
import de.ufo.cinemasystem.models.Snacks;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import de.ufo.cinemasystem.models.CinemaShow;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface CinemaShowRepository extends CrudRepository<CinemaShow, Long> {

	static final Sort DEFAULT_SORT = Sort.sort(CinemaShow.class).by(CinemaShow::getId).descending();

	@Query("SELECT cs FROM CinemaShow cs WHERE cs.startDateTime BETWEEN :fromDateTime AND :toDateTime")
	Streamable<CinemaShow> findCinemaShowsInPeriodOfTime(LocalDateTime fromDateTime, LocalDateTime toDateTime, Sort sort);

	default Streamable<CinemaShow> findCinemaShowsInPeriodOfTime(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
		return findCinemaShowsInPeriodOfTime(fromDateTime, toDateTime, DEFAULT_SORT);
	}

	@Query("SELECT cs FROM CinemaShow cs WHERE cs.startDateTime BETWEEN :fromDateTime AND :toDateTime AND cs.film = :film")
	Streamable<CinemaShow> findCinemaShowsInPeriodOfTime(LocalDateTime fromDateTime, LocalDateTime toDateTime, Film film, Sort sort);

	default Streamable<CinemaShow> findCinemaShowsInPeriodOfTime(LocalDateTime fromDateTime, LocalDateTime toDateTime, Film film) {
		return findCinemaShowsInPeriodOfTime(fromDateTime, toDateTime, film, DEFAULT_SORT);
	}

	@Query("SELECT cs FROM CinemaShow cs WHERE cs.film = :film")
	Streamable<CinemaShow> findAllByFilm(Film film, Sort sort);

	default Streamable<CinemaShow> findAllByFilm(Film film) {
		return findAllByFilm(film, DEFAULT_SORT);
	}

	default Streamable<CinemaShow> findCinemaShowsInWeek(int year, int week) {
		return findCinemaShowsInPeriodOfTime(
			AdditionalDateTimeWorker.getStartWeekDateTime(year, week),
			AdditionalDateTimeWorker.getEndWeekDateTime(year, week)
		);
	}

	default Streamable<CinemaShow> findCinemaShowsInWeek(int year, int week, Sort sort) {
		return findCinemaShowsInPeriodOfTime(
			AdditionalDateTimeWorker.getStartWeekDateTime(year, week),
			AdditionalDateTimeWorker.getEndWeekDateTime(year, week),
			sort
		);
	}

	default Streamable<CinemaShow> findCinemaShowsInWeek(int year, int week, Film film) {
		return findCinemaShowsInPeriodOfTime(
			AdditionalDateTimeWorker.getStartWeekDateTime(year, week),
			AdditionalDateTimeWorker.getEndWeekDateTime(year, week),
			film
		);
	}

	default Streamable<CinemaShow> findCinemaShowsInWeek(int year, int week, Film film, Sort sort) {
		return findCinemaShowsInPeriodOfTime(
			AdditionalDateTimeWorker.getStartWeekDateTime(year, week),
			AdditionalDateTimeWorker.getEndWeekDateTime(year, week),
			film,
			sort
		);
	}

	default Streamable<CinemaShow> findCinemaShowsOnDay(LocalDate date) {
		return findCinemaShowsInPeriodOfTime(
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0),
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59, 59)
		);
	}

	default Streamable<CinemaShow> findCinemaShowsOnDay(LocalDate date, Sort sort) {
		return findCinemaShowsInPeriodOfTime(
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0),
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59, 59),
			sort
		);
	}

	default Streamable<CinemaShow> findCinemaShowsOnDay(LocalDate date, Film film) {
		return findCinemaShowsInPeriodOfTime(
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0),
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59, 59),
			film
		);
	}

	default Streamable<CinemaShow> findCinemaShowsOnDay(LocalDate date, Film film, Sort sort) {
		return findCinemaShowsInPeriodOfTime(
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0),
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59, 59),
			film,
			sort
		);
	}

	default Streamable<CinemaShow> findAll() {
		return findAll(DEFAULT_SORT);
	}

	Streamable<CinemaShow> findAll(Sort sort);
}
