package fr.maxlego08.menu.test.serialization;

import fr.maxlego08.menu.api.utils.SectionSerializable;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

/**
 * Serializes an object to a real file and reads the file back, so a round trip goes through YAML text
 * and catches values that only survive in memory.
 */
public final class SerializationRoundTrip {

    private SerializationRoundTrip() {
    }

    /**
     * Serializes an object, saves it to a file and loads that file again.
     *
     * @param object      The object to serialize.
     * @param sectionPath The section to write the object into, empty for the root of the file.
     * @param file        The file to write.
     * @return The configuration read back from the file.
     */
    public static YamlConfiguration writeAndReread(SectionSerializable object, String sectionPath, File file) throws IOException {
        YamlDocument document = YamlSections.empty();
        Section section = sectionPath.isEmpty() ? document : document.createSection(sectionPath);
        object.serialize(section);
        YamlSections.removeNulls(document);
        document.save(file);
        return YamlConfiguration.loadConfiguration(file);
    }
}
