package de.isb.mjelen.itkons.model;

import de.isb.mjelen.itkons.model.Standard.StandardKonformitaetStufe;
import de.isb.mjelen.itkons.model.enums.AnzahlAnwender;
import de.isb.mjelen.itkons.poi.ElementIdentifiable;
import de.isb.mjelen.itkons.poi.ModelDescriptor;
import de.isb.mjelen.itkons.poi.PoiSupport;
import de.isb.mjelen.itkons.poi.StatefulPoiHelper;
import java.time.LocalDate;
import java.util.Set;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class Informationssystem extends PoiSupport {

  private String code;
  private String name;
  private String beschreibung;
  private String verantwortlich;
  private Integer anzahlInstallationen;
  private AnzahlAnwender anzahlAnwender;
  private Integer investitionGroesse;
  private LocalDate produktivSeit;
  private Set<Organisationseinheit> eingesetztIn;
  private Set<Subsystem> subsysteme;
  private Set<Technologie> technologien;
  private Set<StandardKonformitaet> standardKonformitaet;

  @Override
  public void buildContentRow(StatefulPoiHelper poiHelper, int index) {
    poiHelper.buildContentRowFromStringArray(index, new String[]{
        getCode(),
        getName(),
        getBeschreibung(),
        getVerantwortlich(),
        serializeInteger(getAnzahlInstallationen()),
        getAnzahlAnwender().toString(),
        serializeSet(getSubsysteme()),
        serializeInteger(getInvestitionGroesse()),
        serializeSet(getTechnologien()),
        serializeDate(getProduktivSeit()),
        serializeSet(getEingesetztIn()),
        serializeSet(getStandardKonformitaet())
    });
  }

  @Override
  public Class<? extends ModelDescriptor> getModelDescriptor() {
    return InformationssystemMD.class;
  }

  @Override
  public String getIdentifier() {
    return getCode();
  }

  public static class InformationssystemMD implements ModelDescriptor {

    private static final String[] columns = new String[]{
        "Code",
        "Name",
        "Beschreibung",
        "Verantwortlich",
        "Anzahl Installationen",
        "Anzahl Anwender (Max.)",
        "Subsysteme",
        "Investitionsgröße",
        "Technologien",
        "Eingesetzt seit",
        "Eingesetzt in",
        "Standardkonformität"};

    @Override
    public void buildHeaderRow(StatefulPoiHelper poiHelper) {
      poiHelper.buildHeaderFromTitleArray(columns);
    }

    @Override
    public void autosizeColumns(StatefulPoiHelper poiHelper) {
      poiHelper.autosizeHelper(columns.length);
    }
  }

  @RequiredArgsConstructor
  public static class StandardKonformitaet implements ElementIdentifiable {
    private final Standard standard;
    private final StandardKonformitaetStufe konformitaetStufe;

    @Override
    public String getIdentifier() {
      return standard.getCode() + ":" + konformitaetStufe;
    }
  }
}
