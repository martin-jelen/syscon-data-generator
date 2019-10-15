package de.isb.mjelen.itkons.model;

import de.isb.mjelen.itkons.poi.ModelDescriptor;
import de.isb.mjelen.itkons.poi.PoiSupport;
import de.isb.mjelen.itkons.poi.StatefulPoiHelper;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class Standort extends PoiSupport {
    private String code;
    private String name;
    private String plz;
    private String anschrift;

    @Override
    public void buildContentRow(StatefulPoiHelper poiHelper, int index) {
        poiHelper.buildContentRowFromStringArray(index, new String[]{getCode(), getName(), getPlz(), getAnschrift()});
    }

    @Override
    public Class<? extends ModelDescriptor> getModelDescriptor() {
        return StandortMD.class;
    }

    public static class StandortMD implements ModelDescriptor {
        @Override
        public void buildHeaderRow(StatefulPoiHelper poiHelper) {
            poiHelper.buildHeaderFromTitleArray(new String[]{"Code", "Name", "PLZ", "Anschrift"});
        }

        @Override
        public void autosizeColumns(StatefulPoiHelper poiHelper) {
            poiHelper.autosizeHelper(5);
        }
    }
}
