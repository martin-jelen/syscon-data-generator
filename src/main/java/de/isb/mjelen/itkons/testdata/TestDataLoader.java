package de.isb.mjelen.itkons.testdata;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;

public class TestDataLoader {

    public static void main(String... args) throws IOException {
        CSVReader reader = new CSVReader(new FileReader("c:/Implementierung/Projekte/2019/BAKS/data/allCountries.txt "), '\t', '§');
        String[][] buffer = new String[1000][];

        GeonamesDB db = new GeonamesDB();
        db.connect("c:/Implementierung/Projekte/2019/BAKS/baks.db");

        int batchIdx = 1;
        boolean done = false;
        while (!done) {
            int idx = 0;
            for (idx = 0; idx < 1000; idx++) {
                buffer[idx] = reader.readNext();
                if (buffer[idx] == null) {
                    done = true;
                    break;
                }
            }
            db.loadTestData(buffer, idx);
            System.out.println("Loaded " + (batchIdx++)*1000);
        }

        db.disconnect();
    }

}
