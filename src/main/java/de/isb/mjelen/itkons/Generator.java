package de.isb.mjelen.itkons;

import com.github.javafaker.Faker;
import de.isb.mjelen.itkons.model.Informationssystem;
import de.isb.mjelen.itkons.poi.ModelDescriptor;
import de.isb.mjelen.itkons.poi.StatefulPoiHelper;
import de.isb.mjelen.itkons.testdata.GeonamesDB;
import de.isb.mjelen.itkons.testdata.TestDataRow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

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

        Map<String, Informationssystem> infosysteme = buildInformationssysteme(100);
        writeInformationssysteme(infosysteme, workbook.createSheet("Informationssysteme"));

        db.disconnect();

        FileOutputStream fileOut = new FileOutputStream("systemlandschaft.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    private Map<String, Informationssystem> buildInformationssysteme(int anzahlMax) {
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
                    build();
            result.put(is.getCode(), is);
        }

        return result;
    }


    private void writeInformationssysteme(Map<String, Informationssystem> infosysteme, Sheet informationssystemeSheet) {
        StatefulPoiHelper poiHelper = poiHelperTemplate.withSheet(informationssystemeSheet);

        ModelDescriptor md = new Informationssystem.InformationssystemMD();
        md.buildHeaderRow(poiHelper);
        int idx = 1;
        for (Informationssystem is : infosysteme.values()) {
            is.buildContentRow(poiHelper, idx);
            idx++;
        }
        md.autosizeColumns(poiHelper);
    }

}
