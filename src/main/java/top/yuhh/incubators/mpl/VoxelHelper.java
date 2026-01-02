/**
*    This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
*    If a copy of the MPL was not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
*    A copy of the Covered Software can be obtained here: https://gitlab.com/cable-mc/cobblemon/-/blob/main/common/src/main/kotlin/com/cobblemon/mod/common/block/PastureBlock.kt
 */

package top.yuhh.incubators.mpl;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.cobblemon.mod.common.util.VectorShapeExtensionsKt.voxelShape;

public class VoxelHelper {
    public static VoxelShape buildCollider(Direction direction) {
        return Shapes.or(
                voxelShape(0.875, 0.0, 0.0, 1.0, 1.0, 0.125, direction),
                voxelShape(0.125, 0.0, 0.0, 0.875, 0.125, 0.125, direction),
                voxelShape(0.125, 0.875, 0.0, 0.875, 1.0, 0.125, direction),
                voxelShape(0.125, 0.125, 0.0625, 0.875, 0.875, 0.125, direction),
                voxelShape(0.0625, 0.125, 0.125, 0.125, 0.875, 0.875, direction),
                voxelShape(0.0, 0.0, 0.0, 0.125, 1.0, 0.125, direction),
                voxelShape(0.125, 0.0, 0.125, 0.875, 0.125, 1.0, direction),
                voxelShape(0.875, 0.125, 0.125, 0.9375, 0.875, 0.875, direction),
                voxelShape(0.125, 0.875, 0.125, 0.875, 1.0, 1.0, direction),
                voxelShape(0.875, 0.0, 0.875, 1.0, 1.0, 1.0, direction),
                voxelShape(0.875, 0.0, 0.125, 1.0, 0.125, 0.875, direction),
                voxelShape(0.875, 0.875, 0.125, 1.0, 1.0, 0.875, direction),
                voxelShape(0.0, 0.875, 0.125, 0.125, 1.0, 0.875, direction),
                voxelShape(0.0, 0.0, 0.125, 0.125, 0.125, 0.875, direction),
                voxelShape(0.0, 0.0, 0.875, 0.125, 1.0, 1.0, direction),
                voxelShape(0.0, 0.125, 0.375, 0.0625, 0.875, 0.625, direction),
                voxelShape(0.9375, 0.125, 0.375, 1.0, 0.875, 0.625, direction),
                voxelShape(0.1875, 0.1875, 0.05625, 0.8125, 0.75, 0.05625, direction),
                voxelShape(0.1875, 0.125, 0.3125, 0.8125, 0.3125, 0.875, direction),
                voxelShape(0.1875, 0.125, 0.3125, 0.8125, 0.3125, 0.875, direction),
                voxelShape(0.1875, 0.0625, 0.875, 0.8125, 0.25, 0.875, direction),
                voxelShape(0.1875, 0.25, 0.25, 0.1875, 0.4375, 0.875, direction),
                voxelShape(0.8125, 0.25, 0.25, 0.8125, 0.4375, 0.875, direction),
                voxelShape(0.1875, 0.3125, 0.3125, 0.8125, 0.5, 0.3125, direction),
                voxelShape(0.25, 0.75, 0.3125, 0.75, 1.0, 0.8125, direction),
                // I'm lazy and this gets optimized in union anyway
                voxelShape(0.0, 0.0, 0.0, 0.0625, 1.0, 1.0, direction),
                voxelShape(0.9375, 0.0, 0.0, 1.0, 1.0, 1.0, direction)
        );
    }
}
