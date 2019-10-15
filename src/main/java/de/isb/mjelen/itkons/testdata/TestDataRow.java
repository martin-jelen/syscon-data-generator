package de.isb.mjelen.itkons.testdata;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.TimeZone;

@Data
@Builder
public class TestDataRow {
    private Long rowId;
    private String geonameid;
    private String name;
    private String asciiname;
    private String[] alternatenames;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String feature_class;
    private String feature_code;
    private String country_code;
    private String cc2;
    private String admin1_code;
    private String admin2_code;
    private String admin3_code;
    private String admin4_code;
    private Integer population;
    private Integer elevation;
    private String dem;
    private TimeZone timezone;
    private LocalDate modified;
}
