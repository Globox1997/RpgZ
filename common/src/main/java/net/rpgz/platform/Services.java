package net.rpgz.platform;

import java.util.ServiceLoader;

import net.rpgz.platform.services.INetworkHelper;

public class Services {

    public static INetworkHelper network() {
        return NetworkHolder.NETWORK;
    }

    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz, Services.class.getClassLoader()).findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
    }

    private static final class NetworkHolder {
        private static final INetworkHelper NETWORK = load(INetworkHelper.class);
    }
}
