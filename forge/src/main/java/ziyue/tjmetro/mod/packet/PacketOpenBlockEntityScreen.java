package ziyue.tjmetro.mod.packet;

import net.minecraft.core.BlockPos;
import org.mtr.packet.PacketHandler;
import org.mtr.packet.PacketBufferReceiver;
import org.mtr.packet.PacketBufferSender;

/**
 * @author ZiYueCommentary
 * @see ClientPacketHelper
 * @since 1.0.0-beta-1
 */

public final class PacketOpenBlockEntityScreen extends PacketHandler
{
    private final BlockPos blockPos;

    public PacketOpenBlockEntityScreen(PacketBufferReceiver packetBufferReceiver) {
        blockPos = BlockPos.of(packetBufferReceiver.readLong());
    }

    public PacketOpenBlockEntityScreen(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    @Override
    public void write(PacketBufferSender packetBufferSender) {
        packetBufferSender.writeLong(blockPos.asLong());
    }

    @Override
    public void runClient() {
        ClientPacketHelper.openBlockEntityScreen(blockPos);
    }
}
