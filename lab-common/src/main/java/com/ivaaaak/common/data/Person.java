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
    private final String ownerName;

    @SuppressWarnings("ParameterNumber")
    public Person(String name,
                  Coordinates coordinates,
                  float height,
                  float weight,
                  Color hairColor,
                  Country nationality,
                  Location location,
                  String ownerName) {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = LocalDateTime.now();
        this.height = height;
        this.weight = weight;
        this.hairColor = hairColor;
        this.nationality = nationality;
        this.location = location;
        this.ownerName = ownerName;
    }
    @SuppressWarnings("ParameterNumber")
    public Person(String name,
                  Coordinates coordinates,
                  LocalDateTime creationDate,
                  float height,
                  float weight,
                  Color hairColor,
                  Country nationality,
                  Location location,
                  String ownerName) {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.height = height;
        this.weight = weight;
        this.hairColor = hairColor;
        this.nationality = nationality;
        this.location = location;
        this.ownerName = ownerName;
    }

    @Override
    public String toString() {
        return "Person{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", coordinates=" + coordinates
                + ", creationDate=" + creationDate
                + ", height=" + height
                + ", weight=" + weight
                + ", hairColor=" + hairColor
                + ", nationality=" + nationality
                + ", location=" + location
                + ", ownerName='" + ownerName + '\''
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

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getKey() {
        return key;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public int compareTo(Person o) {
        return (int) (this.height - o.getHeight());
    }

}
