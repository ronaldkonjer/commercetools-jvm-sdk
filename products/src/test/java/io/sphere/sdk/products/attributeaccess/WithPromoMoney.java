package io.sphere.sdk.products.attributeaccess;

import io.sphere.sdk.attributes.AttributeGetterSetter;
import io.sphere.sdk.models.Money;
import io.sphere.sdk.attributes.AttributeTypes;
import io.sphere.sdk.products.Product;

public interface WithPromoMoney {
    default AttributeGetterSetter<Product, Money> promoMoney() {
        return AttributeTypes.ofMoney().getterSetter("promo-money");
    }
}
