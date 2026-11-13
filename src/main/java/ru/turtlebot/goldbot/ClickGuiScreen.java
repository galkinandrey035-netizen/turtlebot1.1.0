package ru.turtlebot.goldbot;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;
public final class ClickGuiScreen extends Screen {
 private final List<GoldModule> modules=new ArrayList<>(GoldModuleManager.all());
 private static final int RH=14,CW=150;
 public ClickGuiScreen(){super(Text.literal("GoldBot ClickGUI"));}
 @Override public void render(DrawContext c,int mx,int my,float delta){
  c.fill(0,0,width,height,0xB0101018);c.drawCenteredTextWithShadow(textRenderer,title,width/2,12,0xFFFFFFFF);
  for(int i=0;i<modules.size();i++){GoldModule m=modules.get(i);int col=i/28,row=i%28,x=12+col*CW,y=34+row*RH;
   c.fill(x-3,y-2,x+CW-8,y+11,0x55303038);c.drawText(textRenderer,m.name(),x,y,m.enabled()?0xFF55DD88:0xFFCCCCCC,false);
   c.drawText(textRenderer,m.category(),x+92,y,0xFF888888,false);}
  super.render(c,mx,my,delta);
 }
 @Override public boolean mouseClicked(double mx,double my,int button){
  if(button==0)for(int i=0;i<modules.size();i++){int col=i/28,row=i%28,x=12+col*CW,y=34+row*RH;
   if(mx>=x-3&&mx<=x+CW-8&&my>=y-2&&my<=y+11){modules.get(i).toggle(client);return true;}}
  return super.mouseClicked(mx,my,button);
 }
 @Override public boolean shouldPause(){return false;}
}