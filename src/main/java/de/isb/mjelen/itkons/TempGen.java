package de.isb.mjelen.itkons;

import com.github.javafaker.Faker;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TempGen {

  private final Random rnd;
  private final Faker faker;

  private TempGen() {
    rnd = new Random(System.currentTimeMillis());
    faker = new Faker(Locale.ENGLISH, rnd);
  }

  public static void main(String... args) throws IOException {
    TempGen gen = new TempGen();
    //gen.genNames();
    gen.genSuche();
  }

  private void genSuche() {
    for (int i = 0; i < 15000; i++) {
      System.out.println(faker.name().firstName());
    }
  }

  private void genNames() {
    for (int i = 0; i < 40000; i++) {
      Date geburt = faker.date().between(new Date(System.currentTimeMillis() - 99l*365*24*60*60*1000), new Date(System.currentTimeMillis() - 15l*365*24*60*60*1000));
      //String geburtString = geburt.getDay() + "." + geburt.getMonth() + "." + geburt.getYear();
      DateFormat sdf = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM, Locale.GERMANY);
      String geburtString = sdf.format(geburt);
      String vorname = faker.name().firstName();
      String nachname = faker.name().lastName();
      System.out.println(
          ";" +
              nachname + ";" +
              vorname + ";" +
              geburtString + ";;" +
              faker.address().cityName() + ";" +
              faker.bothify("AZ.???/????.??-?") + ";" +
              faker.letterify("?") + ";" +
              nachname + ";" +
              vorname + ";" +
              geburtString + ";;"
      );
    }
  }

}
