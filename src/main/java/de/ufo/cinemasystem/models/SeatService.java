package de.ufo.cinemasystem.models;

import de.ufo.cinemasystem.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Service for seat deduplication.
 * @author Yannick Harnisch
 */
@Service
public class SeatService {

	@Autowired
	private SeatRepository seatRepository;

	/**
	 * Sollte der Sitzplatz noch nicht existieren, so wird dieser erstellt und in die Datenbank eingefügt.
	 * Die reine Speicherung der Sitzplätze ist unabhängig von den Kinosälen, Vorführungen und Events
	 * @param row Reihe des Platzes beginnend bei index 0
	 * @param pos Position des Platzes in jeder Reihe beginnend bei index 0, max. 99
	 * @return Sitzplatz-Objekt an der gewünschten Stelle
	 */
	public Seat getSeatOrCreate(int row, int pos) {
		if(row < 0 || pos < 0)
			throw new IllegalArgumentException("Row und Pos müssen größer gleich Null sein!");
		if(pos >= 100)
			throw new IllegalArgumentException("Die max. Platzposition in einer Reihe beträgt 99!");

		var optSeat = seatRepository.findByRowPos(row, pos);
		if(optSeat.isPresent())
			return optSeat.get();
		Seat seat = new Seat(row, pos);
		seatRepository.save(seat);
		return seat;
	}
}
