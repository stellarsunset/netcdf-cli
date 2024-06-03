package com.stellarsunset.netcdf.cli.json;

import java.util.HashSet;
import java.util.Set;

public final class JsonBinding {

    private final Set<AliasedVariable> dimensionVariables;

    private final Set<AliasedVariable> coordinateVariables;

    private JsonBinding(Builder builder) {
        this.dimensionVariables = Set.copyOf(builder.dimensionVariables);
        this.coordinateVariables = Set.copyOf(builder.coordinateVariables);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Set<AliasedVariable> dimensionVariables() {
        return dimensionVariables;
    }

    public Set<AliasedVariable> coordinateVariables() {
        return coordinateVariables;
    }

    public static final class Builder {

        private final Set<AliasedVariable> dimensionVariables = new HashSet<>();

        private final Set<AliasedVariable> coordinateVariables = new HashSet<>();

        private Builder() {
        }

        public Builder addDimensionVariable(AliasedVariable variable) {
            this.dimensionVariables.add(variable);
            return this;
        }

        public Builder addDimensionVariable(String alias, String variableName) {
            return addDimensionVariable(new AliasedVariable(alias, variableName));
        }

        public Builder addCoordinateVariable(AliasedVariable variable) {
            this.coordinateVariables.add(variable);
            return this;
        }

        public Builder addCoordinateVariable(String alias, String variableName) {
            this.coordinateVariables.add(new AliasedVariable(alias, variableName));
            return this;
        }

        public JsonBinding build() {
            return new JsonBinding(this);
        }
    }

    public record AliasedVariable(String alias, String variableName) {
    }
}
