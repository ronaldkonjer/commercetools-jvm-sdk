package io.sphere.sdk.cartdiscounts;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.sphere.sdk.models.Base;

public class RelativeCartDiscountValue extends Base implements CartDiscountValue {
    private final int permyriad;

    @JsonCreator
    private RelativeCartDiscountValue(final int permyriad) {
        this.permyriad = permyriad;
    }

    public int getPermyriad() {
        return permyriad;
    }

    public int getBasisPoint() {
        return getPermyriad();
    }

    public static RelativeCartDiscountValue of(final int permyriad) {
        return new RelativeCartDiscountValue(permyriad);
    }
}
