package io.sphere.sdk.search;

import io.sphere.sdk.models.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class SimpleBaseExpression extends Base {

    protected SimpleBaseExpression() {
    }

    abstract protected String expression();

    public String path() {
        final Pattern pattern = Pattern.compile("^([^:\\s]*)");
        final Matcher matcher = pattern.matcher(expression());
        return matcher.find() ? matcher.group(1) : expression();
    }
}