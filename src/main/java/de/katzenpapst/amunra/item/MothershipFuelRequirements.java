package de.katzenpapst.amunra.item;

import java.util.HashMap;
import java.util.Map;

public class MothershipFuelRequirements {

    protected Map<MothershipFuel, Integer> data;

    public MothershipFuelRequirements() {
        data = new HashMap<MothershipFuel, Integer>();
    }

    public void add(MothershipFuel fuel, int amount) {
        if(!data.containsKey(fuel)) {
            data.put(fuel, amount);
        } else {
            data.put(fuel, data.get(fuel)+amount);
        }
    }

    public void merge(MothershipFuelRequirements other) {
        for(MothershipFuel fuel: other.data.keySet()) {
            add(fuel, other.data.get(fuel));
        }
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int get(MothershipFuel key) {
        if(data.containsKey(key)) {
            return data.get(key);
        }
        return 0;
    }

    public Map<MothershipFuel, Integer> getData() {
        return data;
    }
}
