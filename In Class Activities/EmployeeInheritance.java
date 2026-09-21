public class EmployeeInheritance {
    
    class Employee {
        protected String name;
        protected double baseSalary;

        public Employee (String name, double baseSalary) {
            this.name = name;
            this.baseSalary = baseSalary;
        }

        public double calculateSalary() {
            return baseSalary;
        }

        public void printInfo() {
            System.out.println("Employee's Name: " + name);
            System.out.println("Calculated Salary: " + calculateSalary());
        }
    }

    class Manager extends Employee {
        private double bonus;

        public Manager(String name, double baseSalary, double bonus) {
            super(name, baseSalary);
            this.bonus = bonus;
        }

        public double calculateSalary() {
            return baseSalary + bonus;
        }
    }

    class Salesperson extends Employee {
        private double sales;
        private double commissionRate;

        public Salesperson(String name, double baseSalary, double sales, double commissionRate) {
            super(name, baseSalary);
            this.sales = sales;
            this.commissionRate = commissionRate;
        }

        public double calculateSalary() {
            return baseSalary + (sales * commissionRate);
        }
    }
}
