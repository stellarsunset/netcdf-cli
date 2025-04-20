package com.stellarsunset.netcdf.cli.json;

import com.stellarsunset.netcdf.cli.json.JsonBinding.AliasedVariable;
import io.github.stellarsunset.netcdf.FieldBinding;
import io.github.stellarsunset.netcdf.SchemaBinding;
import ucar.ma2.DataType;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

final class BindingMaker {

    private BindingMaker() {
    }

    /**
     * Uses the target {@link NetcdfFile} to convert the incoming {@link JsonBinding} into a full {@link SchemaBinding}.
     */
    static SchemaBinding<SafeGenerator> createBindingFor(NetcdfFile file, JsonBinding binding, SafeGenerator generator) {

        SchemaBinding.Builder<SafeGenerator> builder = SchemaBinding.builder();

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
                .recordInitializer(generator::writeStartObject)
                .recordFinalizer(g -> g.writeEndObject().writeRaw(System.lineSeparator()))
                .build();
    }

    private static FieldBinding<SafeGenerator> jsonSetter(String fieldName, DataType type) {
        return switch (type) {
            case DOUBLE -> doubles((s, d) -> s.writeDouble(fieldName, d));
            case FLOAT -> floats((s, f) -> s.writeFloat(fieldName, f));
            case CHAR -> chars((s, c) -> s.writeChar(fieldName, c));
            case BOOLEAN -> bools((s, b) -> s.writeBool(fieldName, b));
            case UINT -> ints((s, b) -> s.writeLong(fieldName, Integer.toUnsignedLong(b))); // no unsigned types
            case ENUM4, INT -> ints((s, b) -> s.writeInt(fieldName, b));
            case USHORT -> shorts((s, b) -> s.writeInt(fieldName, Short.toUnsignedInt(b))); // no unsigned
            case ENUM2, SHORT -> shorts((s, b) -> s.writeShort(fieldName, b));
            case UBYTE -> bytes((s, b) -> s.writeInt(fieldName, Short.toUnsignedInt(b)));
            case ENUM1, BYTE -> bytes((s, b) -> s.writeShort(fieldName, (short) b)); // no byte type - upcast to short
            case LONG -> longs((s, l) -> s.writeLong(fieldName, l));
            case ULONG, STRING, STRUCTURE, SEQUENCE, OPAQUE, OBJECT ->
                    throw new IllegalArgumentException(String.format("Unhandled Json binding for NetCDF primitive type: %s", type));
        };
    }

    private static <T> FieldBinding<T> doubles(FieldBinding.Double<T> doubles) {
        return doubles;
    }

    private static <T> FieldBinding<T> floats(FieldBinding.Float<T> floats) {
        return floats;
    }

    private static <T> FieldBinding<T> chars(FieldBinding.Char<T> chars) {
        return chars;
    }

    private static <T> FieldBinding<T> bools(FieldBinding.Bool<T> bools) {
        return bools;
    }

    private static <T> FieldBinding<T> ints(FieldBinding.Int<T> ints) {
        return ints;
    }

    private static <T> FieldBinding<T> shorts(FieldBinding.Short<T> shorts) {
        return shorts;
    }

    private static <T> FieldBinding<T> bytes(FieldBinding.Byte<T> bytes) {
        return bytes;
    }

    private static <T> FieldBinding<T> longs(FieldBinding.Long<T> longs) {
        return longs;
    }
}
