package pneumaticCraft.common.progwidgets;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import pneumaticCraft.client.gui.GuiProgrammer;
import pneumaticCraft.client.gui.programmer.GuiProgWidgetLiquidFilter;
import pneumaticCraft.lib.Textures;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ProgWidgetLiquidFilter extends ProgWidget{
    private Fluid fluid;

    @Override
    public boolean hasStepInput(){
        return false;
    }

    @Override
    public Class<? extends IProgWidget> returnType(){
        return ProgWidgetLiquidFilter.class;
    }

    @Override
    public Class<? extends IProgWidget>[] getParameters(){
        return new Class[]{ProgWidgetLiquidFilter.class};
    }

    @Override
    public String getWidgetString(){
        return "liquidFilter";
    }

    @Override
    public String getGuiTabText(){
        return "bla";
    }

    @Override
    public int getGuiTabColor(){
        return 0xFFFFFFFF;
    }

    @Override
    protected ResourceLocation getTexture(){
        return Textures.PROG_WIDGET_LIQUID_FILTER;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag){
        super.readFromNBT(tag);
        fluid = FluidRegistry.getFluid(tag.getString("fluid"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tag){
        super.writeToNBT(tag);
        if(fluid != null) tag.setString("fluid", fluid.getName());
    }

    public boolean isLiquidValid(Fluid fluid){
        return this.fluid == null || fluid == this.fluid;
    }

    public static boolean isLiquidValid(Fluid fluid, IProgWidget mainWidget, int filterIndex){
        ProgWidgetLiquidFilter widget = (ProgWidgetLiquidFilter)mainWidget.getConnectedParameters()[mainWidget.getParameters().length + filterIndex];
        while(widget != null) {
            if(!widget.isLiquidValid(fluid)) return false;
            widget = (ProgWidgetLiquidFilter)widget.getConnectedParameters()[0];
        }
        widget = (ProgWidgetLiquidFilter)mainWidget.getConnectedParameters()[filterIndex];
        if(widget == null) return true;
        while(widget != null) {
            if(widget.isLiquidValid(fluid)) return true;
            widget = (ProgWidgetLiquidFilter)widget.getConnectedParameters()[0];
        }
        return false;
    }

    @Override
    public WidgetCategory getCategory(){
        return WidgetCategory.PARAMETER;
    }

    @Override
    protected String getExtraStringInfo(){
        return fluid != null ? fluid.getLocalizedName(new FluidStack(fluid, 1)) : I18n.format("gui.progWidget.liquidFilter.noFluid");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen getOptionWindow(GuiProgrammer guiProgrammer){
        return new GuiProgWidgetLiquidFilter(this, guiProgrammer);
    }

    public void setFluid(Fluid fluid){
        this.fluid = fluid;
    }

    public Fluid getFluid(){
        return fluid;
    }

}
