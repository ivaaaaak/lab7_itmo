package com.ivaaaak.common.util;

import com.ivaaaak.common.data.Coordinates;
import com.ivaaaak.common.data.Location;
import com.ivaaaak.common.data.Person;

public interface PersonMakeable {
    Person makePerson();
    Coordinates makeCoordinates();
    Location makeLocation();

}
