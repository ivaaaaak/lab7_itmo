package com.ivaaaak.client;

import com.ivaaaak.common.util.PersonMakeable;
import com.ivaaaak.common.data.Coordinates;
import com.ivaaaak.common.data.Location;
import com.ivaaaak.common.data.Person;
import com.ivaaaak.common.data.Color;
import com.ivaaaak.common.data.Country;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public class PersonMaker implements PersonMakeable {

    private final String ownerName;
    private final InputManager inputManager;
    private final Checker checker;

    public PersonMaker(InputManager inputManager, String ownerName) {
        this.inputManager = inputManager;
        this.checker = new Checker(inputManager);
        this.ownerName = ownerName;
    }

    public Person makePerson() {
        String name = checker.check("Enter NAME: (cannot be an empty string)",
                arg -> (arg).length() > 0,
                "Cannot be an empty string. Please try again:",
                x -> x,
                false);
        Coordinates coordinates = makeCoordinates();
        float height = checker.check("Enter HEIGHT (more than 0, float):",
                arg -> (arg) > 0,
                "Please enter numerical value (more than 0)",
                Float::parseFloat,
                false);
        float weight = checker.check("Enter WEIGHT (more than 0, float):",
                arg -> (arg) > 0,
                "Please enter numerical value (more than 0)",
                Float::parseFloat,
                false);
        System.out.println(Arrays.toString(Color.values()));
        Color hairColor = checker.check("Enter the COLOR exactly as it is printed above:",
                arg -> true,
                "Wrong input. Please try again:",
                Color::valueOf,
                false);
        System.out.println(Arrays.toString(Country.values()));
        Country nationality = checker.check("Enter NATIONALITY exactly as it is printed above (can be null):",
                arg -> true,
                "Wrong input. Please try again:",
                Country::valueOf,
                true);

        Location location = null;
        System.out.println("If you want to initialize LOCATION, type \"+\"");
        String answer = inputManager.readLine();
        if (!answer.isEmpty() && "+".equals(answer)) {
            location = makeLocation();
        }
        return new Person(name, coordinates, height, weight, hairColor, nationality, location, ownerName);
    }

    public Coordinates makeCoordinates() {

        final int max = 172;

        Integer x = checker.check("Enter an integer value of X coordinate (no more than 172):",
                arg -> (arg) <= max,
                "Please enter numerical value (no more than 172):",
                Integer::parseInt,
                false);

        Double y = checker.check("Enter a double value of Y coordinate:",
                arg -> true,
                "Please enter numerical value:",
                Double::parseDouble,
                false);

        return new Coordinates(x, y);
    }

    public Location makeLocation() {

        Long x = checker.check("Enter location X (long):",
                arg -> true,
                "Please enter numerical value:",
                Long::parseLong,
                false);

        Integer y = checker.check("Enter integer location Y:",
                arg -> true,
                "Please enter numerical value:",
                Integer::parseInt,
                false);

        int z = checker.check("Enter integer location Z:",
                arg -> true,
                "Please enter numerical value:",
                Integer::parseInt,
                false);

        return new Location(x, y, z);
    }

    public static class Checker {

        private final InputManager inputManager;

        public Checker(InputManager inputManager) {
            this.inputManager = inputManager;
        }

        public <T> T check(String message,
                           Predicate<T> predicate,
                           String errorMessage,
                           Function<String, T> converter,
                           boolean canBeNull) {

            System.out.println(message);

            while (true) {
                String input = inputManager.readLine();
                if (input.isEmpty() && canBeNull) {
                    return null;
                }
                T value;
                try {
                    value = converter.apply(input);
                } catch (IllegalArgumentException e) {
                    System.out.println(errorMessage);
                    continue;
                }
                if (predicate.test(value)) {
                    return value;
                } else {
                    System.out.println(errorMessage);
                }
            }
        }
    }

}
