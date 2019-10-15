package de.isb.mjelen.itkons.poi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class PoiSupport {
    public abstract void buildContentRow(StatefulPoiHelper poiHelper, int index);
    public abstract Class<? extends ModelDescriptor> getModelDescriptor();
}
