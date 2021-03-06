package pneumaticCraft.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.common.tileentity.TileEntityCompressedIronBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCompressedIron extends BlockPneumaticCraft{

    protected BlockCompressedIron(Material par2Material){
        super(par2Material);
    }

    @Override
    protected Class<? extends TileEntity> getTileEntityClass(){
        return TileEntityCompressedIronBlock.class;
    }

    /**
     * Returns a integer with hex for 0xrrggbb with this color multiplied against the blocks color. Note only called
     * when first determining what to render.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess world, int x, int y, int z){
        TileEntityCompressedIronBlock te = (TileEntityCompressedIronBlock)world.getTileEntity(x, y, z);
        int heatLevel = te.getHeatLevel();
        double[] color = TileEntityCompressedIronBlock.getColorForHeatLevel(heatLevel);
        return 0xFF000000 + ((int)(color[0] * 255) << 16) + ((int)(color[1] * 255) << 8) + (int)(color[2] * 255);
    }

    /**
     * If this returns true, then comparators facing away from this block will use the value from
     * getComparatorInputOverride instead of the actual redstone signal strength.
     */
    @Override
    public boolean hasComparatorInputOverride(){
        return true;
    }

    /**
     * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
     * strength when this block inputs to a comparator.
     */
    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side){
        return TileEntityCompressedIronBlock.getComparatorOutput((int)((TileEntityCompressedIronBlock)world.getTileEntity(x, y, z)).getHeatExchangerLogic(null).getTemperature());

    }

}
