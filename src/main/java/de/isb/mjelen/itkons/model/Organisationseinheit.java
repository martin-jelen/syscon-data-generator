package de.isb.mjelen.itkons.model;

import de.isb.mjelen.itkons.poi.ModelDescriptor;
import de.isb.mjelen.itkons.poi.PoiSupport;
import de.isb.mjelen.itkons.poi.StatefulPoiHelper;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class Organisationseinheit extends PoiSupport {
    private String code;
    private String name;
    private Organisationseinheit uebergeordneteOrgeinheit;

    @Override
    public void buildContentRow(StatefulPoiHelper poiHelper, int index) {
        poiHelper.buildContentRowFromStringArray(index, new String[]{getCode(), getName(), getUebergeordneteOrgeinheit().getCode()});
    }

    @Override
    public Class<? extends ModelDescriptor> getModelDescriptor() {
        return OrganisationseinheitMD.class;
    }

    public static class OrganisationseinheitMD implements ModelDescriptor {
        @Override
        public void buildHeaderRow(StatefulPoiHelper poiHelper) {
            poiHelper.buildHeaderFromTitleArray(new String[]{"Code", "Name", "Uebergeordnete Einheit (Code)"});
        }

        @Override
        public void autosizeColumns(StatefulPoiHelper poiHelper) {
            poiHelper.autosizeHelper(3);
        }
    }

}