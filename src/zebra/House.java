package zebra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

enum Color { RED, GREEN, YELLOW, WHITE, BLUE }
enum Nationality { ENGLISH, SWEDE, DANE, NORWEGIAN, GERMAN }
enum Animal { DOG, CAT, HORSE, BIRD, ZEBRA }
enum Drink { TEA, COFFEE, MILK, WATER, BEER }
enum SmokeBrand { PALLMALL, DUNHILL, BLEND, BLUEMASTER, PRINCE }

class House {
    
    static House h1 = new House(1);
    static House h2 = new House(2);
    static House h3 = new House(3);
    static House h4 = new House(4);
    static House h5 = new House(5);
    static List<House> houses = new ArrayList<>(List.of(h1, h2, h3, h4, h5));
    
    private final int position;
    
    EnumSet<Color> colors = EnumSet.allOf(Color.class);
    EnumSet<Nationality> nationalities = EnumSet.allOf(Nationality.class);
    EnumSet<Animal> animals = EnumSet.allOf(Animal.class);
    EnumSet<Drink> drinks = EnumSet.allOf(Drink.class);
    EnumSet<SmokeBrand> smokeBrands = EnumSet.allOf(SmokeBrand.class);
    
    int getPosition() { return position; }
    
    private House(int position) {
        this.position = position;
    }
    
    private EnumSet getAttributeList(Enum a) {
        String type = a.getClass().getSimpleName();
        switch (type) {
            case "Color":
                return this.colors;
            case "Nationality":
                return this.nationalities;
            case "Animal":
                return this.animals;
            case "Drink":
                return this.drinks;
            case "SmokeBrand":
                return this.smokeBrands;
        }
        
        return null;
    }
    
    
    /**
     * @return a list of houses excluding this one
     */
    private List<House> getOtherHouses() {
        List<House> otherHouses = new ArrayList<>(List.copyOf(houses));
        otherHouses.remove(this);
        return otherHouses;
    }
    
    private List<House> getNeighbours() {
        if (this.equals(House.h1)) { return List.of(House.h2); }
        else if (this.equals(House.h5)) { return List.of(House.h4); }
        else { return List.of(houses.get(houses.indexOf(this) - 1), houses.get(houses.indexOf(this) + 1)); }
    }
    
    // assign this attribute value to this house -> removes it from others
    // returns true if any change occurred
    boolean assign(Enum a) {
        EnumSet aList = getAttributeList(a);
        boolean assigned = aList.retainAll(Collections.singleton(a));
        boolean removedFromOthers = removeFromOthers(a);
        return assigned || removedFromOthers;
    }
    
    // remove attribute from this house if it is present;
    // returns true if any change occurred
    // if, following remove, an attribute list is left with only one attribute, remove it from the others
    private boolean removeAttribute(Enum a) {
        EnumSet aList = this.getAttributeList(a);
        if (aList.contains(a) && aList.size() > 1) {
            boolean changed = aList.remove(a);
            if (aList.size() == 1) {
                removeFromOthers((Enum) new ArrayList(aList).get(0));
            }
            return changed;
        }
        else {
            return false;
        }
    }
    
    // remove attribute from other houses
    // returns true if any change occurred
    private boolean removeFromOthers(Enum a) {
        ArrayList<Boolean> changed = new ArrayList<>();
        for (House otherHouse : this.getOtherHouses()) {
            changed.add(otherHouse.removeAttribute(a));
        }
        return changed.contains(true);
    }
    
    private boolean hasDefinitely(Enum a) {
        return this.getAttributeList(a).equals(EnumSet.of(a));
    }
    
    private boolean hasPossibly(Enum a) {
        return this.getAttributeList(a).contains(a);
    }
    
