package de.ufo.cinemasystem.repository;

import de.ufo.cinemasystem.additionalfiles.AdditionalDateTimeWorker;
import de.ufo.cinemasystem.models.CinemaHall;
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

/**
 * CRUD-Repository für Kinovorführungen
 * @author Yannick Harnisch
 */
@Repository
public interface CinemaShowRepository extends CrudRepository<CinemaShow, Long> {

    /**
     * Standardmäßige Sortierung für Kinovorführungen.
     */
	static final Sort DEFAULT_SORT = Sort.sort(CinemaShow.class).by(CinemaShow::getId).descending();

        /**
         * Finde Alle Kinovorführungen in einer bestimmten Zeit
         * @param fromDateTime Startzeitpunkt
         * @param toDateTime Endzeitpunkt
         * @param sort Sortierung
         * @return Den Kriterien Entsprechende Kinovorführungen
         */
	@Query("SELECT cs FROM CinemaShow cs WHERE cs.startDateTime BETWEEN :fromDateTime AND :toDateTime")
	Streamable<CinemaShow> findCinemaShowsInPeriodOfTime(LocalDateTime fromDateTime, LocalDateTime toDateTime, Sort sort);

        /**
         * Finde Alle Kinovorführungen in einer bestimmten Zeit
         * @param fromDateTime Startzeitpunkt
         * @param toDateTime Endzeitpunkt
         * @return Den Kriterien Entsprechende Kinovorführungen
         */
	default Streamable<CinemaShow> findCinemaShowsInPeriodOfTime(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
		return findCinemaShowsInPeriodOfTime(fromDateTime, toDateTime, DEFAULT_SORT);
	}

        /**
         * Finde Alle Kinovorführungen eines Films in einer bestimmten Zeit
         * @param fromDateTime Startzeitpunkt
         * @param toDateTime Endzeitpunkt
         * @param film Kinofilm
     * @param sort Sortierung
         * @return Den Kriterien Entsprechende Kinovorführungen
         */
	@Query("SELECT cs FROM CinemaShow cs WHERE cs.startDateTime BETWEEN :fromDateTime AND :toDateTime AND cs.film = :film")
	Streamable<CinemaShow> findCinemaShowsInPeriodOfTime(LocalDateTime fromDateTime, LocalDateTime toDateTime, Film film, Sort sort);

        /**
         * Finde Alle Kinovorführungen eines Films in einer bestimmten Zeit
         * @param fromDateTime Startzeitpunkt
         * @param toDateTime Endzeitpunkt
         * @param film Kinofilm
         * @return Den Kriterien Entsprechende Kinovorführungen
         */
	default Streamable<CinemaShow> findCinemaShowsInPeriodOfTime(LocalDateTime fromDateTime, LocalDateTime toDateTime, Film film) {
		return findCinemaShowsInPeriodOfTime(fromDateTime, toDateTime, film, DEFAULT_SORT);
	}

	/**
	 * Finde Alle Kinovorführungen eines Films in einer bestimmten Zeit
	 * @param fromDateTime Startzeitpunkt
	 * @param toDateTime Endzeitpunkt
	 * @param film Kinofilm
	 * @param cinemaHall Kinosaal
	 * @param sort Sortierung
	 * @return Den Kriterien Entsprechende Kinovorführungen
	 */
	@Query("SELECT cs FROM CinemaShow cs WHERE cs.startDateTime BETWEEN :fromDateTime AND :toDateTime AND cs.film = :film AND cs.cinemaHall = :cinemaHall")
	Streamable<CinemaShow> findCinemaShowsInPeriodOfTime(LocalDateTime fromDateTime, LocalDateTime toDateTime, Film film, CinemaHall cinemaHall, Sort sort);

	/**
	 * Finde Alle Kinovorführungen eines Films in einer bestimmten Zeit
	 * @param fromDateTime Startzeitpunkt
	 * @param toDateTime Endzeitpunkt
	 * @param film Kinofilm
	 * @param cinemaHall Kinosaal
	 * @return Den Kriterien Entsprechende Kinovorführungen
	 */
	default Streamable<CinemaShow> findCinemaShowsInPeriodOfTime(LocalDateTime fromDateTime, LocalDateTime toDateTime, Film film, CinemaHall cinemaHall) {
		return findCinemaShowsInPeriodOfTime(fromDateTime, toDateTime, film, cinemaHall, DEFAULT_SORT);
	}

	/**
	 * Finde Alle Kinovorführungen eines Films in einer bestimmten Zeit
	 * @param fromDateTime Startzeitpunkt
	 * @param toDateTime Endzeitpunkt
	 * @param cinemaHall Kinosaal
	 * @param sort Sortierung
	 * @return Den Kriterien Entsprechende Kinovorführungen
	 */
	@Query("SELECT cs FROM CinemaShow cs WHERE cs.startDateTime BETWEEN :fromDateTime AND :toDateTime AND cs.cinemaHall = :cinemaHall")
	Streamable<CinemaShow> findCinemaShowsInPeriodOfTime(LocalDateTime fromDateTime, LocalDateTime toDateTime, CinemaHall cinemaHall, Sort sort);

	/**
	 * Finde Alle Kinovorführungen eines Films in einer bestimmten Zeit
	 * @param fromDateTime Startzeitpunkt
	 * @param toDateTime Endzeitpunkt
	 * @param cinemaHall Kinosaal
	 * @return Den Kriterien Entsprechende Kinovorführungen
	 */
	default Streamable<CinemaShow> findCinemaShowsInPeriodOfTime(LocalDateTime fromDateTime, LocalDateTime toDateTime, CinemaHall cinemaHall) {
		return findCinemaShowsInPeriodOfTime(fromDateTime, toDateTime, cinemaHall, DEFAULT_SORT);
	}

