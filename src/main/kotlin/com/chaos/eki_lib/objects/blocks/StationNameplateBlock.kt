package com.chaos.eki_lib.objects.blocks

import com.chaos.eki_lib.objects.blockentities.StationNameplateBlockEntity
import com.chaos.eki_lib.objects.blocks.base.HorizontalBaseBlock
import com.chaos.eki_lib.objects.items.StationTunerItem
import com.chaos.eki_lib.utils.TagFacts
import com.chaos.eki_lib.utils.extensions.UtilBlockPos
import com.chaos.eki_lib.utils.extensions.format
import com.chaos.eki_lib.utils.voxelshapes.HorizontalVoxelShapes
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.text.LiteralText
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

class StationNameplateBlock :
    HorizontalBaseBlock(
        Settings.of(Material.BARRIER)
            .strength(2.0f, 3.0f)
            .sounds(BlockSoundGroup.BASALT)
    ),
    BlockEntityProvider {
    private val SHAPES = HorizontalVoxelShapes(
        sequenceOf(
            createCuboidShape(-6.0, 6.0, 15.0, 22.0, 16.0, 16.75),
            createCuboidShape(-6.0, 6.0, 14.5, -5.0, 16.0, 15.0),
            createCuboidShape(21.0, 6.0, 14.5, 22.0, 16.0, 15.0),
            createCuboidShape(-5.0, 6.0, 14.5, 21.0, 6.75, 15.0),
            createCuboidShape(-5.0, 15.25, 14.5, 21.0, 16.0, 15.0)
        ),
        sequenceOf(
            createCuboidShape(-6.0, 6.0, -0.75, 22.0, 16.0, 1.0),
            createCuboidShape(21.0, 6.0, 1.0, 22.0, 16.0, 1.5),
            createCuboidShape(-6.0, 6.0, -1.25, 22.0, 16.0, -0.75),
            createCuboidShape(-6.0, 6.0, 1.0, -5.0, 16.0, 1.5),
            createCuboidShape(-5.0, 6.0, 1.0, 21.0, 6.75, 1.5),
            createCuboidShape(-5.0, 15.25, 1.0, 21.0, 16.0, 1.5)
        ),
        sequenceOf(
            createCuboidShape(-0.75, 6.0, -6.0, 1.0, 16.0, 22.0),
            createCuboidShape(1.0, 6.0, -6.0, 1.5, 16.0, -5.0),
            createCuboidShape(-1.25, 6.0, -6.0, -0.75, 16.0, 22.0),
            createCuboidShape(1.0, 6.0, 21.0, 1.5, 16.0, 22.0),
            createCuboidShape(1.0, 6.0, -5.0, 1.5, 6.75, 21.0),
            createCuboidShape(1.0, 15.25, -5.0, 1.5, 16.0, 21.0)
        ),
        sequenceOf(
            createCuboidShape(15.0, 6.0, -6.0, 16.75, 16.0, 22.0),
            createCuboidShape(14.5, 6.0, 21.0, 15.0, 16.0, 22.0),
            createCuboidShape(16.75, 6.0, -6.0, 17.25, 16.0, 22.0),
            createCuboidShape(14.5, 6.0, -6.0, 15.0, 16.0, -5.0),
            createCuboidShape(14.5, 6.0, -5.0, 15.0, 6.75, 21.0),
            createCuboidShape(14.5, 15.25, -5.0, 15.0, 16.0, 21.0)
        )
    )

    override fun onUse(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        player: PlayerEntity?,
        hand: Hand?,
        hit: BlockHitResult?
    ): ActionResult {
        val blockEntity = world?.getBlockEntity(pos)
        val heldStack = player?.getStackInHand(hand)
        val heldItem = heldStack?.item

        return if (heldItem is StationTunerItem && blockEntity is StationNameplateBlockEntity) {
            if (!blockEntity.isTargetExist())
                blockEntity.boundStationPosition = null

            val tag = heldStack.tag

            if (tag != null && tag.contains(TagFacts.Station.POS)) {
                val boundPos = UtilBlockPos.fromArray(tag.getIntArray(TagFacts.Station.POS))
                val boundStation = heldItem.getBoundStation(heldStack, tag, world)

                return if (boundStation != null) {
                    blockEntity.pos = boundStation.pos
                    player.sendMessage(
                        TranslatableText("eki_lib.message.station_bind")
                            .append(LiteralText(boundStation.name).formatted(Formatting.BOLD))
                            .formatted(Formatting.GREEN),
                        true
                    )
                    ActionResult.SUCCESS
                } else {
                    player.sendMessage(
                        TranslatableText("eki_lib.message.station_absence")
                            .append(
                                LiteralText(
                                    boundPos.format()
                                ).formatted(Formatting.ITALIC)
                            )
                            .formatted(Formatting.RED),
                        true
                    )
                    ActionResult.SUCCESS
                }
            }

            player.sendMessage(
                TranslatableText(
                    "eki_lib.message.invalid_item"
                ).formatted(Formatting.RED),
                true
            )

            ActionResult.SUCCESS
        } else ActionResult.PASS
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape =
        SHAPES.getByDirection(state.get(FACING))

    override fun createBlockEntity(world: BlockView?): BlockEntity =
        StationNameplateBlockEntity()
}