package de.isb.mjelen.itkons.model;

import de.isb.mjelen.itkons.poi.ModelDescriptor;
import de.isb.mjelen.itkons.poi.PoiSupport;
import de.isb.mjelen.itkons.poi.StatefulPoiHelper;
import lombok.Data;
import lombok.experimental.SuperBuilder;

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
    private Set<Organisationseinheit> eingesetztIn;
    private Set<Subsystem> subsysteme;
    private Set<Technologie> technologien;

    @Override
    public void buildContentRow(StatefulPoiHelper poiHelper, int index) {
        poiHelper.buildContentRowFromStringArray(index, new String[]{getCode(), getName(), getBeschreibung(), getVerantwortlich(), getAnzahlInstallationen() != null ? getAnzahlInstallationen().toString() : null, getAnzahlAnwender() != null ? getAnzahlAnwender().toString() : null});
    }

    @Override
    public Class<? extends ModelDescriptor> getModelDescriptor() {
        return InformationssystemMD.class;
    }

    public static class InformationssystemMD implements ModelDescriptor {
        @Override
        public void buildHeaderRow(StatefulPoiHelper poiHelper) {
            poiHelper.buildHeaderFromTitleArray(new String[]{"Code", "Name", "Beschreibung", "Verantwortlich", "Anzahl Installationen", "Anzahl Anwender"});
        }

        @Override
        public void autosizeColumns(StatefulPoiHelper poiHelper) {
            poiHelper.autosizeHelper(6);
        }
    }
}