        /**
         * Finde alle Kinovorführungen im Repository eines Films, mit der Angegebenen Sortierung.
         * @param film abzufragender Film
         * @param sort Sortierung
         * @return alle Kinovorführungen im Repository dieses Films.
         */
	@Query("SELECT cs FROM CinemaShow cs WHERE cs.film = :film")
	Streamable<CinemaShow> findAllByFilm(Film film, Sort sort);

        /**
         * Finde alle Kinovorführungen im Repository eines Films
         * @param film abzufragender Film
         * @return alle Kinovorführungen im Repository dieses Films.
         */
	default Streamable<CinemaShow> findAllByFilm(Film film) {
		return findAllByFilm(film, DEFAULT_SORT);
	}

        /**
         * Finde alle Kinovorführungen in einer Bestimmten Woche.
         * @param year Jahr
         * @param week Jahreswoche
         * @return Den Kriterien Entsprechende Kinovorführungen
         */
	default Streamable<CinemaShow> findCinemaShowsInWeek(int year, int week) {
		return findCinemaShowsInPeriodOfTime(
			AdditionalDateTimeWorker.getStartWeekDateTime(year, week),
			AdditionalDateTimeWorker.getEndWeekDateTime(year, week)
		);
	}

        /**
         * Finde alle Kinovorführungen in einer Bestimmten Woche.
         * @param year Jahr
         * @param week Jahreswoche
     * @param sort Sortierung
         * @return Den Kriterien Entsprechende Kinovorführungen
         */
	default Streamable<CinemaShow> findCinemaShowsInWeek(int year, int week, Sort sort) {
		return findCinemaShowsInPeriodOfTime(
			AdditionalDateTimeWorker.getStartWeekDateTime(year, week),
			AdditionalDateTimeWorker.getEndWeekDateTime(year, week),
			sort
		);
	}

        /**
         * Finde alle Kinovorführungen eines Films in einer Bestimmten Woche.
         * @param year Jahr
         * @param week Jahreswoche
     * @param film Kinofilm
         * @return Den Kriterien Entsprechende Kinovorführungen
         */
	default Streamable<CinemaShow> findCinemaShowsInWeek(int year, int week, Film film) {
		return findCinemaShowsInPeriodOfTime(
			AdditionalDateTimeWorker.getStartWeekDateTime(year, week),
			AdditionalDateTimeWorker.getEndWeekDateTime(year, week),
			film
		);
	}

        /**
         * Finde alle Kinovorführungen eines Films in einer Bestimmten Woche.
         * @param year Jahr
         * @param week Jahreswoche
     * @param film Kinofilm
     * @param sort Sortierung
         * @return Den Kriterien Entsprechende Kinovorführungen
         */
	default Streamable<CinemaShow> findCinemaShowsInWeek(int year, int week, Film film, Sort sort) {
		return findCinemaShowsInPeriodOfTime(
			AdditionalDateTimeWorker.getStartWeekDateTime(year, week),
			AdditionalDateTimeWorker.getEndWeekDateTime(year, week),
			film,
			sort
		);
	}

        /**
         * Finde alle Vorführungen an einem Tag
         * @param date Datum
         * @return Den Kriterien Entsprechende Kinovorführungen
         */
	default Streamable<CinemaShow> findCinemaShowsOnDay(LocalDate date) {
		return findCinemaShowsInPeriodOfTime(
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0),
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59, 59)
		);
	}

        /**
         * Finde alle Vorführungen an einem Tag
         * @param date Datum
     * @param sort Sortierung
         * @return Den Kriterien Entsprechende Kinovorführungen
         */
	default Streamable<CinemaShow> findCinemaShowsOnDay(LocalDate date, Sort sort) {
		return findCinemaShowsInPeriodOfTime(
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0),
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59, 59),
			sort
		);
	}

        /**
         * Finde alle Vorführungen eines Films an einem Tag
         * @param date Datum
     * @param film Kinofilm
         * @return Den Kriterien Entsprechende Kinovorführungen
         */
	default Streamable<CinemaShow> findCinemaShowsOnDay(LocalDate date, Film film) {
		return findCinemaShowsInPeriodOfTime(
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0),
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59, 59),
			film
		);
	}

        /**
         * Finde alle Vorführungen eines Films an einem Tag
         * @param date Datum
     * @param film Kinofilm
     * @param sort Sortierung
         * @return Den Kriterien Entsprechende Kinovorführungen
         */
	default Streamable<CinemaShow> findCinemaShowsOnDay(LocalDate date, Film film, Sort sort) {
		return findCinemaShowsInPeriodOfTime(
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0, 0),
			LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), 23, 59, 59),
			film,
			sort
		);
	}

        /**
         * Finde alle Kinovorführungen im Repository, sortiert mit der Standardsortierung.
         * @see #DEFAULT_SORT
         * @return alle Kinovorführungen im Repository
         */
	default Streamable<CinemaShow> findAll() {
		return findAll(DEFAULT_SORT);
	}

        /**
         * Finde alle Kinovorführungen, sortiert mit der angegebenen Sortierung.
         * @param sort Sortierung
         * @return alle Kinovorführungen
         */
	Streamable<CinemaShow> findAll(Sort sort);
}
