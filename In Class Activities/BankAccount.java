public class BankAccount {
    private String ownerName;
    private double balance;
    private String pin;

    public BankAccount(String ownerName, double balance, String pin) {
        this.ownerName = ownerName;
        this.balance = balance;
        this.pin = pin;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public boolean withdraw(double amount, String enteredPin) {
        if (enteredPin.equals(pin) && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public double getBalance() {
        return balance;
    }
}

public class Main {
    public static void main(String[] args) {
        BankAccount acct = new BankAccount("Alex", 100.0, "1234");
        acct.deposit(50);
        //acct.balance = 1000000;   // <-- should NOT be allowed
        //System.out.println(acct.pin); // <-- should NOT be allowed
    }
}