package de.isb.mjelen.itkons.model;

import de.isb.mjelen.itkons.poi.ModelDescriptor;
import de.isb.mjelen.itkons.poi.PoiSupport;
import de.isb.mjelen.itkons.poi.StatefulPoiHelper;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Data
@SuperBuilder
public class Informationssystem extends PoiSupport {
    private String code;
    private String name;
    private String beschreibung;
    private String verantwortlich;
    private Integer anzahlInstallationen;
    private Integer anzahlAnwender;
    private Integer investitionGroesse;
    private LocalDate produktivSeit;
    private Set<Organisationseinheit> eingesetztIn;
    private Set<Subsystem> subsysteme;
    private Set<Technologie> technologien;


    @Override
    public void buildContentRow(StatefulPoiHelper poiHelper, int index) {
        poiHelper.buildContentRowFromStringArray(index, new String[]{
                getCode(),
                getName(),
                getBeschreibung(),
                getVerantwortlich(),
                serializeInteger(getAnzahlInstallationen()),
                serializeInteger(getAnzahlAnwender()),
                serializeInteger(getInvestitionGroesse()),
                serializeSet(getTechnologien()),
                serializeDate(getProduktivSeit()),
                serializeSet(getEingesetztIn())
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
                "Anzahl Anwender",
                "Investitionsgröße",
                "Technologien",
                "Eingesetzt seit",
                "Eingesetzt in"};

        @Override
        public void buildHeaderRow(StatefulPoiHelper poiHelper) {
            poiHelper.buildHeaderFromTitleArray(columns);
        }

        @Override
        public void autosizeColumns(StatefulPoiHelper poiHelper) {
            poiHelper.autosizeHelper(columns.length);
        }
    }
}
