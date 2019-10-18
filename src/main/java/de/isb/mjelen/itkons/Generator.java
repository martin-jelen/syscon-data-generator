package de.isb.mjelen.itkons;

import com.github.javafaker.Faker;
import com.github.javafaker.service.RandomService;
import de.isb.mjelen.itkons.model.*;
import de.isb.mjelen.itkons.poi.ModelDescriptor;
import de.isb.mjelen.itkons.poi.PoiSupport;
import de.isb.mjelen.itkons.poi.StatefulPoiHelper;
import de.isb.mjelen.itkons.testdata.GeonamesDB;
import de.isb.mjelen.itkons.testdata.TestDataRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

        Map<String, Technologie> technologien = buildTechnologien(30);
        Map<String, Organisationseinheit> orgEinheiten = buildOrganisationseinheiten(5);
        Map<String, Informationssystem> infosysteme = buildInformationssysteme(100, technologien, orgEinheiten);

        writeSheet(infosysteme, new Informationssystem.InformationssystemMD(), workbook.createSheet("Informationssysteme"));
        writeSheet(orgEinheiten, new Organisationseinheit.OrganisationseinheitMD(), workbook.createSheet("Organisationseinheiten"));
        writeSheet(technologien, new Technologie.TechnologieMD(), workbook.createSheet("Technologien"));

        db.disconnect();

        FileOutputStream fileOut = new FileOutputStream(String.format("systemlandschaft-%d.xlsx", System.currentTimeMillis()));
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    private Map<String, Standort> buildStandorte(int anzahlMax) {
        Map<String, Standort> result = new HashMap<>();

        return result;
    }

    private Map<String, Organisationseinheit> buildOrganisationseinheiten(int anzahlMax) {
        Map<String, Organisationseinheit> result = new HashMap<>();

        for (int i = 0; i < anzahlMax; i++) {
            TestDataRow row = db.getRandomByFeatureCode(faker.random(), "TOWR");
            Organisationseinheit orgEinheit = Organisationseinheit.builder().
                    code(faker.letterify("ORG.????").toUpperCase()).
                    name(row.getName()).
                    build();
            result.put(orgEinheit.getCode(), orgEinheit);
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

        return result;
    }

    private Map<String, Informationssystem> buildInformationssysteme(int anzahlMax, Map<String, Technologie> technologien, Map<String, Organisationseinheit> orgEinheiten) {
        Map<String, Informationssystem> result = new HashMap<>();

        for (int i = 0; i < anzahlMax; i++) {
            TestDataRow row = db.getRandomByFeatureCode(faker.random(), "MT");
            Informationssystem is = Informationssystem.builder().
                    code(faker.letterify("IS.????").toUpperCase()).
                    name(row.getName()).
                    beschreibung(faker.lorem().sentence(7, 4)).
                    verantwortlich(faker.name().fullName()).
                    anzahlAnwender(row.getElevation()).
                    anzahlInstallationen(faker.random().nextInt(1, 10)).
                    technologien((new BuildHelper<Technologie>()).buildRandomSet(technologien, faker.random(), 5)).
                    produktivSeit(LocalDate.of(1990 + rnd.nextInt(30), 1 + rnd.nextInt(12), 1 + rnd.nextInt(28))).
                    eingesetztIn((new BuildHelper<Organisationseinheit>()).buildRandomSet(orgEinheiten, faker.random(), 2)).
                    investitionGroesse(faker.random().nextInt(50, 5000) * 1000).
                    build();
            result.put(is.getCode(), is);
        }

        return result;
    }


    private Map<String, Subsystem> buildSubsysteme(int anzahlMax) {
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
    }

}
