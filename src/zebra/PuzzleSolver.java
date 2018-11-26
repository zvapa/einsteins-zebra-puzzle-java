package zebra;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class PuzzleSolver {
    
    public static void main(String[] args) {
        
        // attributes are assigned to the 5 houses (clue 1)
        // houses start with complete lists of attributes, and values are excluded as clues are introduced
        
        // starting with clues which are direct assignments:
        // clue 9: In the middle house they drink milk.
        House.h3.assign(Drink.MILK);
        // clue 10: The Norwegian lives in the first house.
        House.h1.assign(Nationality.NORWEGIAN);
        
        // declaring an array of booleans to track changes
        ArrayList<Boolean> changes = new ArrayList<>();
        
        // iterate through the remaining clues multiple times, until no more changes occur
        do {
            changes.clear();
            // clue 2: The English man lives in the red house
            changes.add(House.assignPair(Nationality.ENGLISH, Color.RED));
            // clue 3: The Swede has a dog
            changes.add(House.assignPair(Nationality.SWEDE, Animal.DOG));
            // clue 4: The Dane drinks tea.
            changes.add(House.assignPair(Nationality.DANE, Drink.TEA));
            // clue 5: The green house is immediately to the left of the white house.
            changes.add(House.assignLeftOf(Color.GREEN, Color.WHITE));
            // clue 6: They drink coffee in the green house.
            changes.add(House.assignPair(Drink.COFFEE, Color.GREEN));
            // clue 7: The man who smokes Pall Mall has birds.
            changes.add(House.assignPair(SmokeBrand.PALLMALL, Animal.BIRD));
            // clue 8: In the yellow house they smoke Dunhill.
            changes.add(House.assignPair(Color.YELLOW, SmokeBrand.DUNHILL));
            // clue 11: The man who smokes Blend lives in the house next to the house with cats.
            changes.add(House.assignNextTo(SmokeBrand.BLEND, Animal.CAT));
            // clue 12: In a house next to the house where they have a horse, they smoke Dunhill.
            changes.add(House.assignNextTo(Animal.HORSE, SmokeBrand.DUNHILL));
            // clue 13: The man who smokes Blue Master drinks beer.
            changes.add(House.assignPair(SmokeBrand.BLUEMASTER, Drink.BEER));
            // clue 14: The German smokes Prince.
            changes.add(House.assignPair(Nationality.GERMAN, SmokeBrand.PRINCE));
            // clue 15: The Norwegian lives next to the blue house.!!!
            changes.add(House.assignNextTo(Nationality.NORWEGIAN, Color.BLUE));
            // clue 16: They drink water in a house next to the house where they smoke Blend.
            changes.add(House.assignNextTo(Drink.WATER, SmokeBrand.BLEND));
        } while (changes.contains(true));
        
        // finally, print the attributes for all houses
        for (House house : House.houses) {
            System.out.println(
                    house.getPosition() + "\n" +
                    house.colors + "\n" +
                    house.nationalities + "\n" +
                    house.animals + "\n" +
                    house.drinks + "\n" +
                    house.smokeBrands + "\n"
            );
        }
    }
}
