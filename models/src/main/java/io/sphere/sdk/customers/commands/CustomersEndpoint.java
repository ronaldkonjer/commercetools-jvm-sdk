package io.sphere.sdk.customers.commands;

import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.customers.CustomerSignInResult;
import io.sphere.sdk.http.JsonEndpoint;

final class CustomersEndpoint {
    static JsonEndpoint<Customer> ENDPOINT = JsonEndpoint.of(Customer.typeReference(), "/customers");
    static JsonEndpoint<CustomerSignInResult> ENDPOINT_SIGNIN_RESULT = JsonEndpoint.of(CustomerSignInResult.typeReference(), ENDPOINT.endpoint());
}
