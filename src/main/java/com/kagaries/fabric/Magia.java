package com.kagaries.fabric;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kagaries.fabric.config.MagiaConfig;
import com.kagaries.fabric.world.block.ModBlocks;
import com.kagaries.fabric.world.entity.ModEntities;
import com.kagaries.fabric.world.item.ModItems;
import com.kagaries.util.Reference;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Magia implements ModInitializer {

    public static final String MOD_ID = "me-fabric";
    public static final MagiaConfig CONFIG = MagiaConfig.createAndLoad();

    private static final StackWalker STACK_WALKER;

    public static final ModContainer CONTAINER = FabricLoader.getInstance().getModContainer(MOD_ID).get();
    public static final String VERSION = CONTAINER.getMetadata().getVersion().getFriendlyString();

    @Override
    public void onInitialize() {
        getLogger().info("Initializing Magia Edition: {}", VERSION);
        ModEntities.createDefaultAttributes();
        FieldRegistrationHandler.register(ModBlocks.class, "magia-blocks", true);
        FieldRegistrationHandler.register(ModItems.class, "magia-items", true);
        FieldRegistrationHandler.register(ModEntities.class, "magia-entities", true);

        ModItems.init();

        getOutdated();
    }

    public static Logger getLogger() {
        return LoggerFactory.getLogger(STACK_WALKER.getCallerClass());
    }

    static {
        STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    }

    class Version {
        String id;
    }

    class JsonResponse {
        String current;
        List<Version> versions;
    }

    //Gets a Json page from an url that is read to check if the current version is out-of-date or not.
    public static void getOutdated() {
        if (!CONFIG.checkForUpdates()) {
            Magia.getLogger().info("Skipping Update Check");
            return;
        }
        try {
            Magia.getLogger().info("Checking for Update...");
            // URL to fetch data from
            String urlString = Reference.fabricVersionsURL;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response from the input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            reader.close();

            // Get the raw response as a string
            String response = responseBuilder.toString();

            Gson gson = new Gson();

            // Try parsing the response to see if it's a JSON object or a string
            JsonElement jsonElement = JsonParser.parseString(response);

            if (jsonElement.isJsonObject()) {
                // If it's a JSON object, parse it using your custom class
                JsonResponse jsonResponse = gson.fromJson(response, JsonResponse.class);
                //this.latestVersionID = jsonResponse.current;
                if (!jsonResponse.current.equals(Magia.VERSION)) {
                    Magia.getLogger().error("Version: {} is out of date, latest is: {}", Magia.VERSION, jsonResponse.current);
                }
            } else if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
                // If it's a plain string, handle it accordingly
                Magia.getLogger().warn("Got versions json as a string!");
            } else {
                //Nothing Here
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
