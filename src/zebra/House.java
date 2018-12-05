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
    
    /**
     * Gets this house's position (1, 2, etc.).
     *
     * @return the house pre-defined position
     */
    int getPosition() { return position; }
    
    private House(int position) {this.position = position;}
    
    /**
     * Gets this house's list of possible attribute values for the type corresponding to the given value.
     *
     * @param a the attribute value
     * @return the list of all possible attribute values for this house, or null if no valid attribute type is provided
     */
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
     * Gets a list of houses other than the current one.
     *
     * @return the list of other houses
     */
    private List<House> getOtherHouses() {
        List<House> otherHouses = new ArrayList<>(List.copyOf(houses));
        otherHouses.remove(this);
        return otherHouses;
    }
    
    /**
     * Returns the list of neighbouring houses.
     * If called on house1, it returns a list containing just house2. If called on house5 (the last house), it returns a
     * list containing just house4. For all other houses it returns the houses to the left and right of the current
     * house.
     *
     * @return a list of houses representing the neighbours of this this house.
     */
    private List<House> getNeighbours() {
        if (this.equals(House.h1)) { return List.of(House.h2); }
        else if (this.equals(House.h5)) { return List.of(House.h4); }
        else { return List.of(houses.get(houses.indexOf(this) - 1), houses.get(houses.indexOf(this) + 1)); }
    }
    
    /**
     * Assigns this attribute value to this house, and removes it from the other houses.
     *
     * @param a the attribute to be assigned
     * @return <code>true</code> if any changes occurred as a result, or <code>false</code> otherwise
     */
    boolean assign(Enum a) {
        EnumSet aList = getAttributeList(a);
        boolean assigned = aList.retainAll(Collections.singleton(a));
        boolean removedFromOthers = removeFromOthers(a);
        return assigned || removedFromOthers;
    }
    
    /**
     * Removes the attribute from this house if it is present.
     * If following remove, the attribute's list is left with only one value, this value is deemed the
     * <i>definitive</i> value for this attribute in this house, and is then removed from the other houses.
     *
     * @return <code>true</code> if any changes occurred as a result, or <code>false</code> otherwise
     */
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
    
    /**
     * Removes this attribute value from the other houses.
     *
     * @return <code>true</code> if any changes occurred as a result, or <code>false</code> otherwise
     */
    private boolean removeFromOthers(Enum a) {
        ArrayList<Boolean> changed = new ArrayList<>();
        for (House otherHouse : this.getOtherHouses()) {
            changed.add(otherHouse.removeAttribute(a));
        }
        return changed.contains(true);
    }
    
    /**
     * Checks if this value is definitive for this attribute in this house.
     *
     * @return <code>true</code> if the specified value is the <i>definitive</i> value for this attribute
     * in this house, or <code>false</code> otherwise
     */
    private boolean hasDefinitely(Enum a) {
        return this.getAttributeList(a).equals(EnumSet.of(a));
    }
    
    /**
     * Checks if this value is possible for this attribute in this house.
     *
     * @return <code>true</code> if the specified value is <i>possible</i> for this attribute in this house, or
     * <code>false</code> otherwise
     */
    private boolean hasPossibly(Enum a) {
        return this.getAttributeList(a).contains(a);
    }
    
    /**
     * Binds two attributes together. House with attribute {@code a1} will also have {@code a2}, and vice-versa.
     *
     * @param a1 first attribute
     * @param a2 second attribute
     * @return <code>true</code> if any changes occurred as a result, or <code>false</code> otherwise
     */
    static boolean assignPair(Enum a1, Enum a2) {
        ArrayList<Boolean> changed = new ArrayList<>();
        
        for (House house : houses) {
            // if a house definitely has a1, it definitely has a2;
            if (house.hasDefinitely(a1)) {
                changed.add(house.removeFromOthers(a1));
                changed.add(house.assign(a2));
            }
            // if a house definitely has a2, it definitely has a1;
            if (house.hasDefinitely(a2)) {
                changed.add(house.removeFromOthers(a2));
                changed.add(house.assign(a1));
            }
            // if a house doesn't have a1, remove a2 from it;
            if (!house.hasPossibly(a1)) {
                changed.add(house.removeAttribute(a2));
            }
            // if a house doesn't have a2, remove a1 from it;
            if (!house.hasPossibly(a2)) {
                changed.add(house.removeAttribute(a1));
            }
        }
        return changed.contains(true);
    }
    
    /**
     * Assigns the given attributes so that they are next to each other.
     *
     * @return <code>true</code> if any changes occurred as a result, or <code>false</code> otherwise
     */
    static boolean assignNextTo(Enum a1, Enum a2) {
        ArrayList<Boolean> changed = new ArrayList<>();
        for (House house : houses) {
            // if a house which definitely has a1, has only one adjacent house -> that house will definitely have a2
            if (house.hasDefinitely(a1) && house.getNeighbours().size() == 1) {
                changed.add(house.getNeighbours().get(0).assign(a2));
            }
            // if a house which definitely has a2, has only one adjacent house -> that house will definitely have a1
            if (house.hasDefinitely(a2) && house.getNeighbours().size() == 1) {
                changed.add(house.getNeighbours().get(0).assign(a1));
            }
            // if a house which possibly has a1, has no adjacent houses which possibly have a2 -> remove a1
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
            // if a house which possibly has a2, has no adjacent houses which possibly have a1 -> remove a2
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
    
    /**
     * Assigns the first attribute to the house the left of the house with the second attribute.
     *
     * @param a1 first attribute
     * @param a2 second attribute
     * @return <code>true</code> if any changes occurred as a result, or <code>false</code> otherwise
     */
    static boolean assignLeftOf(Enum a1, Enum a2) {
        ArrayList<Boolean> changed = new ArrayList<>();
        // a2 cannot be the first house -> remove a2 from house1
        changed.add(House.h1.removeAttribute(a2));
        // a1 cannot be the last house -> remove a1 from house5
        changed.add(House.h5.removeAttribute(a1));
        
        // for houses 2:5:
        for (House house : houses.subList(1, 5)) {
            // if house definitely has a2 -> house-1 definitely has a1
            if (house.hasDefinitely(a2)) {
                changed.add(houses.get(houses.indexOf(house) - 1).assign(a1));
            }
            // if house does not have a2 -> house-1 does not have a1
            if (!house.hasPossibly(a2)) {
                changed.add(houses.get(houses.indexOf(house) - 1).removeAttribute(a1));
            }
        }
        // for houses 1:4:
        for (House house : houses.subList(0, 4)) {
            // if house definitely has a1 -> house+1 definitely has a2
            if (house.hasDefinitely(a1)) {
                changed.add(houses.get(houses.indexOf(house) + 1).assign(a2));
            }
            // if house does not have a1 -> house+1 does not have a2
            if (!house.hasPossibly(a1)) {
                changed.add(houses.get(houses.indexOf(house) + 1).removeAttribute(a2));
            }
        }
        return changed.contains(true);
    }
}
