package io.sphere.sdk.products.attributeaccess;

import io.sphere.sdk.attributes.AttributeGetterSetter;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.attributes.AttributeTypes;
import io.sphere.sdk.products.Product;

public interface WithLongDescription {
    default AttributeGetterSetter<Product, LocalizedString> longDescription() {
        return AttributeTypes.ofLocalizedString().getterSetter("longDescription");
    }
}
