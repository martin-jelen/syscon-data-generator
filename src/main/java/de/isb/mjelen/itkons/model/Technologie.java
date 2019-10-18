package de.isb.mjelen.itkons.model;

import de.isb.mjelen.itkons.poi.ModelDescriptor;
import de.isb.mjelen.itkons.poi.PoiSupport;
import de.isb.mjelen.itkons.poi.StatefulPoiHelper;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Data
@SuperBuilder
public class Technologie extends PoiSupport {

    private String code;
    private String name;
    private String beschreibung;
    private LocalDate endOfLife;
    private Set<Standard> erfuelltStandards;

    public void buildContentRow(StatefulPoiHelper poiHelper, int index) {
        poiHelper.buildContentRowFromStringArray(index, new String[]{getCode(), getName(), getBeschreibung(), serializeDate(getEndOfLife())});
    }

    @Override
    public Class<? extends ModelDescriptor> getModelDescriptor() {
        return TechnologieMD.class;
    }

    public static class TechnologieMD implements ModelDescriptor {
        private static final String[] columns = new String[]{"Code", "Name", "Beschreibung", "End-of-Life"};

        @Override
        public void buildHeaderRow(StatefulPoiHelper poiHelper) {
            poiHelper.buildHeaderFromTitleArray(columns);
        }

        @Override
        public void autosizeColumns(StatefulPoiHelper poiHelper) {
            poiHelper.autosizeHelper(columns.length);
        }
    }

    @Override
    public String getIdentifier() {
        return getCode();
    }

}
