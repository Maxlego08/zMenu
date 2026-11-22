package fr.maxlego08.menu.save;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.Comments;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.utils.format.NodeRole;
import fr.maxlego08.menu.api.utils.yaml.LegacyBooleanSchema;
import fr.maxlego08.menu.zcore.utils.VersionedYamlFile;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * messages.yml is the default file of the messages, written from the {@code Message} enum.
 * Run the tests with {@code -Dzmenu.generateMessages=true} to write it again after changing the enum.
 */
class MessagesFileTest {

    private static final Path FILE = Path.of("src/main/resources/messages.yml");

    static String generate() throws IOException {
        YamlDocument document = YamlDocument.create(InputStream.nullInputStream(), GeneralSettings.DEFAULT, LegacyBooleanSchema.loaderSettings(), LegacyBooleanSchema.dumperSettings());
        document.set(VersionedYamlFile.VERSION_ROUTE, 1);
        Comments.set(document.getBlock(VersionedYamlFile.VERSION_ROUTE), NodeRole.KEY, Comments.Position.BEFORE, List.of(
                Comments.create("", Comments.Position.BEFORE),
                Comments.create(" Documentation: https://docs.groupez.dev/zmenu/getting-started", Comments.Position.BEFORE),
                Comments.create(" Online editor: https://minecraft-inventory-builder.com/", Comments.Position.BEFORE),
                Comments.create("", Comments.Position.BEFORE),
                Comments.BLANK_LINE,
                Comments.create(" Version of this file, zMenu uses it to add its new messages. Do not change it.", Comments.Position.BEFORE)
        ));
        MessageLoader.addMissingMessages(document);
        return document.dump();
    }

    @Test
    void messagesFileMatchesTheEnum() throws IOException {
        String expected = generate();
        if (Boolean.getBoolean("zmenu.generateMessages")) {
            Files.writeString(FILE, expected);
        }
        String actual = Files.exists(FILE) ? Files.readString(FILE).replace("\r\n", "\n") : "";
        assertEquals(expected, actual, "messages.yml does not match the Message enum, run the tests with -Dzmenu.generateMessages=true");
    }
}
