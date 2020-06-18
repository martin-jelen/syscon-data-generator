package de.isb.mjelen.itkons;

import com.github.javafaker.Faker;
import com.github.javafaker.service.RandomService;
import de.isb.mjelen.itkons.generator.Distribution;
import de.isb.mjelen.itkons.model.Fachprozess;
import de.isb.mjelen.itkons.model.Fachprozess.FachprozessMD;
import de.isb.mjelen.itkons.model.Informationssystem;
import de.isb.mjelen.itkons.model.Informationssystem.StandardKonformitaet;
import de.isb.mjelen.itkons.model.Organisationseinheit;
import de.isb.mjelen.itkons.model.Standard;
import de.isb.mjelen.itkons.model.Standard.StandardArt;
import de.isb.mjelen.itkons.model.Standard.StandardKonformitaetStufe;
import de.isb.mjelen.itkons.model.Standort;
import de.isb.mjelen.itkons.model.Standort.StandortMD;
import de.isb.mjelen.itkons.model.Subsystem;
import de.isb.mjelen.itkons.model.Technologie;
import de.isb.mjelen.itkons.model.enums.AnzahlAnwender;
import de.isb.mjelen.itkons.poi.ModelDescriptor;
import de.isb.mjelen.itkons.poi.PoiSupport;
import de.isb.mjelen.itkons.poi.StatefulPoiHelper;
import de.isb.mjelen.itkons.testdata.GeonamesDB;
import de.isb.mjelen.itkons.testdata.TestDataRow;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Die Namen werden aus der Open-Data-Datenbank von geonames.org genommen, die in SQLite importiert wurden.
 * <p>
 * Die Feature-Codes sind unter http://www.geonames.org/export/codes.html gelistet
 */
public class Generator {

  private final Random rnd;
  private final Faker faker;
  private final Workbook workbook;
  private final StatefulPoiHelper poiHelperTemplate;
  private final GeonamesDB db;

  public Generator() {
    rnd = new Random(System.currentTimeMillis());
    faker = new Faker(Locale.ENGLISH, rnd);
    workbook = new XSSFWorkbook();
    poiHelperTemplate = createPoiHelperTemplate(workbook);
    db = new GeonamesDB();
  }

  public static void main(String... args) throws IOException {
    Generator gen = new Generator();
    gen.generateTestData();
  }

  private StatefulPoiHelper createPoiHelperTemplate(Workbook workbook) {
    CreationHelper createHelper = workbook.getCreationHelper();

    Font headerFont = workbook.createFont();
    headerFont.setBold(true);
    CellStyle headerCellStyle = workbook.createCellStyle();
    headerCellStyle.setFont(headerFont);

    CellStyle dateCellStyle = workbook.createCellStyle();
    dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd.MM.yyyy"));

    CellStyle textCellStyle = workbook.createCellStyle();
    textCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("text"));

