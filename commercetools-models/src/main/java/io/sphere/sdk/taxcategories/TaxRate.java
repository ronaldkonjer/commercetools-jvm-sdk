package io.sphere.sdk.taxcategories;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.neovisionaries.i18n.CountryCode;

import javax.annotation.Nullable;
import java.util.List;

@JsonDeserialize(as=TaxRateImpl.class)
public interface TaxRate {
    String getId();

    String getName();

    Double getAmount();

    Boolean isIncludedInPrice();

    CountryCode getCountry();

    @Nullable
    String getState();

    //TODO check if nullable
    List<SubRate> getSubRates();

    Boolean isExternallySet();

    boolean equalsIgnoreId(TaxRate other);
}
