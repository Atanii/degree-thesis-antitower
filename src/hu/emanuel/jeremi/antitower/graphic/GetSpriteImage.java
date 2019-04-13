/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.emanuel.jeremi.antitower.graphic;

import hu.emanuel.jeremi.antitower.entity.Enemy.EnemyType;
import hu.emanuel.jeremi.antitower.entity.Sprite.SpriteSequence;
import hu.emanuel.jeremi.antitower.entity.item.ItemType;
import java.awt.image.BufferedImage;

/**
 *
 * @author User
 */
public interface GetSpriteImage {

    public SpriteSequence getEnemySprites(EnemyType type, int x, int y, int id);

    public BufferedImage getItemSprite(ItemType type);

    public BufferedImage getDecorationSprite(int tileId);

    public BufferedImage getItemOverheadIcon(ItemType type);

}
