/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.emanuel.jeremi.antitower.graphic;

import hu.emanuel.jeremi.antitower.entity.Enemy.EnemyType;
import hu.emanuel.jeremi.antitower.entity.Sprite.SpriteSequence;

/**
 *
 * @author User
 */
public interface GetEnemySpriteSequence {
    
    public SpriteSequence getEnemySprites(EnemyType type, int x, int y, int id);
    
}
