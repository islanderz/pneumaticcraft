package pneumaticCraft.common.progwidgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pneumaticCraft.client.gui.GuiProgrammer;
import pneumaticCraft.client.gui.programmer.GuiProgWidgetAreaShow;
import pneumaticCraft.common.ai.StringFilterEntitySelector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ProgWidgetAreaItemBase extends ProgWidget implements IAreaProvider, IEntityProvider,
        IItemFiltering{

    @Override
    public boolean hasStepInput(){
        return true;
    }

    @Override
    public Class<? extends IProgWidget> returnType(){
        return null;
    }

    @Override
    public Class<? extends IProgWidget>[] getParameters(){
        return new Class[]{ProgWidgetArea.class, ProgWidgetItemFilter.class};
    }

    @Override
    public void addErrors(List<String> curInfo){
        super.addErrors(curInfo);
        if(getConnectedParameters()[0] == null) {
            curInfo.add("gui.progWidget.area.error.noArea");
        }
    }

    public static IBlockAccess getCache(Collection<ChunkPosition> area, World world){
        if(area.size() == 0) return world;
        int minX, minY, minZ, maxX, maxY, maxZ;
        Iterator<ChunkPosition> iterator = area.iterator();
        ChunkPosition p = iterator.next();
        minX = maxX = p.chunkPosX;
        minY = maxY = p.chunkPosY;
        minZ = maxZ = p.chunkPosZ;
        while(iterator.hasNext()) {
            p = iterator.next();
            minX = Math.min(minX, p.chunkPosX);
            minY = Math.min(minY, p.chunkPosY);
            minZ = Math.min(minZ, p.chunkPosZ);
            maxX = Math.max(maxX, p.chunkPosX);
            maxY = Math.max(maxY, p.chunkPosY);
            maxZ = Math.max(maxZ, p.chunkPosZ);
        }
        return new ChunkCache(world, minX, minY, minZ, maxX, maxY, maxZ, 0);
    }

    @Override
    public void getArea(Set<ChunkPosition> area){
        getArea(area, (ProgWidgetArea)getConnectedParameters()[0], (ProgWidgetArea)getConnectedParameters()[getParameters().length]);
    }

    public static void getArea(Set<ChunkPosition> area, ProgWidgetArea whitelistWidget, ProgWidgetArea blacklistWidget){
        if(whitelistWidget == null) return;
        ProgWidgetArea widget = whitelistWidget;
        while(widget != null) {
            widget.getArea(area);
            widget = (ProgWidgetArea)widget.getConnectedParameters()[0];
        }
        widget = blacklistWidget;
        while(widget != null) {
            Set<ChunkPosition> blacklistedArea = new HashSet<ChunkPosition>();
            widget.getArea(area);
            area.removeAll(blacklistedArea);
            widget = (ProgWidgetArea)widget.getConnectedParameters()[0];
        }
    }

    @Override
    public boolean isItemValidForFilters(ItemStack item){
        return isItemValidForFilters(item, -1);
    }

    public boolean isItemValidForFilters(ItemStack item, int blockMetadata){
        return ProgWidgetItemFilter.isItemValidForFilters(item, ProgWidget.getConnectedWidgetList(this, 1), ProgWidget.getConnectedWidgetList(this, getParameters().length + 1), blockMetadata);
    }

    public boolean isItemFilterEmpty(){
        return getConnectedParameters()[1] == null && getConnectedParameters()[3] == null;
    }

    public List<Entity> getEntitiesInArea(World world, IEntitySelector filter){
        return getEntitiesInArea((ProgWidgetArea)getConnectedParameters()[0], (ProgWidgetArea)getConnectedParameters()[getParameters().length], world, filter, null);
    }

    public static List<Entity> getValidEntities(World world, IProgWidget widget){
        StringFilterEntitySelector whitelistFilter = getEntityFilter((ProgWidgetString)widget.getConnectedParameters()[1], true);
        StringFilterEntitySelector blacklistFilter = getEntityFilter((ProgWidgetString)widget.getConnectedParameters()[widget.getParameters().length + 1], false);
        return getEntitiesInArea((ProgWidgetArea)widget.getConnectedParameters()[0], (ProgWidgetArea)widget.getConnectedParameters()[widget.getParameters().length], world, whitelistFilter, blacklistFilter);
    }

    @Override
    public List<Entity> getValidEntities(World world){
        return getValidEntities(world, this);
    }

    public static boolean isEntityValid(Entity entity, IProgWidget widget){
        StringFilterEntitySelector whitelistFilter = getEntityFilter((ProgWidgetString)widget.getConnectedParameters()[1], true);
        StringFilterEntitySelector blacklistFilter = getEntityFilter((ProgWidgetString)widget.getConnectedParameters()[widget.getParameters().length + 1], false);
        return whitelistFilter.isEntityApplicable(entity) && !blacklistFilter.isEntityApplicable(entity);
    }

    @Override
    public boolean isEntityValid(Entity entity){
        return isEntityValid(entity, this);
    }

    public static StringFilterEntitySelector getEntityFilter(ProgWidgetString widget, boolean allowEntityIfNoFilter){
        StringFilterEntitySelector filter = new StringFilterEntitySelector();
        if(widget != null) {
            while(widget != null) {
                filter.addEntry(widget.string);
                widget = (ProgWidgetString)widget.getConnectedParameters()[0];
            }
        } else if(allowEntityIfNoFilter) {
            filter.setFilter("");
        }
        return filter;
    }

    public static List<Entity> getEntitiesInArea(ProgWidgetArea whitelistWidget, ProgWidgetArea blacklistWidget, World world, IEntitySelector whitelistFilter, IEntitySelector blacklistFilter){
        if(whitelistWidget == null) return new ArrayList<Entity>();
        Set<Entity> entities = new HashSet<Entity>();
        ProgWidgetArea widget = whitelistWidget;
        while(widget != null) {
            entities.addAll(widget.getEntitiesWithinArea(world, whitelistFilter));
            widget = (ProgWidgetArea)widget.getConnectedParameters()[0];
        }
        widget = blacklistWidget;
        while(widget != null) {
            entities.removeAll(widget.getEntitiesWithinArea(world, whitelistFilter));
            widget = (ProgWidgetArea)widget.getConnectedParameters()[0];
        }
        if(blacklistFilter != null) {
            Entity[] entArray = entities.toArray(new Entity[entities.size()]);
            for(Entity entity : entArray) {
                if(blacklistFilter.isEntityApplicable(entity)) {
                    entities.remove(entity);
                }
            }
        }
        return new ArrayList<Entity>(entities);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen getOptionWindow(GuiProgrammer guiProgrammer){
        return new GuiProgWidgetAreaShow(this, guiProgrammer);
    }

    @Override
    public WidgetDifficulty getDifficulty(){
        return WidgetDifficulty.EASY;
    }
}
