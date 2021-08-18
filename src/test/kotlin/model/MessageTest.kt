package model

import de.axelrindle.broadcaster.model.JsonMessage
import de.axelrindle.broadcaster.model.SimpleMessage
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class MessageTest : ShouldSpec({

    should("create a text message with the given input") {
        SimpleMessage("hello there").getText() shouldBe "hello there"
    }

    should("convert a json definition to an array of components") {
        val json = JsonMessage("""
        [
            "",
            {
                "text":"Hello",
                "color":"gold"
            },
            {
                "text":" "
            },
            {
                "text":"World",
                "color":"dark_blue",
                "hoverEvent":{
                    "action":"show_text",
                    "contents":"Hover me"
                }
            }
        ]
    """.trimIndent())
        json.components.size shouldBe 4
    }

})