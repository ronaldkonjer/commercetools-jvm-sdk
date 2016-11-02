package io.sphere.sdk.annotations.processors;

import io.sphere.sdk.annotations.FactoryMethod;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.*;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.sphere.sdk.annotations.processors.ClassModelFactory.*;
import static io.sphere.sdk.annotations.processors.ResourceDraftBuilderClassModelFactory.BEAN_GETTER_PREDICATE;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;

final class ClassConfigurer {
    public static SourceHolder ofSource(final TypeElement typeElement) {
        return new SourceHolder(typeElement);
    }

    public static class SourceHolder {
        private final TypeElement typeElement;

        private SourceHolder(final TypeElement typeElement) {
            this.typeElement = typeElement;
        }

        public PackageHolder samePackageFromSource() {
            return packageFrom(typeElement);
        }

        public PackageHolder packageFrom(final TypeElement typeElement) {
            final String packageName = ClassModelFactory.packageName(typeElement);
            return new PackageHolder(packageName, typeElement);
        }
    }

    public static class PackageHolder {
        private final String packageName;
        private final TypeElement typeElement;

        private PackageHolder(final String packageName, final TypeElement typeElement) {
            this.packageName = packageName;
            this.typeElement = typeElement;
        }

        public ImportHolder imports() {
            return new ImportHolder(typeElement, this);
        }
    }

    public static class ImportHolder {
        private final TypeElement typeElement;
        private final List<String> imports = new LinkedList<>();
        private final PackageHolder packageHolder;

        public ImportHolder(final TypeElement typeElement, final PackageHolder packageHolder) {

            this.typeElement = typeElement;
            this.packageHolder = packageHolder;
        }

        public ImportHolder defaultImports() {
            return addImport("javax.annotation.Nullable");
        }

        public ImportHolder addImport(final String s) {
            imports.add(s);
            return this;
        }


        public JavadocHolder classJavadoc(final String s) {
            return new JavadocHolder(typeElement, this, s);
        }
    }

    public static class JavadocHolder {
        private final TypeElement typeElement;
        private final ImportHolder importHolder;
        private final String javadoc;

        public JavadocHolder(final TypeElement typeElement, final ImportHolder importHolder, final String javadoc) {
            this.typeElement = typeElement;
            this.importHolder = importHolder;
            this.javadoc = javadoc;
        }


        public ClassModifierHolder modifiers(final String ... modifiers) {
            return modifiers(asList(modifiers));
        }

        public ClassModifierHolder modifiers(final List<String> modifiers) {
            return new ClassModifierHolder(modifiers, this);
        }
    }

    public static class ClassModifierHolder {
        private final List<String> modifiers;
        private final JavadocHolder holder;

        private ClassModifierHolder(final List<String> modifiers, final JavadocHolder holder) {
            this.modifiers = modifiers;
            this.holder = holder;
        }

        public ClassTypeHolder classType() {
            return new ClassTypeHolder(ClassType.CLASS, this, holder.importHolder.packageHolder.typeElement);
        }
    }

    public static class ClassTypeHolder {
        private final ClassType classType;
        private final ClassModifierHolder classModifierHolder;
        private final TypeElement typeElement;

        private ClassTypeHolder(final ClassType classType, final ClassModifierHolder classModifierHolder, final TypeElement typeElement) {
            this.classType = classType;
            this.classModifierHolder = classModifierHolder;
            this.typeElement = typeElement;
        }

        public ClassNameHolder className(final UnaryOperator<String> op) {
            return new ClassNameHolder(op.apply(this.classModifierHolder.holder.importHolder.packageHolder.typeElement.getSimpleName().toString()), this, typeElement);
        }
    }

    public static class ClassNameHolder {
        private final TypeElement typeElement;
        private final ClassModelBuilder builder;

        private ClassNameHolder(final String name, final ClassTypeHolder classTypeHolder, final TypeElement typeElement) {
            this.typeElement = typeElement;
            this.builder = ClassModelBuilder.of(name, classTypeHolder.classType);
            this.builder.packageName(classTypeHolder.classModifierHolder.holder.importHolder.packageHolder.packageName);
            classTypeHolder.classModifierHolder.holder.importHolder.imports.forEach(imp -> builder.addImport(imp));
            classTypeHolder.classModifierHolder.modifiers.forEach(modifier -> builder.addModifiers(modifier));
            builder.setJavadoc(classTypeHolder.classModifierHolder.holder.javadoc);
        }

        public BaseClassHolder extending(final String baseClassName) {
            return new BaseClassHolder(baseClassName, builder, typeElement);
        }

        public BaseClassHolder extending(final Class<?> baseClass) {
            return extending(baseClass.getName());
        }
    }

    public static class BaseClassHolder {
        private final ClassModelBuilder builder;
        private final TypeElement typeElement;

        private BaseClassHolder(final String baseClassName, final ClassModelBuilder builder, final TypeElement typeElement) {
            this.builder = builder;
            this.typeElement = typeElement;
            builder.setBaseClassName(baseClassName);
        }

