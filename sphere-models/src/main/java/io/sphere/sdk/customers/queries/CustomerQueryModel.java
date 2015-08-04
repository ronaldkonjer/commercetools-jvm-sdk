package io.sphere.sdk.customers.queries;

import io.sphere.sdk.customergroups.CustomerGroup;
import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.queries.*;

public final class CustomerQueryModel extends DefaultModelQueryModelImpl<Customer> {

    public static CustomerQueryModel of() {
        return new CustomerQueryModel(null, null);
    }

    private CustomerQueryModel(final QueryModel<Customer> parent, final String pathSegment) {
        super(parent, pathSegment);
    }

    public StringQuerySortingModel<Customer> firstName() {
        return stringModel("firstName");
    }

    public StringQuerySortingModel<Customer> lastName() {
        return stringModel("lastName");
    }

    public StringQuerySortingModel<Customer> email() {
        return stringModel("email");
    }

    public StringQuerySortingModel<Customer> defaultShippingAddressId() {
        return stringModel("defaultShippingAddressId");
    }

    public StringQuerySortingModel<Customer> defaultBillingAddressId() {
        return stringModel("defaultBillingAddressId");
    }

    public BooleanQueryModel<Customer> isEmailVerified() {
        return booleanModel("isEmailVerified");
    }

    public StringQuerySortingModel<Customer> externalId() {
        return stringModel("externalId");
    }

    public ReferenceQueryModel<Customer, CustomerGroup> customerGroup() {
        return referenceModel("customerGroup");
    }
}