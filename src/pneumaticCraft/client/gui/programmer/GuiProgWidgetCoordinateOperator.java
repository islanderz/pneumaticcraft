package pneumaticCraft.client.gui.programmer;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;
import pneumaticCraft.client.gui.GuiProgrammer;
import pneumaticCraft.client.gui.widget.GuiRadioButton;
import pneumaticCraft.client.gui.widget.IGuiWidget;
import pneumaticCraft.client.gui.widget.WidgetTextField;
import pneumaticCraft.common.progwidgets.ProgWidgetCoordinateOperator;

public class GuiProgWidgetCoordinateOperator extends GuiProgWidgetAreaShow<ProgWidgetCoordinateOperator>{

    private WidgetTextField variableField;

    public GuiProgWidgetCoordinateOperator(ProgWidgetCoordinateOperator widget, GuiProgrammer guiProgrammer){
        super(widget, guiProgrammer);
    }

    @Override
    public void initGui(){
        super.initGui();

        List<GuiRadioButton> radioButtons = new ArrayList<GuiRadioButton>();
        GuiRadioButton radioButton = new GuiRadioButton(0, guiLeft + 7, guiTop + 42, 0xFF000000, "+  -");
        radioButtons.add(radioButton);
        radioButton.checked = !widget.isMultiplyAndDividing();
        radioButton.otherChoices = radioButtons;
        addWidget(radioButton);
        radioButton = new GuiRadioButton(1, guiLeft + 7, guiTop + 54, 0xFF000000, "* /");
        radioButtons.add(radioButton);
        radioButton.checked = widget.isMultiplyAndDividing();
        radioButton.otherChoices = radioButtons;
        addWidget(radioButton);

        variableField = new WidgetTextField(fontRendererObj, guiLeft + 90, guiTop + 42, 80, fontRendererObj.FONT_HEIGHT + 1);
        addWidget(variableField);
        variableField.setText(widget.getVariable());
    }

    @Override
    public void actionPerformed(IGuiWidget guiWidget){
        if(guiWidget.getID() == 0 || guiWidget.getID() == 1) {
            widget.setMultiplyDividing(guiWidget.getID() == 1);
        }
        super.actionPerformed(guiWidget);
    }

    @Override
    public void keyTyped(char chr, int keyCode){
        if(keyCode == 1) {
            widget.setVariable(variableField.getText());
        }
        super.keyTyped(chr, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        super.drawScreen(mouseX, mouseY, partialTicks);
        fontRendererObj.drawString(I18n.format("gui.progWidget.coordinate.variableName"), guiLeft + 90, guiTop + 30, 0xFF000000);
        fontRendererObj.drawString(I18n.format("gui.progWidget.coordinateOperator.operator"), guiLeft + 7, guiTop + 30, 0xFF000000);
    }
}