    // if a1 is 'bound' to a2:
    //      - if a house definitely has a1 -> it definitely has a2
    //      - if a house definitely has a2 -> it definitely has a1
    //      - if a house doesn't have a1 -> remove a2 from it
    //      - if a house doesn't have a2 -> remove a1 from it
    static boolean assignPair(Enum a1, Enum a2) {
        ArrayList<Boolean> changed = new ArrayList<>();
    
        for (House house : houses) {
            if (house.hasDefinitely(a1)) {
                changed.add(house.removeFromOthers(a1));
                changed.add(house.assign(a2));
            }
            if (house.hasDefinitely(a2)) {
                changed.add(house.removeFromOthers(a2));
                changed.add(house.assign(a1));
            }
            if (!house.hasPossibly(a1)) {
                changed.add(house.removeAttribute(a2));
            }
            if (!house.hasPossibly(a2)) {
                changed.add(house.removeAttribute(a1));
            }
        }
        return changed.contains(true);
    }
    
    
    // a1 adjacent to a2 means:
    //      -> if a house which definitely has a1 has only one adjacent house -> that house will have a2
    //      -> if a house which definitely has a2 has only one adjacent house -> that house will have a1
    //      -> if a house which possibly has a1, has no adjacent house which possibly have a2 -> remove a1
    //      from that house
    //      -> if a house which possibly has a2, has no adjacent house which possibly have a1 -> remove a2
    //      from that house
    // Returns true if any changes occurred.
    static boolean assignNextTo(Enum a1, Enum a2) {
        ArrayList<Boolean> changed = new ArrayList<>();
        for (House house : houses) {
            if (house.hasDefinitely(a1) && house.getNeighbours().size() == 1) {
                changed.add(house.getNeighbours().get(0).assign(a2));
            }
            if (house.hasDefinitely(a2) && house.getNeighbours().size() == 1) {
                changed.add(house.getNeighbours().get(0).assign(a1));
            }
            
            if (house.hasPossibly(a1)) {
                int minValidNeighbours = 0;
                for (House neighbour : house.getNeighbours()) {
                    if (neighbour.hasPossibly(a2)) {
                        minValidNeighbours += 1;
                    }
                }
                if (minValidNeighbours == 0) {
                    changed.add(house.removeAttribute(a1));
                }
            }
            
            if (house.hasPossibly(a2)) {
                int minValidNeighbours = 0;
                for (House neighbour : house.getNeighbours()) {
                    if (neighbour.hasPossibly(a1)) {
                        minValidNeighbours += 1;
                    }
                }
                if (minValidNeighbours == 0) {
                    changed.add(house.removeAttribute(a2));
                }
            }
        }
        return changed.contains(true);
    }
    
    // if a1 is immediately to the left of a2:
    //      - a2 cannot be the first house -> remove a2 from house1
    //      - a1 cannot be the last house -> remove a1 from house5
    //      - for houses 2:5:
    //              - if house definitely has a2 -> house-1 definitely has a1
    //              - if house does not have a2 -> house-1 does not have a1
    //      - for houses 1:4:
    //              - if house definitely has a1 -> house+1 definitely has a2
    //              - if house does not have a1 -> house+1 does not have a2
    // Returns true if any changes occurred.
    static boolean assignLeftOf(Enum a1, Enum a2) {
        ArrayList<Boolean> changed = new ArrayList<>();
        changed.add(House.h1.removeAttribute(a2));
        changed.add(House.h5.removeAttribute(a1));
        
        for (House house : houses.subList(1, 5)) {
            if (house.hasDefinitely(a2)) {
                changed.add(houses.get(houses.indexOf(house) - 1).assign(a1));
            }
            if (!house.hasPossibly(a2)) {
                changed.add(houses.get(houses.indexOf(house) - 1).removeAttribute(a1));
            }
        }
        
        for (House house : houses.subList(0, 4)) {
            if (house.hasDefinitely(a1)) {
                changed.add(houses.get(houses.indexOf(house) + 1).assign(a2));
            }
            if (!house.hasPossibly(a1)) {
                changed.add(houses.get(houses.indexOf(house) + 1).removeAttribute(a2));
            }
        }
        return changed.contains(true);
    }
}
