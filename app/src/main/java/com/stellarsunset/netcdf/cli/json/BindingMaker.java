package com.stellarsunset.netcdf.cli.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.stellarsunset.netcdf.SchemaBinding;
import com.stellarsunset.netcdf.cli.json.JsonBinding.AliasedVariable;
import com.stellarsunset.netcdf.field.FieldSetter;
import ucar.ma2.DataType;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.IOException;

final class BindingMaker {

    private BindingMaker() {
    }

    /**
     * Uses the target {@link NetcdfFile} to convert the incoming {@link JsonBinding} into a full {@link SchemaBinding}.
     */
    static SchemaBinding<JsonGenerator> createBindingFor(NetcdfFile file, JsonBinding binding, JsonGenerator generator) {

        SchemaBinding.Builder<JsonGenerator> builder = SchemaBinding.builder();

        for (AliasedVariable dimensionVariable : binding.dimensionVariables()) {

            Variable variable = file.findVariable(dimensionVariable.variableName());

            if (variable != null) {
                builder.dimensionVariable(
                        variable.getDimension(0).getName(),
                        variable.getFullName(),
                        jsonSetter(dimensionVariable.alias(), variable.getDataType())
                );
            }
        }

        for (AliasedVariable coordinateVariable : binding.coordinateVariables()) {

            Variable variable = file.findVariable(coordinateVariable.variableName());

            if (variable != null) {
                builder.coordinateVariable(
                        variable.getFullName(),
                        jsonSetter(coordinateVariable.alias(), variable.getDataType())
                );
            }
        }

        return builder
                .recordInitializer(() -> {
                    try {
                        generator.writeStartObject();
                        return generator;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .recordFinalizer(g -> {
                    try {
                        g.writeEndObject();
                        g.writeRaw(System.lineSeparator());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .build();
    }

    private static FieldSetter<JsonGenerator> jsonSetter(String fieldName, DataType type) {
        return switch (type) {
            case DOUBLE -> FieldSetter.doubles((s, d) -> {
                s.writeNumberField(fieldName, d);
                return s;
            });
            case FLOAT -> FieldSetter.floats((s, f) -> {
                s.writeNumberField(fieldName, f);
                return s;
            });
            case CHAR -> FieldSetter.characters((s, c) -> {
                s.writeStringField(fieldName, Character.toString(c));
                return s;
            });
            case BOOLEAN -> FieldSetter.booleans((s, b) -> {
                s.writeBooleanField(fieldName, b);
                return s;
            });
            case ENUM4, UINT, INT -> FieldSetter.ints((s, i) -> {
                s.writeNumberField(fieldName, i);
                return s;
            });
            case ENUM2, USHORT, SHORT -> FieldSetter.shorts((s, h) -> {
                s.writeNumberField(fieldName, h);
                return s;
            });
            case ENUM1, UBYTE, BYTE -> FieldSetter.bytes((s, b) -> {
                s.writeBinaryField(fieldName, new byte[]{b});
                return s;
            });
            case ULONG, LONG -> FieldSetter.longs((s, l) -> {
                s.writeNumberField(fieldName, l);
                return s;
            });
            case STRING, STRUCTURE, SEQUENCE, OPAQUE, OBJECT -> FieldSetter.noop();
        };
    }
}
