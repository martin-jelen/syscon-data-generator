package de.isb.mjelen.itkons.testdata;

import com.github.javafaker.service.RandomService;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;

import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.TimeZone;

public class GeonamesDB {
    private static final String JDBC_SQLITE_PREFIX = "jdbc:sqlite:";
    private ObjectIntHashMap<String> countByFeatureClass = new ObjectIntHashMap<>();
    private ObjectIntHashMap<String> countByFeatureCode = new ObjectIntHashMap<>();

    private Connection conn;

    public void connect(String pathWithSlashes) {
        try {
            // db parameters
            String url = JDBC_SQLITE_PREFIX + pathWithSlashes;
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            conn.setAutoCommit(false);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void disconnect() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void loadTestData(String[][] buffer, int idx) {
        String sql = "INSERT INTO geonames (geonameid, name, asciiname, alternatenames, latitude, longitude, feature_class, feature_code, country_code, cc2, admin1_code, admin2_code, admin3_code, admin4_code, population, elevation, dem, timezone, modified, feature_class_lfd_nr, feature_code_lfd_nr) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < idx; i++) {
                ps.setString(1, buffer[i][0]);
                ps.setString(2, buffer[i][1]);
                ps.setString(3, buffer[i][2]);
                ps.setString(4, buffer[i][3]);
                ps.setString(5, buffer[i][4]);
                ps.setString(6, buffer[i][5]);
                ps.setString(7, buffer[i][6]);
                ps.setString(8, buffer[i][7]);
                ps.setString(9, buffer[i][8]);
                ps.setString(10, buffer[i][9]);
                ps.setString(11, buffer[i][10]);
                ps.setString(12, buffer[i][11]);
                ps.setString(13, buffer[i][12]);
                ps.setString(14, buffer[i][13]);
                ps.setString(15, buffer[i][14]);
                ps.setString(16, buffer[i][15]);
                ps.setString(17, buffer[i][16]);
                ps.setString(18, buffer[i][17]);
                ps.setString(19, buffer[i][18]);
                ps.setInt(20, countByFeatureClass.addToValue(buffer[i][6], 1));
                ps.setInt(21, countByFeatureCode.addToValue(buffer[i][7], 1));

                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TestDataRow getRandomByFeatureCode(RandomService random, String featureCode) {
        return getRandomWithWhere(random, "feature_code = ? AND feature_code_lfd_nr = ?", featureCode, random.nextLong(countByFeatureCode.get(featureCode)));
    }

    public TestDataRow getRandomAnything(RandomService random) {
        return getRandomWithWhere(random, "rowid = ?", random.nextLong(11953580L));
    }

    private TestDataRow getRandomWithWhere(RandomService random, String where, Object... whereArgs) {
        TestDataRow result = null;

        final String sqlBase = "SELECT geonameid, " +
                "name, " +
                "asciiname, " +
                "alternatenames, " +
                "latitude, " +
                "longitude, " +
                "feature_class, " +
                "feature_code, " +
                "country_code, " +
                "cc2, " +
                "admin1_code, " +
                "admin2_code, " +
                "admin3_code, " +
                "admin4_code, " +
                "population, " +
                "elevation, " +
                "dem, " +
                "timezone, " +
                "modified " +
                "FROM geonames WHERE ";

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
        formatter.setParseBigDecimal(true);

        try (PreparedStatement ps = conn.prepareStatement(sqlBase + where)) {
            int idxWhere = 1;
            for(Object warg : whereArgs) {
                ps.setObject(idxWhere++, warg);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = TestDataRow.builder()
                        .geonameid(rs.getString("geonameid"))
                        .name(rs.getString("name"))
                        .asciiname(rs.getString("asciiname"))
                        .alternatenames(notEmpty(rs.getString("alternatenames")) ? rs.getString("alternatenames").split("\\|") : null)
                        .latitude(notEmpty(rs.getString("latitude")) ? (BigDecimal) formatter.parseObject(rs.getString("latitude")) : null)
                        .longitude(notEmpty(rs.getString("longitude")) ? (BigDecimal) formatter.parseObject(rs.getString("longitude")) : null)
                        .feature_class(rs.getString("feature_class"))
                        .feature_code(rs.getString("feature_code"))
                        .country_code(rs.getString("country_code"))
                        .cc2(rs.getString("cc2"))
                        .admin1_code(rs.getString("admin1_code"))
                        .admin2_code(rs.getString("admin2_code"))
                        .admin3_code(rs.getString("admin3_code"))
                        .admin4_code(rs.getString("admin4_code"))
                        .population(notEmpty(rs.getString("population")) ? Integer.parseInt(rs.getString("population")) : null)
                        .elevation(notEmpty(rs.getString("elevation")) ? Integer.parseInt(rs.getString("elevation")) : null)
                        .dem(rs.getString("dem"))
                        .timezone(notEmpty(rs.getString("timezone")) ? TimeZone.getTimeZone(rs.getString("timezone")) : null)
                        .modified(notEmpty(rs.getString("modified")) ? LocalDate.parse(rs.getString("modified")) : null)
                        .build();
            }

        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }


        return result;
    }

    private boolean notEmpty(String txt) {
        return (txt != null) && !txt.isEmpty();
    }

    public void initCounters() {
        countByFeatureClass.addToValue("P", 4748476);
        countByFeatureClass.addToValue("S", 2419503);
        countByFeatureClass.addToValue("H", 2220029);
        countByFeatureClass.addToValue("T", 1588248);
        countByFeatureClass.addToValue("A", 449601);
        countByFeatureClass.addToValue("L", 411375);
        countByFeatureClass.addToValue("R", 48394);
        countByFeatureClass.addToValue("V", 48289);
        countByFeatureClass.addToValue("U", 14606);

        countByFeatureCode.addToValue("PPL", 4329567);
        countByFeatureCode.addToValue("STM", 879962);
        countByFeatureCode.addToValue("MT", 401295);
        countByFeatureCode.addToValue("HLL", 377660);
        countByFeatureCode.addToValue("FRM", 329150);
        countByFeatureCode.addToValue("SCH", 291685);
        countByFeatureCode.addToValue("LK", 267539);
        countByFeatureCode.addToValue("CH", 249918);
        countByFeatureCode.addToValue("HTL", 241561);
        countByFeatureCode.addToValue("STMI", 207321);
        countByFeatureCode.addToValue("ADM4", 165257);
        countByFeatureCode.addToValue("LCTY", 162833);
        countByFeatureCode.addToValue("PPLL", 160636);
        countByFeatureCode.addToValue("ISL", 154154);
        countByFeatureCode.addToValue("BLDG", 152687);
        countByFeatureCode.addToValue("CMTY", 148386);
        countByFeatureCode.addToValue("ADM3", 144061);
        countByFeatureCode.addToValue("VAL", 134935);
        countByFeatureCode.addToValue("PPLX", 113812);
        countByFeatureCode.addToValue("WLL", 107852);
        countByFeatureCode.addToValue("", 95197);
        countByFeatureCode.addToValue("RSV", 92460);
        countByFeatureCode.addToValue("PRK", 88168);
        countByFeatureCode.addToValue("PT", 85168);
        countByFeatureCode.addToValue("SPNG", 78968);
        countByFeatureCode.addToValue("PND", 78287);
        countByFeatureCode.addToValue("WAD", 75268);
        countByFeatureCode.addToValue("AREA", 71693);
        countByFeatureCode.addToValue("DAM", 68686);
        countByFeatureCode.addToValue("RSTN", 66288);
        countByFeatureCode.addToValue("MRSH", 54066);
        countByFeatureCode.addToValue("PO", 52847);
        countByFeatureCode.addToValue("ADM5", 51149);
        countByFeatureCode.addToValue("BAY", 50415);
        countByFeatureCode.addToValue("HMSD", 49441);
        countByFeatureCode.addToValue("CNL", 49310);
        countByFeatureCode.addToValue("PPLQ", 48096);
        countByFeatureCode.addToValue("PK", 46488);
        countByFeatureCode.addToValue("ADM2", 44646);
        countByFeatureCode.addToValue("HSE", 43400);
        countByFeatureCode.addToValue("MN", 42665);
        countByFeatureCode.addToValue("PASS", 42346);
        countByFeatureCode.addToValue("MSTY", 41488);
        countByFeatureCode.addToValue("RDGE", 41461);
        countByFeatureCode.addToValue("BDG", 39504);
        countByFeatureCode.addToValue("ADMD", 34130);
        countByFeatureCode.addToValue("CMP", 29390);
        countByFeatureCode.addToValue("CAPE", 29087);
        countByFeatureCode.addToValue("INLT", 28874);
        countByFeatureCode.addToValue("FRST", 28439);
        countByFeatureCode.addToValue("RK", 28249);
        countByFeatureCode.addToValue("PPLA3", 28147);
        countByFeatureCode.addToValue("SHRN", 27806);
        countByFeatureCode.addToValue("MTS", 27428);
        countByFeatureCode.addToValue("PPLA4", 26663);
        countByFeatureCode.addToValue("TOWR", 26295);
        countByFeatureCode.addToValue("RVN", 24756);
        countByFeatureCode.addToValue("AIRP", 23371);
        countByFeatureCode.addToValue("PLN", 22324);
        countByFeatureCode.addToValue("BUSTP", 22051);
        countByFeatureCode.addToValue("HSP", 21585);
        countByFeatureCode.addToValue("RSVT", 20858);
        countByFeatureCode.addToValue("PPLA2", 20466);
        countByFeatureCode.addToValue("EST", 18698);
        countByFeatureCode.addToValue("HLLS", 18374);
        countByFeatureCode.addToValue("RNCH", 17777);
        countByFeatureCode.addToValue("MALL", 17631);
        countByFeatureCode.addToValue("RUIN", 17324);
        countByFeatureCode.addToValue("COVE", 17294);
        countByFeatureCode.addToValue("RSVI", 17266);
        countByFeatureCode.addToValue("ISLET", 16555);
        countByFeatureCode.addToValue("FLD", 16255);
        countByFeatureCode.addToValue("RDJCT", 15690);
        countByFeatureCode.addToValue("RESF", 15121);
        countByFeatureCode.addToValue("BCH", 14238);
        countByFeatureCode.addToValue("SLP", 14046);
        countByFeatureCode.addToValue("GRAZ", 13926);
        countByFeatureCode.addToValue("PPLF", 13259);
        countByFeatureCode.addToValue("LIBR", 12740);
        countByFeatureCode.addToValue("TRL", 12378);
        countByFeatureCode.addToValue("TRIG", 12300);
        countByFeatureCode.addToValue("SWMP", 11732);
        countByFeatureCode.addToValue("SPUR", 11708);
        countByFeatureCode.addToValue("HSPC", 11525);
        countByFeatureCode.addToValue("DPR", 11402);
        countByFeatureCode.addToValue("HDLD", 11043);
        countByFeatureCode.addToValue("AIRF", 10966);
        countByFeatureCode.addToValue("CLF", 10755);
        countByFeatureCode.addToValue("RF", 10535);
        countByFeatureCode.addToValue("ST", 10283);
        countByFeatureCode.addToValue("RES", 10177);
        countByFeatureCode.addToValue("MUS", 10169);
        countByFeatureCode.addToValue("CHN", 10156);
        countByFeatureCode.addToValue("TMB", 10037);
        countByFeatureCode.addToValue("FRMT", 9736);
        countByFeatureCode.addToValue("DUNE", 9382);
        countByFeatureCode.addToValue("RECG", 9171);
        countByFeatureCode.addToValue("RSTP", 8889);
        countByFeatureCode.addToValue("CULT", 8881);
        countByFeatureCode.addToValue("GAP", 8673);
        countByFeatureCode.addToValue("SHOL", 8547);
        countByFeatureCode.addToValue("TMPL", 8451);
        countByFeatureCode.addToValue("WTRH", 8257);
        countByFeatureCode.addToValue("GLCR", 8123);
        countByFeatureCode.addToValue("MSQE", 7888);
        countByFeatureCode.addToValue("FLLS", 7846);
        countByFeatureCode.addToValue("HUT", 7527);
        countByFeatureCode.addToValue("ISLS", 7254);
        countByFeatureCode.addToValue("SHSU", 7208);
        countByFeatureCode.addToValue("RKS", 6797);
        countByFeatureCode.addToValue("MESA", 6767);
        countByFeatureCode.addToValue("RPDS", 6363);
        countByFeatureCode.addToValue("ADMF", 6045);
        countByFeatureCode.addToValue("LKI", 6002);
        countByFeatureCode.addToValue("RSRT", 5916);
        countByFeatureCode.addToValue("HBR", 5739);
        countByFeatureCode.addToValue("TRB", 5643);
        countByFeatureCode.addToValue("BAR", 5584);
        countByFeatureCode.addToValue("OILF", 5543);
        countByFeatureCode.addToValue("RD", 5379);
        countByFeatureCode.addToValue("CSTL", 5223);
        countByFeatureCode.addToValue("ANS", 5076);
        countByFeatureCode.addToValue("PNDI", 4936);
        countByFeatureCode.addToValue("AIRH", 4751);
        countByFeatureCode.addToValue("STMC", 4713);
        countByFeatureCode.addToValue("SQR", 4504);
        countByFeatureCode.addToValue("MFG", 4497);
        countByFeatureCode.addToValue("MOOR", 4453);
        countByFeatureCode.addToValue("CHNM", 4434);
        countByFeatureCode.addToValue("CTRR", 4249);
        countByFeatureCode.addToValue("FRMQ", 4241);
        countByFeatureCode.addToValue("STMD", 4100);
        countByFeatureCode.addToValue("RSD", 4067);
        countByFeatureCode.addToValue("LKS", 4021);
        countByFeatureCode.addToValue("HSTS", 4020);
        countByFeatureCode.addToValue("AIRQ", 4017);
        countByFeatureCode.addToValue("OVF", 3999);
        countByFeatureCode.addToValue("ADM1", 3954);
        countByFeatureCode.addToValue("PEN", 3888);
        countByFeatureCode.addToValue("PS", 3772);
        countByFeatureCode.addToValue("INDS", 3757);
        countByFeatureCode.addToValue("STMB", 3709);
        countByFeatureCode.addToValue("CNYN", 3632);
        countByFeatureCode.addToValue("CAVE", 3626);
        countByFeatureCode.addToValue("GRGE", 3607);
        countByFeatureCode.addToValue("MNMT", 3588);
        countByFeatureCode.addToValue("FT", 3545);
        countByFeatureCode.addToValue("PPLA", 3532);
        countByFeatureCode.addToValue("HSEC", 3442);
        countByFeatureCode.addToValue("LGN", 3441);
        countByFeatureCode.addToValue("DTCH", 3396);
        countByFeatureCode.addToValue("SCHC", 3386);
        countByFeatureCode.addToValue("POOL", 3221);
        countByFeatureCode.addToValue("UPLD", 3197);
        countByFeatureCode.addToValue("ADM4H", 3182);
        countByFeatureCode.addToValue("VIN", 3174);
        countByFeatureCode.addToValue("LDNG", 3143);
        countByFeatureCode.addToValue("CRKT", 3127);
        countByFeatureCode.addToValue("LTHSE", 3063);
        countByFeatureCode.addToValue("RESV", 3020);
        countByFeatureCode.addToValue("SWT", 3003);
        countByFeatureCode.addToValue("STDM", 3000);
        countByFeatureCode.addToValue("MSSN", 2992);
        countByFeatureCode.addToValue("INSM", 2989);
        countByFeatureCode.addToValue("WTLD", 2711);
        countByFeatureCode.addToValue("MKT", 2626);
        countByFeatureCode.addToValue("CRRL", 2614);
        countByFeatureCode.addToValue("GDN", 2473);
        countByFeatureCode.addToValue("PPLW", 2414);
        countByFeatureCode.addToValue("PRT", 2395);
        countByFeatureCode.addToValue("RGN", 2312);
        countByFeatureCode.addToValue("PAN", 2165);
        countByFeatureCode.addToValue("PP", 2124);
        countByFeatureCode.addToValue("MLWND", 2049);
        countByFeatureCode.addToValue("BNK", 2037);
        countByFeatureCode.addToValue("MND", 2023);
        countByFeatureCode.addToValue("STMM", 1999);
        countByFeatureCode.addToValue("PAL", 1987);
        countByFeatureCode.addToValue("SBKH", 1943);
        countByFeatureCode.addToValue("SCRP", 1933);
        countByFeatureCode.addToValue("OCH", 1931);
        countByFeatureCode.addToValue("FJD", 1912);
        countByFeatureCode.addToValue("PLDR", 1910);
        countByFeatureCode.addToValue("REST", 1889);
        countByFeatureCode.addToValue("RESN", 1855);
        countByFeatureCode.addToValue("BANK", 1814);
        countByFeatureCode.addToValue("AGRC", 1804);
        countByFeatureCode.addToValue("STRT", 1763);
        countByFeatureCode.addToValue("FORD", 1760);
        countByFeatureCode.addToValue("ESTX", 1751);
        countByFeatureCode.addToValue("FCL", 1701);
        countByFeatureCode.addToValue("LKN", 1696);
        countByFeatureCode.addToValue("STMX", 1692);
        countByFeatureCode.addToValue("MTRO", 1686);
        countByFeatureCode.addToValue("MAR", 1636);
        countByFeatureCode.addToValue("BOG", 1607);
        countByFeatureCode.addToValue("STNM", 1551);
        countByFeatureCode.addToValue("PLAT", 1542);
        countByFeatureCode.addToValue("TNL", 1536);
        countByFeatureCode.addToValue("GROVE", 1531);
        countByFeatureCode.addToValue("RSTNQ", 1462);
        countByFeatureCode.addToValue("SAND", 1454);
        countByFeatureCode.addToValue("ML", 1405);
        countByFeatureCode.addToValue("BCN", 1383);
        countByFeatureCode.addToValue("THTR", 1373);
        countByFeatureCode.addToValue("UNIV", 1368);
        countByFeatureCode.addToValue("DEVH", 1352);
        countByFeatureCode.addToValue("GRVE", 1336);
        countByFeatureCode.addToValue("ADM3H", 1325);
        countByFeatureCode.addToValue("SMU", 1309);
        countByFeatureCode.addToValue("RHSE", 1286);
        countByFeatureCode.addToValue("CNLSB", 1283);
        countByFeatureCode.addToValue("ATHF", 1281);
        countByFeatureCode.addToValue("LKO", 1278);
        countByFeatureCode.addToValue("MNQ", 1232);
        countByFeatureCode.addToValue("NTK", 1219);
        countByFeatureCode.addToValue("GRSLD", 1195);
        countByFeatureCode.addToValue("ISLX", 1192);
        countByFeatureCode.addToValue("ESTY", 1185);
        countByFeatureCode.addToValue("WTLDI", 1181);
        countByFeatureCode.addToValue("STMA", 1178);
        countByFeatureCode.addToValue("TRGD", 1087);
        countByFeatureCode.addToValue("SCRB", 1075);
        countByFeatureCode.addToValue("SDL", 1056);
        countByFeatureCode.addToValue("ANCH", 1033);
        countByFeatureCode.addToValue("LEV", 1014);
        countByFeatureCode.addToValue("HTH", 1013);
        countByFeatureCode.addToValue("WHRF", 1004);
    }
}
