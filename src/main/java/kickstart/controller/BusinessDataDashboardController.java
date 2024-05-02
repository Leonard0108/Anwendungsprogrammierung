package kickstart.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

//benötigte Klassen: Bestellung, (Ausgaben für Personalkosten, Ausgaben für momentan geliehene Filme)
@Controller
public class BusinessDataDashboardController {

	//Anzeigen von Tageseinnahmen Diagramm, darunter Monatsumsatz Diagramm
	@GetMapping("/business-data-dashboard")
	//@PreAuthorize("hasRole('BOSS')")
	public String getDashboard(Model m){
		return "business-data-dashboard-boss-renderer";
	}

}
