package de.isb.mjelen.itkons.poi;

public interface ModelDescriptor {
    void buildHeaderRow(StatefulPoiHelper poiHelper);
    void autosizeColumns(StatefulPoiHelper poiHelper);
}
