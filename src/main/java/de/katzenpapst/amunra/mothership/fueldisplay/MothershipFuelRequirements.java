package de.katzenpapst.amunra.mothership.fueldisplay;

import java.util.HashMap;
import java.util.Map;

public class MothershipFuelRequirements {

    protected Map<MothershipFuelDisplay, Integer> data;

    public MothershipFuelRequirements() {
        data = new HashMap<MothershipFuelDisplay, Integer>();
    }

    public void add(MothershipFuelDisplay fuel, int amount) {
        if(!data.containsKey(fuel)) {
            data.put(fuel, amount);
        } else {
            data.put(fuel, data.get(fuel)+amount);
        }
    }

    public void merge(MothershipFuelRequirements other) {
        for(MothershipFuelDisplay fuel: other.data.keySet()) {
            add(fuel, other.data.get(fuel));
        }
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int get(MothershipFuelDisplay key) {
        if(data.containsKey(key)) {
            return data.get(key);
        }
        return 0;
    }

    public Map<MothershipFuelDisplay, Integer> getData() {
        return data;
    }
}
