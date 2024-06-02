package com.stellarsunset.netcdf.cli.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.stellarsunset.netcdf.SchemaBinding;
import com.stellarsunset.netcdf.field.FieldSetter;
import ucar.ma2.DataType;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.util.Set;

final class BindingMaker {

    static SchemaBinding<JsonGenerator> createBindingFor(NetcdfFile file, Set<String> dimensionVariables, Set<String> coordinateVariables) {

        SchemaBinding.Builder<JsonGenerator> builder = SchemaBinding.builder();



        return builder.build();
    }

    private FieldSetter<JsonGenerator> jsonSetter(String fieldName, DataType type) {
        return switch (type) {
            case DOUBLE -> FieldSetter.doubles((s, v) -> {
                s.writeNumberField(fieldName, v);
            });
            case FLOAT -> shouldBe(variable, setter, FloatSetter.class);
            case CHAR -> shouldBe(variable, setter, CharacterSetter.class);
            case BOOLEAN -> shouldBe(variable, setter, BooleanSetter.class);
            case ENUM4, UINT, INT -> shouldBe(variable, setter, IntSetter.class);
            case ENUM2, USHORT, SHORT -> shouldBe(variable, setter, ShortSetter.class);
            case ENUM1, UBYTE, BYTE -> shouldBe(variable, setter, ByteSetter.class);
            case ULONG, LONG -> shouldBe(variable, setter, LongSetter.class);
            case STRING, STRUCTURE, SEQUENCE, OPAQUE, OBJECT ->
                    Optional.of(new Error.UnhandledVariableType(variable.getFullName(), variable.getDataType()));
        };
    }

    private SchemaBinding.Builder<JsonGenerator> configureCoordinateVariable(SchemaBinding.Builder<JsonGenerator> builder, Variable variable) {
        return switch (variable.getDataType()) {
            case DOUBLE -> builder.doubleCoordinateVariable(variable.getFullName(), (g, s) -> );
            case FLOAT -> shouldBe(variable, setter, FloatSetter.class);
            case CHAR -> shouldBe(variable, setter, CharacterSetter.class);
            case BOOLEAN -> shouldBe(variable, setter, BooleanSetter.class);
            case ENUM4, UINT, INT -> shouldBe(variable, setter, IntSetter.class);
            case ENUM2, USHORT, SHORT -> shouldBe(variable, setter, ShortSetter.class);
            case ENUM1, UBYTE, BYTE -> shouldBe(variable, setter, ByteSetter.class);
            case ULONG, LONG -> shouldBe(variable, setter, LongSetter.class);
            case STRING, STRUCTURE, SEQUENCE, OPAQUE, OBJECT ->
                    Optional.of(new Error.UnhandledVariableType(variable.getFullName(), variable.getDataType()));
        };
    }

    private SchemaBinding.Builder<JsonGenerator> configureDimensionVariable(SchemaBinding.Builder<JsonGenerator> builder, Variable variable) {
        return switch (variable.getDataType()) {
            case DOUBLE -> ;
            case FLOAT -> shouldBe(variable, setter, FloatSetter.class);
            case CHAR -> shouldBe(variable, setter, CharacterSetter.class);
            case BOOLEAN -> shouldBe(variable, setter, BooleanSetter.class);
            case ENUM4, UINT, INT -> shouldBe(variable, setter, IntSetter.class);
            case ENUM2, USHORT, SHORT -> shouldBe(variable, setter, ShortSetter.class);
            case ENUM1, UBYTE, BYTE -> shouldBe(variable, setter, ByteSetter.class);
            case ULONG, LONG -> shouldBe(variable, setter, LongSetter.class);
            case STRING, STRUCTURE, SEQUENCE, OPAQUE, OBJECT ->
                    Optional.of(new Error.UnhandledVariableType(variable.getFullName(), variable.getDataType()));
        };
    }
}
