package de.ufo.cinemasystem.models;

@Entity
@Table( public lass Orders extends Order {

    //      pr      private Money SnacksSumme;
    private CinemaShow show;

    @SuppressWarnings({ "unused", "deprecation" })
    private Orders() {
    }

    public Orders(UserAccountIdentifier useraccountidentifier, CinemaShow show) {
        super(useraccountide       this.order = new Order(useraccountidentifier);
        this.TicketSumme = Money.of(0, "EUR");
        this.SnacksSumme = Money.of(0, "EUR");
        this.show = show;
    }

    public Money getTicketSumme() {
        return TicketSumme;
    }


    public Money getSnacksSumme() {
        return SnacksSumme;
    }

    public CinemaShow getCinemaShow() {
        return show;
    }
    
    public Money addSnacks(Snacks snack) {
        order.addOrderLine(snack, Quantity.of(1));
        SnacksSumme.add(snack.getPrice(

     oney addTicket(Tick
    o

    return TicketSumme;
    }
}







