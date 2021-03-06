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
  private Standort standort;

  @Override
  public void buildContentRow(StatefulPoiHelper poiHelper, int index) {
    poiHelper.buildContentRowFromStringArray(index,
        new String[]{
            getCode(),
            getName(),
            getUebergeordneteOrgeinheit() != null ? getUebergeordneteOrgeinheit().getCode() : null,
            getStandort() != null ? getStandort().getCode() : null
        });
  }

  @Override
  public Class<? extends ModelDescriptor> getModelDescriptor() {
    return OrganisationseinheitMD.class;
  }

  @Override
  public String getIdentifier() {
    return getCode();
  }

  public static class OrganisationseinheitMD implements ModelDescriptor {

    private static final String[] columns = new String[]{"Code", "Name", "Uebergeordnete Einheit (Code)", "Standort (Code)"};

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
