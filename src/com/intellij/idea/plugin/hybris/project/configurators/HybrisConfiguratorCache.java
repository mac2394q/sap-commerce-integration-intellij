package com.intellij.idea.plugin.hybris.project.configurators;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class HybrisConfiguratorCache {

    private static final Logger LOG = Logger.getInstance(HybrisConfiguratorCache.class);

    private final Map<String, Ref<Properties>> path2Properties = new HashMap<>();

    @Nullable
    private Properties getParsedProperties(@NotNull String filePath) {
        Ref<Properties> propertiesRef = path2Properties.get(FileUtil.toSystemIndependentName(filePath));

        if (propertiesRef != null) {
            return propertiesRef.get();
        }
        final File file = new File(filePath);

        if (!file.exists()) {
            return null;
        }
        Properties properties = new Properties();

        try (FileReader fr = new FileReader(file)) {
            properties.load(fr);
        } catch (IOException e) {
            LOG.debug(e);
            properties = null;
        }
        path2Properties.put(filePath, Ref.create(properties));
        return properties;
    }

    @Nullable
    public String findPropertyInFiles(@NotNull Iterable<File> files, @NotNull String propertyName) {
        for (File file : files) {
            final String candidate = findPropertyInFile(file, propertyName);

            if (candidate != null) {
                return candidate;
            }
        }
        return null;
    }

    @Nullable
    public String findPropertyInFile(@NotNull File file, @NotNull String propertyName) {
        final Properties properties = getParsedProperties(file.getPath());

        if (properties == null) {
            return null;
        }
        return (String) properties.get(propertyName);
    }
}
