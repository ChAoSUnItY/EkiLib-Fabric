package com.chaos.eki_lib.objects.blockentities.renderers

import com.chaos.eki_lib.objects.blockentities.StationNameplateBlockEntity
import com.chaos.eki_lib.objects.blocks.base.HorizontalBaseBlock
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import java.nio.charset.StandardCharsets

class StationNameplateBlockEntityRenderer(dispatcher: BlockEntityRenderDispatcher?) :
    BlockEntityRenderer<StationNameplateBlockEntity>(dispatcher) {

    override fun render(
        entity: StationNameplateBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider?,
        light: Int,
        overlay: Int
    ) {
        matrices.push()
        matrices.translate(0.5, 0.5, 0.5)
        matrices.multiply(entity.cachedState
            ?.get(HorizontalBaseBlock.FACING)?.asRotation()?.let { Vector3f.POSITIVE_Y.getDegreesQuaternion(-it) })
        matrices.translate(0.0, -0.3125, -0.42)
        matrices.scale(0.010416667F, -0.010416667F, 0.010416667F)
        matrices.scale(2F, 3F, 2F)
        val textRenderer = dispatcher.textRenderer
        val str = toUnicode(if (entity.isTargetExist()) entity.getBoundStation()?.name ?: "" else "")

        if (str.length > 17) {
            matrices.scale(0.75F, 1.25F, 0.75F)
            matrices.translate(0.0, 3.5, 0.0)
        }

        textRenderer.draw(
            "str",
            -(textRenderer.getWidth(str) / 2).toFloat(),
            -20f,
            NativeImage.getAbgrColor(1, 256, 256, 256),
            false,
            matrices.peek().model,
            vertexConsumers,
            false,
            0,
            light
        )
        matrices.pop()
    }

    private fun toUnicode(str: String): String = String(str.toByteArray(StandardCharsets.UTF_8), StandardCharsets.UTF_8)
}