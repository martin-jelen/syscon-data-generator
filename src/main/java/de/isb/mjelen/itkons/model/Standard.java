package de.isb.mjelen.itkons.model;

import de.isb.mjelen.itkons.poi.ModelDescriptor;
import de.isb.mjelen.itkons.poi.PoiSupport;
import de.isb.mjelen.itkons.poi.StatefulPoiHelper;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class Standard extends PoiSupport {

    private String code;
    private String art;
    private String name;

    @Override
    public void buildContentRow(StatefulPoiHelper poiHelper, int index) {
        poiHelper.buildContentRowFromStringArray(index, new String[]{getCode(), getArt(), getName()});
    }

    @Override
    public Class<? extends ModelDescriptor> getModelDescriptor() {
        return StandardMD.class;
    }

    public static class StandardMD implements ModelDescriptor {
        private static final String[] columns = new String[]{"Code", "Art", "Name"};

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
