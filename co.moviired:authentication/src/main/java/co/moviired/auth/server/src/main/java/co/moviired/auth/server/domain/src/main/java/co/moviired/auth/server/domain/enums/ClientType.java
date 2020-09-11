package co.moviired.auth.server.domain.enums;


import java.util.stream.Stream;

public enum ClientType {

    CHANNEL("CHANNEL"), SUBSCRIBER("SUBSCRIBER");

    private final String name;

    ClientType(String s) {
        name = s;
    }

    public static ClientType resolve(String type) {
        return Stream.of(values()).filter(clientType -> clientType.name.equals(type)).findFirst().orElse(null);
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

}

