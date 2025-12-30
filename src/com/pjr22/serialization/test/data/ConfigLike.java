package com.pjr22.serialization.test.data;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Test class that simulates the Config class behavior from the reported issue.
 * This class has a constructor that creates new TreeMap instances from the parameters,
 * which would fail with null values.
 */
public class ConfigLike {
    private static final long serialVersionUID = 1L;

    public final Map<String, String> client;
    public final Map<String, Integer> combat;
    public final Map<String, Boolean> features;
    public final Map<String, Integer> game;
    public final Map<String, Integer> map;
    public final Map<String, String> server;
    public final Map<String, List<String>> sounds;

    public ConfigLike() {
        client = new TreeMap<>();
        combat = new TreeMap<>();
        features = new TreeMap<>();
        game = new TreeMap<>();
        map = new TreeMap<>();
        server = new TreeMap<>();
        sounds = new TreeMap<>();
    }

    public ConfigLike(
            Map<String, String> client,
            Map<String, Integer> combat,
            Map<String, Boolean> features,
            Map<String, Integer> game,
            Map<String, Integer> map,
            Map<String, String> server,
            Map<String, List<String>> sounds) {
        // This creates new TreeMap instances from the parameters
        // If any parameter is null, this will throw NullPointerException
        this.client = new TreeMap<>(client);
        this.combat = new TreeMap<>(combat);
        this.features = new TreeMap<>(features);
        this.game = new TreeMap<>(game);
        this.map = new TreeMap<>(map);
        this.server = new TreeMap<>(server);
        this.sounds = new TreeMap<>(sounds);
    }

    public Map<String, String> getClient() {
        return client;
    }

    public Map<String, Integer> getCombat() {
        return combat;
    }

    public Map<String, Boolean> getFeatures() {
        return features;
    }

    public Map<String, Integer> getGame() {
        return game;
    }

    public Map<String, Integer> getMap() {
        return map;
    }

    public Map<String, String> getServer() {
        return server;
    }

    public Map<String, List<String>> getSounds() {
        return sounds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigLike that = (ConfigLike) o;

        if (!client.equals(that.client)) return false;
        if (!combat.equals(that.combat)) return false;
        if (!features.equals(that.features)) return false;
        if (!game.equals(that.game)) return false;
        if (!map.equals(that.map)) return false;
        if (!server.equals(that.server)) return false;
        return sounds.equals(that.sounds);
    }

    @Override
    public int hashCode() {
        int result = client.hashCode();
        result = 31 * result + combat.hashCode();
        result = 31 * result + features.hashCode();
        result = 31 * result + game.hashCode();
        result = 31 * result + map.hashCode();
        result = 31 * result + server.hashCode();
        result = 31 * result + sounds.hashCode();
        return result;
    }
}
