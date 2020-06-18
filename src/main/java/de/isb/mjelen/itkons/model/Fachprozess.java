package de.isb.mjelen.itkons.model;

import de.isb.mjelen.itkons.poi.ModelDescriptor;
import de.isb.mjelen.itkons.poi.PoiSupport;
import de.isb.mjelen.itkons.poi.StatefulPoiHelper;
import java.util.Set;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class Fachprozess extends PoiSupport {

  private String code;
  private String name;
  private String verantwortlich;
  private Set<Informationssystem> informationssysteme;

  @Override
  public void buildContentRow(StatefulPoiHelper poiHelper, int index) {
    poiHelper.buildContentRowFromStringArray(index, new String[]{
        getCode(),
        getName(),
        getVerantwortlich(),
        serializeSet(informationssysteme)
    });
  }

  @Override
  public Class<? extends ModelDescriptor> getModelDescriptor() {
    return FachprozessMD.class;
  }

  @Override
  public String getIdentifier() {
    return null;
  }

  public static class FachprozessMD implements ModelDescriptor {

    private static final String[] columns = new String[]{
        "Code",
        "Name",
        "Verantwortlich",
        "Standardkonformität"};

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