        public InterfacesHolder implementingBasedOnSourceName(final Function<String, String> op) {
            return implementing(op.apply(typeElement.getSimpleName().toString()));
        }

        public InterfacesHolder implementing(final String ... interfaces) {
            return new InterfacesHolder(asList(interfaces), builder, typeElement);
        }

        public InterfacesHolder implementing(final TypeElement typeElement) {
            return implementing(typeElement.getSimpleName().toString());
        }
    }

    public static class InterfacesHolder {
        private final ClassModelBuilder builder;
        private final TypeElement typeElement;

        private InterfacesHolder(final List<String> interfaces, final ClassModelBuilder builder, final TypeElement typeElement) {
            this.builder = builder;
            this.typeElement = typeElement;
            builder.interfaces(interfaces);
        }

        public FieldsHolder fields() {
            return new FieldsHolder(builder, typeElement);
        }
    }

    public static class FieldsHolder {
        private final ClassModelBuilder builder;
        private final TypeElement typeElement;

        private FieldsHolder(final ClassModelBuilder builder, final TypeElement typeElement) {
            this.builder = builder;
            this.typeElement = typeElement;
        }

        public FieldsHolder fields(final List<FieldModel> fields) {
            fields.forEach(builder::addField);
            return this;
        }

        public FieldsHolder fieldsFromInterfaceBeanGetters(final boolean finalFields) {
//            if (typeElement.getSimpleName().toString().equals("Channel")) {
//                final List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
//
////                final String s = interfaces.get(0).accept(new StringStringTypeVisitor(), "");
//                final DeclaredType declaredType = (DeclaredType) interfaces.get(0);
//                final String s = declaredType.asElement().getEnclosedElements().toString();
//                //TODO use visitor to get also other methods
//                //todo check for default implemented, also toReference
//                throw new RuntimeException(s);
//            }

            final Stream<? extends Element> elementStream = elementStreamIncludingInterfaces();
            final List<FieldModel> fields = elementStream
                    .filter(BEAN_GETTER_PREDICATE)
                    .map(DistinctElementWrapper::new)
                    .distinct()
                    .map(wrapper -> wrapper.element)
                    .map(typeElement -> createField(typeElement, finalFields))
                    .collect(Collectors.toList());
            return fields(fields);
        }

        private Stream<? extends Element> elementStreamIncludingInterfaces() {
            final Stream<? extends Element> interfacesMethodsStream = typeElement.getInterfaces().stream()
                    .filter(i -> i instanceof DeclaredType)
                    .map(i -> (DeclaredType) i)
                    .flatMap(i -> i.asElement().getEnclosedElements().stream());
            return Stream.concat(typeElement.getEnclosedElements().stream(), interfacesMethodsStream);
        }

        private static class DistinctElementWrapper {
            private final Element element;

            private DistinctElementWrapper(final Element element) {
                this.element = element;
            }

            @Override
            public boolean equals(final Object o) {
                if (this == o) return true;
                if (!(o instanceof DistinctElementWrapper)) return false;
                final DistinctElementWrapper that = (DistinctElementWrapper) o;
                return Objects.equals(element.getSimpleName(), that.element.getSimpleName());
            }

            @Override
            public int hashCode() {
                return Objects.hash(element.getSimpleName());
            }
        }

        public ConstructorsHolder constructors() {
            return new ConstructorsHolder(builder, typeElement);
        }

        private static class StringStringTypeVisitor implements TypeVisitor<String, String> {
            @Override
            public String visit(final TypeMirror t, final String s) {
                return null;
            }

            @Override
            public String visit(final TypeMirror t) {
                return null;
            }

            @Override
            public String visitPrimitive(final PrimitiveType t, final String s) {
                return null;
            }

            @Override
            public String visitNull(final NullType t, final String s) {
                return null;
            }

            @Override
            public String visitArray(final ArrayType t, final String s) {
                return null;
            }

            @Override
            public String visitDeclared(final DeclaredType t, final String s) {
                return "declared " + t.toString();
            }

            @Override
            public String visitError(final ErrorType t, final String s) {
                return null;
            }

            @Override
            public String visitTypeVariable(final TypeVariable t, final String s) {
                return null;
            }

            @Override
            public String visitWildcard(final WildcardType t, final String s) {
                return null;
            }

            @Override
            public String visitExecutable(final ExecutableType t, final String s) {
                return null;
            }

            @Override
            public String visitNoType(final NoType t, final String s) {
                return null;
            }

            @Override
            public String visitUnknown(final TypeMirror t, final String s) {
                return null;
            }

            @Override
            public String visitUnion(final UnionType t, final String s) {
                return null;
            }

            @Override
            public String visitIntersection(final IntersectionType t, final String s) {
                return null;
            }
        }
    }

    public static class ConstructorsHolder {
        private final ClassModelBuilder builder;
        private final TypeElement typeElement;

        private ConstructorsHolder(final ClassModelBuilder builder, final TypeElement typeElement) {
            this.builder = builder;
            this.typeElement = typeElement;
        }

