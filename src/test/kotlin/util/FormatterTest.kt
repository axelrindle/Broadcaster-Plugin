package util

import de.axelrindle.broadcaster.util.Formatter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class FormatterTest : ShouldSpec({

    should("format newlines") {
        Formatter.format("%n") shouldBe "\n"
    }

    should("format total amount of online players") {
        Formatter.format("%online_players%") shouldBe "10"
    }

    should("format maximum amount of players") {
        Formatter.format("%max_players%") shouldBe "2147483647"
    }

    should("format custom placeholders") {
        Formatter.format("<3") shouldBe "❤"
        Formatter.format("[*]") shouldBe "★"
        Formatter.format("[**]") shouldBe "✹"
        Formatter.format("[p]") shouldBe "●"
        Formatter.format("[v]") shouldBe "✔"
        Formatter.format("[+]") shouldBe "♦"
        Formatter.format("[++]") shouldBe "✦"
        Formatter.format("[x]") shouldBe "█"
        Formatter.format("[/]") shouldBe "▌"
    }

    should("fail with null parameter") {
        shouldThrow<NullPointerException> {
            // let's imagine java code calling the method with a null value
            val method = Formatter.javaClass.getMethod("format", String::class.java)
            method.invoke(null, null)
        }
    }

})