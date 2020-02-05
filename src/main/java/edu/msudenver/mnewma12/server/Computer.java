package edu.msudenver.mnewma12.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Computer {

    public static final List<Computer> COMPUTERS = new ArrayList<Computer>() {{
        add(new Computer(1, "New Inspiron 15", 379.99, 157));
        add(new Computer(2, "New Inspiron 17", 449.99, 128));
        add(new Computer(3, "New Inspiron 15R", 549.99, 202));
        add(new Computer(4, "New Inspiron 15z Ultrabook", 749.99, 315));
        add(new Computer(5, "XPS 14 Ultrabook", 999.99, 261));
        add(new Computer(6, "New XPS 12 UltrabookXPS", 1199.99, 178));
    }};

    public static final Map<Integer, Computer> ID_TO_COMPUTER =
            COMPUTERS.stream().collect(Collectors.toMap(c -> c.ID, c -> c));

    public int ID;

    public String description;

    private double price;

    private int count;

    public Computer() {}

    public Computer(int ID, String description, double price, int count) {
        this.ID = ID;
        this.description = description;
        this.price = price;
        this.count = count;
    }

    @Override
    public String toString() {
        return "#" + ID + " " + description + " $" + price + " (" + count + " available)";
    }
}
