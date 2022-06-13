package com.ivaaaak.common.data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Person implements Comparable<Person>, Serializable {

    private static final long serialVersionUID = 1462127365705758780L;

    private Integer id; //не null, больше 0, уникальное, генерируется автоматически
    private final String name; //не null, строка не пустая
    private final Coordinates coordinates; //не null
    private final LocalDateTime creationDate; //не null, генерируется автоматически
    private final float height; //больше 0
    private final float weight; //больше 0
    private final Color hairColor; //не null
    private final Country nationality; //может быть null
    private final Location location; // может быть null
    private Integer key;

    @SuppressWarnings("ParameterNumber")
    public Person(String name,
                  Coordinates coordinates,
                  float height,
                  float weight,
                  Color hairColor,
                  Country nationality,
                  Location location) {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDateTime.now();
        this.height = height;
        this.weight = weight;
        this.hairColor = hairColor;
        this.nationality = nationality;
        this.location = location;
    }
    @SuppressWarnings("ParameterNumber")
    public Person(String name,
                  Coordinates coordinates,
                  LocalDateTime creationDate,
                  float height,
                  float weight,
                  Color hairColor,
                  Country nationality,
                  Location location) {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.height = height;
        this.weight = weight;
        this.hairColor = hairColor;
        this.nationality = nationality;
        this.location = location;
    }

    @Override
    public String toString() {
        return key + " = Person{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", coordinates=" + coordinates
                + ", creationDate=" + creationDate
                + ", height=" + height
                + ", weight=" + weight
                + ", hairColor=" + hairColor
                + ", nationality=" + nationality
                + ", location=" + location + '\''
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Person person = (Person) o;
        return Float.compare(person.height, height) == 0 && Float.compare(person.weight, weight) == 0 && Objects.equals(id, person.id) && Objects.equals(name, person.name) && Objects.equals(coordinates, person.coordinates) && Objects.equals(creationDate, person.creationDate) && hairColor == person.hairColor && nationality == person.nationality && Objects.equals(location, person.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, height, weight, hairColor, nationality, location);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public Location getLocation() {
        return location;
    }

    public float getHeight() {
        return height;
    }

    public float getWeight() {
        return weight;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public Country getNationality() {
        return nationality;
    }

    public Integer getKey() {
        return key;
    }


    @Override
    public int compareTo(Person o) {
        return (int) (this.height - o.getHeight());
    }

}
