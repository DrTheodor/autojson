package dev.drtheo.autojson.benchmark.comparison.ait.data;

import com.google.gson.*;
import dev.drtheo.autojson.benchmark.comparison.ait.TardisComponentRegistry;

import java.util.function.Consumer;

public class TardisHandlersManager {

    public TardisComponent[] components = new TardisComponent[30];

    {
        components[0] = new BiomeHandler();
//            components[1] = new ButlerHandler();
        components[2] = new CloakHandler();
        components[3] = new DoorHandler();
        components[4] = new ExteriorEnvironmentHandler();
//        components[5] = new ExtraHandler();
        components[6] = new FuelHandler();
//        components[7] = new HadsHandler();
        components[8] = new InteriorChangingHandler();
        components[9] = new LandingPadHandler();
//        components[10] = new LoyaltyHandler();
        components[11] = new OpinionHandler();
//        components[12] = new OvergrownHandler();
        components[13] = new RealFlightHandler();
//        components[14] = new SeatHandler();
        components[15] = new SelfDestructHandler();
        components[16] = new ServerAlarmHandler();
        components[17] = new ServerHumHandler();
        components[18] = new ShieldHandler();
        components[19] = new SiegeHandler();
//        components[20] = new SonicHandler();
        components[21] = new StatsHandler();
//        components[22] = new SubSystemHandler();
        components[23] = new CrashHandler();
        components[24] = new WaypointHandler();
    }

    public void forEach(Consumer<TardisComponent> consumer) {
        for (TardisComponent component : components) {
            if (component == null)
                continue;

            consumer.accept(component);
        }
    }

    public void set(TardisComponent component) {
        components[component.getId().getIndex()] = component;
    }

    public static Object serializer() {
        return new Serializer();
    }

    static class Serializer implements JsonSerializer<TardisHandlersManager>, JsonDeserializer<TardisHandlersManager> {

        @Override
        public TardisHandlersManager deserialize(JsonElement json, java.lang.reflect.Type type,
                                                 JsonDeserializationContext context) throws JsonParseException {
            TardisHandlersManager manager = new TardisHandlersManager();
            JsonObject map = json.getAsJsonObject();

            TardisComponentRegistry registry = TardisComponentRegistry.getInstance();

            for (String key : map.keySet()) {
                JsonElement element = map.get(key);
                TardisComponent.IdLike id = registry.get(key);

                if (id == null) {
                    System.err.println("Can't find a component id with name " + key);
                    continue;
                }

                manager.set(context.deserialize(element, id.clazz()));
            }

            for (int i = 0; i < manager.components.length; i++) {
                if (manager.components[i] == null)
                    continue;

                TardisComponent.IdLike id = registry.get(i);

                manager.set(id.create());
            }

            return manager;
        }

        @Override
        public JsonElement serialize(TardisHandlersManager manager, java.lang.reflect.Type type,
                                     JsonSerializationContext context) {
            JsonObject result = new JsonObject();

            manager.forEach(component -> {
                TardisComponent.IdLike idLike = component.getId();

                if (idLike == null) {
                    System.err.println("Id was null for " + component.getClass());
                    return;
                }

                result.add(idLike.name(), context.serialize(component));
            });

            return result;
        }
    }
}
