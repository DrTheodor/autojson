package dev.drtheo.autojson.benchmark.ait;

public class Id {
    public String namespace;
    public String path;

    public Id(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public static Id fromString(String s) {
        int colon = s.indexOf(':');
        String namespace = s.substring(0, colon);
        String path = s.substring(colon + 1);
        return new Id(namespace, path);
    }
}
