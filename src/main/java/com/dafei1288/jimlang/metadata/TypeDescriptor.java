package com.dafei1288.jimlang.metadata;

public final class TypeDescriptor {
    public enum Kind { BUILTIN, JAVA }
    public enum Builtin { STRING, BOOLEAN, INT, FLOAT, NUMBER, ARRAY, OBJECT }

    private final Kind kind;
    private final Builtin builtin;
    private final Class<?> javaClass;

    private TypeDescriptor(Kind k, Builtin b, Class<?> c){ this.kind=k; this.builtin=b; this.javaClass=c; }

    public static TypeDescriptor ofBuiltin(Builtin b){ return new TypeDescriptor(Kind.BUILTIN, b, null); }
    public static TypeDescriptor ofJava(Class<?> c){ return new TypeDescriptor(Kind.JAVA, null, c); }

    public Kind kind(){ return kind; }
    public Builtin builtin(){ return builtin; }
    public Class<?> javaClass(){ return javaClass; }

    public static TypeDescriptor parse(String typeName){
        if (typeName == null) return null;
        String t = typeName.trim();
        switch (t) {
            case "string": return ofBuiltin(Builtin.STRING);
            case "boolean": return ofBuiltin(Builtin.BOOLEAN);
            case "int": return ofBuiltin(Builtin.INT);
            case "float": return ofBuiltin(Builtin.FLOAT);
            case "number": return ofBuiltin(Builtin.NUMBER);
            case "array": return ofBuiltin(Builtin.ARRAY);
            case "object": return ofBuiltin(Builtin.OBJECT);
            default:
                try {
                    return ofJava(Class.forName(t));
                } catch (ClassNotFoundException e) {
                    // fallback: unknown -> treat as Java type name not found; caller handles error on use
                    throw new RuntimeException("Unknown type: " + t, e);
                }
        }
    }

    public static boolean isAssignable(TypeDescriptor expected, Object value){
        if (expected == null || value == null) return true; // null passes; no expected means untyped
        if (expected.kind == Kind.JAVA) {
            return expected.javaClass.isInstance(value);
        }
        switch (expected.builtin) {
            case STRING: return (value instanceof String);
            case BOOLEAN: return (value instanceof Boolean);
            case ARRAY: return (value instanceof java.util.List);
            case OBJECT: return (value instanceof java.util.Map);
            case NUMBER: return (value instanceof Number);
            case INT:
                if (value instanceof Integer || value instanceof Long || value instanceof Short || value instanceof Byte) return true;
                if (value instanceof Number) {
                    double d = ((Number)value).doubleValue();
                    return Math.floor(d) == d; // integer-valued
                }
                return false;
            case FLOAT:
                return (value instanceof Double) || (value instanceof Float) || (value instanceof Number);
            default: return true;
        }
    }

    public static Object coerceIfNeeded(TypeDescriptor expected, Object value){
        if (expected == null || value == null) return value;
        if (expected.kind == Kind.BUILTIN){
            switch (expected.builtin){
                case FLOAT:
                    if (value instanceof Number) return ((Number)value).doubleValue();
                    break;
                case INT:
                    if (value instanceof Number){
                        double d = ((Number)value).doubleValue();
                        if (Math.floor(d) == d) return Integer.valueOf((int)d);
                    }
                    break;
                default:
                    break;
            }
        }
        return value;
    }
}