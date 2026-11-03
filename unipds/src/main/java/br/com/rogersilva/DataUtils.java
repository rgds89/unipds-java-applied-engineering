package br.com.rogersilva;

import net.datafaker.Faker;

import java.util.Locale;

public class DataUtils {
    public String geraNome(){
        Faker faker = new Faker(new Locale( "pt", "BR" ));
        return faker.name().fullName();
    }
}
