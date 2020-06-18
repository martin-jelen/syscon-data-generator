package de.isb.mjelen.itkons.model;

import de.isb.mjelen.itkons.poi.ModelDescriptor;
import de.isb.mjelen.itkons.poi.PoiSupport;
import de.isb.mjelen.itkons.poi.StatefulPoiHelper;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@SuperBuilder
public class Subsystem extends PoiSupport {

    private String code;
    private String name;
    private String beschreibung;
    private Informationssystem subsystemVon;
    private Set<Technologie> technologien;
    private Set<Subsystem> subsysteme;

    public void buildContentRow(StatefulPoiHelper poiHelper, int index) {
        poiHelper.buildContentRowFromStringArray(index, new String[]{getCode(), getName(), getBeschreibung(), getSubsystemVon().getCode()});
    }

    @Override
    public Class<? extends ModelDescriptor> getModelDescriptor() {
        return SubsystemMD.class;
    }

    public static class SubsystemMD implements ModelDescriptor {
        private static final String[] columns = new String[]{"Code", "Name", "Beschreibung", "Subsystem von (Code)"};

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
