package GameObjects;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import Engine.GameObject;
import Engine.Scene;
import Engine.Vector2;
import Engine.Engine;

public class WeaponSelect extends GameObject {
    static ArrayList<WeaponSelect> weaponIcons = new ArrayList<>();
    static int selectedWeapon = -1;

    public static void createIcons(Scene scene) {
        WeaponSelect pistol = new WeaponSelect("pistol");
        WeaponSelect shotgun = new WeaponSelect("shotgun");
        WeaponSelect rifle = new WeaponSelect("rifle");

        weaponIcons.add(pistol);
        weaponIcons.add(shotgun);
        weaponIcons.add(rifle);
        selectWeapon(0);

        scene.addObject(pistol);
        scene.addObject(shotgun);
        scene.addObject(rifle);

        float xPos = 40;

        for (WeaponSelect weapon : weaponIcons) {
            weapon.position.y = 225;
            weapon.position.x = xPos;
            xPos += 65;
        }
    }

    public static void selectWeapon(int weaponIndex) {
        if (weaponIndex == selectedWeapon) {
            return;
        }
        for (WeaponSelect weapon : weaponIcons) {
            weapon.scale = new Vector2(0.04f, 0.04f);
        }

        WeaponSelect weapon = weaponIcons.get(weaponIndex);
        weapon.scale = new Vector2(0.05f, 0.05f);
        selectedWeapon = weaponIndex;
    }

    @Override
    protected void draw(Graphics2D g2d) {
        if (currentSprite == null) {
            return;
        }
        AffineTransform at = new AffineTransform();
        at.translate(position.x, position.y);
        at.rotate(Math.toRadians(rotation));
        at.scale(scale.x, scale.y);
        at.translate(-currentSprite.getImage().getWidth() * currentSprite.getPivot().x, 
            -currentSprite.getImage().getHeight() * currentSprite.getPivot().y);
        g2d.drawImage(currentSprite.getImage(), at, null);
    }

    WeaponSelect(String imageName) {
        scale = new Vector2(0.05f, 0.05f);
        setSprite(imageName);
        setLayer(1000);
    }
}
