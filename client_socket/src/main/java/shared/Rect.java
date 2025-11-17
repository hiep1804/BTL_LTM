/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

/**
 *
 * @author hn235
 */
class Rect {
    private int x, y, w, h;
    private Color color;
    private String value;
    private boolean movable;
    private int originalX, originalY; // lưu vị trí hiện tại của bóng (có thể thay đổi khi snap vào ô)
    private int initialX, initialY; // lưu vị trí gốc ban đầu không bao giờ thay đổi

    public Rect(int x, int y, int w, int h, Color color, String value, boolean movable) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.color = color;
        this.value = value;
        this.movable = movable;
        this.originalX = x;
        this.originalY = y;
        this.initialX = x;
        this.initialY = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public int getOriginalX() {
        return originalX;
    }

    public void setOriginalX(int originalX) {
        this.originalX = originalX;
    }

    public int getOriginalY() {
        return originalY;
    }

    public void setOriginalY(int originalY) {
        this.originalY = originalY;
    }

    public int getInitialX() {
        return initialX;
    }

    public int getInitialY() {
        return initialY;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, w, h);
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Vẽ bóng đổ (shadow)
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillOval(x + 5, y + 5, w, h);
        
        // Vẽ quả bóng bay với gradient
        GradientPaint gradient;
        if (movable) {
            // Bóng bay màu sắc sinh động cho ô di chuyển
            Color[] balloonColors = {
                new Color(255, 107, 107), // Đỏ
                new Color(255, 193, 7),   // Vàng
                new Color(76, 175, 80),   // Xanh lá
                new Color(33, 150, 243),  // Xanh dương
                new Color(156, 39, 176),  // Tím
                new Color(255, 87, 34),   // Cam
                new Color(233, 30, 99),   // Hồng
                new Color(0, 188, 212)    // Xanh ngọc
            };
            int colorIndex = Math.abs(value.hashCode()) % balloonColors.length;
            Color balloonColor = balloonColors[colorIndex];
            gradient = new GradientPaint(
                x, y, balloonColor.brighter(),
                x + w, y + h, balloonColor.darker()
            );
        } else {
            // Bóng bay xám nhạt cho ô đứng im
            if (value == null || value.isEmpty()) {
                gradient = new GradientPaint(
                    x, y, new Color(240, 240, 240),
                    x + w, y + h, new Color(200, 200, 200)
                );
            } else {
                // Khi đã có giá trị, hiển thị màu xanh lá nhạt
                gradient = new GradientPaint(
                    x, y, new Color(129, 199, 132),
                    x + w, y + h, new Color(76, 175, 80)
                );
            }
        }
        
        g2d.setPaint(gradient);
        g2d.fillOval(x, y, w, h);
        
        // Vẽ viền bóng bay
        g2d.setColor(movable ? new Color(255, 255, 255, 150) : new Color(0, 0, 0, 100));
        g2d.drawOval(x, y, w, h);
        
        // Vẽ highlight (ánh sáng phản chiếu)
        GradientPaint highlight = new GradientPaint(
            x + w/4, y + h/4, new Color(255, 255, 255, 180),
            x + w/3, y + h/3, new Color(255, 255, 255, 0)
        );
        g2d.setPaint(highlight);
        g2d.fillOval(x + w/6, y + h/6, w/3, h/3);
        
        // Vẽ dây bóng bay (cho ô di chuyển)
        if (movable) {
            g2d.setColor(new Color(100, 100, 100));
            int centerX = x + w/2;
            int bottomY = y + h;
            g2d.drawLine(centerX, bottomY, centerX, bottomY + 15);
            // Nút dây
            g2d.fillOval(centerX - 2, bottomY + 15, 4, 4);
        }
        
        // Vẽ text với font đẹp
        if (value != null && !value.isEmpty()) {
            g2d.setColor(Color.WHITE);
            Font font = new Font("Arial", Font.BOLD, 24);
            g2d.setFont(font);
            
            // Vẽ shadow cho text
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(value);
            int textHeight = fm.getAscent();
            int textX = x + (w - textWidth) / 2;
            int textY = y + (h + textHeight) / 2 - 4;
            
            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.drawString(value, textX + 1, textY + 1);
            
            g2d.setColor(Color.WHITE);
            g2d.drawString(value, textX, textY);
        }
        
        g2d.dispose();
    }
}
