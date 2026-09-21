public class Payments {
    void pay(double amount);
}


class CreditCard extends Payments {
    private String cardNumber;
    private String name;

    public CreditCard(String cardNumber, String name) {
        this.cardNumber = cardNumber;
        this.name = name;
    }

    @Override
    public void pay(double amount) {
        System.out.printf("Paid: ", amount, " using Credit Card.");
    }
}

class Venmo extends Payments {
    private String userName;

    public Venmo(String userName) {
        this.userName = userName;
    }

    @Override
    public void pay(double amount) {
        System.out.printf("Paid: ", amount, " using Venmo.");
    }
}

public static void main(String[] args) {
        Payments myCreditCard = new CreditCard("1111222233334444", "Nathan Smith");
        Payments myVenmo = new Venmo("nathan_smith");
}