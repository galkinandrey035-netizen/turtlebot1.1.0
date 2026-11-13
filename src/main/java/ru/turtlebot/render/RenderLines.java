package ru.turtlebot.render;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

public final class RenderLines {
    private RenderLines() {}
    public static void drawBox(VertexConsumer out, MatrixStack.Entry entry, Box box,
            float red, float green, float blue, float alpha) {
        double x1=box.minX, y1=box.minY, z1=box.minZ, x2=box.maxX, y2=box.maxY, z2=box.maxZ;
        line(out,entry,x1,y1,z1,x2,y1,z1,red,green,blue,alpha);
        line(out,entry,x2,y1,z1,x2,y1,z2,red,green,blue,alpha);
        line(out,entry,x2,y1,z2,x1,y1,z2,red,green,blue,alpha);
        line(out,entry,x1,y1,z2,x1,y1,z1,red,green,blue,alpha);
        line(out,entry,x1,y2,z1,x2,y2,z1,red,green,blue,alpha);
        line(out,entry,x2,y2,z1,x2,y2,z2,red,green,blue,alpha);
        line(out,entry,x2,y2,z2,x1,y2,z2,red,green,blue,alpha);
        line(out,entry,x1,y2,z2,x1,y2,z1,red,green,blue,alpha);
        line(out,entry,x1,y1,z1,x1,y2,z1,red,green,blue,alpha);
        line(out,entry,x2,y1,z1,x2,y2,z1,red,green,blue,alpha);
        line(out,entry,x2,y1,z2,x2,y2,z2,red,green,blue,alpha);
        line(out,entry,x1,y1,z2,x1,y2,z2,red,green,blue,alpha);
    }
    private static void line(VertexConsumer out, MatrixStack.Entry entry,
            double x1,double y1,double z1,double x2,double y2,double z2,
            float r,float g,float b,float a) {
        out.vertex(entry,(float)x1,(float)y1,(float)z1).color(r,g,b,a).normal(entry,0,1,0);
        out.vertex(entry,(float)x2,(float)y2,(float)z2).color(r,g,b,a).normal(entry,0,1,0);
    }
}