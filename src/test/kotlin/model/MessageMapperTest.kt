package model

import de.axelrindle.broadcaster.Broadcaster
import de.axelrindle.broadcaster.model.JsonMessage
import de.axelrindle.broadcaster.model.MessageMapper
import de.axelrindle.broadcaster.model.SimpleMessage
import de.axelrindle.pocketknife.PocketConfig
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.beOfType
import java.io.File
import java.nio.file.Files

class MessageMapperTest : ShouldSpec({

    val plugin = Broadcaster.get()
    val config = PocketConfig(plugin)
    config.register("strings", javaClass.getResourceAsStream("/message-mapper-test-strings.yml"))

    // simple string mapping
    should("map simple strings") {
        val messages = config.access("strings")?.getList("Messages")
        messages shouldNotBe null
        messages!!.forEach { message ->
            val mapped = MessageMapper.mapConfigEntry(message)
            mapped shouldNotBe null
            mapped should beOfType<SimpleMessage>()
        }
    }

    // json definition mapping
    should("map json definitions") {
        val jsonTestDefinition = "[\"\",{\"text\":\"Hello\",\"color\":\"gold\"},{\"text\":\" \"},{\"text\":\"World\",\"color\":\"dark_blue\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":\"Hover me\"}}]"
        val jsonFile = File(plugin.dataFolder, "json/test.json")
        jsonFile.parentFile.mkdir()
        Files.write(jsonFile.toPath(), jsonTestDefinition.toByteArray())

        MessageMapper.mapConfigEntry(24) shouldBe null
        MessageMapper.mapConfigEntry("Hello there") should beOfType<SimpleMessage>()
        MessageMapper.mapConfigEntry(linkedMapOf(
            Pair("Type", "json"),
            Pair("Definition", "test")
        )) should beOfType(JsonMessage::class)
    }

    should("fail for unknown types") {
        MessageMapper.mapConfigEntry(linkedMapOf(
            Pair("Type", "unknown")
        )) shouldBe null
    }

})