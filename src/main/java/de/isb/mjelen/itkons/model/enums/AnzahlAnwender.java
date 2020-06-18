package de.isb.mjelen.itkons.model.enums;

import com.github.javafaker.service.RandomService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AnzahlAnwender {
  XXS(1, "XXS"), XS(10, "XS"), S(50, "S"), M(100, "M"), L(500, "L"), XL(1000, "XL"), XXL(10000, "XXL"), UNBEKANNT(-1, "unbekannt");

  private final int anzahlMax;
  private final String beschreibung;

  public static AnzahlAnwender getRandom(RandomService random) {
    int randomInt = random.nextInt(AnzahlAnwender.values().length);
    return values()[randomInt];
  }

  public String toString() {
    if (UNBEKANNT == this) {
      return beschreibung;
    } else {
      return beschreibung + " (" + anzahlMax + ")";
    }
  }
}
