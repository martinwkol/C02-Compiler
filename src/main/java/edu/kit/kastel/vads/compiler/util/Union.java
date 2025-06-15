package edu.kit.kastel.vads.compiler.util;

import org.jspecify.annotations.Nullable;

public class Union<R, S> {
    @Nullable private R r;
    @Nullable private S s;

    private Union(@Nullable R r, @Nullable S s) {
        this.r = r;
        this.s = s;
    }

    public static <R, S> Union<R, S> fromFirst(R r) {
        return new Union<>(r, null);
    }

    public static <R, S> Union<R, S> fromSecond(S s) {
        return new Union<>(null, s);
    }

    public boolean isFirst() {
        return this.r != null;
    }

    public boolean isSecond() {
        return this.s != null;
    }

    public R first() {
        if (r == null) throw new RuntimeException("Union is not first");
        return r;
    }

    public S second() {
        if (s == null) throw new RuntimeException("Union is not second");
        return s;
    }

    public void setFirst(R r) {
        this.r = r;
        this.s = null;
    }

    public void setSecond(S s) {
        this.r = null;
        this.s = s;
    }
}
