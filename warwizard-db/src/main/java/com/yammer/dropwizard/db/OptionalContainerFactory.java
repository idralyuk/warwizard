package com.yammer.dropwizard.db;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.skife.jdbi.v2.ContainerBuilder;
import org.skife.jdbi.v2.tweak.ContainerFactory;

public class OptionalContainerFactory implements ContainerFactory<Optional<?>> {
    @Override
    public boolean accepts(Class<?> type) {
        return Optional.class.isAssignableFrom(type);
    }

    @Override
    public ContainerBuilder<Optional<?>> newContainerBuilderFor(Class<?> type) {
        return new OptionalContainerBuilder();
    }

    private static class OptionalContainerBuilder implements ContainerBuilder<Optional<?>> {
        private Object contents;

        @Override
        public ContainerBuilder<Optional<?>> add(Object it) {
            Preconditions.checkState(contents == null, "May only have one object in an Optional");
            contents = it;
            return this;
        }

        @Override
        public Optional<?> build() {
            return Optional.fromNullable(contents);
        }
    }
}
