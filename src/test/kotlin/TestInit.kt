import be.seeseemelk.mockbukkit.MockBukkit
import de.axelrindle.broadcaster.Broadcaster
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.AutoScan

@AutoScan
object TestInit : ProjectListener {

    override suspend fun beforeProject() {
        MockBukkit.mock()
        MockBukkit.getMock().setPlayers(10)
        MockBukkit.load(Broadcaster::class.java)
    }

    override suspend fun afterProject() {
        MockBukkit.unmock()
    }

}