        public ConstructorsHolder constructorForAllFields() {
            addDraftConstructors(builder);
            return new ConstructorsHolder(builder, typeElement);
        }

        public MethodsHolder methods() {
            return new MethodsHolder(builder, typeElement);
        }
    }

    public static class MethodsHolder {
        private final ClassModelBuilder builder;
        private final TypeElement typeElement;

        private MethodsHolder(final ClassModelBuilder builder, final TypeElement typeElement) {
            this.builder = builder;
            this.typeElement = typeElement;
        }

        public MethodsHolder factoryMethodsAccordingToAnnotations() {
            ClassModelFactory.addFactoryMethods(builder, typeElement);
            return this;
        }

        public MethodsHolder builderMethods() {
            ClassModelFactory.addBuilderMethods(builder, typeElement);
            return this;
        }

        public MethodsHolder buildMethod() {
            ClassModelFactory.addBuildMethod(builder, typeElement);
            return this;
        }

        public ClassModel build() {
            return builder.build();
        }

        public MethodsHolder gettersForFields() {
            final List<FieldModel> fieldModels = instanceFieldsSorted(builder);
            fieldModels.forEach(field -> builder.addMethod(createGetter(field)));
            return this;
        }

        public MethodsHolder withers() {
            addDslMethods(builder, typeElement);
            addNewBuilderMethod(builder, typeElement);
            return this;
        }

        public MethodsHolder factoryMethodFromInterfaceInstance(final boolean finalFields) {
            final MethodModel m = new MethodModel();
            m.setModifiers(asList("public", "static"));
            m.setName("of");
            m.setReturnType(builder.getName());
            final MethodParameterModel p = new MethodParameterModel();
            p.setModifiers(singletonList("final"));
            p.setType(typeElement.getSimpleName().toString());
            p.setName("template");
            m.setParameters(singletonList(p));
            final String dsd = typeElement.getEnclosedElements()
                    .stream()
                    .filter(BEAN_GETTER_PREDICATE)
                    .map(element -> {
                        final FieldModel field = createField(element, finalFields);
                        return ImmutablePair.of(field.getName(), element.getSimpleName().toString());
                    })
                    .sorted(Comparator.comparing(ImmutablePair::getLeft))
                    .map(pair -> format("template.%s()", pair.getRight()))
                    .collect(joining(", "));
            final String body = "return new " + builder.getName() + "(" + dsd + ");";
            m.setBody(body);
            builder.addMethod(m);
            return this;
        }

        public <A extends Annotation> MethodsHolder additionalContents(final Class<A> a, final Function<A, String[]> op) {
            final A annotation = typeElement.getAnnotation(a);
            if (annotation != null) {
                final String[] thingsToAdd = op.apply(annotation);
                final String s = Arrays.stream(thingsToAdd).collect(joining("\n"));
                builder.setAdditions(s);
            }
            return this;
        }
    }

    private static MethodModel createGetter(final FieldModel field) {
        final String methodName = "get" + StringUtils.capitalize(field.getName());
        final MethodModel method = new MethodModel();
        method.setName(methodName);
        method.setReturnType(field.getType());
        method.setBody("return " + field.getName() + ";");
        method.addModifiers("public");

        field.getAnnotations()
                .stream()
                .filter(a -> "Nullable".equals(a.getName()))
                .findFirst()
                .ifPresent(a -> method.setAnnotations(singletonList(createNullableAnnotation())));

        final List<AnnotationModel> list = new LinkedList<>(method.getAnnotations());
        final AnnotationModel a = new AnnotationModel();
        a.setName("Override");
        list.add(a);
        method.setAnnotations(list);

        return method;
    }

    private static void addDslMethods(final ClassModelBuilder builder, final TypeElement typeElement) {
        typeElement.getEnclosedElements()
                .stream()
                .filter(NORMAL_GETTERS_PREDICATE)
                .forEach(element -> addDslMethod(element, builder));
    }

    private static void addDslMethod(final Element element, final ClassModelBuilder builder) {
        final String fieldName = fieldNameFromGetter(element.toString());
        final MethodModel method = new MethodModel();
        final String name = witherNameFromGetter(element);
        method.setName(name);
        method.setReturnType(builder.getName());
        method.addModifiers("public");
        method.setParameters(singletonList(getBuilderMethodParameterModel(element, fieldName)));
        final HashMap<String, Object> values = new HashMap<>();
        values.put("fieldName", fieldName);
        method.setBody(Templates.render("witherMethodBody", values));

        builder.addMethod(method);
    }

    private static void addNewBuilderMethod(final ClassModelBuilder builder, final TypeElement typeElement) {
        final MethodModel method = new MethodModel();
        method.setModifiers(singletonList("private"));
        final String associatedBuilderName = ResourceDraftBuilderClassModelFactory.builderName(typeElement);
        builder.addImport(builder.build().getPackageName() + "." + associatedBuilderName);
        method.setReturnType(associatedBuilderName);
        method.setName("newBuilder");
        method.setBody("return new " + associatedBuilderName + "(" + fieldNamesSortedString(builder) + ");");
        builder.addMethod(method);
    }
}