    return new StatefulPoiHelper(headerCellStyle, dateCellStyle, textCellStyle);
  }

  private void generateTestData() throws IOException {
    db.connect("c:/Implementierung/Projekte/2019/BAKS/baks.db");
    db.initCounters();

    Map<String, Standard> standards = buildStandards(20);
    Map<String, Technologie> technologien = buildTechnologien(30);
    Map<String, Standort> standorte = buildStandorte(5);
    Map<String, Organisationseinheit> orgEinheiten = buildOrganisationseinheiten(15, standorte);
    List<String> verantwortlichePersonen = buildVerantwortlichePersonen(100);
    Map<String, Subsystem> subsysteme = buildSubsysteme(300, verantwortlichePersonen);
    Map<String, Informationssystem> infosysteme = buildInformationssysteme(100, technologien, orgEinheiten, subsysteme, verantwortlichePersonen, standards);
    Map<String, Fachprozess> fachprozesse = buildFachprozesse(15, infosysteme);

    writeSheet(fachprozesse, new FachprozessMD(), workbook.createSheet("Fachprozesse"));
    writeSheet(infosysteme, new Informationssystem.InformationssystemMD(), workbook.createSheet("Informationssysteme"));
    writeSheet(orgEinheiten, new Organisationseinheit.OrganisationseinheitMD(), workbook.createSheet("Organisationseinheiten"));
    writeSheet(technologien, new Technologie.TechnologieMD(), workbook.createSheet("Technologien"));
    writeSheet(standards, new Standard.StandardMD(), workbook.createSheet("Standards"));
    writeSheet(standorte, new StandortMD(), workbook.createSheet("Standorte"));

    db.disconnect();

    FileOutputStream fileOut = new FileOutputStream(String.format("systemlandschaft-%d.xlsx", System.currentTimeMillis()));
    workbook.write(fileOut);
    fileOut.close();
    workbook.close();
  }

  private Map<String, Fachprozess> buildFachprozesse(int anzahlMax, Map<String, Informationssystem> informationssysteme) {
    Map<String, Fachprozess> result = new HashMap<>(anzahlMax);
    Distribution distISAnzahl = Distribution.buildLogistic(0, 5);
    Distribution distTSAuswahl = Distribution.buildLogNormal(0, informationssysteme.size());

    for (int i = 0; i < anzahlMax; i++) {
      Fachprozess fp = Fachprozess.builder().
          code(faker.letterify("PROC.????").toUpperCase()).
          name(faker.commerce().material()).
          informationssysteme((new BuildHelper<Informationssystem>()).buildRandomSet(informationssysteme, distISAnzahl, distTSAuswahl)).
          build();
      result.put(fp.getCode(), fp);
    }

    return result;
  }

  private List<String> buildVerantwortlichePersonen(int anzahlMaxSysteme) {
    int anzahlPersonen = faker.random().nextInt((new Double(anzahlMaxSysteme * 0.1)).intValue(), (new Double(anzahlMaxSysteme * 0.5)).intValue());
    List<String> result = new ArrayList<>(anzahlPersonen);

    for (int i = 0; i < anzahlPersonen; i++) {
      result.add(faker.name().fullName());
    }

    return result;
  }

  private Map<String, Standort> buildStandorte(int anzahlMax) {
    Map<String, Standort> result = new HashMap<>(anzahlMax);

    for (int i = 0; i < anzahlMax; i++) {
      Standort standort = Standort.builder().
          code(faker.letterify("LOC.????").toUpperCase()).
          name(faker.friends().location()).
          anschrift(faker.address().fullAddress()).
          plz(faker.address().zipCode()).
          build();
      result.put(standort.getCode(), standort);
    }

    return result;
  }

  private Map<String, Organisationseinheit> buildOrganisationseinheiten(int anzahlMax, Map<String, Standort> standorte) {
    Map<String, Organisationseinheit> result = new HashMap<>();
    Distribution distStandorte = Distribution.buildNormal(0, standorte.size());
    ArrayList<Standort> standorteList = new ArrayList<>(standorte.values());

    for (int i = 0; i < anzahlMax; i++) {
      Organisationseinheit orgEinheit = Organisationseinheit.builder().
          code(faker.letterify("ORG.????").toUpperCase()).
          name(faker.commerce().department()).
          standort(standorteList.get(Math.max(0,distStandorte.nextValue()-1))).
          build();
      result.put(orgEinheit.getCode(), orgEinheit);
    }

    int ebenen = faker.random().nextInt(1,3);
    int anzahlUebrig = anzahlMax;
    ArrayList<Organisationseinheit> uebrigeOrgEinheiten = new ArrayList<>(result.values());
    ArrayList<Organisationseinheit> ebeneDrueber = new ArrayList<>();
    for (int i = 0; i< ebenen; i++) {
      ArrayList<Organisationseinheit> neueEbeneDrueber = new ArrayList<>();
      Distribution distEbeneDrueber = Distribution.buildNormal(0, ebeneDrueber.size());
      int anzhalAufEbene = faker.random().nextInt(anzahlUebrig/ebenen*(i+1));
      for(int j = 0; j<anzhalAufEbene; j++) {
        int idx = faker.random().nextInt(--anzahlUebrig);
        Organisationseinheit org = uebrigeOrgEinheiten.remove(idx);
        org.setUebergeordneteOrgeinheit(ebeneDrueber.isEmpty() ? null : ebeneDrueber.get(Math.max(0,distEbeneDrueber.nextValue()-1)));
        neueEbeneDrueber.add(org);
      }
      ebeneDrueber = neueEbeneDrueber;
    }

    return result;
  }


  private Map<String, Technologie> buildTechnologien(int anzahlMax) {
    Map<String, Technologie> result = new HashMap<>();

    for (int i = 0; i < anzahlMax; i++) {
      TestDataRow row = db.getRandomByFeatureCode(faker.random(), "PRK");
      Technologie tech = Technologie.builder().
          code(faker.letterify("TECH.????").toUpperCase()).
          name(row.getName()).
          beschreibung(faker.lorem().sentence(3, 2)).
          endOfLife(LocalDate.of(2015 + rnd.nextInt(10), 1 + rnd.nextInt(12), 1 + rnd.nextInt(28))).
          build();
      result.put(tech.getCode(), tech);
    }

    return result;
  }

  private Map<String, Standard> buildStandards(int anzahlMax) {
    Map<String, Standard> result = new HashMap<>();

    for (int i = 0; i < anzahlMax; i++) {
      Standard std = Standard.builder().
          code(faker.letterify("STD.????").toUpperCase()).
          name(faker.animal().name()).
          art(StandardArt.values()[faker.random().nextInt(StandardArt.values().length)]).
          build();
      result.put(std.getCode(), std);
    }

    return result;
  }

  private Map<String, Informationssystem> buildInformationssysteme(int anzahlMax, Map<String, Technologie> technologien,
      Map<String, Organisationseinheit> orgEinheiten, Map<String, Subsystem> subsysteme, List<String> verantwortlichePersonen,
      Map<String, Standard> standards) {
    Map<String, Informationssystem> result = new HashMap<>();
    Distribution distTechAnzahl = Distribution.buildLogistic(0, 8);
    Distribution distTechAuswahl = Distribution.buildLogNormal(0, technologien.size());
    Distribution distPersonen = Distribution.buildLogNormal(0, verantwortlichePersonen.size());
    Distribution distStandardKonformitaet = Distribution.buildLogNormal(0, StandardKonformitaetStufe.values().length);
    Distribution distStandardAnzahl = Distribution.buildExponential(0, 5);
    Distribution distStandardAuswahl = Distribution.buildNormal(0, standards.size());

    for (int i = 0; i < anzahlMax; i++) {
      TestDataRow row = db.getRandomByFeatureCode(faker.random(), "MT");
      Set<Standard> standardAuswahl = (new BuildHelper<Standard>()).buildRandomSet(standards, distStandardAnzahl, distStandardAuswahl);
      Informationssystem is = Informationssystem.builder().
          code(faker.letterify("IS.????").toUpperCase()).
          name(row.getName()).
          beschreibung(faker.lorem().sentence(7, 4)).
          verantwortlich(verantwortlichePersonen.get(Math.max(0, distPersonen.nextValue())-1)).
          anzahlAnwender(AnzahlAnwender.getRandom(faker.random())).
          anzahlInstallationen(faker.random().nextInt(1, 10)).
          technologien((new BuildHelper<Technologie>()).buildRandomSet(technologien, distTechAnzahl, distTechAuswahl)).
          produktivSeit(LocalDate.of(1990 + rnd.nextInt(30), 1 + rnd.nextInt(12), 1 + rnd.nextInt(28))).
          eingesetztIn((new BuildHelper<Organisationseinheit>()).buildRandomSet(orgEinheiten, faker.random(), 2)).
          investitionGroesse(faker.random().nextInt(50, 5000) * 1000).
          standardKonformitaet(standardAuswahl.stream().map(
              std -> new Informationssystem.StandardKonformitaet(std, Standard.StandardKonformitaetStufe.values()[Math.max(0,distStandardKonformitaet.nextValue()-1)])
              ).collect(Collectors.toSet())).
          build();
      result.put(is.getCode(), is);
    }

    return result;
  }


  private Map<String, Subsystem> buildSubsysteme(int anzahlMax, List<String> verantwortlichePersonen) {
    Map<String, Subsystem> result = new HashMap<>();

    return result;
  }

  private void writeSheet(Map<String, ? extends PoiSupport> kategorie, ModelDescriptor md, Sheet sheet) {
    StatefulPoiHelper poiHelper = poiHelperTemplate.withSheet(sheet);

    md.buildHeaderRow(poiHelper);
    int idx = 1;
    for (PoiSupport ps : kategorie.values()) {
      ps.buildContentRow(poiHelper, idx);
      idx++;
    }
    md.autosizeColumns(poiHelper);
  }

  private static class BuildHelper<T extends PoiSupport> {

    public Set<T> buildRandomSet(Map<String, T> daten, RandomService random, int maxSize) {
      Set<T> result = new HashSet<>();
      List<T> values = new ArrayList<>(daten.values());
      int rndSize = 1 + random.nextInt(maxSize);
      for (int i = 0; i < rndSize; i++) {
        result.add((T) values.get(random.nextInt(daten.size())));
      }
      return result;
    }

    public Set<T> buildRandomSet(Map<String, T> daten, Distribution distSize, Distribution distElemente) {
      Set<T> result = new HashSet<>();
      List<T> values = new ArrayList<>(daten.values());
      int rndSize = distSize.nextValue();
      for (int i = 0; i < rndSize; i++) {
        int elementNr = Integer.MAX_VALUE;
        while (elementNr >= daten.size()) { // nur falls die Verteilung falsch konfiguriert war
          elementNr = distElemente.nextValue();
        }
        result.add((T) values.get(elementNr));
      }
      return result;
    }
  }

}
