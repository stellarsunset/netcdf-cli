package com.stellarsunset.netcdf.cli.json;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

record SafeGenerator(JsonGenerator delegate) implements AutoCloseable {

    SafeGenerator writeStartObject() {
        try {
            this.delegate.writeStartObject();
            return this;
        } catch (IOException e) {
            throw new UnableToWriteJsonFieldException("start_object", e);
        }
    }

    SafeGenerator writeDouble(String name, double value) {
        try {
            this.delegate.writeNumberField(name, value);
            return this;
        } catch (IOException e) {
            throw new UnableToWriteJsonFieldException(name, e);
        }
    }

    SafeGenerator writeFloat(String name, float value) {
        try {
            this.delegate.writeNumberField(name, value);
            return this;
        } catch (IOException e) {
            throw new UnableToWriteJsonFieldException(name, e);
        }
    }

    SafeGenerator writeChar(String name, char value) {
        try {
            this.delegate.writeStringField(name, Character.toString(value));
            return this;
        } catch (IOException e) {
            throw new UnableToWriteJsonFieldException(name, e);
        }
    }

    SafeGenerator writeBool(String name, boolean value) {
        try {
            this.delegate.writeBooleanField(name, value);
            return this;
        } catch (IOException e) {
            throw new UnableToWriteJsonFieldException(name, e);
        }
    }

    SafeGenerator writeInt(String name, int value) {
        try {
            this.delegate.writeNumberField(name, value);
            return this;
        } catch (IOException e) {
            throw new UnableToWriteJsonFieldException(name, e);
        }
    }

    SafeGenerator writeShort(String name, short value) {
        try {
            this.delegate.writeNumberField(name, value);
            return this;
        } catch (IOException e) {
            throw new UnableToWriteJsonFieldException(name, e);
        }
    }

    SafeGenerator writeLong(String name, long value) {
        try {
            this.delegate.writeNumberField(name, value);
            return this;
        } catch (IOException e) {
            throw new UnableToWriteJsonFieldException(name, e);
        }
    }

    SafeGenerator writeEndObject() {
        try {
            this.delegate.writeEndObject();
            return this;
        } catch (IOException e) {
            throw new UnableToWriteJsonFieldException("end_object", e);
        }
    }

    SafeGenerator writeRaw(String text) {
        try {
            this.delegate.writeRaw(text);
            return this;
        } catch (IOException e) {
            throw new UnableToWriteJsonFieldException("raw_content", e);
        }
    }

    @Override
    public void close() throws Exception {
        this.delegate.close();
    }

    static final class UnableToWriteJsonFieldException extends RuntimeException {
        public UnableToWriteJsonFieldException(String field, Throwable throwable) {
            super(String.format("Unable to write Json content for field: %s", field), throwable);
        }
    }
}
