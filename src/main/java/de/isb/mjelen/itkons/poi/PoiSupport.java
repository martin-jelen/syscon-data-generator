package de.isb.mjelen.itkons.poi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.poi.ss.formula.functions.T;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

@SuperBuilder
public abstract class PoiSupport {
    public abstract void buildContentRow(StatefulPoiHelper poiHelper, int index);
    public abstract Class<? extends ModelDescriptor> getModelDescriptor();
    public abstract String getIdentifier();

    protected String serializeSet(Set<? extends PoiSupport> daten) {
        return daten == null ? "" : daten.stream().map(element -> element.getIdentifier()).collect(Collectors.joining(";"));
    }

    protected String serializeInteger(Integer nr) {
        return nr != null ? nr.toString() : "";
    }

    protected String serializeDate(LocalDate when) {
        return when != null ? when.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "";
    }
}
