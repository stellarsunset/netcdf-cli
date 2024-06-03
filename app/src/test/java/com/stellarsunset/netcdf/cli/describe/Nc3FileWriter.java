package com.stellarsunset.netcdf.cli.describe;

import ucar.ma2.ArrayInt;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;
import ucar.nc2.write.NetcdfFormatWriter;

import java.io.File;
import java.io.IOException;
import java.util.List;

record Nc3FileWriter() {

    void write(File fileToWrite) {

        NetcdfFormatWriter.Builder builder = NetcdfFormatWriter.createNewNetcdf3(fileToWrite.getAbsolutePath());

        Dimension dimX = builder.addDimension("x", 10);
        Dimension dimY = builder.addDimension("y", 10);
        Dimension dimZ = builder.addDimension("z", 5);

        // Possible dimension variables
        builder.addVariable("x", DataType.INT, List.of(dimX));
        builder.addVariable("y", DataType.INT, List.of(dimY));
        builder.addVariable("z", DataType.INT, List.of(dimZ));

        // Possible coordinate variables
        builder.addVariable("xy", DataType.INT, List.of(dimX, dimY));
        builder.addVariable("zy", DataType.INT, List.of(dimZ, dimY));
        builder.addVariable("xyz", DataType.INT, List.of(dimX, dimY, dimZ));

        builder.setFill(true);

        ArrayInt xData = makeDimensionArray(10);
        ArrayInt yData = makeDimensionArray(10);
        ArrayInt zData = makeDimensionArray(5);

        try (NetcdfFormatWriter writer = builder.build()) {

            Variable varX = writer.findVariable("x");
            writer.write(varX, xData);

            Variable varY = writer.findVariable("y");
            writer.write(varY, yData);

            Variable varZ = writer.findVariable("z");
            writer.write(varZ, zData);

        } catch (InvalidRangeException e) {
            throw new IllegalArgumentException("Bad range for write.", e);
        } catch (IOException e) {
            throw new RuntimeException("IO error occurred during write.", e);
        }
    }

    private ArrayInt makeDimensionArray(int max) {

        ArrayInt data = new ArrayInt.D1(max, false);
        Index index = data.getIndex();

        for (int i = 0; i < data.getShape()[0]; i++) {
            data.set(index.set(i), i);
        }

        return data;
    }
}
